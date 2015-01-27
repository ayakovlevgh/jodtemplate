/*
 * 
 * Copyright 2015 Andrey Yakovlev
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package jodtemplate.pptx.io;

import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import jodtemplate.contenttype.ContentTypes;
import jodtemplate.io.xml.ContentTypesReader;
import jodtemplate.pptx.Presentation;
import jodtemplate.pptx.Slide;
import jodtemplate.pptx.io.xml.PresentationXmlRelsReader;
import jodtemplate.pptx.io.xml.SlideXmlRelsReader;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.io.FilenameUtils;

public class PPTXReaderImpl implements PPTXReader {

    private ContentTypesReader contentTypesReader;

    private PresentationXmlRelsReader presentationXmlRelsReader;

    private SlideXmlRelsReader slideXmlRelsReader;

    private PPTXImageReader imageReader;

    public PPTXReaderImpl() {
        this(new ContentTypesReader(), new PresentationXmlRelsReader(),
                new SlideXmlRelsReader(), new PPTXImageReader());
    }

    public PPTXReaderImpl(final ContentTypesReader contentTypesReader,
            final PresentationXmlRelsReader presentationXmlRelsReader,
            final SlideXmlRelsReader slideXmlRelsReader,
            final PPTXImageReader imageReader) {
        this.contentTypesReader = contentTypesReader;
        this.presentationXmlRelsReader = presentationXmlRelsReader;
        this.slideXmlRelsReader = slideXmlRelsReader;
        this.imageReader = imageReader;
    }

    @Override
    public Presentation read(final Resources resources) throws IOException, XMLStreamException {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        ContentTypes contentTypes = new ContentTypes();
        contentTypes = contentTypesReader.read("/[Content_Types].xml", resources, xmlInputFactory, contentTypes);
        final String presentationXmlPath = contentTypes.getOverridesByType(ContentTypes.PRESENTATION_TYPE).get(0)
                .getPartName();
        Presentation presentation = new Presentation(presentationXmlPath);
        presentation = presentationXmlRelsReader.read(presentation.getXmlRelsPath(),
                resources, xmlInputFactory, presentation);
        for (Slide slide : presentation.getSlides()) {
            final String slideXmlPath = FilenameUtils.normalize(presentation.getFullPath()
                    + slide.getRelationship().getTarget(), true);
            final String slideXmlRelsPath = Utils.getRelsPath(slideXmlPath);
            slideXmlRelsReader.read(slideXmlRelsPath, resources, xmlInputFactory, slide);
        }
        imageReader.read(resources, presentation);

        return presentation;
    }

}
