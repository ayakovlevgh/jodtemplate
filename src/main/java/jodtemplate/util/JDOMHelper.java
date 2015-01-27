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
package jodtemplate.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import jodtemplate.io.StandaloneOutputProcessor;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.SlimJDOMFactory;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public class JDOMHelper {

    public Document createJDOMDocument(final InputStream stream) throws JDOMException, IOException {
        final SAXBuilder jdomBuilder = getJDomBuilder();
        final Document jdomDocument = jdomBuilder.build(stream);
        return jdomDocument;
    }

    public Document createJDOMDocument(final String string) throws JDOMException, IOException {
        final SAXBuilder jdomBuilder = getJDomBuilder();
        final Document jdomDocument = jdomBuilder.build(new StringReader(string));
        return jdomDocument;
    }

    public String getRawContents(final Document dom) throws IOException {
        final Format format = Format.getRawFormat();
        format.setLineSeparator(LineSeparator.UNIX);
        final XMLOutputter outputter = new XMLOutputter(format, new StandaloneOutputProcessor());
        final Writer writer = new StringWriter();
        outputter.output(dom, writer);
        return writer.toString();
    }

    public void write(final Document dom, final OutputStream stream) throws IOException {
        final Format format = Format.getRawFormat();
        format.setLineSeparator(LineSeparator.UNIX);
        final XMLOutputter outputter = new XMLOutputter(format, new StandaloneOutputProcessor());
        outputter.output(dom, stream);
    }

    private SAXBuilder getJDomBuilder() {
        final SAXBuilder jdomBuilder = new SAXBuilder();
        jdomBuilder.setJDOMFactory(new SlimJDOMFactory());
        jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return jdomBuilder;
    }
}
