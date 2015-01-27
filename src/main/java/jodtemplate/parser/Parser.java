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

import java.util.ArrayList;
import java.util.List;

import jodtemplate.parser.state.NoTagState;
import jodtemplate.parser.state.ParsingState;
import jodtemplate.parser.state.TagBeginState;
import jodtemplate.parser.state.TagBodyState;
import jodtemplate.parser.state.TagEndState;

public class Parser {

    private static final int BEGIN_END_TAG_LENGTH = 2;

    private ParsingState noTagState;
    private ParsingState tagBeginState;
    private ParsingState tagBodyState;
    private ParsingState tagEndState;

    private ParsingState currentState;

    private StringBuilder buffer;

    private final String beginTag;

    private final String endTag;

    public Parser(final String beginTag, final String endTag) {
        this.beginTag = beginTag;
        this.endTag = endTag;
        this.noTagState = new NoTagState(this);
        this.tagBeginState = new TagBeginState(this);
        this.tagBodyState = new TagBodyState(this);
        this.tagEndState = new TagEndState(this);
        this.currentState = noTagState;
        this.buffer = new StringBuilder();
    }

    public String getBeginTag() {
        return beginTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public ParsingState getCurrentState() {
        return currentState;
    }

    public ParsingState getNoTagState() {
        return noTagState;
    }

    public ParsingState getTagBeginState() {
        return tagBeginState;
    }

    public ParsingState getTagBodyState() {
        return tagBodyState;
    }

    public ParsingState getTagEndState() {
        return tagEndState;
    }

    public List<String> parse(final String text) {
        if (beginTag.length() != BEGIN_END_TAG_LENGTH || endTag.length() != BEGIN_END_TAG_LENGTH) {
            throw new IllegalStateException("Begin tag and end tag must consist of two characters.");
        }

        final List<String> parsedOut = new ArrayList<>();

        for (int charIndex = 0; charIndex < text.length(); ++charIndex) {
            processCharacter(text.charAt(charIndex), buffer, parsedOut);
        }

        if (currentState == noTagState && buffer.length() != 0) {
            parsedOut.add(buffer.toString());
            buffer.setLength(0);
        }

        return parsedOut;
    }

    private void processCharacter(final char character, final StringBuilder buffer, final List<String> parsedOut) {
        currentState.processCharacter(character, buffer, parsedOut);
    }

    public void setCurrentState(final ParsingState currentState) {
        this.currentState = currentState;
    }

}
