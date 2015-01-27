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
package jodtemplate.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.xml.stream.XMLInputFactory;

import jodtemplate.Relationship;
import jodtemplate.contenttype.ContentTypes;
import jodtemplate.contenttype.OverrideElement;
import jodtemplate.io.xml.ContentTypesReader;
import jodtemplate.pptx.Presentation;
import jodtemplate.pptx.Slide;
import jodtemplate.pptx.io.PPTXImageReader;
import jodtemplate.pptx.io.PPTXReaderImpl;
import jodtemplate.pptx.io.xml.PresentationXmlRelsReader;
import jodtemplate.pptx.io.xml.SlideXmlRelsReader;
import jodtemplate.resource.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class PPTXReaderImplTest {

    @InjectMocks
    private PPTXReaderImpl reader;

    @Mock
    private ContentTypesReader contentTypesReader;

    @Mock
    private PresentationXmlRelsReader presXmlRelsReader;

    @Mock
    private SlideXmlRelsReader slideXmlRelsReader;

    @Mock
    private PPTXImageReader imageReader;

    @Mock
    private Resources resources;

    @Test
    public void testRead() throws Exception {
        final String presentationXml = "/ppt/presentation.xml";
        final String presentationRelsXml = "/ppt/_rels/presentation.xml.rels";

        final ContentTypes contentTypes = new ContentTypes();
        doAnswer(new Answer<ContentTypes>() {
            @Override
            public ContentTypes answer(final InvocationOnMock invocation) throws Throwable {
                //final ContentTypes contentTypes = invocation.getArgumentAt(3, ContentTypes.class);
                final OverrideElement overrideElement = new OverrideElement();
                overrideElement.setPartName(presentationXml);
                overrideElement.setContentType(ContentTypes.PRESENTATION_TYPE);
                contentTypes.addOverrideElement(overrideElement);
                return contentTypes;
            }
        }).when(contentTypesReader).read(eq("/[Content_Types].xml"), eq(resources),
                any(XMLInputFactory.class), any(ContentTypes.class));

        final Presentation presentation = new Presentation(presentationXml);
        final Slide slide = new Slide();
        doAnswer(new Answer<Presentation>() {
            @Override
            public Presentation answer(final InvocationOnMock invocation) throws Throwable {
                //final Presentation presentation = invocation.getArgumentAt(3, Presentation.class);
                slide.setRelationship(new Relationship("rId1", "slides/slide1.xml", Relationship.SLIDE_TYPE));
                presentation.addSlide(slide);
                return presentation;
            }
        }).when(presXmlRelsReader).read(eq(presentationRelsXml), eq(resources),
                any(XMLInputFactory.class), any(Presentation.class));

        doReturn(slide).when(slideXmlRelsReader).read(eq("/ppt/slides/slide1.xml"), eq(resources),
                any(XMLInputFactory.class), eq(slide));

        doNothing().when(imageReader).read(resources, presentation);

        final Presentation result = reader.read(resources);

        assertEquals(presentation, result);

        verify(contentTypesReader, times(1)).read(eq("/[Content_Types].xml"), eq(resources),
                any(XMLInputFactory.class), any(ContentTypes.class));
        verify(presXmlRelsReader, times(1)).read(eq(presentationRelsXml), eq(resources),
                any(XMLInputFactory.class), any(Presentation.class));
        verify(slideXmlRelsReader, times(1)).read(eq("/ppt/slides/_rels/slide1.xml.rels"), eq(resources),
                any(XMLInputFactory.class), eq(slide));
        verify(imageReader, times(1)).read(eq(resources), eq(presentation));
    }
}
