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

import java.util.List;

import jodtemplate.image.ImageField;
import jodtemplate.style.StylizedString;

public class Model {

    private String title;

    private StylizedString description;

    private ImageField image;

    private List<Item> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public StylizedString getDescription() {
        return description;
    }

    public void setDescription(final StylizedString description) {
        this.description = description;
    }

    public ImageField getImage() {
        return image;
    }

    public void setImage(final ImageField image) {
        this.image = image;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(final List<Item> items) {
        this.items = items;
    }

}
