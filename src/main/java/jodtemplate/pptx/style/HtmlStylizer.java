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

import jodtemplate.Relationship;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.pptx.Slide;
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
    private static final String A_TAG = "a";

    private static final String HREF_ATTR = "href";

    private static final String ONE = "1";
    private static final String SNG = "sng";

    private static final int DEFAULT_INDENTATION = 457200;

    @Override
    public List<Element> stylize(final String text, final Element arPr, final Element apPr, final Slide slide)
            throws JODTemplateException {
        final Document htmlDoc = Jsoup.parse(text);
        try {
            return process(htmlDoc.body(), arPr, apPr, slide);
        } catch (IOException e) {
            throw new JODTemplateException("Stylizer error", e);
        }
    }

    private List<Element> process(final org.jsoup.nodes.Element element, final Element arPr, final Element apPr,
            final Slide slide) throws IOException {

        if (BR_TAG.equals(element.tagName())) {
            return Arrays.asList(new Element(PPTXDocument.BR_ELEMENT, getDrawingmlNamespace()));
        }

        final List<org.jsoup.nodes.Element> tags = getAllTags(element);

        final List<Element> elements = new ArrayList<>();
        for (Node node : element.childNodes()) {
            if (node instanceof org.jsoup.nodes.Element) {
                elements.addAll(process((org.jsoup.nodes.Element) node, arPr, apPr, slide));
            } else if (node instanceof TextNode) {
                final TextNode textNode = (TextNode) node;
                elements.add(createTextElement(tags, arPr, textNode, slide));
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

    private Element createTextElement(final List<org.jsoup.nodes.Element> tags, final Element arPr,
            final TextNode textNode, final Slide slide) {
        final Element ar = new Element(PPTXDocument.R_ELEMENT, getDrawingmlNamespace());
        final Element formattedArPr = applyFormatting(tags, arPr, slide);
        if (formattedArPr.hasAttributes() || formattedArPr.getContentSize() != 0) {
            ar.addContent(formattedArPr);
        }
        final Element at = new Element(PPTXDocument.T_ELEMENT, getDrawingmlNamespace());
        at.setText(textNode.getWholeText());
        ar.addContent(at);
        return ar;
    }

    private List<Element> createListElements(final List<org.jsoup.nodes.Element> tags, final List<Element> elements,
            final Element apPr, final org.jsoup.nodes.Element element) {
        final Element ap = new Element(PPTXDocument.P_ELEMENT, getDrawingmlNamespace());
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

    private Element applyListFormatting(final List<org.jsoup.nodes.Element> tags,
            final org.jsoup.nodes.Element element, final Element apPr) {
        final Element apPrToAdd;
        if (apPr == null) {
            apPrToAdd = new Element(PPTXDocument.PPR_ELEMENT, getDrawingmlNamespace());
        } else {
            apPrToAdd = apPr.clone();
        }
        apPrToAdd.setAttribute(PPTXDocument.INDENT_ATTR, String.valueOf(-DEFAULT_INDENTATION));
        final Element abuFont = new Element(PPTXDocument.BUFONT_ELEMENT, getDrawingmlNamespace());
        apPrToAdd.addContent(abuFont);
        if (UL_TAG.equals(element.parent().tagName())) {
            abuFont.setAttribute(PPTXDocument.CHARSET_ATTR, "0");
            abuFont.setAttribute(PPTXDocument.PANOSE_ATTR, "020B0604020202020204");
            abuFont.setAttribute(PPTXDocument.PITCH_FAMILY_ATTR, "34");
            abuFont.setAttribute(PPTXDocument.TYPEFACE_ATTR, "Arial");
            final Element abuChar = new Element(PPTXDocument.BUCHAR_ELEMENT, getDrawingmlNamespace());
            abuChar.setAttribute(PPTXDocument.CHAR_ATTR, "•");
            apPrToAdd.addContent(abuChar);
        } else if (OL_TAG.equals(element.parent().tagName())) {
            abuFont.setAttribute(PPTXDocument.TYPEFACE_ATTR, "+mj-lt");
            final Element abuAutonum = new Element(PPTXDocument.BUAUTONUM_ELEMENT, getDrawingmlNamespace());
            abuAutonum.setAttribute(PPTXDocument.TYPE_ATTR, "arabicPeriod");
            apPrToAdd.addContent(abuAutonum);
        }
        final Collection<org.jsoup.nodes.Element> listItemTags = CollectionUtils.select(tags,
                new Predicate<org.jsoup.nodes.Element>() {
                    @Override
                    public boolean evaluate(final org.jsoup.nodes.Element tag) {
                        return LI_TAG.equals(tag.tagName());
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
        final Element ap = new Element(PPTXDocument.P_ELEMENT, getDrawingmlNamespace());
        final Element apPrToAdd;
        if (apPr == null) {
            apPrToAdd = new Element(PPTXDocument.PPR_ELEMENT, getDrawingmlNamespace());
        } else {
            apPrToAdd = apPr.clone();
        }
        final Element abuNone = new Element(PPTXDocument.BUNONE_ELEMENT, getDrawingmlNamespace());
        apPrToAdd.addContent(abuNone);
        ap.addContent(apPrToAdd);
        ap.addContent(elements);
        return ap;
    }

    private Element applyFormatting(final List<org.jsoup.nodes.Element> tags, final Element arPr, final Slide slide) {
        final Element formatted;
        if (arPr == null) {
            formatted = new Element(PPTXDocument.RPR_ELEMENT, getDrawingmlNamespace());
        } else {
            formatted = arPr.clone();
        }
        for (org.jsoup.nodes.Element tag : tags) {
            final String tagName = tag.tagName();
            if (U_TAG.equals(tagName) || INS_TAG.equals(tagName)) {
                formatted.setAttribute(U_TAG, SNG);
            } else if (I_TAG.equals(tagName) || EM_TAG.equals(tagName)) {
                formatted.setAttribute(I_TAG, ONE);
            } else if (B_TAG.equals(tagName) || STRONG_TAG.equals(tagName)) {
                formatted.setAttribute(B_TAG, ONE);
            } else if (A_TAG.equals(tagName)) {
                createHyperlink(tag, formatted, slide);
            }
        }
        return formatted;
    }

    private void createHyperlink(final org.jsoup.nodes.Element tag, final Element formatted, final Slide slide) {
        Element hlinkClickElement = formatted.getChild(PPTXDocument.HLINK_CLICK_ELEMENT,
                getDrawingmlNamespace());
        if (hlinkClickElement == null) {
            hlinkClickElement = new Element(PPTXDocument.HLINK_CLICK_ELEMENT, getDrawingmlNamespace());
            formatted.addContent(hlinkClickElement);
        }
        final Relationship relationship = new Relationship();
        relationship.setId(slide.getNextId());
        relationship.setTarget(tag.attr(HREF_ATTR));
        relationship.setTargetMode(Relationship.EXTERNAL_TARGET_MODE);
        relationship.setType(Relationship.HYPERLINK_TYPE);
        slide.addOtherRelationship(relationship);
        hlinkClickElement
                .setAttribute(PPTXDocument.ID_ATTR, relationship.getId(), getRelationshipsNamespace());
    }

    private List<org.jsoup.nodes.Element> getAllTags(final org.jsoup.nodes.Element element) {
        final List<org.jsoup.nodes.Element> tags = new ArrayList<>();
        org.jsoup.nodes.Element tempElement = element;
        while (!tempElement.tagName().equals(BODY_TAG)) {
            tags.add(tempElement);
            tempElement = tempElement.parent();
        }
        return tags;
    }

    private Namespace getDrawingmlNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE_PREFIX, PPTXDocument.DRAWINGML_NAMESPACE);
    }

    private Namespace getRelationshipsNamespace() {
        return Namespace.getNamespace(PPTXDocument.RELATIONSHIPS_NAMESPACE_PREFIX,
                PPTXDocument.RELATIONSHIPS_NAMESPACE);
    }

}
