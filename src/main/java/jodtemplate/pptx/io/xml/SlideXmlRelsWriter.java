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
package jodtemplate.pptx.io.xml;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jodtemplate.OOXMLDocument;
import jodtemplate.Relationship;
import jodtemplate.io.xml.XmlWriter;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;

public class SlideXmlRelsWriter implements XmlWriter<Slide> {

    @Override
    public void write(final Resources resources, final Slide slide, final XMLOutputFactory xmlOutputFactory)
            throws XMLStreamException, IOException {
        final String slideXmlPath = FilenameUtils.normalize(slide.getPresentation().getFullPath()
                + slide.getRelationship().getTarget(), true);
        final String slideXmlRelsPath = Utils.getRelsPathNoPrefixSeparator(slideXmlPath);
        final Resource slideXmlRelsRes = resources.getResource(slideXmlRelsPath);

        try (final OutputStream os = slideXmlRelsRes.getOutputStream()) {
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n".getBytes(CharEncoding.UTF_8));
            final XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(os);
            writer.writeStartElement(OOXMLDocument.RELATIONSHIPS_ELEMENT);
            writer.writeNamespace("", OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE);
            for (Relationship rel : slide.getOtherRelationships()) {
                writer.writeEmptyElement(OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE,
                        OOXMLDocument.RELATIONSHIP_ELEMENT);
                writer.writeAttribute(OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE,
                        OOXMLDocument.ID_ATTRIBUTE, rel.getId());
                writer.writeAttribute(OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE,
                        OOXMLDocument.TYPE_ATTRIBUTE, rel.getType());
                writer.writeAttribute(OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE,
                        OOXMLDocument.TARGET_ATTRIBUTE, rel.getTarget());
                writer.flush();
            }
            writer.writeEndElement();
            writer.writeEndDocument();

            writer.flush();
            writer.close();
        }
    }

}
