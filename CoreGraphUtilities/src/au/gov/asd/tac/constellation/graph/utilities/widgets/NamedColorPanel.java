/*
 * Copyright 2010-2019 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.graph.utilities.widgets;

import au.gov.asd.tac.constellation.utilities.color.ConstellationColor;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;

/**
 *
 * @author algol
 */
final class NamedColorPanel extends AbstractColorChooserPanel {

    private ColorSelectionModel csm;

    public NamedColorPanel() {
        initComponents();
//        colorList.setCellRenderer(new NamedColorCellRenderer());
    }

    private void setColor(Color jc) {
        final ConstellationColor cv = ConstellationColor.fromJavaColor(jc);
        colorList.setSelectedValue(cv, true);
        csm.setSelectedColor(jc);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        colorList = new javax.swing.JList<>();

        colorList.setModel(new NamedColorListModel());
        colorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        colorList.setToolTipText(org.openide.util.NbBundle.getMessage(NamedColorPanel.class, "NamedColorPanel.colorList.toolTipText")); // NOI18N
        colorList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                colorListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(colorList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void colorListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_colorListValueChanged
    {//GEN-HEADEREND:event_colorListValueChanged
        setColor((colorList.getSelectedValue()).getJavaColor());
    }//GEN-LAST:event_colorListValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<au.gov.asd.tac.constellation.utilities.color.ConstellationColor> colorList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void installChooserPanel(JColorChooser enclosingChooser) {
        csm = enclosingChooser.getSelectionModel();
        super.installChooserPanel(enclosingChooser);
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_N;
    }

    @Override
    public int getDisplayedMnemonicIndex() {
        return 0;
    }

    @Override
    public void updateChooser() {
        setColor(getColorFromModel());
    }

    @Override
    protected void buildChooser() {
        // Method Override required
    }

    @Override
    public String getDisplayName() {
        return "Named colors";
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    private static class NamedColorListModel extends AbstractListModel<ConstellationColor> {

        @Override
        public int getSize() {
//            System.out.printf("@~getSize %d\n", ColorValue.NAMED_COLOR_LIST.size());
            return ConstellationColor.NAMED_COLOR_LIST.size();
        }

        @Override
        public ConstellationColor getElementAt(int index) {
//            System.out.printf("@~getElementAt %d %s\n", index, ColorValue.NAMED_COLOR_LIST.get(index));
            return ConstellationColor.NAMED_COLOR_LIST.get(index);
        }
    }

//    private static class NamedColorCellRenderer implements ListCellRenderer
//    {
//        @Override
//        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
//        {
//            return new JPanel()
//            {
//                @Override
//                public void paintComponent(Graphics g)
//                {
////                    super.paintComponent(g);
////                    setPreferredSize(new Dimension(100, 13));
//                    if(isSelected)
//                    {
//                        setBackground(list.getSelectionBackground());
//                        setForeground(list.getSelectionForeground());
//                    }
//                    else
//                    {
//                        setBackground(list.getBackground());
//                        setForeground(list.getForeground());
//                    }
//                    setEnabled(list.isEnabled());
//                    setFont(list.getFont());
//                    setOpaque(true);
//                    final Color gcolor = g.getColor();
//                    System.out.printf("~~renderer value=%d %s\n", index, value);
//                    final ColorValue cv = (ColorValue)value;
//                    final Color color = new Color(cv.red, cv.green, cv.blue, cv.alpha);
//                    int px = 0;
//                    final Rectangle r = getBounds();
//                    System.out.printf("@~graphics=%s\n  %s\n  %s\n  %s\n  %s\n", g, r, gcolor, color, getLocation());
//                    g.drawRect(r.x, r.y+r.height/2-5, 10, 10);
//                    g.setColor(color);
//                    g.fillRect(r.x + 1, r.y+r.height/2-4, 9, 9);
//                    px = 18;
//                    final String text = cv.name!=null ? cv.name : String.format("%f,%f,%f,%f", cv.red, cv.green, cv.blue, cv.alpha);
//                    ((Graphics2D)g).setRenderingHints(IconUtilities.getHints());
//                    final FontMetrics fm = g.getFontMetrics();
//                    g.setColor(gcolor);
//                    g.drawString(text, r.x+px, r.y+(r.height-fm.getHeight())/2 + fm.getAscent());
//                    System.out.printf("@~rend %s\n", text);
//                }
//                @Override
//                public Dimension getPreferredSize()
//                {
//                    final FontMetrics fm = getFontMetrics(getFont());
//                    final Rectangle2D r = fm.getStringBounds("Square + The longest color name", getGraphics());
//                    final Dimension d = new Dimension((int)r.getWidth(), (int)r.getHeight());
//                    System.out.printf("@~preferredSize %s\n", d);
//                    return d;
//                }
//            };
//        }
//    }
}
