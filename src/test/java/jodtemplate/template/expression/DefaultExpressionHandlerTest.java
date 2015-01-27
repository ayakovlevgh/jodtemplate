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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.template.TemplateEngine;
import jodtemplate.template.expression.DefaultExpressionHandler;
import jodtemplate.template.expression.VariableExpression;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultExpressionHandlerTest {

    private DefaultExpressionHandler handler;

    @Mock
    private TemplateEngine templateEngine;

    @Before
    public void setUp() {
        handler = new DefaultExpressionHandler(templateEngine, "{{", "}}");
    }

    @Test
    public void testTranslateVariable() throws Exception {
        final String variable = "model.field";
        final List<String> params = Arrays.asList("param", "param(opts)", "param(\"opts\")");
        final String defaultValue = "default";
        final String expression = "{{model.field?param?param(opts)?param(“opts”)!“default”}}";
        final String translatedExpression =
                "[#if (model.field)??]${model.field?param1?param2(opts)?param(\"opts\")}[#else]default[/#if]";
        final VariableExpression variableExpression = new VariableExpression(variable, params, defaultValue);
        when(templateEngine.createVariable(Matchers.eq(variableExpression))).thenReturn(translatedExpression);

        final String result = handler.translateExpression(expression);

        assertEquals(translatedExpression, result);
    }

    @Test(expected = JODTemplateException.class)
    public void testTranslateVariableIncorrectSyntax() throws Exception {
        handler.translateExpression("{{incorrect syntax here;}}");
    }

    @Test
    public void testIsExpressionTrue() {
        final boolean result = handler.isExpression("{{model.field}}");
        assertEquals(true, result);
    }

    @Test
    public void testIsExpressionFalse() {
        final boolean result = handler.isExpression("not expression");
        assertEquals(false, result);
    }

}
