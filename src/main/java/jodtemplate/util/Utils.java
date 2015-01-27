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
package jodtemplate.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static String getRelsPath(final String path) {
        final String fullPath = FilenameUtils.getFullPath(path);
        final String fileName = FilenameUtils.getName(path);
        return fullPath + "_rels/" + fileName + ".rels";
    }

    public static String getRelsPathNoPrefixSeparator(final String path) {
        final String relsPath = getRelsPath(path);
        return removePrefixSeparator(relsPath);
    }

    public static String removePrefixSeparator(final String path) {
        final String prefix = FilenameUtils.getPrefix(path);
        if ("/".equals(prefix) || "\\".equals(prefix)) {
            return StringUtils.substring(path, 1);
        }
        return path;
    }

    public static void createParentFolders(final File file) throws IOException {
        if (!file.getParentFile().exists()) {
            final boolean dirsCreated = file.getParentFile().mkdirs();
            if (!dirsCreated) {
                throw new IOException();
            }
        }
    }

    public static void createRequiredFolders(final File targetFolder) throws IOException {
        createParentFolders(targetFolder);
        if (!targetFolder.exists()) {
            final boolean dirCreated = targetFolder.mkdir();
            if (!dirCreated) {
                throw new IOException();
            }
        }
    }

}
