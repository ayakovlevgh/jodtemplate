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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jodtemplate.DomProcessor;
import jodtemplate.parser.Parser;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resources;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;

public class FormatTagsPreprocessor implements DomProcessor {

    @Override
    public Document process(final Map<String, Object> context, final Document document, final Slide slide,
            final Resources resources, final Configuration configuration) {
        final IteratorIterable<Element> apElements = document.getDescendants(Filters.element(PPTXDocument.P_ELEMENT,
                getNamespace()));
        final List<Element> apElementsList = new ArrayList<>();
        while (apElements.hasNext()) {
            apElementsList.add(apElements.next());
        }
        for (Element ap : apElementsList) {
            final List<Element> apChildrenList = ap.getChildren();
            if (apChildrenList.size() != 0) {
                final List<Element> arabrElementsListResult = processArAndABrElements(apChildrenList, configuration
                        .getParserFactory().createParser());
                int firstArElementIndex = ap.indexOf(ap.getChild(PPTXDocument.R_ELEMENT, getNamespace()));
                if (firstArElementIndex < 0) {
                    firstArElementIndex = 0;
                }
                ap.removeChildren(PPTXDocument.R_ELEMENT, getNamespace());
                ap.removeChildren(PPTXDocument.BR_ELEMENT, getNamespace());
                ap.addContent(firstArElementIndex, arabrElementsListResult);
            }
        }
        return document;
    }

    private Namespace getNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE);
    }

    private List<Element> processArAndABrElements(final List<Element> apChildrenList, final Parser parser) {
        final List<Element> arabrElementsListResult = new ArrayList<>(apChildrenList.size());
        for (Element child : apChildrenList) {
            if (PPTXDocument.R_ELEMENT.equals(child.getName())) {
                final String text = child.getChild(PPTXDocument.T_ELEMENT, getNamespace()).getText();
                final List<String> parsed = parser.parse(text);
                for (String part : parsed) {
                    if (StringUtils.isNotEmpty(part)) {
                        final Element arOut = child.clone();
                        arOut.getChild(PPTXDocument.T_ELEMENT, getNamespace()).setText(part);
                        arabrElementsListResult.add(arOut);
                    }
                }
            } else if (PPTXDocument.BR_ELEMENT.equals(child.getName())) {
                final Element abrOut = child.clone();
                arabrElementsListResult.add(abrOut);
            }
        }
        return arabrElementsListResult;
    }
}
