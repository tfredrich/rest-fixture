/*  Copyright 2009 Fabrizio Cannizzo
 *
 *  This file is part of JMeterRestSampler.
 *
 *  JMeterRestSampler (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  JMeterRestSampler is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JMeterRestSampler.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please see http://smartrics.blogspot.com
 */
package smartrics.jmeter.sampler.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jorphan.gui.JLabeledTextField;

import smartrics.jmeter.sampler.JmxSampler;

/**
 * UI for the JMX data, used by the JmxGui
 */
@SuppressWarnings("serial")
public class JmxPanel extends VerticalPanel {

    List<ChangeListener> listeners = new LinkedList<ChangeListener>();

    private JTextField jmxUrl;
    private JComboBox usedMemoryType;
    private JLabeledTextField fileName;
    private JLabeledTextField samplingFrequency;
    private JCheckBox saveGraph;

    public JmxPanel() {
        setBorder(BorderFactory.createTitledBorder("JMX"));
        HorizontalPanel jmxDataPanel = new HorizontalPanel();
        JLabel label = new JLabel("Url");
        jmxDataPanel.add(label);
        jmxUrl = new JTextField("service:jmx:rmi:///jndi/rmi://<host>:<port>/jmxrmi", 50);
        jmxDataPanel.add(jmxUrl);
        label = new JLabel("Memory");
        jmxDataPanel.add(label);
        usedMemoryType = new JComboBox(new String[] { JmxSampler.HEAP_MEM });
        // usedMemoryType = new JComboBox(new String[] { JmxSampler.HEAP_MEM,
        // JmxSampler.NON_HEAP_MEM});
        jmxDataPanel.add(usedMemoryType);
        samplingFrequency = new JLabeledTextField("Sampling Frequency (sec)", 5);
        jmxDataPanel.add(samplingFrequency);
        HorizontalPanel graphFilePanel = new HorizontalPanel();
        saveGraph = new JCheckBox("Save graph");
        graphFilePanel.add(saveGraph);
        fileName = new JLabeledTextField("File", 40);
        graphFilePanel.add(fileName);
        fileName.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                String t = fileName.getText();
                boolean reset = false;
                if (t != null) {
                    t = t.trim();
                    reset = true;
                }
                if (!t.toLowerCase().endsWith(".png")) {
                    t = t + ".png";
                    reset = true;
                }
                if (reset) {
                    fileName.setText(t);

                }
            }
        });
        add(jmxDataPanel);
        add(graphFilePanel);
    }

    public void setSaveGraph(boolean save) {
        if (saveGraph != null) {
            saveGraph.setSelected(save);
        }
    }

    public boolean isSaveGraph() {
        return saveGraph.isSelected();
    }

    public void setSaveFileTo(String fname) {
        fileName.setText(fname);
    }

    public String getSaveFileTo() {
        return fileName.getText();
    }

    public void setUsedMemoryType(String memType) {
        usedMemoryType.setSelectedItem(memType);
    }

    public String getUsedMemoryType() {
        return usedMemoryType.getSelectedItem().toString();
    }

    public void setUrl(String u) {
        if (u == null)
            u = "";
        jmxUrl.setText(u);
    }

    public String getUrl() {
        return jmxUrl.getText();
    }

    public void setSamplingFrequency(int sf) {
        if (sf > 0) {
            samplingFrequency.setText(Integer.toString(sf));
        }
    }

    public int getSamplingFrequency() {
        return Integer.parseInt(samplingFrequency.getText());
    }
}
