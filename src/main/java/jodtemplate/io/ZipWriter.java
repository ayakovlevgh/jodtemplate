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
package jodtemplate.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;

import org.apache.commons.io.IOUtils;

public class ZipWriter {

    public void zipResources(final Resources resources, final OutputStream output) throws IOException {
        try (final ZipOutputStream zos = new ZipOutputStream(output);
                final BufferedOutputStream bos = new BufferedOutputStream(zos)) {
            for (Resource resource : resources.getResources()) {
                zos.putNextEntry(new ZipEntry(resource.getName()));
                try (final InputStream is = resource.getInputStream()) {
                    zos.write(IOUtils.toByteArray(is));
                }
                zos.closeEntry();
            }
        }
    }

}
