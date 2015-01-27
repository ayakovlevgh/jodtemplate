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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jodtemplate.DomProcessor;
import jodtemplate.parser.ParserFactory;
import jodtemplate.pptx.style.HtmlStylizer;
import jodtemplate.resource.factory.FileResourcesFactory;
import jodtemplate.resource.factory.ResourcesFactory;
import jodtemplate.style.HtmlString;
import jodtemplate.style.Stylizer;
import jodtemplate.template.FreemarkerTemplateEngine;
import jodtemplate.template.TemplateEngine;
import jodtemplate.template.expression.DefaultExpressionHandler;
import jodtemplate.template.expression.ExpressionHandler;

public class Configuration {

    private ExpressionHandler expressionHandler;

    private ParserFactory parserFactory;

    private ResourcesFactory resourcesFactory;

    private String beginTag;

    private String endTag;

    private final List<DomProcessor> preprocessors = new ArrayList<>();

    private final List<DomProcessor> postprocessors = new ArrayList<>();

    private final Map<String, Stylizer> stylizers = new HashMap<>();

    public Configuration() {
        beginTag = "{{";
        endTag = "}}";
        expressionHandler = new DefaultExpressionHandler(
                new FreemarkerTemplateEngine(Locale.ENGLISH), beginTag, endTag);
        parserFactory = new ParserFactory(beginTag, endTag);
        resourcesFactory = new FileResourcesFactory();
        stylizers.put(HtmlString.class.getName(), new HtmlStylizer());
        preprocessors.addAll(DefaultPreprocessorsFactory.getPreprocessors());
        postprocessors.addAll(DefaultPostprocessorsFactory.getPostprocessors());
    }

    public String getBeginTag() {
        return beginTag;
    }

    public void setBeginTag(final String beginTag) {
        this.beginTag = beginTag;
        expressionHandler.setBeginTag(beginTag);
        parserFactory.setBeginTag(beginTag);
    }

    public String getEndTag() {
        return endTag;
    }

    public void setEndTag(final String endTag) {
        this.endTag = endTag;
        expressionHandler.setEndTag(endTag);
        parserFactory.setEndTag(endTag);
    }

    public Locale getLocale() {
        return expressionHandler.getEngine().getLocale();
    }

    public void setLocale(final Locale locale) {
        expressionHandler.getEngine().setLocale(locale);
    }

    public ExpressionHandler getExpressionHandler() {
        return expressionHandler;
    }

    public void setExpressionHandler(final ExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }

    public TemplateEngine getTemplateEngine() {
        return expressionHandler.getEngine();
    }

    public void setTemplateEngine(final TemplateEngine templateEngine) {
        expressionHandler.setEngine(templateEngine);
    }

    public void addPreprocessor(final DomProcessor preprocessor) {
        preprocessors.add(preprocessor);
    }

    public void addPreprocessors(final List<DomProcessor> preprocessors) {
        this.preprocessors.addAll(preprocessors);
    }

    public void clearPreprocessors() {
        preprocessors.clear();
    }

    public List<DomProcessor> getPreprocessors() {
        return new ArrayList<>(preprocessors);
    }

    public void addPostprocessor(final DomProcessor postprocessor) {
        postprocessors.add(postprocessor);
    }

    public void addPostprocessors(final List<DomProcessor> postprocessors) {
        this.postprocessors.addAll(postprocessors);
    }

    public void clearPostprocessors() {
        postprocessors.clear();
    }

    public List<DomProcessor> getPostprocessors() {
        return new ArrayList<>(postprocessors);
    }

    public ParserFactory getParserFactory() {
        return parserFactory;
    }

    public void setParserFactory(final ParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public void setStylizerForClass(final Stylizer stylizer, final Class<?> stylizedClass) {
        final String className = stylizedClass.getName();
        if (stylizer == null) {
            stylizers.remove(className);
        } else {
            stylizers.put(className, stylizer);
        }
    }

    public Stylizer getStylizer(final String className) {
        return stylizers.get(className);
    }

    public ResourcesFactory getResourcesFactory() {
        return resourcesFactory;
    }

    public void setResourcesFactory(final ResourcesFactory resourcesFactory) {
        this.resourcesFactory = resourcesFactory;
    }

}
