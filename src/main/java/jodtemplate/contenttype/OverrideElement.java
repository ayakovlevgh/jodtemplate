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
package jodtemplate.contenttype;

public class OverrideElement {
    private String contentType;
    private String partName;

    public OverrideElement() {
    }

    public OverrideElement(final String contentType, final String partName) {
        this.contentType = contentType;
        this.partName = partName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getPartName() {
        return partName;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public void setPartName(final String partName) {
        this.partName = partName;
    }
}
