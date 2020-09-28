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
package au.gov.asd.tac.constellation.views.notes;

import au.gov.asd.tac.constellation.graph.Graph;
import au.gov.asd.tac.constellation.graph.NotesConcept;
import au.gov.asd.tac.constellation.views.JavaFxTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 *
 * @author sol695510
 */
@TopComponent.Description(
        preferredID = "NotesViewTopComponent",
        iconBase = "au/gov/asd/tac/constellation/views/notes/resources/notes-view.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(
        mode = "explorer",
        openAtStartup = false)
@ActionID(
        category = "Window",
        id = "au.gov.asd.tac.constellation.views.notes.NotesViewTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Experimental/Views", position = 500),
    @ActionReference(path = "Shortcuts", name = "CS-N")})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_NotesViewAction",
        preferredID = "NotesViewTopComponent")
@Messages({
    "CTL_NotesViewAction=Notes View",
    "CTL_NotesViewTopComponent=Notes View",
    "HINT_NotesViewTopComponent=Notes View"})

public class NotesViewTopComponent extends JavaFxTopComponent<NotesViewPane> {

    private final NotesViewController notesViewController;
    private final NotesViewPane notesViewPane;

    /**
     * Creates new NotesViewTopComponent
     */
    public NotesViewTopComponent() {
        setName(Bundle.CTL_NotesViewTopComponent());
        setToolTipText(Bundle.HINT_NotesViewTopComponent());
        initComponents();

        notesViewController = new NotesViewController(NotesViewTopComponent.this);
        notesViewPane = new NotesViewPane(notesViewController);

        initContent();

        addAttributeValueChangeHandler(NotesConcept.MetaAttribute.NOTES_VIEW_STATE, graph -> {
            if (!needsUpdate()) {
                return;
            }
            notesViewController.readState();
        });
    }

    // TODO: This view sometimes doesnt update itself when two graphs are active, and only one is closed
    // It could be something to do with preparing the pane, and then loading the graphreport
    // A possible race condition with the javafx run thread.
    // Below are actions that are called when graph is opened - can use these to reload notes when change graph occurs
    @Override
    protected void handleNewGraph(final Graph graph) {
        if (needsUpdate() && graph != null) {
            preparePane();
            notesViewPane.setGraphRecord(graph.getId());
        }
    }

    @Override
    protected void handleGraphOpened(final Graph graph) {
        if (needsUpdate() && graph != null) {
            preparePane();
            notesViewPane.setGraphRecord(graph.getId());
        }
    }

    @Override
    protected void handleGraphClosed(final Graph graph) {
        if (needsUpdate() && graph != null) {
            preparePane();
        }
    }

    @Override
    protected void handleComponentOpened() {
        preparePane();
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        preparePane();
    }

    private void preparePane() {
        // TODO: setup pane with any graphreports
        notesViewPane.clearContents();
        notesViewController.readState();
        notesViewController.addAttributes();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents

    @Override
    protected String createStyle() {
        return "resources/notes-view.css";
    }

    @Override
    protected NotesViewPane createContent() {
        return notesViewPane;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}