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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.resource.factory.ResourcesFactory;

public class ZipReader {

    private static final int READ_BUFFER_SIZE = 2048;

    public Resources unzipContents(final InputStream inputStream, final ResourcesFactory factory)
            throws IOException {
        try (final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream))) {
            final Resources resources = factory.createResources();
            try {
                final byte[] buffer = new byte[READ_BUFFER_SIZE];
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    final Resource resource = resources.createResource(entry.getName());
                    if (!entry.isDirectory()) {
                        try (final OutputStream out = resource.getOutputStream()) {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                }

                return resources;
            } catch (IOException | RuntimeException e) {
                resources.clean();
                throw e;
            }
        }
    }

}
