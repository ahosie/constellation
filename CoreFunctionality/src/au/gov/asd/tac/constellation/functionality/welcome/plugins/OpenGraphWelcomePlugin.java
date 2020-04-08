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
package au.gov.asd.tac.constellation.functionality.welcome.plugins;

import au.gov.asd.tac.constellation.functionality.welcome.WelcomePageProvider;
import au.gov.asd.tac.constellation.graph.file.open.OpenFile;
import au.gov.asd.tac.constellation.graph.file.open.FileChooser;
import au.gov.asd.tac.constellation.plugins.PluginInfo;
import java.io.File;
import javax.swing.JFileChooser;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * The Open Graph plugin for the Welcome Page.
 *
 * @author canis_majoris
 */

@ServiceProvider(service = WelcomePageProvider.class)
@PluginInfo(tags = {"WELCOME"})
@NbBundle.Messages("OpenGraphWelcomePlugin=Open Graph Welcome Plugin")
public class OpenGraphWelcomePlugin extends WelcomePageProvider {
    
    /**
     * stores the last current directory of the file chooser
     */
    private static File currentDirectory = null;
    private static boolean running;

    /**
     * Get a unique reference that is used to identify the plugin 
     *
     * @return a unique reference
     */
    @Override
    public String getName() {
        return OpenGraphWelcomePlugin.class.getName();
    }
    
    /**
     * Get a textual description that appears on the Welcome Page.
     *
     * @return a unique reference
     */
    @Override
    public String getDescription() {
        return "Select a graph to open from File Explorer";
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
        if (running) {
            return;
        }
        try {
            running = true;
            JFileChooser chooser = prepareFileChooser();
            File[] files;
            try {
                files = chooseFilesToOpen(chooser);
                currentDirectory = chooser.getCurrentDirectory();
            } catch (UserCancelException ex) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                OpenFile.openFile(files[i], -1);
            }
        } finally {
            running = false;
        }

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
    
    private HelpCtx getHelpCtx() {
        return new HelpCtx(this.getClass().getName());
    }
    
    /**
     * Creates and initializes a file chooser.
     *
     * @return the initialized file chooser
     */
    protected JFileChooser prepareFileChooser() {
        JFileChooser chooser = new FileChooser();
        chooser.setCurrentDirectory(getCurrentDirectory());
        HelpCtx.setHelpIDString(chooser, getHelpCtx().getHelpID());

        return chooser;
    }
    
    /**
     * Displays the specified file chooser and returns a list of selected files.
     *
     * @param chooser file chooser to display
     * @return array of selected files,
     * @exception org.openide.util.UserCancelException if the user cancelled the
     * operation
     */
    public static File[] chooseFilesToOpen(final JFileChooser chooser)
            throws UserCancelException {
        File[] files;
        do {
            int selectedOption = chooser.showOpenDialog(
                    WindowManager.getDefault().getMainWindow());

            if (selectedOption != JFileChooser.APPROVE_OPTION) {
                throw new UserCancelException();
            }
            files = chooser.getSelectedFiles();
        } while (files.length == 0);
        return files;
    }
    
    private static File getCurrentDirectory() {
        if (Boolean.getBoolean("netbeans.openfile.197063")) {
            // Prefer to open from parent of active editor, if any.
            TopComponent activated = TopComponent.getRegistry().getActivated();
            if (activated != null && WindowManager.getDefault().isOpenedEditorTopComponent(activated)) {
                DataObject d = activated.getLookup().lookup(DataObject.class);
                if (d != null) {
                    File f = FileUtil.toFile(d.getPrimaryFile());
                    if (f != null) {
                        return f.getParentFile();
                    }
                }
            }
        }
        // Otherwise, use last-selected directory, if any.
        if (currentDirectory != null && currentDirectory.exists()) {
            return currentDirectory;
        }
        // Fall back to default location ($HOME or similar).
        currentDirectory = new File(System.getProperty("user.home"));  // algol
        return currentDirectory;
    }
}
