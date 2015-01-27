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
package jodtemplate.pptx.io;

import java.io.IOException;
import java.io.InputStream;

import jodtemplate.Image;
import jodtemplate.pptx.Presentation;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

public class PPTXImageReader {

    public void read(final Resources resources, final Presentation presentation) throws IOException {
        final String imagesPath = Utils.removePrefixSeparator(presentation.getFullPath() + "media/");
        for (Resource resource : resources.getResources()) {
            if (resource.getName().startsWith(imagesPath)) {
                final String fileName = FilenameUtils.getName(resource.getName());
                if (fileName.matches("^imageJodT.+?$")) {
                    try (final InputStream imageStream = resource.getInputStream()) {
                        final String md5 = DigestUtils.md5Hex(imageStream);
                        final Image image = new Image();
                        image.setFullPath("/" + resource.getName());
                        image.setMd5(md5);
                        presentation.addImage(image);
                    }
                }
            }
        }
    }
}
