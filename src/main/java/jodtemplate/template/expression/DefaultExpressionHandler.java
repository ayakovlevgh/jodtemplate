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
package jodtemplate.template.expression;

import java.util.Arrays;
import java.util.List;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.template.TemplateEngine;

import org.apache.commons.lang3.StringUtils;

public class DefaultExpressionHandler implements ExpressionHandler {

    private static final String POINT = ".";

    private static final Character INLINE_LIST_ITEM_BEGIN = '(';
    private static final Character INLINE_LIST_ITEM_END = ')';

    private static final String BEGIN_LIST_TAG_PATTERN = "{{#list %s as %s}}";
    private static final String END_LIST_TAG_PATTERN = "{{#end list}}";

    private static final String REGEXP_START = "^";
    private static final String REGEXP_END = "$";

    private static final String VAR_REGEXP_PART = "([a-zA-Z_]+[a-zA-Z_0-9]*(\\[[0-9]+\\])?)";
    private static final String VAR_REGEXP_SEPARATOR = "(\\.)";
    private static final String VAR_REGEXP_PARAMS = "(\\?[a-zA-Z_]+(\\((([a-zA-Z_]+)|(\"([^\"]|\\\")+\"))\\))*)*";
    private static final String VAR_REGEXP_DEFAULT_VALUE = "(\\!\"([^\"]|\\\")*\")?";
    private static final String VAR_REGEXP_VARIABLE = VAR_REGEXP_PART
            + "(" + VAR_REGEXP_SEPARATOR + VAR_REGEXP_PART + ")*";
    private static final String VAR_REGEXP_WITH_PARAMS = REGEXP_START + VAR_REGEXP_VARIABLE
            + VAR_REGEXP_PARAMS + VAR_REGEXP_DEFAULT_VALUE + REGEXP_END;
    private static final String DEFAULT_VALUE_SEPARATOR = "!";
    private static final String PARAMS_VALUE_SEPARATOR = "?";

    private static final String BEGIN_LIST_REGEXP = REGEXP_START + "#list\\s+?"
            + VAR_REGEXP_VARIABLE + "\\s+?as\\s+?" + VAR_REGEXP_PART + REGEXP_END;

    private static final String END_LIST_REGEXP = REGEXP_START + "#end\\s+?list" + REGEXP_END;

    // e.g. (model1.model2).field1.field2
    private static final String INLINE_LIST_REGEXP = REGEXP_START + "\\(" + VAR_REGEXP_VARIABLE + "\\)(\\."
            + VAR_REGEXP_VARIABLE + ")*?" + REGEXP_END;

    private String beginTag;

    private String endTag;

    private TemplateEngine engine;

    public DefaultExpressionHandler(final TemplateEngine engine, final String beginTag, final String endTag) {
        this.engine = engine;
        this.beginTag = beginTag;
        this.endTag = endTag;
    }

    @Override
    public boolean isExpression(final String text) {
        return text.startsWith(beginTag) && text.endsWith(endTag);
    }

    @Override
    public String translateExpression(final String expression) throws JODTemplateException {
        final String expressionBody = getExpressionBody(expression);
        String trandlatedExpression;
        if (isVariableBody(expressionBody)) {
            final VariableExpression variableExpression = createVariableExpressionNoCheck(expressionBody);
            trandlatedExpression = engine.createVariable(variableExpression);
        } else if (isBeginListBody(expressionBody)) {
            final ListExpression beginListExpression = createBeginListExpressionNoCheck(expressionBody);
            trandlatedExpression = engine.createBeginListDirective(beginListExpression);
        } else if (isEndListBody(expressionBody)) {
            trandlatedExpression = engine.createEndListDirective();
        } else {
            throw new JODTemplateException("Expression syntax error: " + expression);
        }
        return trandlatedExpression;
    }

    @Override
    public VariableExpression createVariableExpression(final String expression) throws JODTemplateException {
        final String expressionBody = getExpressionBody(expression);
        if (isVariableBody(expressionBody)) {
            return createVariableExpressionNoCheck(expressionBody);
        } else {
            throw new JODTemplateException("Expression is not a variable: " + expression);
        }
    }

    @Override
    public ListExpression createBeginListExpression(final String expression) throws JODTemplateException {
        final String expressionBody = getExpressionBody(expression);
        if (isBeginListBody(expressionBody)) {
            return createBeginListExpressionNoCheck(expressionBody);
        } else {
            throw new JODTemplateException("Expression is not a begin list: " + expression);
        }
    }

    @Override
    public InlineListExpression createInlineListExpression(final String expression) throws JODTemplateException {
        final String expressionBody = getExpressionBody(expression);
        if (isInlineListBody(expressionBody)) {
            return createInlineListExpressionNoCheck(expressionBody);
        } else {
            throw new JODTemplateException("Expression is not an inline list: " + expression);
        }
    }

    @Override
    public String getBeginTag() {
        return beginTag;
    }

    @Override
    public void setBeginTag(final String beginTag) {
        this.beginTag = beginTag;

    }

    @Override
    public String getEndTag() {
        return endTag;
    }

    @Override
    public void setEndTag(final String endTag) {
        this.endTag = endTag;
    }

    @Override
    public TemplateEngine getEngine() {
        return engine;
    }

    @Override
    public void setEngine(final TemplateEngine engine) {
        this.engine = engine;
    }

    @Override
    public boolean isVariable(final String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        final String expressionBody = getExpressionBody(expression);
        return expressionBody.matches(VAR_REGEXP_WITH_PARAMS);
    }

    @Override
    public boolean isBeginList(final String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        final String expressionBody = getExpressionBody(expression);
        return expressionBody.matches(BEGIN_LIST_REGEXP);
    }

    @Override
    public boolean isEndList(final String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        final String expressionBody = getExpressionBody(expression);
        return expressionBody.matches(END_LIST_REGEXP);
    }

    @Override
    public boolean isInlineList(final String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        final String expressionBody = getExpressionBody(expression);
        return expressionBody.matches(INLINE_LIST_REGEXP);
    }

    @Override
    public String createVariable(final String variable) {
        return beginTag + variable + endTag;
    }

    @Override
    public String createBeginList(final String what, final String as) {
        return String.format(BEGIN_LIST_TAG_PATTERN, what, as);
    }

    @Override
    public String createEndList() {
        return END_LIST_TAG_PATTERN;
    }

    private VariableExpression createVariableExpressionNoCheck(final String expressionBody) {
        String defaultValue = StringUtils.substringAfterLast(expressionBody, DEFAULT_VALUE_SEPARATOR).trim();
        defaultValue = StringUtils.remove(defaultValue, '"');
        final String variableWithParams = StringUtils.substringBefore(expressionBody, DEFAULT_VALUE_SEPARATOR);
        final String variable = StringUtils.substringBefore(variableWithParams, PARAMS_VALUE_SEPARATOR);
        final String paramsString = StringUtils.substringAfter(variableWithParams, PARAMS_VALUE_SEPARATOR);
        final List<String> params = Arrays.asList(StringUtils.split(paramsString, PARAMS_VALUE_SEPARATOR));
        final VariableExpression variableExpression = new VariableExpression(variable, params, defaultValue);
        return variableExpression;
    }

    private ListExpression createBeginListExpressionNoCheck(final String expressionBody) {
        final String[] parts = StringUtils.split(expressionBody);
        final int whatIndex = 1;
        final int asIndex = 3;
        return new ListExpression(parts[whatIndex], parts[asIndex]);
    }

    private InlineListExpression createInlineListExpressionNoCheck(final String expressionBody) {
        final String listFullName = getInlineListFullName(expressionBody);
        final String listName = getInlineListName(listFullName);
        final String itemName = listName + "_item";
        final String variable = replaceInlineListItemWithVariable(expressionBody, itemName);
        return new InlineListExpression(listFullName, itemName, variable);
    }

    private static String getInlineListName(final String text) {
        if (text.contains(POINT)) {
            return StringUtils.substringAfterLast(text, POINT);
        }
        return text;
    }

    private static String getInlineListFullName(final String text) {
        return StringUtils.substringBetween(text, INLINE_LIST_ITEM_BEGIN.toString(), INLINE_LIST_ITEM_END.toString());
    }

    private static String replaceInlineListItemWithVariable(final String text, final String replace) {
        return text.replaceFirst("\\" + INLINE_LIST_ITEM_BEGIN + ".*?\\" + INLINE_LIST_ITEM_END, replace);
    }

    private String getExpressionBody(final String expression) {
        String expressionBody = StringUtils.substringBetween(expression, beginTag, endTag);
        expressionBody = StringUtils.trim(expressionBody);
        expressionBody = expressionBody.replaceAll("[“”]", "\"");
        return expressionBody;
    }

    private boolean isVariableBody(final String expressionBody) {
        return expressionBody.matches(VAR_REGEXP_WITH_PARAMS);
    }

    private boolean isBeginListBody(final String expressionBody) {
        return expressionBody.matches(BEGIN_LIST_REGEXP);
    }

    private boolean isEndListBody(final String expressionBody) {
        return expressionBody.matches(END_LIST_REGEXP);
    }

    private boolean isInlineListBody(final String expressionBody) {
        return expressionBody.matches(INLINE_LIST_REGEXP);
    }

}
