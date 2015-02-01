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
package jodtemplate.pptx.style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.style.Stylizer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HtmlStylizer implements Stylizer {

    private static final String BODY_TAG = "body";
    private static final String P_TAG = "p";
    private static final String U_TAG = "u";
    private static final String I_TAG = "i";
    private static final String B_TAG = "b";
    private static final String BR_TAG = "br";
    private static final String STRONG_TAG = "strong";
    private static final String EM_TAG = "em";
    private static final String INS_TAG = "ins";
    private static final String OL_TAG = "ol";
    private static final String UL_TAG = "ul";
    private static final String LI_TAG = "li";

    private static final String ONE = "1";
    private static final String SNG = "sng";

    private static final int DEFAULT_INDENTATION = 457200;

    @Override
    public List<Element> stylize(final String text, final Element arPr, final Element apPr)
            throws JODTemplateException {
        final Document htmlDoc = Jsoup.parse(text);
        try {
            return process(htmlDoc.body(), arPr, apPr);
        } catch (IOException e) {
            throw new JODTemplateException("Stylizer error", e);
        }
    }

    private List<Element> process(final org.jsoup.nodes.Element element, final Element arPr, final Element apPr)
            throws IOException {

        if (BR_TAG.equals(element.tagName())) {
            return Arrays.asList(new Element(PPTXDocument.BR_ELEMENT, getNamespace()));
        }

        final List<String> tags = getAllTags(element);

        final List<Element> elements = new ArrayList<>();
        for (Node node : element.childNodes()) {
            if (node instanceof org.jsoup.nodes.Element) {
                elements.addAll(process((org.jsoup.nodes.Element) node, arPr, apPr));
            } else if (node instanceof TextNode) {
                final TextNode textNode = (TextNode) node;
                elements.add(createTextElement(tags, arPr, textNode));
            }
        }
        if (LI_TAG.equals(element.tagName())) {
            return createListElements(tags, elements, apPr, element);
        }
        if (P_TAG.equals(element.tagName())) {
            return Arrays.asList(createParagraphElement(elements, apPr));
        }
        return elements;
    }

    private Element createTextElement(final List<String> tags, final Element arPr, final TextNode textNode) {
        final Element ar = new Element(PPTXDocument.R_ELEMENT, getNamespace());
        final Element formattedArPr = applyFormatting(tags, arPr);
        if (formattedArPr.hasAttributes()) {
            ar.addContent(formattedArPr);
        }
        final Element at = new Element(PPTXDocument.T_ELEMENT, getNamespace());
        at.setText(textNode.getWholeText());
        ar.addContent(at);
        return ar;
    }

    private List<Element> createListElements(final List<String> tags, final List<Element> elements,
            final Element apPr, final org.jsoup.nodes.Element element) {
        final Element ap = new Element(PPTXDocument.P_ELEMENT, getNamespace());
        final Element apPrToAdd = applyListFormatting(tags, element, apPr);
        ap.addContent(apPrToAdd);
        final List<Element> listResult = new ArrayList<>();
        listResult.add(ap);
        for (Element el : elements) {
            if (PPTXDocument.P_ELEMENT.equals(el.getName())) {
                listResult.add(el);
            } else {
                ap.addContent(el);
            }
        }
        return listResult;
    }

    private Element applyListFormatting(final List<String> tags, final org.jsoup.nodes.Element element,
            final Element apPr) {
        final Element apPrToAdd;
        if (apPr == null) {
            apPrToAdd = new Element(PPTXDocument.PPR_ELEMENT, getNamespace());
        } else {
            apPrToAdd = apPr.clone();
        }
        apPrToAdd.setAttribute(PPTXDocument.INDENT_ATTR, String.valueOf(-DEFAULT_INDENTATION));
        final Element abuFont = new Element(PPTXDocument.BUFONT_ELEMENT, getNamespace());
        apPrToAdd.addContent(abuFont);
        if (UL_TAG.equals(element.parent().tagName())) {
            abuFont.setAttribute(PPTXDocument.CHARSET_ATTR, "0");
            abuFont.setAttribute(PPTXDocument.PANOSE_ATTR, "020B0604020202020204");
            abuFont.setAttribute(PPTXDocument.PITCH_FAMILY_ATTR, "34");
            abuFont.setAttribute(PPTXDocument.TYPEFACE_ATTR, "Arial");
            final Element abuChar = new Element(PPTXDocument.BUCHAR_ELEMENT, getNamespace());
            abuChar.setAttribute(PPTXDocument.CHAR_ATTR, "•");
            apPrToAdd.addContent(abuChar);
        } else if (OL_TAG.equals(element.parent().tagName())) {
            abuFont.setAttribute(PPTXDocument.TYPEFACE_ATTR, "+mj-lt");
            final Element abuAutonum = new Element(PPTXDocument.BUAUTONUM_ELEMENT, getNamespace());
            abuAutonum.setAttribute(PPTXDocument.TYPE_ATTR, "arabicPeriod");
            apPrToAdd.addContent(abuAutonum);
        }
        final Collection<String> listItemTags = CollectionUtils.select(tags, new Predicate<String>() {
            @Override
            public boolean evaluate(final String tag) {
                return LI_TAG.equals(tag);
            }
        });
        final int listLevel = listItemTags.size();
        if (listLevel > 1) {
            apPrToAdd.setAttribute(PPTXDocument.LVL_ATTR, String.valueOf(listLevel - 1));
        }
        apPrToAdd.setAttribute(PPTXDocument.MAR_L_ATTR, String.valueOf(DEFAULT_INDENTATION * listLevel));
        return apPrToAdd;
    }

    private Element createParagraphElement(final List<Element> elements, final Element apPr) {
        final Element ap = new Element(PPTXDocument.P_ELEMENT, getNamespace());
        final Element apPrToAdd;
        if (apPr == null) {
            apPrToAdd = new Element(PPTXDocument.PPR_ELEMENT, getNamespace());
        } else {
            apPrToAdd = apPr.clone();
        }
        final Element abuNone = new Element(PPTXDocument.BUNONE_ELEMENT, getNamespace());
        apPrToAdd.addContent(abuNone);
        ap.addContent(apPrToAdd);
        ap.addContent(elements);
        return ap;
    }

    private Element applyFormatting(final List<String> tags, final Element arPr) {
        final Element formatted;
        if (arPr == null) {
            formatted = new Element(PPTXDocument.RPR_ELEMENT, getNamespace());
        } else {
            formatted = arPr.clone();
        }
        for (String tag : tags) {
            if (U_TAG.equals(tag) || INS_TAG.equals(tag)) {
                formatted.setAttribute(U_TAG, SNG);
            } else if (I_TAG.equals(tag) || EM_TAG.equals(tag)) {
                formatted.setAttribute(I_TAG, ONE);
            } else if (B_TAG.equals(tag) || STRONG_TAG.equals(tag)) {
                formatted.setAttribute(B_TAG, ONE);
            }
        }
        return formatted;
    }

    private List<String> getAllTags(final org.jsoup.nodes.Element element) {
        final List<String> tags = new ArrayList<>();
        org.jsoup.nodes.Element tempElement = element;
        while (!tempElement.tagName().equals(BODY_TAG)) {
            tags.add(tempElement.tagName());
            tempElement = tempElement.parent();
        }
        return tags;
    }

    private Namespace getNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE_PREFIX, PPTXDocument.DRAWINGML_NAMESPACE);
    }

}
