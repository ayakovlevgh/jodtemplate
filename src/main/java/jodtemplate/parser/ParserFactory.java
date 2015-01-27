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
package jodtemplate.parser;

public class ParserFactory {

    private String beginTag;

    private String endTag;

    public ParserFactory(final String beginTag, final String endTag) {
        this.beginTag = beginTag;
        this.endTag = endTag;
    }

    public Parser createParser() {
        return new Parser(beginTag, endTag);
    }

    public String getBeginTag() {
        return beginTag;
    }

    public void setBeginTag(final String beginTag) {
        this.beginTag = beginTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public void setEndTag(final String endTag) {
        this.endTag = endTag;
    }

}
