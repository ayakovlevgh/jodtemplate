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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jodtemplate.DomProcessor;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resources;
import jodtemplate.style.Stylizer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;

public class StylePostprocessor implements DomProcessor {

    private static final String STYLIZED_KEYWORD = " stylized ";

    @Override
    public Document process(final Map<String, Object> context, final Document document, final Slide slide,
            final Resources resources, final Configuration configuration) throws JODTemplateException {
        final IteratorIterable<Element> atElements = document.getDescendants(Filters.element(PPTXDocument.T_ELEMENT,
                getNamespace()));
        final List<Element> atElementsList = new ArrayList<>();
        while (atElements.hasNext()) {
            atElementsList.add(atElements.next());
        }
        for (Element at : atElementsList) {
            if (at.getContentSize() != 0) {
                final Content content = at.getContent(0);
                if (content instanceof Comment) {
                    final Comment comment = (Comment) content;
                    processComment(comment, at, configuration);
                }
            }
        }
        return document;
    }

    private void processComment(final Comment comment, final Element at, final Configuration configuration)
            throws JODTemplateException {
        String commentText = comment.getText();
        if (commentText.startsWith(STYLIZED_KEYWORD)) {
            commentText = StringUtils.removeStart(commentText, STYLIZED_KEYWORD);
            final String className = StringUtils.substringBefore(commentText, ":");
            commentText = StringUtils.removeStart(commentText, className + ": ");
            final Stylizer stylizer = configuration.getStylizer(className);
            if (stylizer == null) {
                throw new JODTemplateException("Unable to find stylizer");
            }
            final String text = StringUtils.removeStart(commentText, " stylized: ");
            final Element ar = at.getParentElement();
            final Element ap = ar.getParentElement();
            final int arIndex = ap.indexOf(ar);
            final Element arPr = getArPrElement(ar);
            final Element apPr = getApPrElement(ap);
            final Element sourceApPr = ObjectUtils.clone(apPr);
            cleanApPrElement(apPr);

            final List<Element> stylizedElements = stylizer.stylize(text, arPr, apPr);

            ap.removeContent(ar);
            final List<Element> remains = getRemainingElements(arIndex, ap);
            for (Element el : remains) {
                ap.removeContent(el);
            }

            final int currentApIndex = injectElementsInDocument(stylizedElements, ap, apPr, arIndex);
            injectRemainsInDocument(remains, ap, sourceApPr, currentApIndex);
        }
    }

    private int injectElementsInDocument(final List<Element> stylizedElements, final Element ap, final Element apPr,
            final int arIndex) {
        int index = arIndex;
        final Element txBody = ap.getParentElement();
        int apIndex = txBody.indexOf(ap) + 1;
        boolean createNewAp = false;
        Element currentAp = ap;
        for (Element element : stylizedElements) {
            if (element.getName().equals(PPTXDocument.P_ELEMENT)) {
                currentAp = element;
                txBody.addContent(apIndex, currentAp);
                apIndex++;
                createNewAp = true;
            } else {
                if (createNewAp) {
                    currentAp = new Element(PPTXDocument.P_ELEMENT, getNamespace());
                    if (apPr != null) {
                        currentAp.addContent(ObjectUtils.clone(apPr));
                    }
                    txBody.addContent(apIndex, currentAp);
                    apIndex++;
                    createNewAp = false;
                }
                if (currentAp == ap) {
                    currentAp.addContent(index, element);
                    index++;
                } else {
                    currentAp.addContent(element);
                }
            }
        }
        return apIndex;
    }

    private void injectRemainsInDocument(final List<Element> remains, final Element ap, final Element apPr,
            final int apIndex) {
        if (CollectionUtils.isNotEmpty(remains)) {
            final Element txBody = ap.getParentElement();
            final Element apWithRemains = new Element(PPTXDocument.P_ELEMENT, getNamespace());
            if (apPr != null) {
                apWithRemains.addContent(apPr);
            }
            apWithRemains.addContent(remains);
            txBody.addContent(apIndex, apWithRemains);
        }
    }

    private List<Element> getRemainingElements(final int fromIndex, final Element ap) {
        final List<Element> remains = new ArrayList<>();
        for (int i = fromIndex; i < ap.getContentSize(); ++i) {
            final Content apChild = ap.getContent(i);
            if (apChild instanceof Element) {
                final Element apChildElement = (Element) apChild;
                if (PPTXDocument.R_ELEMENT.equals(apChildElement.getName())
                        || PPTXDocument.BR_ELEMENT.equals(apChildElement.getName())) {
                    remains.add(apChildElement);
                }
            }
        }
        return remains;
    }

    private Element getArPrElement(final Element ar) {
        final List<Element> arPrElements = ar.getContent(Filters.element(PPTXDocument.RPR_ELEMENT, getNamespace()));
        Element arPr = null;
        if (CollectionUtils.isNotEmpty(arPrElements)) {
            arPr = arPrElements.get(0).clone();
            arPr.removeAttribute("b", getNamespace());
            arPr.removeAttribute("i", getNamespace());
            arPr.removeAttribute("u", getNamespace());
        }
        return arPr;
    }

    private Element getApPrElement(final Element ap) {
        final List<Element> apPrElements = ap.getContent(Filters.element(PPTXDocument.PPR_ELEMENT, getNamespace()));
        if (CollectionUtils.isNotEmpty(apPrElements)) {
            return apPrElements.get(0).clone();
        }
        return null;
    }

    private void cleanApPrElement(final Element apPr) {
        if (apPr != null) {
            apPr.removeChild(PPTXDocument.BUNONE_ELEMENT, getNamespace());
            apPr.removeChild(PPTXDocument.BUCHAR_ELEMENT, getNamespace());
            apPr.removeChild(PPTXDocument.BUAUTONUM_ELEMENT, getNamespace());
            apPr.removeChild(PPTXDocument.BUFONT_ELEMENT, getNamespace());
        }
    }

    private Namespace getNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE_PREFIX, PPTXDocument.DRAWINGML_NAMESPACE);
    }

}
