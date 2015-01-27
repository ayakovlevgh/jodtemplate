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

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jodtemplate.template.FreemarkerTemplateEngine;
import jodtemplate.template.expression.VariableExpression;

import org.junit.Before;
import org.junit.Test;

public class FreemarkerTemplateEngineTest {

    private FreemarkerTemplateEngine engine;

    @Before
    public void setUp() {
        engine = new FreemarkerTemplateEngine(Locale.ENGLISH);
    }

    @Test
    public void testProcess() throws Exception {
        final Map<String, Object> context = new HashMap<>();
        context.put("field", "value");
        final String template = "<t>[#if field??]${field}[/#if]</t>";
        final String filledTemplate = "<t>value</t>";
        final Writer result = new StringWriter();

        engine.process("name", template, context, result);

        assertEquals(filledTemplate, result.toString());
    }

    @Test
    public void testCreateVariable() throws Exception {
        final String variable = "field";
        final List<String> params = Arrays.asList("param", "param(opts)");
        final String defaultValue = "default";

        final VariableExpression variableExpression = new VariableExpression(variable, params, defaultValue);
        final String result = engine.createVariable(variableExpression);

        assertEquals("[#if (field)??]${field?param?param(opts)}[#else]default[/#if]", result);
    }

    @Test
    public void testCreateVariableNoParams() throws Exception {
        final String variable = "field";
        final List<String> params = Collections.emptyList();
        final String defaultValue = "default";

        final VariableExpression variableExpression = new VariableExpression(variable, params, defaultValue);
        final String result = engine.createVariable(variableExpression);


        assertEquals("[#if (field)??]${field}[#else]default[/#if]", result);
    }
}
