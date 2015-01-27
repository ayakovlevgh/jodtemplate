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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jodtemplate.OOXMLDocumentProcessor;
import jodtemplate.exception.JODTemplateException;
import jodtemplate.io.ZipReader;
import jodtemplate.io.ZipWriter;
import jodtemplate.pptx.Configuration;
import jodtemplate.pptx.PPTXDocument;
import jodtemplate.resource.Resources;
import jodtemplate.resource.factory.ResourcesFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PPTXDocumentTest {

    @InjectMocks
    private PPTXDocument document;

    @Mock
    private Configuration configuration;

    @Mock
    private OOXMLDocumentProcessor documentProcessor;

    @Mock
    private ZipReader zipReader;

    @Mock
    private ZipWriter zipWriter;

    @Test
    public void testSetConfiguration() {
        document.setConfiguration(configuration);

        verify(documentProcessor, times(1)).setConfiguration(configuration);
    }

    @Test
    public void testProcess() throws Exception {
        final Map<String, Object> context = new HashMap<>();
        final InputStream input = Mockito.mock(InputStream.class);
        final OutputStream output = Mockito.mock(OutputStream.class);
        final Resources resources = Mockito.mock(Resources.class);
        final ResourcesFactory factory = Mockito.mock(ResourcesFactory.class);

        doNothing().when(resources).clean();
        when(configuration.getResourcesFactory()).thenReturn(factory);
        when(zipReader.unzipContents(input, factory)).thenReturn(resources);

        document.process(context, input, output);

        verify(zipReader, times(1)).unzipContents(input, factory);
        verify(documentProcessor, times(1)).process(context, resources);
        verify(zipWriter, times(1)).zipResources(resources, output);
        verify(resources, times(1)).clean();
    }

    @Test(expected = JODTemplateException.class)
    public void testProcessDocumentProcessorProcessError() throws Exception {
        final Map<String, Object> context = new HashMap<>();
        final InputStream input = Mockito.mock(InputStream.class);
        final OutputStream output = Mockito.mock(OutputStream.class);
        final Resources resources = Mockito.mock(Resources.class);
        final ResourcesFactory factory = Mockito.mock(ResourcesFactory.class);

        doNothing().when(resources).clean();
        when(configuration.getResourcesFactory()).thenReturn(factory);
        when(zipReader.unzipContents(input, factory)).thenReturn(resources);
        doThrow(JODTemplateException.class).when(documentProcessor).process(context, resources);

        document.process(context, input, output);

        verify(zipReader, times(1)).unzipContents(input, factory);
        verify(documentProcessor, times(1)).process(context, resources);
        verify(zipWriter, times(0)).zipResources(resources, output);
        verify(resources, times(1)).clean();
    }
    
    @Test(expected = JODTemplateException.class)
    public void testProcessThrowExceptionAndDeleteDirectoryIfIOErrorOccurred() throws Exception {
        final Map<String, Object> context = new HashMap<>();
        final InputStream input = Mockito.mock(InputStream.class);
        final OutputStream output = Mockito.mock(OutputStream.class);
        final Resources resources = Mockito.mock(Resources.class);
        final ResourcesFactory factory = Mockito.mock(ResourcesFactory.class);

        doNothing().when(resources).clean();
        when(configuration.getResourcesFactory()).thenReturn(factory);
        when(zipReader.unzipContents(input, factory)).thenReturn(resources);;
        doThrow(IOException.class).when(zipWriter).zipResources(resources, output);

        document.process(context, input, output);

        verify(zipReader, times(1)).unzipContents(input, factory);
        verify(documentProcessor, times(1)).process(context, resources);
        verify(zipWriter, times(1)).zipResources(resources, output);
        verify(resources, times(1)).clean();
    }

}
