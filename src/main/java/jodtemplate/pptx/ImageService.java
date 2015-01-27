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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import jodtemplate.Image;
import jodtemplate.Relationship;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.image.ImageField;
import jodtemplate.image.ImageMetadataExtractor;
import jodtemplate.resource.Resource;
import jodtemplate.resource.Resources;
import jodtemplate.util.Utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class ImageService {

    public void insertImage(final ImageField imageField, final Slide slide, final Resources resources,
            final Element pic) throws JODTemplateException {
        try {
            final InputStream is = imageField.getInputStream();
            if (is != null) {
                try (final InputStream bis = new BufferedInputStream(is)) {

                    final byte[] imageContents = IOUtils.toByteArray(bis);

                    final Image image = getImage(imageContents, slide.getPresentation(), resources);

                    final Relationship imageRel = getImageRelationship(image, slide);

                    final Attribute embed = pic
                            .getChild(PPTXDocument.BLIPFILL_ELEMENT, getPresentationmlNamespace())
                            .getChild(PPTXDocument.BLIP_ELEMENT, getDrawingmlNamespace())
                            .getAttribute(PPTXDocument.EMBED_ATTR, getRelationshipsNamespace());
                    embed.setValue(imageRel.getId());

                    setPicSize(pic, image);
                }
            } else {
                pic.getParent().removeContent(pic);
            }
        } catch (IOException | DataConversionException e) {
            throw new JODTemplateException(e);
        }
    }

    private Relationship getImageRelationship(final Image image, final Slide slide) {
        final Path imageFullPath = Paths.get(image.getFullPath());
        final Path slideFullPath = Paths.get(FilenameUtils.getFullPath(slide.getPresentation().getFullPath()
                + slide.getRelationship().getTarget()));
        final Path relativeImagePath = slideFullPath.relativize(imageFullPath);
        final String normRelativeImagePath = FilenameUtils.separatorsToUnix(relativeImagePath.toString());

        Relationship imageRel = slide.getRelationshipByTarget(normRelativeImagePath);

        if (imageRel == null) {
            imageRel = new Relationship();
            imageRel.setId(slide.getNextId());
            imageRel.setTarget(normRelativeImagePath);
            imageRel.setType(Relationship.IMAGE_TYPE);
            slide.addOtherRelationship(imageRel);
        }

        return imageRel;
    }

    private Image getImage(final byte[] imageContents, final Presentation presentation, final Resources resources)
            throws IOException {
        final Image image;
        final String md5 = DigestUtils.md5Hex(imageContents);
        if (presentation.containsImage(md5)) {
            image = presentation.getImage(md5);
        } else {
            image = new Image();
            image.setMd5(md5);

            final ImageMetadataExtractor simpleImageInfo = new ImageMetadataExtractor(imageContents);
            image.setWidth(simpleImageInfo.getWidth());
            image.setHeight(simpleImageInfo.getHeight());
            image.setExtension(simpleImageInfo.getMimeType());

            final int imageIndex = presentation.getNumberOfImages() + 1;
            image.setFullPath(presentation.getFullPath() + "media/imageJodT" + imageIndex + "."
                    + image.getExtension());

            presentation.addImage(image);

            final Resource imageResource = resources.createResource(Utils.removePrefixSeparator(image.getFullPath()));

            try (final OutputStream resOutput = imageResource.getOutputStream()) {
                IOUtils.write(imageContents, resOutput);
            }
        }
        return image;
    }

    private ImageSize calculateNewImageSize(final int cx, final int cy, final int w, final int h) {
        final double cRatio = cx / (double) cy;
        final double ratio = w / (double) h;
        int resX;
        int resY;
        if (w == h) {
            resX = Math.min(cx, cy);
            resY = Math.min(cx, cy);
        } else {
            if (cRatio > ratio) {
                resY = cy;
                resX = (int) (cy * ratio);
            } else {
                resX = cx;
                resY = (int) (cx / ratio);
            }
        }
        return new ImageSize(resX, resY);
    }

    private void setPicSize(final Element pic, final Image image) throws DataConversionException {
        final Element ext = pic.getChild(PPTXDocument.SPPR_ELEMENT, getPresentationmlNamespace())
                .getChild(PPTXDocument.XFRM_ELEMENT, getDrawingmlNamespace())
                .getChild(PPTXDocument.EXT_ELEMENT, getDrawingmlNamespace());

        final Attribute cxAttr = ext.getAttribute("cx");
        final Attribute cyAttr = ext.getAttribute("cy");
        final int cx = cxAttr.getIntValue();
        final int cy = cyAttr.getIntValue();

        final ImageSize newSize = calculateNewImageSize(cx, cy, image.getWidth(), image.getHeight());
        cxAttr.setValue(String.valueOf(newSize.width));
        cyAttr.setValue(String.valueOf(newSize.height));
    }

    private Namespace getDrawingmlNamespace() {
        return Namespace.getNamespace(PPTXDocument.DRAWINGML_NAMESPACE);
    }

    private Namespace getPresentationmlNamespace() {
        return Namespace.getNamespace(PPTXDocument.PRESENTATIONML_NAMESPACE);
    }

    private Namespace getRelationshipsNamespace() {
        return Namespace.getNamespace(PPTXDocument.RELATIONSHIPS_NAMESPACE);
    }

    private static final class ImageSize {
        private int width;

        private int height;

        private ImageSize(final int width, final int height) {
            this.width = width;
            this.height = height;
        }
    }
}
