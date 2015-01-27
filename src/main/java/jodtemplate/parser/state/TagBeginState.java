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
package jodtemplate.parser.state;

import java.util.List;

import jodtemplate.parser.Parser;

public class TagBeginState implements ParsingState {

    private final Parser parser;

    public TagBeginState(final Parser parser) {
        this.parser = parser;
    }

    @Override
    public void processCharacter(final char character, final StringBuilder buffer,
            final List<String> parsedOut) {
        if (character == parser.getBeginTag().charAt(1)) {
            buffer.deleteCharAt(buffer.length() - 1);
            if (buffer.length() != 0) {
                parsedOut.add(buffer.toString());
                buffer.setLength(0);
            }
            buffer.append(parser.getBeginTag());
            parser.setCurrentState(parser.getTagBodyState());
        } else {
            buffer.append(character);
            parser.setCurrentState(parser.getNoTagState());
        }
    }

}
