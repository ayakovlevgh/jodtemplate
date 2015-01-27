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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jodtemplate.DomProcessor;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.image.ImageField;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.ImageService;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resources;
import jodtemplate.template.expression.ExpressionHandler;
import jodtemplate.template.expression.VariableExpression;

import org.apache.commons.beanutils.PropertyUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;

public class PicPreprocessor implements DomProcessor {

    private final ImageService imageService;

    public PicPreprocessor() {
        this(new ImageService());
    }

    public PicPreprocessor(final ImageService imageService) {
        this.imageService = imageService;
    }

    public Document process(final Map<String, Object> context, final Document document, final Slide slide,
            final Resources resources, final Configuration configuration) throws JODTemplateException {
        final ExpressionHandler expressionHandler = configuration.getExpressionHandler();

        final IteratorIterable<Element> picElements = document.getDescendants(Filters.element(PPTXDocument.PIC_ELEMENT,
                getPresentationmlNamespace()));
        final List<Element> picElementsList = new ArrayList<>();
        while (picElements.hasNext()) {
            picElementsList.add(picElements.next());
        }

        for (Element pic : picElementsList) {
            final Attribute descr = pic.getChild(PPTXDocument.NVPICPR_ELEMENT, getPresentationmlNamespace())
                    .getChild(PPTXDocument.CNVPR_ELEMENT, getPresentationmlNamespace())
                    .getAttribute(PPTXDocument.DESCR_ATTR);
            if (descr != null && expressionHandler.isExpression(descr.getValue())) {
                final VariableExpression expression = expressionHandler.createVariableExpression(descr.getValue());
                Object value;
                try {
                    value = PropertyUtils.getNestedProperty(context, expression.getVariable());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new JODTemplateException("Unable to get value: " + expression.getVariable());
                }
                if (value instanceof ImageField) {
                    final ImageField imageField = (ImageField) value;
                    imageService.insertImage(imageField, slide, resources, pic);
                } else {
                    throw new JODTemplateException("Field " + expression.getVariable() + " should contain image.");
                }
            }

        }

        return document;
    }

    private Namespace getPresentationmlNamespace() {
        return Namespace.getNamespace(PPTXDocument.PRESENTATIONML_NAMESPACE);
    }

}
