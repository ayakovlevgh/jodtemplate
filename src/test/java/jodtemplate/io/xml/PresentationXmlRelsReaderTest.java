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

import jodtemplate.Relationship;
import jodtemplate.pptx.Presentation;
import jodtemplate.pptx.Slide;
import jodtemplate.pptx.io.xml.PresentationXmlRelsReader;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PresentationXmlRelsReaderTest {

    @InjectMocks
    private PresentationXmlRelsReader reader;

    @Mock
    private Resources resources;

    @Mock
    private Resource presentationXmlRelsRes;

    @Test
    public void testRead() throws Exception {
        final String presentationXml = "/ppt/file.xml";
        final String presentationXmlRels = "/ppt/_rels/file.xml.rels";
        final String presentationXmlRelsNoPrefix = "ppt/_rels/file.xml.rels";
        final String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Target=\"slides/slide1.xml\" "
                + "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide\"/>"
                + "<Relationship Id=\"rId2\" Target=\"slides/slide2.xml\" "
                + "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide\"/>"
                + "<Relationship Id=\"rId3\" Target=\"theme/theme1.xml\" "
                + "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme\"/>"
                + "</Relationships>";
        final int numOfSlides = 2;
        final int numOfOtherRels = 1;

        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        when(presentationXmlRelsRes.getInputStream()).thenReturn(new ByteArrayInputStream(testXml.getBytes()));
        when(resources.getResource(presentationXmlRelsNoPrefix)).thenReturn(presentationXmlRelsRes);

        final Presentation result = new Presentation(presentationXml);
        reader.read(presentationXmlRels, resources, xmlInputFactory, result);

        verify(resources, times(1)).getResource(presentationXmlRelsNoPrefix);
        verify(presentationXmlRelsRes, times(1)).getInputStream();

        assertEquals("/ppt/", result.getFullPath());

        final List<Slide> slides = result.getSlides();
        assertEquals(numOfSlides, slides.size());
        assertEquals(slides.get(0).getRelationship().getId(), "rId1");
        assertEquals(slides.get(0).getRelationship().getTarget(), "slides/slide1.xml");
        assertEquals(slides.get(0).getRelationship().getType(),
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide");
        assertEquals(slides.get(1).getRelationship().getId(), "rId2");
        assertEquals(slides.get(1).getRelationship().getTarget(), "slides/slide2.xml");
        assertEquals(slides.get(1).getRelationship().getType(),
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide");

        final List<Relationship> otherRels = result.getOtherRelationships();
        assertEquals(numOfOtherRels, otherRels.size());
        assertEquals(otherRels.get(0).getId(), "rId3");
        assertEquals(otherRels.get(0).getTarget(), "theme/theme1.xml");
        assertEquals(otherRels.get(0).getType(),
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme");
    }

}
