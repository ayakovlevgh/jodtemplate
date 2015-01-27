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
package jodtemplate.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryResources implements Resources {

    private final Map<String, Resource> resources = new HashMap<>();

    @Override
    public Resource createResource(final String path) throws IOException {
        final Resource resource = new MemoryResource(path);
        resources.put(path, resource);
        return resource;
    }

    @Override
    public List<Resource> getResources() {
        return Collections.unmodifiableList(new ArrayList<>(resources.values()));
    }

    @Override
    public Resource getResource(final String path) {
        return resources.get(path);
    }

    @Override
    public void clean() throws IOException {

    }

}
