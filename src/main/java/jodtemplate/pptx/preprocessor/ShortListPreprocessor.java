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
import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resources;
import jodtemplate.template.expression.ExpressionHandler;
import jodtemplate.template.expression.InlineListExpression;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;

public class ShortListPreprocessor implements DomProcessor {

    private final String parentElement;

    public ShortListPreprocessor(final String parentElement) {
        this.parentElement = parentElement;
    }

    @Override
    public Document process(final Map<String, Object> context, final Document document, final Slide slide,
            final Resources resources, final Configuration configuration) throws JODTemplateException {
        final IteratorIterable<Element> parentElements = document.getDescendants(
                Filters.element(parentElement, getNamespace()));
        final List<Element> parentElementsList = new ArrayList<>();
        while (parentElements.hasNext()) {
            parentElementsList.add(parentElements.next());
        }

        for (final Element parent : parentElementsList) {
            final IteratorIterable<Element> atElements = parent.getDescendants(
                    Filters.element(PPTXDocument.T_ELEMENT, getNamespace()));
            final List<Element> atElementsList = new ArrayList<>();
            while (atElements.hasNext()) {
                atElementsList.add(atElements.next());
            }

            final ExpressionHandler expressionHandler = configuration.getExpressionHandler();
            boolean isLoop = false;
            InlineListExpression expression = null;
            for (final Element at : atElementsList) {
                final String text = at.getText();
                if (configuration.getExpressionHandler().isInlineList(text)) {
                    expression = expressionHandler.createInlineListExpression(text);
                    at.setText(expressionHandler.createVariable(expression.getVariable()));
                    isLoop = true;
                }
            }
            if (isLoop) {
                int apIndex = parent.getParent().indexOf(parent);
                final String beginList = expressionHandler.createBeginList(expression.getWhat(), expression.getAs());
                final String endList = expressionHandler.createEndList();
                parent.getParent().addContent(apIndex, new Comment(beginList));
                apIndex++;
                parent.getParent().addContent(apIndex + 1, new Comment(endList));
            }
        }
        return document;
    }

    private Namespace getNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE);
    }

}
