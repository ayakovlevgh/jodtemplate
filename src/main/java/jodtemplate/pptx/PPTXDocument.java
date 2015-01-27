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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jodtemplate.OOXMLDocument;
import jodtemplate.OOXMLDocumentProcessor;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.io.ZipReader;
import jodtemplate.io.ZipWriter;
import jodtemplate.resource.Resources;

public class PPTXDocument implements OOXMLDocument {

    public static final String DRAWINGML_NAMESPACE = "http://schemas.openxmlformats.org/drawingml/2006/main";
    public static final String DRAWINGML_NAMESPACE_PREFIX = "a";
    public static final String PRESENTATIONML_NAMESPACE = "http://schemas.openxmlformats.org/presentationml/2006/main";

    public static final String R_ELEMENT = "r";
    public static final String BR_ELEMENT = "br";
    public static final String T_ELEMENT = "t";
    public static final String P_ELEMENT = "p";
    public static final String PPR_ELEMENT = "pPr";
    public static final String RPR_ELEMENT = "rPr";
    public static final String BUNONE_ELEMENT = "buNone";
    public static final String BUAUTONUM_ELEMENT = "buAutoNum";
    public static final String BUCHAR_ELEMENT = "buChar";
    public static final String BUFONT_ELEMENT = "buFont";
    public static final String END_PARA_R_PR_ELEMENT = "endParaRPr";
    public static final String PIC_ELEMENT = "pic";
    public static final String NVPICPR_ELEMENT = "nvPicPr";
    public static final String CNVPR_ELEMENT = "cNvPr";
    public static final String SPPR_ELEMENT = "spPr";
    public static final String XFRM_ELEMENT = "xfrm";
    public static final String BLIPFILL_ELEMENT = "blipFill";
    public static final String BLIP_ELEMENT = "blip";
    public static final String EXT_ELEMENT = "ext";
    public static final String TR_ELEMENT = "tr";

    public static final String TYPE_ATTR = "type";
    public static final String CHAR_ATTR = "char";
    public static final String LVL_ATTR = "lvl";
    public static final String CHARSET_ATTR = "charset";
    public static final String PANOSE_ATTR = "panose";
    public static final String PITCH_FAMILY_ATTR = "pitchFamily";
    public static final String TYPEFACE_ATTR = "typeface";
    public static final String INDENT_ATTR = "indent";
    public static final String MAR_L_ATTR = "marL";
    public static final String DESCR_ATTR = "descr";
    public static final String EMBED_ATTR = "embed";

    private final OOXMLDocumentProcessor documentProcessor;

    private Configuration configuration;

    private ZipReader zipReader;

    private ZipWriter zipWriter;

    public PPTXDocument() {
        this(new Configuration());
    }

    public PPTXDocument(final Configuration configuration) {
        this(configuration, new PPTXDocumentProcessor(configuration), new ZipReader(), new ZipWriter());
    }

    public PPTXDocument(final Configuration configuration, final OOXMLDocumentProcessor documentProcessor,
            final ZipReader zipReader, final ZipWriter zipWriter) {
        this.configuration = configuration;
        this.documentProcessor = documentProcessor;
        this.zipReader = zipReader;
        this.zipWriter = zipWriter;
    }

    @Override
    public void process(final Map<String, Object> context, final InputStream templateInputStream,
            final OutputStream output) throws JODTemplateException {
        try {
            final Resources resources = zipReader.unzipContents(templateInputStream,
                    configuration.getResourcesFactory());
            try {
                documentProcessor.process(context, resources);
                zipWriter.zipResources(resources, output);
            } finally {
                resources.clean();
            }
        } catch (IOException e) {
            throw new JODTemplateException("Process template IO error", e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
        this.documentProcessor.setConfiguration(configuration);
    }

}
