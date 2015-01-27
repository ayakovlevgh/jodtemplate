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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import jodtemplate.contenttype.ContentTypes;
import jodtemplate.contenttype.DefaultElement;
import jodtemplate.contenttype.OverrideElement;
import jodtemplate.io.xml.ContentTypesReader;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContentTypesReaderTest {

    @InjectMocks
    private ContentTypesReader reader;

    @Mock
    private Resources resources;

    @Mock
    private Resource contentTypesRes;

    @Test
    public void testRead() throws Exception {
        final String contentTypesXml = "/file.xml";
        final String contentTypesXmlNoPrefix = "file.xml";
        final String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default ContentType=\"image/jpeg\" Extension=\"jpeg\"/>"
                + "<Default ContentType=\"application/xml\" Extension=\"xml\"/>"
                + "<Override "
                + "ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml\" "
                + "PartName=\"/ppt/presentation.xml\"/>"
                + "<Override "
                + "ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slide+xml\" "
                + "PartName=\"/ppt/slides/slide1.xml\"/>"
                + "</Types>";
        final int defaultElementsNum = 2;
        final int overrideElementsNum = 2;
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        when(contentTypesRes.getInputStream()).thenReturn(new ByteArrayInputStream(testXml.getBytes()));
        when(resources.getResource(contentTypesXmlNoPrefix)).thenReturn(contentTypesRes);

        final ContentTypes result = new ContentTypes();
        reader.read(contentTypesXml, resources, xmlInputFactory, result);

        verify(resources, times(1)).getResource(contentTypesXmlNoPrefix);
        verify(contentTypesRes, times(1)).getInputStream();

        final List<DefaultElement> defaultElements = result.getDefaultElements();
        assertEquals(defaultElementsNum, defaultElements.size());
        assertEquals(defaultElements.get(0).getContentType(), "image/jpeg");
        assertEquals(defaultElements.get(0).getExtension(), "jpeg");
        assertEquals(defaultElements.get(1).getContentType(), "application/xml");
        assertEquals(defaultElements.get(1).getExtension(), "xml");

        final List<OverrideElement> overrideElements = result.getOverrideElements();
        assertEquals(overrideElementsNum, overrideElements.size());
        assertEquals(overrideElements.get(0).getContentType(),
                "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml");
        assertEquals(overrideElements.get(0).getPartName(), "/ppt/presentation.xml");
        assertEquals(overrideElements.get(1).getContentType(),
                "application/vnd.openxmlformats-officedocument.presentationml.slide+xml");
        assertEquals(overrideElements.get(1).getPartName(), "/ppt/slides/slide1.xml");
    }
}
