package smartrics.jmeter.sampler.gui;

import java.awt.Container;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.GraphVisualizer;

import smartrics.jmeter.sampler.jmx.MemUsageJmxClient;
import smartrics.jmeter.sampler.jmx.MemUsageJmxClient.MemoryData;

@SuppressWarnings("serial")
public class JvmMemoryUsageVisualizer extends GraphVisualizer {

    private JmxPanel jmxPanel;

    public JvmMemoryUsageVisualizer() {
        super();
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
        newRes.setSamplerData(Long.toString(d.getHeapData()));
        newRes.setSuccessful(true);
        newRes.setStampAndTime(0, d.getHeapData());
        super.add(newRes);
    }

    public void configure(TestElement el) {
        super.configure(el);
        jmxPanel.setUrl(el.getPropertyAsString(JmxPanel.JMX_URL));
    }

    public void modifyTestElement(TestElement c) {
        super.modifyTestElement(c);
        c.setProperty(JmxPanel.JMX_URL, jmxPanel.getUrl());
    }

}
