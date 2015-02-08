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
package jodtemplate.pptx.postprocessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jodtemplate.TestUtils;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.Slide;
import jodtemplate.style.Stylizer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StylePostprocessorTest {

    private static final String STYLIZED_STRING_CLASS_NAME = "some.package.StylizedString";

    @InjectMocks
    private StylePostprocessor postprocessor;

    @Mock
    private Stylizer stylizer;

    private final List<Element> stylized = new ArrayList<>();

    @Mock
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        stylized.add(TestUtils.createApElement("elem 1"));
        stylized.add(TestUtils.createArElement("elem 2"));
        stylized.add(TestUtils.createApElement("elem 3"));

        when(stylizer.stylize(anyString(), any(Element.class), any(Element.class), any(Slide.class)))
                .thenReturn(stylized);
        when(configuration.getStylizer(STYLIZED_STRING_CLASS_NAME)).thenReturn(stylizer);
    }

    @Test
    public void testProcess() throws Exception {
        final String sourceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                + "xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<p:cSld><p:spTree><p:sp><p:txBody>"
                + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buFont/><a:buChar/></a:pPr>"
                + "<a:r><a:rPr b=\"1\" dirty=\"0\" err=\"1\" lang=\"en-US\" smtClean=\"0\"/>"
                + "<a:t><!-- stylized " + STYLIZED_STRING_CLASS_NAME
                + ": <p>elem 1</p><b>elem 2</b><p>elem 3</p>--></a:t></a:r>"
                + "<a:r><a:t>text 1</a:t></a:r><a:endParaRPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\"/></a:p>"
                + "<a:p><a:r><a:t>text 2</a:t></a:r></a:p></p:txBody></p:sp></p:spTree></p:cSld></p:sld>";
        Document dom = TestUtils.createJDOMDocument(sourceXml);

        dom = postprocessor.process(null, dom, null, null, configuration);

        final String result = TestUtils.convertDocumentToText(dom);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<p:sld xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                + "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<p:cSld><p:spTree><p:sp><p:txBody>"
                + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buFont /><a:buChar /></a:pPr>"
                + "<a:endParaRPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\" /></a:p>"
                + "<a:p><a:r><a:t>elem 1</a:t></a:r></a:p>"
                + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buNone /></a:pPr>"
                + "<a:r><a:t>elem 2</a:t></a:r></a:p>"
                + "<a:p><a:r><a:t>elem 3</a:t></a:r></a:p>"
                + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buFont /><a:buChar /></a:pPr>"
                + "<a:r><a:t>text 1</a:t></a:r></a:p>"
                + "<a:p><a:r><a:t>text 2</a:t></a:r></a:p></p:txBody></p:sp></p:spTree></p:cSld></p:sld>\n", result);
    }

    /*    @Test
        public void testProcessExpressionArShouldBeDeleted() throws Exception {
            final String sourceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                    + "<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                    + "xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                    + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                    + "<p:cSld><p:spTree><p:sp><p:txBody>"
                    + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buNone/></a:pPr>"
                    + "<a:r><a:rPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\"/>"
                    + "<a:t><!-- stylized " + STYLIZED_STRING_CLASS_NAME
                    + ": <p><strong>text</strong></p><ul><li>item</li></ul--></a:t></a:r>"
                    + "<a:endParaRPr dirty=\"0\" lang=\"en-US\"/></a:p>"
                    + "</p:txBody></p:sp></p:spTree></p:cSld></p:sld>";
            Document dom = TestUtils.createJDOMDocument(sourceXml);


            final Element at = new Element("t", getNamespace());
            at.setText("text");
            final Element ar = new Element("r", getNamespace());
                ar.addContent(at);
                return ar;
            }

            public static Element createApElement(final String text) {
                final Element ap = new Element("p", getNamespace());
                ap.addContent(createArElement(text));
                return ap;
            }
            when(stylizer.stylize(anyString(), any(Element.class), any(Element.class))).thenReturn(stylized);

            dom = postprocessor.process(null, dom, null, null, configuration);

            final String result = TestUtils.convertDocumentToText(dom);

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                    + "<p:sld xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                    + "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                    + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                    + "<p:cSld><p:spTree><p:sp><p:txBody>"
                    + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buFont /><a:buChar /></a:pPr>"
                    + "<a:endParaRPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\" /></a:p>"
                    + "<a:p><a:r><a:t>elem 1</a:t></a:r></a:p>"
                    + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\" /><a:r><a:t>elem 2</a:t></a:r></a:p>"
                    + "<a:p><a:r><a:t>elem 3</a:t></a:r></a:p>"
                    + "<a:p><a:pPr indent=\"-285750\" marL=\"285750\"><a:buFont /><a:buChar /></a:pPr>"
                    + "<a:r><a:t>text 1</a:t></a:r></a:p>"
                    + "<a:p><a:r><a:t>text 2</a:t></a:r></a:p></p:txBody></p:sp></p:spTree></p:cSld></p:sld>\n", result);
        }*/

    @Test
    public void testProcessNoApPrAndArPr() throws Exception {
        final String sourceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                + "xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<p:cSld><p:spTree><p:sp><p:txBody>"
                + "<a:p><a:r><a:t><!-- stylized " + STYLIZED_STRING_CLASS_NAME
                + ": <p>text</p><b>text</b><p>text</p>--></a:t></a:r>"
                + "<a:r><a:t>text 1</a:t></a:r><a:endParaRPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\"/></a:p>"
                + "<a:p><a:r><a:t>text 2</a:t></a:r></a:p></p:txBody></p:sp></p:spTree></p:cSld></p:sld>";
        Document dom = TestUtils.createJDOMDocument(sourceXml);

        dom = postprocessor.process(null, dom, null, null, configuration);

        final String result = TestUtils.convertDocumentToText(dom);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<p:sld xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                + "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<p:cSld><p:spTree><p:sp><p:txBody>"
                + "<a:p><a:endParaRPr dirty=\"0\" lang=\"en-US\" smtClean=\"0\" /></a:p>"
                + "<a:p><a:r><a:t>elem 1</a:t></a:r></a:p>"
                + "<a:p><a:pPr><a:buNone /></a:pPr><a:r><a:t>elem 2</a:t></a:r></a:p>"
                + "<a:p><a:r><a:t>elem 3</a:t></a:r></a:p>"
                + "<a:p><a:r><a:t>text 1</a:t></a:r></a:p>"
                + "<a:p><a:r><a:t>text 2</a:t></a:r></a:p></p:txBody></p:sp></p:spTree></p:cSld></p:sld>\n", result);
    }

}
