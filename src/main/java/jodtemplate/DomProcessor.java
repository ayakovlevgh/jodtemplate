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

import java.util.Map;

import jodtemplate.exception.JODTemplateException;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.Slide;
import jodtemplate.resource.Resources;

import org.jdom2.Document;

public interface DomProcessor {

    Document process(Map<String, Object> context, Document document, Slide slide, Resources resources,
            Configuration configuration) throws JODTemplateException;

}
