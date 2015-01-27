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
package jodtemplate.io.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jodtemplate.OOXMLDocument;
import jodtemplate.contenttype.ContentTypes;
import jodtemplate.contenttype.DefaultElement;
import jodtemplate.contenttype.OverrideElement;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.lang3.StringUtils;

public class ContentTypesReader implements XmlReader<ContentTypes> {

    @Override
    public ContentTypes read(final String path, final Resources resources, final XMLInputFactory xmlInputFactory,
            final ContentTypes contentTypes) throws XMLStreamException, IOException {
        final Resource contentTypesRes = resources.getResource(Utils.removePrefixSeparator(path));
        try (final InputStream is = contentTypesRes.getInputStream()) {
            final XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(is);
            int event = xmlStreamReader.next();
            while (event != XMLStreamConstants.END_DOCUMENT) {
                if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    final String elementNS = xmlStreamReader.getName().getNamespaceURI();
                    final String elementName = xmlStreamReader.getName().getLocalPart();
                    if (OOXMLDocument.DEFAULT_ELEMENT.equals(elementName)
                            && OOXMLDocument.CONTENT_TYPES_NAMESPACE.equals(elementNS)) {
                        contentTypes.addDefaultElement(createDefaultElement(xmlStreamReader));
                    }
                    if (OOXMLDocument.OVERRIDE_ELEMENT.equals(elementName)
                            && OOXMLDocument.CONTENT_TYPES_NAMESPACE.equals(elementNS)) {
                        contentTypes.addOverrideElement(createOverrideElement(xmlStreamReader));
                    }
                }
                event = xmlStreamReader.next();
            }
        }

        return contentTypes;
    }

    private OverrideElement createOverrideElement(final XMLStreamReader xmlStreamReader) {
        final OverrideElement overrideElement = new OverrideElement();
        for (int index = 0; index < xmlStreamReader.getAttributeCount(); ++index) {
            final String attributeName = xmlStreamReader.getAttributeName(index).getLocalPart();
            final String attributeNS = xmlStreamReader.getAttributeName(index).getNamespaceURI();
            if (OOXMLDocument.CONTENT_TYPE_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                overrideElement.setContentType(xmlStreamReader.getAttributeValue(index));
            } else if (OOXMLDocument.PART_NAME_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                overrideElement.setPartName(xmlStreamReader.getAttributeValue(index));
            }
        }
        return overrideElement;
    }

    private DefaultElement createDefaultElement(final XMLStreamReader xmlStreamReader) {
        final DefaultElement defaultElement = new DefaultElement();
        for (int index = 0; index < xmlStreamReader.getAttributeCount(); ++index) {
            final String attributeName = xmlStreamReader.getAttributeName(index).getLocalPart();
            final String attributeNS = xmlStreamReader.getAttributeName(index).getNamespaceURI();
            if (OOXMLDocument.CONTENT_TYPE_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                defaultElement.setContentType(xmlStreamReader.getAttributeValue(index));
            } else if (OOXMLDocument.EXTENSION_ATTRIBUTE.equals(attributeName) && StringUtils.isBlank(attributeNS)) {
                defaultElement.setExtension(xmlStreamReader.getAttributeValue(index));
            }
        }
        return defaultElement;
    }

}
