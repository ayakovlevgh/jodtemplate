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
package jodtemplate.pptx.preprocessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jodtemplate.TestUtils;
import jodtemplate.parser.Parser;
import jodtemplate.parser.ParserFactory;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.preprocessor.FormatTagsPreprocessor;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FormatTagsPreprocessorTest {

    @InjectMocks
    private FormatTagsPreprocessor prerprocessor;

    @Mock
    private Parser parser;

    @Mock
    private ParserFactory parserFactory;

    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        when(configuration.getParserFactory()).thenReturn(parserFactory);
        when(parserFactory.createParser()).thenReturn(parser);
    }

    @Test
    public void testProcess() throws Exception {
        final String sourceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                + "xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<p:cSld><p:spTree><p:sp><p:txBody>"
                + "<a:p><a:r><a:t>{{field}</a:t></a:r><a:r><a:t>} text {{field}}</a:t></a:r></a:p>"
                + "</p:txBody></p:sp></p:spTree></p:cSld></p:sld>";
        Document dom = TestUtils.createJDOMDocument(sourceXml);
        final List<String> firstParserResult = new ArrayList<>();
        final List<String> secondParserResult = Arrays.asList("{{field}}", " text ", "{{field}}");
        when(parser.parse("{{field}")).thenReturn(firstParserResult);
        when(parser.parse("} text {{field}}")).thenReturn(secondParserResult);

        dom = prerprocessor.process(null, dom, null, null, configuration);

        final String result = TestUtils.convertDocumentToText(dom);

        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                        + "<p:sld xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" "
                        + "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
                        + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                        + "<p:cSld><p:spTree><p:sp><p:txBody>"
                        + "<a:p><a:r><a:t>{{field}}</a:t></a:r><a:r><a:t> text </a:t></a:r><a:r><a:t>{{field}}</a:t></a:r></a:p>"
                        + "</p:txBody></p:sp></p:spTree></p:cSld></p:sld>\n", result);
    }
}
