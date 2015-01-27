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

import jodtemplate.exception.JODTemplateException;
import jodtemplate.template.TemplateEngine;

public interface ExpressionHandler {

    boolean isExpression(String text);

    String translateExpression(String expression) throws JODTemplateException;

    String getBeginTag();

    void setBeginTag(final String beginTag);

    void setEngine(TemplateEngine engine);

    TemplateEngine getEngine();

    void setEndTag(String endTag);

    String getEndTag();

    VariableExpression createVariableExpression(String expression) throws JODTemplateException;
    
    ListExpression createBeginListExpression(String expression) throws JODTemplateException;

    InlineListExpression createInlineListExpression(String expression) throws JODTemplateException;

    boolean isVariable(String expression);

    boolean isBeginList(String expression);

    boolean isEndList(String expression);

    boolean isInlineList(String expression);

    String createEndList();

    String createBeginList(String what, String as);

    String createVariable(String variable);

}
