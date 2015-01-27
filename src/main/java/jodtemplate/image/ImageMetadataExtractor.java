/*
 * 
 * A Java class to determine image width, height and MIME types for a number of
 * image file formats without loading the whole image data.
 * 
 * Original name: SimpleImageInfo.java
 * Original version: 0.1
 * 
 * This file was originally created by Jaimon Mathew <http://www.jaimon.co.uk>
 * and licensed under the Apache License, Version 2.0.
 * 
 * It was modified in order to satisfy code style check.
 *
 */

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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

//CHECKSTYLE.OFF: MagicNumber
public class ImageMetadataExtractor {

    private int height;
    private int width;
    private String mimeType;

    public ImageMetadataExtractor(final File file) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            processStream(is);
        }
    }

    public ImageMetadataExtractor(final InputStream is) throws IOException {
        processStream(is);
    }

    public ImageMetadataExtractor(final byte[] bytes) throws IOException {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            processStream(is);
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "MIME Type : " + mimeType + "\t Width : " + width + "\t Height : " + height;
    }

    private void processStream(final InputStream is) throws IOException {
        final int c1 = is.read();
        final int c2 = is.read();
        final int c3 = is.read();

        mimeType = null;
        width = -1;
        height = -1;

        if (isGif(c1, c2, c3)) {
            getGifInfo(is);
        } else if (isJpeg(c1, c2)) {
            getJpegInfo(is, c3);
        } else if (isPng(c1, c2, c3)) {
            getPngInfo(is);
        } else if (c1 == 66 && c2 == 77) {
            getBmpInfo(is);
        } else {
            final int c4 = is.read();
            if (isTiff(c1, c2, c3, c4)) {
                getTiffInfo(is, c1);
            }
        }
        if (mimeType == null) {
            throw new IOException("Unsupported image type");
        }
    }

    private boolean isTiff(final int c1, final int c2, final int c3, final int c4) {
        final boolean tiffCheck1 = c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42;
        final boolean tiffCheck2 = c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0;
        return tiffCheck1 || tiffCheck2;
    }

    private boolean isPng(final int c1, final int c2, final int c3) {
        return c1 == 137 && c2 == 80 && c3 == 78;
    }

    private boolean isJpeg(final int c1, final int c2) {
        return c1 == 0xFF && c2 == 0xD8;
    }

    private boolean isGif(final int c1, final int c2, final int c3) {
        return c1 == 'G' && c2 == 'I' && c3 == 'F';
    }

    private void getTiffInfo(final InputStream is, final int c1) throws IOException {
        final boolean bigEndian = c1 == 'M';
        int ifd = 0;
        int entries;
        ifd = readInt(is, 4, bigEndian);
        IOUtils.skipFully(is, ifd - 8);
        entries = readInt(is, 2, bigEndian);
        for (int i = 1; i <= entries; i++) {
            final int tag = readInt(is, 2, bigEndian);
            final int fieldType = readInt(is, 2, bigEndian);
            int valOffset;
            if (fieldType == 3 || fieldType == 8) {
                valOffset = readInt(is, 2, bigEndian);
                IOUtils.skipFully(is, 2);
            } else {
                valOffset = readInt(is, 4, bigEndian);
            }
            if (tag == 256) {
                width = valOffset;
            } else if (tag == 257) {
                height = valOffset;
            }
            if (width != -1 && height != -1) {
                mimeType = "tiff";
                break;
            }
        }
    }

    private void getBmpInfo(final InputStream is) throws IOException {
        IOUtils.skipFully(is, 15);
        width = readInt(is, 2, false);
        IOUtils.skipFully(is, 2);
        height = readInt(is, 2, false);
        mimeType = "bmp";
    }

    private void getPngInfo(final InputStream is) throws IOException {
        IOUtils.skipFully(is, 15);
        width = readInt(is, 2, true);
        IOUtils.skipFully(is, 2);
        height = readInt(is, 2, true);
        mimeType = "png";
    }

    private void getJpegInfo(final InputStream is, final int c3) throws IOException {
        int c = c3;
        while (c == 255) {
            final int marker = is.read();
            final int len = readInt(is, 2, true);
            if (marker == 192 || marker == 193 || marker == 194) {
                IOUtils.skipFully(is, 1);
                height = readInt(is, 2, true);
                width = readInt(is, 2, true);
                mimeType = "jpeg";
                break;
            }
            IOUtils.skipFully(is, len - 2);
            c = is.read();
        }
    }

    private void getGifInfo(final InputStream is) throws IOException {
        IOUtils.skipFully(is, 3);
        width = readInt(is, 2, false);
        height = readInt(is, 2, false);
        mimeType = "gif";
    }

    private int readInt(final InputStream is, final int noOfBytes, final boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? (noOfBytes - 1) * 8 : 0;
        final int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

}
// CHECKSTYLE.ON: MagicNumber
