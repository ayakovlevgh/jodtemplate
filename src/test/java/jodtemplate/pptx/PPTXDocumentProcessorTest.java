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
package jodtemplate.pptx;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodtemplate.DomProcessor;
import jodtemplate.Relationship;
import jodtemplate.TestUtils;
import jodtemplate.parser.Parser;
import jodtemplate.parser.ParserFactory;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocumentProcessor;
import jodtemplate.pptx.Presentation;
import jodtemplate.pptx.Slide;
import jodtemplate.pptx.io.PPTXReader;
import jodtemplate.pptx.io.PPTXWriter;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.template.TemplateEngine;
import jodtemplate.template.expression.ExpressionHandler;
import jodtemplate.util.JDOMHelper;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class PPTXDocumentProcessorTest {

    private static final String SLIDE_REL_TARGET = "slides/slide1.xml";
    private static final String SLIDE_REL_ID = "1";

    @InjectMocks
    private PPTXDocumentProcessor processor;

    @Mock
    private Configuration configuration;

    @Mock
    private PPTXReader pptxReader;
    
    @Mock
    private PPTXWriter pptxWriter;

    @Mock
    private JDOMHelper jdomHelper;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private ExpressionHandler expressionHandler;

    @Mock
    private Parser parser;

    @Mock
    private ParserFactory parserFactory;

    @Mock
    private Resources resources;

    @Mock
    private Resource resource;

    @Mock
    private DomProcessor preprocessor;
    private List<DomProcessor> preprocessors;

    @Mock
    private DomProcessor postprocessor;
    private List<DomProcessor> postprocessors;

    @Before
    public void setUp() {
        preprocessors = Arrays.asList(preprocessor);
        postprocessors = Arrays.asList(postprocessor);
        when(configuration.getExpressionHandler()).thenReturn(expressionHandler);
        when(configuration.getPreprocessors()).thenReturn(preprocessors);
        when(configuration.getPostprocessors()).thenReturn(postprocessors);
        when(configuration.getParserFactory()).thenReturn(parserFactory);
        when(parserFactory.createParser()).thenReturn(parser);
        when(expressionHandler.getEngine()).thenReturn(templateEngine);
    }

    @Test
    public void testProcess() throws Exception {
        final Map<String, Object> context = new HashMap<>();

        final Presentation presentation = getPresentation();
        final Slide slide = presentation.getSlides().get(0);

        final String slideXml = "<p>"
                + "<r><t>text {{fi</t></r>"
                + "<r><t>eld</t></r>"
                + "<r><t>}}</t></r>"
                + "</p>";
        final Document dom = TestUtils.createJDOMDocument(slideXml);
        final String preprocessedSlideXml = "<p>"
                + "<r><t>text </t></r>"
                + "<r><t>{{field}}</t></r>"
                + "</p>";
        final String translatedSlideXml = "<p>"
                + "<r><t>text </t></r>"
                + "<r><t>${field}</t></r>"
                + "</p>";
        final String filledSlideXml = "<p>"
                + "<r><t>text </t></r>"
                + "<r><t>value</t></r>"
                + "</p>";
        final String parsedPart1 = "<p><r><t>text </t></r><r><t>";
        final String parsedPart2 = "{{field}}";
        final String parsedPart3 = "</t></r></p>";

        when(pptxReader.read(resources)).thenReturn(presentation);
        when(resources.getResource("ppt/slides/slide1.xml")).thenReturn(resource);
        final InputStream is = Mockito.mock(InputStream.class);
        when(resource.getInputStream()).thenReturn(is);
        when(jdomHelper.createJDOMDocument(is)).thenReturn(dom);
        when(preprocessor.process(context, dom, slide, resources, configuration)).thenReturn(dom);
        when(jdomHelper.getRawContents(dom)).thenReturn(preprocessedSlideXml);
        when(parser.parse(preprocessedSlideXml)).thenReturn(
                Arrays.asList(parsedPart1, parsedPart2, parsedPart3));
        when(expressionHandler.isExpression(parsedPart1)).thenReturn(false);
        when(expressionHandler.isExpression(parsedPart2)).thenReturn(true);
        when(expressionHandler.isExpression(parsedPart3)).thenReturn(false);
        when(expressionHandler.translateExpression(parsedPart2)).thenReturn("${field}");
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final Writer writer = invocation.getArgumentAt(3, Writer.class);
                writer.write(filledSlideXml);
                return null;
            }
        }).when(templateEngine).process(eq(SLIDE_REL_ID), eq(translatedSlideXml), eq(context), any(Writer.class));

        final Document filledDom = TestUtils.createJDOMDocument(filledSlideXml);

        when(jdomHelper.createJDOMDocument(filledSlideXml)).thenReturn(filledDom);
        when(postprocessor.process(context, filledDom, slide, resources, configuration)).thenReturn(filledDom);
        final OutputStream os = Mockito.mock(OutputStream.class);
        when(resource.getOutputStream()).thenReturn(os);
        doNothing().when(jdomHelper).write(filledDom, os);
        doNothing().when(pptxWriter).write(resources, presentation);

        processor.process(context, resources);

        verify(pptxReader, times(1)).read(resources);
        verify(jdomHelper, times(1)).createJDOMDocument(is);
        verify(jdomHelper, times(1)).getRawContents(dom);
        verify(preprocessor, times(1)).process(context, dom, slide, resources, configuration);
        verify(configuration, times(1)).getParserFactory();
        verify(parserFactory, times(1)).createParser();
        verify(templateEngine, times(1)).process(eq(SLIDE_REL_ID), eq(translatedSlideXml), eq(context),
                any(Writer.class));
        verify(jdomHelper, times(1)).createJDOMDocument(filledSlideXml);
        verify(postprocessor, times(1)).process(context, filledDom, slide, resources, configuration);
        verify(jdomHelper, times(1)).write(filledDom, os);
        verify(pptxWriter, times(1)).write(resources, presentation);

    }

    private Presentation getPresentation() {
        final Presentation presentation = new Presentation("/ppt/pres.xml");
        final Slide slide = new Slide();
        final Relationship slideRel = new Relationship();
        slideRel.setTarget(SLIDE_REL_TARGET);
        slideRel.setId(SLIDE_REL_ID);
        slide.setRelationship(slideRel);
        slide.setPresentation(presentation);
        presentation.addSlide(slide);
        return presentation;
    }
}
