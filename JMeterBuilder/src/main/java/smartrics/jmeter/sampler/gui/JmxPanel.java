package smartrics.jmeter.sampler.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.HorizontalPanel;

@SuppressWarnings("serial")
public class JmxPanel extends HorizontalPanel {
    public static final String JMX_URL = "Jmx.url";
    List<ChangeListener> listeners = new LinkedList<ChangeListener>();

    private JTextField jmxUrl;

    public JmxPanel() {
        setBorder(BorderFactory.createTitledBorder("JMX"));
        JLabel label = new JLabel("Url");
        add(label);
        jmxUrl = new JTextField("service:jmx:rmi:///jndi/rmi://<host>:<port>/jmxrmi", 50);
        add(jmxUrl);
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
