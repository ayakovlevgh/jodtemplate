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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.Configuration;

public interface OOXMLDocument {

    String CONTENT_TYPES_NAMESPACE = "http://schemas.openxmlformats.org/package/2006/content-types";
    String RELATIONSHIPS_RELS_NAMESPACE = "http://schemas.openxmlformats.org/package/2006/relationships";
    String RELATIONSHIPS_NAMESPACE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";

    String DEFAULT_ELEMENT = "Default";
    String OVERRIDE_ELEMENT = "Override";
    String CONTENT_TYPE_ATTRIBUTE = "ContentType";
    String EXTENSION_ATTRIBUTE = "Extension";
    String PART_NAME_ATTRIBUTE = "PartName";

    String RELATIONSHIPS_ELEMENT = "Relationships";
    String RELATIONSHIP_ELEMENT = "Relationship";
    String ID_ATTRIBUTE = "Id";
    String TYPE_ATTRIBUTE = "Type";
    String TARGET_ATTRIBUTE = "Target";
    String TARGET_MODE_ATTRIBUTE = "TargetMode";

    void process(Map<String, Object> context, InputStream templateInputStream, OutputStream output)
            throws JODTemplateException;

    Configuration getConfiguration();

    void setConfiguration(Configuration configuration);

}
