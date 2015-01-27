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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

public class ContentTypes {

    public static final String SLIDE_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.slide+xml";
    public static final String PRESENTATION_TYPE =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml";

    private List<DefaultElement> defaultElements = new ArrayList<>();
    private List<OverrideElement> overrideElements = new ArrayList<>();

    public void addDefaultElement(final DefaultElement element) {
        if (element != null) {
            defaultElements.add(element);
        }
    }

    public void addOverrideElement(final OverrideElement element) {
        if (element != null) {
            overrideElements.add(element);
        }
    }

    public List<DefaultElement> getDefaultElements() {
        return Collections.unmodifiableList(new ArrayList<>(defaultElements));
    }

    public List<OverrideElement> getOverrideElements() {
        return Collections.unmodifiableList(new ArrayList<>(overrideElements));
    }

    public List<OverrideElement> getOverridesByType(final String type) {
        final List<OverrideElement> selectedList = (List<OverrideElement>) CollectionUtils.select(overrideElements,
                new Predicate<OverrideElement>() {
                    @Override
                    public boolean evaluate(final OverrideElement source) {
                        return type.equals(source.getContentType());
                    }
                });
        return Collections.unmodifiableList(selectedList);
    }

}
