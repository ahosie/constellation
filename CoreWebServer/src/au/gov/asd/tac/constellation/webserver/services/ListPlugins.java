/*
 * Copyright 2010-2020 Australian Signals Directorate
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
 */
package au.gov.asd.tac.constellation.webserver.services;

import au.gov.asd.tac.constellation.pluginframework.PluginRegistry;
import au.gov.asd.tac.constellation.webserver.RestService;
import static au.gov.asd.tac.constellation.webserver.api.RestUtilities.getBooleanArg;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author algol
 */
 @ServiceProvider(service=RestService.class)
public class ListPlugins implements RestService {

    @Override
    public String getName() {
        return "list_plugins";
    }

    @Override
    public void service(final Map<String, String[]> args, final InputStream in, final OutputStream out) throws IOException {
        final boolean alias = getBooleanArg(args, "alias", false);

        final ObjectMapper mapper = new ObjectMapper();
        final ArrayNode root = mapper.createArrayNode();
        PluginRegistry.getPluginClassNames()
            .stream()
            .map((name) -> alias ? PluginRegistry.getAlias(name) : name)
            .forEachOrdered((name) -> {root.add(name);});

        mapper.writeValue(out, root);
    }
}
