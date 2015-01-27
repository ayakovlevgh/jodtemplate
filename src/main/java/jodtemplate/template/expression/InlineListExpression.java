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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class InlineListExpression {

    private final String what;

    private final String as;

    private final String variable;

    public InlineListExpression(final String what, final String as, final String variable) {
        this.what = what;
        this.as = as;
        this.variable = variable;
    }

    public String getWhat() {
        return what;
    }

    public String getAs() {
        return as;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(what)
                .append(as)
                .append(variable)
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
        final InlineListExpression rhs = (InlineListExpression) obj;
        return new EqualsBuilder()
                .append(what, rhs.what)
                .append(as, rhs.as)
                .append(variable, rhs.variable)
                .isEquals();
    }
}
