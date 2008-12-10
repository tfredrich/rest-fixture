package smartrics.jmeter.sampler.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.GraphVisualizer;

import smartrics.jmeter.sampler.jmx.MemUsageJmxClient;
import smartrics.jmeter.sampler.jmx.MemUsageJmxClient.MemoryData;

@SuppressWarnings("serial")
public class JvmMemoryUsageVisualizer extends GraphVisualizer {

    private JmxPanel jmxPanel;

    public JvmMemoryUsageVisualizer() {
        super();
        fixComponents(this);
    }

    protected Container makeTitlePanel() {
        Container panel = super.makeTitlePanel();
        if (jmxPanel == null)
            jmxPanel = new JmxPanel();
        panel.add(jmxPanel);
        return panel;
    }

    public String getStaticLabel() {
        return "JMX Memory Usage";
    }

    public void add(SampleResult res) {
        MemUsageJmxClient c = new MemUsageJmxClient();
        c.setUrl(jmxPanel.getUrl());
        MemoryData d = c.getData();
        SampleResult newRes = new SampleResult();
        newRes.setSampleLabel("lbl");
        long mem = byte2Kbyte(d.getUsedNonHeap());
        if (JmxPanel.HEAP_MEM.equals(jmxPanel.getUsedMemoryType())) {
            mem = byte2Kbyte(d.getUsedHeap());
        }
        newRes.setSamplerData(Long.toString(mem));
        newRes.setStampAndTime(0, mem);
        newRes.setSuccessful(true);
        super.add(newRes);
    }

    private long byte2Kbyte(long n) {
        return n / 1024;
    }

    public void configure(TestElement el) {
        super.configure(el);
        jmxPanel.setUrl(el.getPropertyAsString(JmxPanel.JMX_URL));
        jmxPanel.setUsedMemoryType(el.getPropertyAsString(JmxPanel.JMX_USED_MEM_TYPE));
    }

    public void modifyTestElement(TestElement c) {
        super.modifyTestElement(c);
        c.setProperty(JmxPanel.JMX_URL, jmxPanel.getUrl());
        c.setProperty(JmxPanel.JMX_USED_MEM_TYPE, jmxPanel.getUsedMemoryType());
    }

    private void fixComponents(Container root) {
        // as the GraphVisualiser is tight to time representation, and because
        // we need to display megabytes
        // need to find the component JLabel with the 'ms' displayed and replace
        // it with Mb
        String val = JMeterUtils.getResString("graph_results_ms");
        for (Component c : root.getComponents()) {
            if (c instanceof Container) {
                fixComponents((Container) c);
            }
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                if (val.equals(l.getText())) {
                    l.setText("Kb");
                }
            }
        }
    }

}
