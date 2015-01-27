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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VariableExpression {

    private final String variable;

    private final List<String> params;

    private final String defaultValue;

    public VariableExpression(final String variable, final List<String> params, final String defaultValue) {
        this.variable = variable;
        this.params = new ArrayList<>(params);
        this.defaultValue = defaultValue;
    }

    public String getVariable() {
        return variable;
    }

    public List<String> getParams() {
        return Collections.unmodifiableList(new ArrayList<>(params));
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(variable)
                .append(params)
                .append(defaultValue)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final VariableExpression rhs = (VariableExpression) obj;
        return new EqualsBuilder()
                .append(variable, rhs.variable)
                .append(params, rhs.params)
                .append(defaultValue, rhs.defaultValue)
                .isEquals();
    }

}
