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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import jodtemplate.DomProcessor;
import jodtemplate.OOXMLDocumentProcessor;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.parser.Parser;
import jodtemplate.pptx.io.PPTXReader;
import jodtemplate.pptx.io.PPTXReaderImpl;
import jodtemplate.pptx.io.PPTXWriter;
import jodtemplate.pptx.io.PPTXWriterImpl;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.template.expression.ExpressionHandler;
import jodtemplate.util.JDOMHelper;
import jodtemplate.util.Utils;

import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;

public class PPTXDocumentProcessor implements OOXMLDocumentProcessor {

    private Configuration configuration;

    private final PPTXReader pptxReader;

    private final PPTXWriter pptxWriter;

    private final JDOMHelper jdomHelper;

    public PPTXDocumentProcessor(final Configuration configuration) {
        this(configuration, new PPTXReaderImpl(), new PPTXWriterImpl(), new JDOMHelper());
    }

    public PPTXDocumentProcessor(final Configuration configuration, final PPTXReader pptxReader,
            final PPTXWriter pptxWriter, final JDOMHelper jdomHelper) {
        this.configuration = configuration;
        this.pptxReader = pptxReader;
        this.pptxWriter = pptxWriter;
        this.jdomHelper = jdomHelper;
    }

    @Override
    public void process(final Map<String, Object> context, final Resources resources) throws JODTemplateException {
        final ExpressionHandler expressionHandler = configuration.getExpressionHandler();
        try {
            final Presentation presentation = pptxReader.read(resources);
            for (final Slide slide : presentation.getSlides()) {
                final String slideXmlPath = FilenameUtils.normalize(presentation.getFullPath()
                        + slide.getRelationship().getTarget(), true);
                final Resource slideRes = resources.getResource(Utils.removePrefixSeparator(slideXmlPath));
                Document dom;
                try (final InputStream is = slideRes.getInputStream()) {
                    dom = jdomHelper.createJDOMDocument(is);
                }

                for (final DomProcessor preprocessor : configuration.getPreprocessors()) {
                    dom = preprocessor.process(context, dom, slide, resources, configuration);
                }

                final String rawContents = jdomHelper.getRawContents(dom);
                final Parser parser = configuration.getParserFactory().createParser();
                final List<String> parsedParts = parser.parse(rawContents);
                final StringBuilder translatedContents = new StringBuilder();
                for (final String parsedPart : parsedParts) {
                    if (expressionHandler.isExpression(parsedPart)) {
                        translatedContents.append(expressionHandler.translateExpression(parsedPart));
                    } else {
                        translatedContents.append(parsedPart);
                    }
                }
                final Writer writer = new StringWriter();
                expressionHandler.getEngine().process(slide.getRelationship().getId(), translatedContents.toString(),
                        context, writer);
                final String filledContents = writer.toString();
                dom = jdomHelper.createJDOMDocument(filledContents);

                for (final DomProcessor postprocessor : configuration.getPostprocessors()) {
                    dom = postprocessor.process(context, dom, slide, resources, configuration);
                }

                try (final OutputStream os = slideRes.getOutputStream()) {
                    jdomHelper.write(dom, os);
                }

            }
            pptxWriter.write(resources, presentation);

        } catch (IOException | XMLStreamException | JDOMException e) {
            throw new JODTemplateException("Template processing error", e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

}
