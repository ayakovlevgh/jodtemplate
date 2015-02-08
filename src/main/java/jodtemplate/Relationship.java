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
package jodtemplate;

public class Relationship {

    public static final String SLIDE_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide";
    public static final String IMAGE_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image";
    public static final String HYPERLINK_TYPE =
            "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink";

    public static final String ID_PREFIX = "rId";

    public static final String EXTERNAL_TARGET_MODE = "External";

    private String id;
    private String target;
    private String targetMode;
    private String type;

    public Relationship() {

    }

    public Relationship(final String id, final String target, final String type) {
        this.id = id;
        this.target = target;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(final String targetMode) {
        this.targetMode = targetMode;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}
