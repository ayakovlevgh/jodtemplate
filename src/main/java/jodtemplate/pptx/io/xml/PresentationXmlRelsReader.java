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
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jodtemplate.OOXMLDocument;
import jodtemplate.Relationship;
import jodtemplate.io.xml.XmlReader;
import jodtemplate.pptx.Presentation;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.lang3.StringUtils;

public class PresentationXmlRelsReader implements XmlReader<Presentation> {

    @Override
    public Presentation read(final String path, final Resources resources, final XMLInputFactory xmlInputFactory,
            final Presentation presentation) throws XMLStreamException, IOException {
        final Resource presentationXmlRelsRes = resources.getResource(Utils.removePrefixSeparator(path));
        try (final InputStream is = presentationXmlRelsRes.getInputStream()) {
            final XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(is);
            int event = xmlStreamReader.next();
            while (event != XMLStreamConstants.END_DOCUMENT) {
                if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    final String elementNS = xmlStreamReader.getName().getNamespaceURI();
                    final String elementName = xmlStreamReader.getName().getLocalPart();
                    if (OOXMLDocument.RELATIONSHIP_ELEMENT.equals(elementName)
                            && OOXMLDocument.RELATIONSHIPS_RELS_NAMESPACE.equals(elementNS)) {
                        final Relationship relationship = createRelationshipElement(xmlStreamReader);
                        if (Relationship.SLIDE_TYPE.equals(relationship.getType())) {
                            final Slide slide = new Slide();
                            slide.setRelationship(relationship);
                            slide.setPresentation(presentation);
                            presentation.addSlide(slide);
                        } else {
                            presentation.addOtherRelationship(relationship);
                        }
                    }
                }
                event = xmlStreamReader.next();
            }
        }

        return presentation;
    }

    private Relationship createRelationshipElement(final XMLStreamReader xmlStreamReader) {
        final Relationship relationship = new Relationship();
        for (int index = 0; index < xmlStreamReader.getAttributeCount(); ++index) {
            final String attributeName = xmlStreamReader.getAttributeName(index).getLocalPart();
            final String attributeNS = xmlStreamReader.getAttributeName(index).getNamespaceURI();
            if (OOXMLDocument.ID_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                relationship.setId(xmlStreamReader.getAttributeValue(index));
            } else if (OOXMLDocument.TARGET_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                relationship.setTarget(xmlStreamReader.getAttributeValue(index));
            } else if (OOXMLDocument.TYPE_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                relationship.setType(xmlStreamReader.getAttributeValue(index));
            }
        }
        return relationship;
    }

}
