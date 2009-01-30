package smartrics.jmeter.sampler.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.GraphVisualizer;

import smartrics.jmeter.sampler.JmxSampleResult;

@SuppressWarnings("serial")
public class JvmMemoryUsageVisualizer extends GraphVisualizer {
    private int count = 0;

    public JvmMemoryUsageVisualizer() {
        super();
        fixComponents(this);
    }

    public String getStaticLabel() {
        return "JMX Memory Usage";
    }

    @Override
    public void add(SampleResult res) {
        SampleResult nres = res;
        if (res instanceof JmxSampleResult) {
            nres = new SampleResult();
            long mem = ((JmxSampleResult) res).getValue();
            nres.setSampleCount(count++);
            nres.setSamplerData(Long.toString(mem));
            nres.setStampAndTime(0, mem);
            nres.setSuccessful(true);
        }
        super.add(nres);
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
