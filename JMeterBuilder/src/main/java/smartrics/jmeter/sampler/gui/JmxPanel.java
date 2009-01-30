package smartrics.jmeter.sampler.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jorphan.gui.JLabeledTextField;

import smartrics.jmeter.sampler.JmxSampler;

@SuppressWarnings("serial")
public class JmxPanel extends HorizontalPanel {

    List<ChangeListener> listeners = new LinkedList<ChangeListener>();

    private JTextField jmxUrl;
    private JComboBox usedMemoryType;
    private JCheckBox saveGraph;
    private JLabeledTextField fileName;

    public JmxPanel() {
        setBorder(BorderFactory.createTitledBorder("JMX"));
        JLabel label = new JLabel("Url");
        add(label);
        jmxUrl = new JTextField("service:jmx:rmi:///jndi/rmi://<host>:<port>/jmxrmi", 50);
        add(jmxUrl);
        label = new JLabel("Memory");
        add(label);
        usedMemoryType = new JComboBox(new String[] { JmxSampler.HEAP_MEM, JmxSampler.NON_HEAP_MEM });
        add(usedMemoryType);
        HorizontalPanel graphFilePanel = new HorizontalPanel();
        saveGraph = new JCheckBox("Save graph");
        graphFilePanel.add(saveGraph);
        fileName = new JLabeledTextField("File", 40);
        graphFilePanel.add(fileName);
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
}
