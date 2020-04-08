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
package au.gov.asd.tac.constellation.plugins.importexport;

import au.gov.asd.tac.constellation.functionality.welcome.WelcomePageProvider;
import au.gov.asd.tac.constellation.plugins.PluginInfo;
import au.gov.asd.tac.constellation.plugins.importexport.delimited.DelimitedFileImporterStage;
import javafx.application.Platform;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;


/**
 * The Open Delimited File plugin for the Welcome Page.
 *
 * @author canis_majoris
 */
@ServiceProvider(service = WelcomePageProvider.class)
@PluginInfo(tags = {"WELCOME"})
@NbBundle.Messages("DelimitedFileWelcomePlugin=Delimited File Welcome Plugin")
public class DelimitedFileWelcomePlugin extends WelcomePageProvider {

    /**
     * Get a unique reference that is used to identify the plugin 
     *
     * @return a unique reference
     */
    @Override
    public String getName() {
        return DelimitedFileWelcomePlugin.class.getName();
    }
    
    /**
     * Get a textual description that appears on the Welcome Page.
     *
     * @return a unique reference
     */
    @Override
    public String getDescription() {
        return "Open the Delimited File Importer";
    }
    
    /**
     * Returns a link to a resource that can be used instead of text.
     *
     * @return a unique reference
     */
    @Override
    public String getImage() {
        return null;
    }
    /**
     * This method describes what action should be taken when the 
     * link is clicked on the Welcome Page
     *
     */
    @Override
    public void run() {
        Platform.runLater(() -> {
            final DelimitedFileImporterStage stage = new DelimitedFileImporterStage();
            stage.show();
        });      
    }

    /**
     * Determines whether this analytic appear on the Welcome Page 
     *
     * @return true is this analytic should be visible, false otherwise.
     */
    @Override
    public boolean isVisible() {
        return true;
    }
}
