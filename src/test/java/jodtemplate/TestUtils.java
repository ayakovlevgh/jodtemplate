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
package jodtemplate;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import jodtemplate.io.StandaloneOutputProcessor;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.SlimJDOMFactory;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

    public static String convertElementToText(final Element element) throws IOException {
        final Format format = Format.getRawFormat();
        format.setLineSeparator(LineSeparator.UNIX);
        final XMLOutputter outputter = new XMLOutputter(format, new StandaloneOutputProcessor());
        final Writer writer = new StringWriter();
        outputter.output(element, writer);
        return writer.toString();
    }

    public static String convertDocumentToText(final Document document) throws IOException {
        final Format format = Format.getRawFormat();
        format.setLineSeparator(LineSeparator.UNIX);
        final XMLOutputter outputter = new XMLOutputter(format, new StandaloneOutputProcessor());
        final Writer writer = new StringWriter();
        outputter.output(document, writer);
        return writer.toString();
    }

    public static Document createJDOMDocument(final String string) throws JDOMException, IOException {
        final SAXBuilder jdomBuilder = getJDomBuilder();
        final Document jdomDocument = jdomBuilder.build(new StringReader(string));
        return jdomDocument;
    }

    public static Element createArElement(final String text) {
        final Element at = new Element("t", getNamespace());
        at.setText(text);
        final Element ar = new Element("r", getNamespace());
        ar.addContent(at);
        return ar;
    }

    public static Element createApElement(final String text) {
        final Element ap = new Element("p", getNamespace());
        ap.addContent(createArElement(text));
        return ap;
    }

    private static SAXBuilder getJDomBuilder() {
        final SAXBuilder jdomBuilder = new SAXBuilder();
        jdomBuilder.setJDOMFactory(new SlimJDOMFactory());
        jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return jdomBuilder;
    }

    private static Namespace getNamespace() {
        return Namespace.getNamespace("a", "http://schemas.openxmlformats.org/drawingml/2006/main");
    }

}
