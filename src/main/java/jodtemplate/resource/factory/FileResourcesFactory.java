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
package jodtemplate.resource.factory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import jodtemplate.resource.FileResources;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.io.FileUtils;

public class FileResourcesFactory implements ResourcesFactory {

    private final String tempPath;

    public FileResourcesFactory() {
        this(System.getProperty("java.io.tmpdir"));
    }

    public FileResourcesFactory(final String tempPath) {
        this.tempPath = tempPath;
    }

    @Override
    public Resources createResources() throws IOException {
        final File targetFolder = new File(tempPath, UUID.randomUUID().toString());
        try {
            Utils.createRequiredFolders(targetFolder);
            return new FileResources(targetFolder);
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFolder);
            throw e;
        }
    }

}
