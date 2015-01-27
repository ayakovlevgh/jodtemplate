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
package jodtemplate.pptx.style;

import static org.junit.Assert.assertEquals;

import java.util.List;

import jodtemplate.TestUtils;
import jodtemplate.pptx.style.HtmlStylizer;
import jodtemplate.style.Stylizer;

import org.jdom2.Element;
import org.junit.Test;

public class HtmlStylizerTest {

    private final Stylizer stylizer = new HtmlStylizer();

    @Test
    public void testSimpleText() throws Exception {
        final List<Element> result = stylizer.stylize("test text", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testBTag() throws Exception {
        final List<Element> result = stylizer.stylize("<b>test text</b>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr b=\"1\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testITag() throws Exception {
        final List<Element> result = stylizer.stylize("<i>test text</i>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr i=\"1\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testUTag() throws Exception {
        final List<Element> result = stylizer.stylize("<u>test text</u>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr u=\"sng\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testStrongTag() throws Exception {
        final List<Element> result = stylizer.stylize("<strong>test text</strong>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr b=\"1\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testEmTag() throws Exception {
        final List<Element> result = stylizer.stylize("<em>test text</em>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr i=\"1\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testInsTag() throws Exception {
        final List<Element> result = stylizer.stylize("<ins>test text</ins>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:rPr u=\"sng\" /><a:t>test text</a:t></a:r>", elementText);
    }

    @Test
    public void testPTag() throws Exception {
        final List<Element> result = stylizer.stylize("<p>test text</p>", null, null);
        assertEquals(result.size(), 1);
        final String elementText = TestUtils.convertElementToText(result.get(0));
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr><a:buNone /></a:pPr><a:r><a:t>test text</a:t></a:r></a:p>", elementText);
    }

    @Test
    public void testUlTag() throws Exception {
        final List<Element> result = stylizer.stylize(
                "<ul><li>item 1<ul><li>item 1.1</li></ul></li><li>item 2</li></ul>", null, null);
        assertEquals(result.size(), 3);
        final String elementText1 = TestUtils.convertElementToText(result.get(0));
        final String elementText2 = TestUtils.convertElementToText(result.get(1));
        final String elementText3 = TestUtils.convertElementToText(result.get(2));
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" marL=\"457200\">"
                + "<a:buFont charset=\"0\" panose=\"020B0604020202020204\" pitchFamily=\"34\" typeface=\"Arial\" />"
                + "<a:buChar char=\"•\" /></a:pPr><a:r><a:t>item 1</a:t></a:r></a:p>", elementText1);
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" lvl=\"1\" marL=\"914400\">"
                + "<a:buFont charset=\"0\" panose=\"020B0604020202020204\" pitchFamily=\"34\" typeface=\"Arial\" />"
                + "<a:buChar char=\"•\" /></a:pPr><a:r><a:t>item 1.1</a:t></a:r></a:p>", elementText2);
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" marL=\"457200\">"
                + "<a:buFont charset=\"0\" panose=\"020B0604020202020204\" pitchFamily=\"34\" typeface=\"Arial\" />"
                + "<a:buChar char=\"•\" /></a:pPr><a:r><a:t>item 2</a:t></a:r></a:p>", elementText3);
    }

    @Test
    public void testOlTag() throws Exception {
        final List<Element> result = stylizer.stylize(
                "<ol><li>item 1<ol><li>item 1.1</li></ol></li><li>item 2</li></ol>", null, null);
        assertEquals(result.size(), 3);
        final String elementText1 = TestUtils.convertElementToText(result.get(0));
        final String elementText2 = TestUtils.convertElementToText(result.get(1));
        final String elementText3 = TestUtils.convertElementToText(result.get(2));
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" marL=\"457200\">" + "<a:buFont typeface=\"+mj-lt\" />"
                + "<a:buAutoNum type=\"arabicPeriod\" /></a:pPr><a:r><a:t>item 1</a:t></a:r></a:p>", elementText1);
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" lvl=\"1\" marL=\"914400\">" + "<a:buFont typeface=\"+mj-lt\" />"
                + "<a:buAutoNum type=\"arabicPeriod\" /></a:pPr><a:r><a:t>item 1.1</a:t></a:r></a:p>", elementText2);
        assertEquals("<a:p xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:pPr indent=\"-457200\" marL=\"457200\">" + "<a:buFont typeface=\"+mj-lt\" />"
                + "<a:buAutoNum type=\"arabicPeriod\" /></a:pPr><a:r><a:t>item 2</a:t></a:r></a:p>", elementText3);
    }

    @Test
    public void testBrTag() throws Exception {
        final List<Element> result = stylizer.stylize("test <br />text", null, null);
        assertEquals(result.size(), 3);
        final String elementText1 = TestUtils.convertElementToText(result.get(0));
        final String elementText2 = TestUtils.convertElementToText(result.get(1));
        final String elementText3 = TestUtils.convertElementToText(result.get(2));
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:t>test </a:t></a:r>", elementText1);
        assertEquals("<a:br xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" />", elementText2);
        assertEquals("<a:r xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:t>text</a:t></a:r>", elementText3);
    }
}
