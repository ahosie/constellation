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
package au.gov.asd.tac.constellation.testing.construction;

import au.gov.asd.tac.constellation.graph.node.GraphNode;
import au.gov.asd.tac.constellation.plugins.PluginExecution;
import au.gov.asd.tac.constellation.testing.CoreTestingPluginRegistry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Create a random structured graph.
 *
 * @author sirius
 */
@ActionID(category = "Experimental", id = "au.gov.asd.tac.constellation.testing.construction.StructuredGraphBuilderAction")
@ActionRegistration(displayName = "#CTL_StructuredGraphBuilderAction", surviveFocusChange = true)
@ActionReferences({
    @ActionReference(path = "Menu/Experimental/Build Graph", position = 0)
})
@Messages("CTL_StructuredGraphBuilderAction=Structured Graph Builder")
public final class StructuredGraphBuilderAction implements ActionListener {

    private final GraphNode context;

    public StructuredGraphBuilderAction(final GraphNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(final ActionEvent ev) {
        PluginExecution.withPlugin(CoreTestingPluginRegistry.STRUCTURED_GRAPH_BUILDER)
                .withParameter(StructuredGraphBuilderPlugin.BACKBONE_SIZE_PARAMETER_ID, 1000)
                .withParameter(StructuredGraphBuilderPlugin.BACKBONE_DENSITY_PARAMETER_ID, 30)
                .withParameter(StructuredGraphBuilderPlugin.RADIUS, 100)
                .interactively(true)
                .executeLater(context.getGraph());
    }
}
