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
package jodtemplate.samples;

import java.util.Date;

public class Item {

    private String value1;

    private String value2;

    private Date date;

    public Item(final String value1, final String value2, final Date date) {
        this.value1 = value1;
        this.value2 = value2;
        if (date != null) {
            this.date = new Date(date.getTime());
        }
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(final String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(final String value2) {
        this.value2 = value2;
    }

    public Date getDate() {
        if (date != null) {
            return new Date(date.getTime());
        }
        return null;
    }

    public void setDate(final Date date) {
        if (date != null) {
            this.date = new Date(date.getTime());
        }
    }

}
