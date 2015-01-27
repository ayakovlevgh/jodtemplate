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
package jodtemplate.samples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodtemplate.image.FileImageField;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.resource.factory.MemoryResourcesFactory;
import jodtemplate.style.HtmlString;

public final class Example {

    private Example() {
    }

    public static void main(final String[] args) throws Exception {
        final Model model = new Model();
        model.setTitle("Lorem ipsum");
        model.setDescription(new HtmlString("<p><strong>Morbi lobortis mi vitae maximus.</strong></p>"
                + "<ol>"
                + "<li>Pellentesque cursus<ul>"
                + "<li><em>Etiam luctus</em></li>"
                + "<li><em>Proin et tellus</em></li>"
                + "</ul></li>"
                + "<li>Aliquam tempor<ul>"
                + "<li><em>Vivamus sit</em></li>"
                + "</ul></li>"
                + "<li>Pellentesque sed</li>"
                + "</ol>"));
        model.setImage(new FileImageField(new File("src/samples/resources/image.jpg")));
        final List<Item> items = Arrays.asList(new Item("value1", "value2", new Date()),
                new Item("value3", "value4", new Date()), new Item("value5", "value6", new Date()));
        model.setItems(items);

        final Configuration configuration = new Configuration();
        configuration.setResourcesFactory(new MemoryResourcesFactory());
        final PPTXDocument doc = new PPTXDocument(configuration);
        final Map<String, Object> context = new HashMap<>();
        context.put("model", model);
        final InputStream template = new BufferedInputStream(
                new FileInputStream("src/samples/resources/template.pptx"));
        final OutputStream result = new BufferedOutputStream(
                new FileOutputStream("target/result.pptx"));
        doc.process(context, template, result);
    }

}
