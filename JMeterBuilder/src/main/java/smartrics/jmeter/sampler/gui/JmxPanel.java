package smartrics.jmeter.sampler.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.HorizontalPanel;

@SuppressWarnings("serial")
public class JmxPanel extends HorizontalPanel {
    public static String HEAP_MEM = "heap";
    public static String NON_HEAP_MEM = "non heap";

    public static final String JMX_URL = "Jmx.url";
    public static final String JMX_USED_MEM_TYPE = "Jmx.used_memory_type";

    List<ChangeListener> listeners = new LinkedList<ChangeListener>();

    private JTextField jmxUrl;
    private JComboBox usedMemoryType;

    public JmxPanel() {
        setBorder(BorderFactory.createTitledBorder("JMX"));
        JLabel label = new JLabel("Url");
        add(label);
        jmxUrl = new JTextField("service:jmx:rmi:///jndi/rmi://<host>:<port>/jmxrmi", 50);
        add(jmxUrl);
        label = new JLabel("Memory");
        add(label);
        usedMemoryType = new JComboBox(new String[] { HEAP_MEM, NON_HEAP_MEM });
        add(usedMemoryType);
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
