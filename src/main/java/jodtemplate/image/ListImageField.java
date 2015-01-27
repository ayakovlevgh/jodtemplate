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
package jodtemplate.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListImageField implements ImageField {

    private List<ImageField> imageFields;

    private int currentFileIndex;

    private boolean loop;

    public ListImageField(final List<ImageField> imageFields) {
        this(imageFields, false);
    }

    public ListImageField(final List<ImageField> imageFields, final boolean loop) {
        this.imageFields = new ArrayList<>(imageFields);
        this.loop = loop;
        this.currentFileIndex = 0;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream stream = null;
        if (imageFields.size() > 0 && currentFileIndex < imageFields.size()) {
            stream = imageFields.get(currentFileIndex).getInputStream();
            currentFileIndex++;
            if (currentFileIndex == imageFields.size() && loop) {
                currentFileIndex = 0;
            }
        }
        return stream;
    }

}
