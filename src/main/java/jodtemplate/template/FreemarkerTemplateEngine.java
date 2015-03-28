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
package jodtemplate.template;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.template.expression.ListExpression;
import jodtemplate.template.expression.VariableExpression;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerTemplateEngine implements TemplateEngine {

    private static final String BEGIN_LIST_PATTERN = "[#if (%s)?has_content][#list %s as %s]";
    private static final String END_LIST_PATTERN = "[/#list][/#if]";
    private static final String VARIABLE_PATTERN = "[#if (%s)??]${%s%s}[#else]%s[/#if]";

    private Configuration configuration;

    public FreemarkerTemplateEngine(final Locale locale) {
        configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setObjectWrapper(new JODTemplateObjectWrapper(Configuration.VERSION_2_3_21));
        configuration.setDefaultEncoding(CharEncoding.UTF_8);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        configuration.setLocale(locale);
    }

    @Override
    public String createBeginListDirective(final ListExpression beginListExpression) {
        return String.format(BEGIN_LIST_PATTERN, beginListExpression.getWhat(), beginListExpression.getWhat(),
                beginListExpression.getAs());
    }

    @Override
    public String createEndListDirective() {
        return END_LIST_PATTERN;
    }

    @Override
    public String createVariable(final VariableExpression expression) {
        final StringBuilder paramsString = new StringBuilder();
        for (String param : expression.getParams()) {
            if (StringUtils.isNotBlank(param)) {
                paramsString.append("?").append(param);
            }
        }
        return String.format(VARIABLE_PATTERN, expression.getVariable(), expression.getVariable(), paramsString,
                expression.getDefaultValue());
    }

    @Override
    public void process(final String templateName, final String templateString, final Map<String, Object> context,
            final Writer out) throws JODTemplateException {
        try {
            final Template template = new Template(templateName, new StringReader(templateString), configuration);
            template.process(context, out);
        } catch (IOException | TemplateException e) {
            throw new JODTemplateException("Error while processing template " + templateName, e);
        }

    }

    @Override
    public Locale getLocale() {
        return configuration.getLocale();
    }

    @Override
    public void setLocale(final Locale locale) {
        configuration.setLocale(locale);
    }

}
