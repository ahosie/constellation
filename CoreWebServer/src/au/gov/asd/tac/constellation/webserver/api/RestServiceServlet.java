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
package au.gov.asd.tac.constellation.webserver.api;

import au.gov.asd.tac.constellation.webserver.RestService;
import au.gov.asd.tac.constellation.webserver.WebServer.ConstellationHttpServlet;
import au.gov.asd.tac.constellation.webserver.impl.PluginImpl;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * A web service that allows a client to call CONSTELLATION REST services.
 *
 * @author algol
 */
@ServiceProvider(service = ConstellationHttpServlet.class)
@WebServlet(
        name = "ServicesAPI",
        description = "REST API for services",
        urlPatterns = {"/v1/service/*"})
public class RestServiceServlet extends ConstellationApiServlet {

    @Override
    protected void get(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // Which service is being called?
        //
        final String service = request.getPathInfo().substring(1);

        // Look for that service and call it.
        //
        for(final RestService rs : Lookup.getDefault().lookupAll(RestService.class)) {
            final String name = rs.getName();
            if(service.equals(name)) {
                try {
                    rs.service(request.getParameterMap(), request.getInputStream(), response.getOutputStream());
                } catch(final IOException | RuntimeException ex) {
                    throw new ServletException(ex);
                }
                return;
            }
        }

        throw new ServletException(String.format("Unknown REST service '%s'", service));
    }

    @Override
    protected void post(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        switch (request.getPathInfo()) {
            case "/run":
                // Run a plugin, optionally with parameters.
                final String graphId = request.getParameter("graph_id");
                final String pluginName = request.getParameter("name");
                if (pluginName == null) {
                    throw new ServletException("No plugin specified!");
                } else {
                    try {
                        PluginImpl.post_run(graphId, pluginName, request.getInputStream());
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                        throw new ServletException(ex);
                    }
                }
                break;
            default:
                throw new ServletException(String.format("Unknown API path %s", request.getPathInfo()));
        }
    }
}
