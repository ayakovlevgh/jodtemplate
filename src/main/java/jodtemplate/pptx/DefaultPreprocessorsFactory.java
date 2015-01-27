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
package jodtemplate.pptx;

import java.util.ArrayList;
import java.util.List;

import jodtemplate.DomProcessor;
import jodtemplate.pptx.preprocessor.FormatTagsPreprocessor;
import jodtemplate.pptx.preprocessor.PicPreprocessor;
import jodtemplate.pptx.preprocessor.ShortListPreprocessor;

public final class DefaultPreprocessorsFactory {

    private DefaultPreprocessorsFactory() {
        throw new UnsupportedOperationException();
    }

    public static List<DomProcessor> getPreprocessors() {
        final List<DomProcessor> preprocessors = new ArrayList<>();
        preprocessors.add(new FormatTagsPreprocessor());
        preprocessors.add(new ShortListPreprocessor(PPTXDocument.TR_ELEMENT));
        preprocessors.add(new ShortListPreprocessor(PPTXDocument.P_ELEMENT));
        preprocessors.add(new PicPreprocessor());
        return preprocessors;
    }
}
