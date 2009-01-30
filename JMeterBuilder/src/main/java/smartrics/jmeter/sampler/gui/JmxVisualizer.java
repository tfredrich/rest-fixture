package smartrics.jmeter.sampler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveGraphicsService;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

import smartrics.jmeter.sampler.JmxSampleResult;

@SuppressWarnings("serial")
public class JmxVisualizer extends AbstractVisualizer {

    private JmxGraph graph;
    private String memType;

    private Map<Long, Long> model = new HashMap<Long, Long>();

    public JmxVisualizer() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        graph = new JmxGraph();
        graph.setTitle("Heap memory");
        graph.setXAxisTitle("time");
        graph.setYAxisLabels("Kb");
        add(graph, BorderLayout.CENTER);
    }

    public String getStaticLabel() {
        return "JMX New Memory Usage";
    }

    public void add(SampleResult res) {
        if (res instanceof JmxSampleResult) {
            JmxSampleResult result = (JmxSampleResult) res;
            model.put(result.getStartTime(), result.getValue());
            if (memType == null)
                memType = result.getMemType();
            updateGraph();
        }
    }

    private void updateGraph() {
        try {
            if (model.keySet().size() == 0)
                return;
            renderChart();
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    public void clearData() {
        model.clear();
    }

    public String getLabelResource() {
        return "jmx.visualizer";
    }

    public double[][] createDatasetWithAverage(List<Double> data) {
        double[][] dataset = new double[2][data.size()];
        // Iterator itr = data.iterator();
        double avg = 0;
        for (int idx = 0; idx < data.size(); idx++) {
            dataset[0][idx] = data.get(idx);
            avg += dataset[0][idx];
            avg /= (idx + 1);
            dataset[1][idx] = avg;
        }
        return dataset;
    }

    public double[][] convertToDouble(List<Double> data) {
        double[][] dataset = new double[1][data.size()];
        // Iterator itr = data.iterator();
        for (int idx = 0; idx < data.size(); idx++) {
            dataset[0][idx] = data.get(idx);
        }
        return dataset;
    }

    private List<Long> modelOrderedKeys() {
        List<Long> keys = new ArrayList<Long>();
        keys.addAll(model.keySet());
        Collections.sort(keys);
        return keys;
    }

    private List<Double> modelDataAsList(List<Long> k) {
        List<Double> l = new ArrayList<Double>();
        for (Long h : k) {
            l.add(new Double(model.get(h)));
        }
        return l;
    }

    public JComponent renderChart() {

        List<Long> orderedKeys = modelOrderedKeys();
        List<String> xlabels = formatKeys(orderedKeys);
        double[][] dbset = convertToDouble(modelDataAsList(orderedKeys));
        JComponent g = renderGraphics(dbset, (String[]) xlabels.toArray(new String[xlabels.size()]));

        SaveGraphicsService serv = new SaveGraphicsService();
        String filename = "PPP.png";
        serv.saveJComponent("/home/fabrizio/Desktop/" + filename, SaveGraphicsService.PNG, g);

        g.repaint();

        return g;
    }

    private List<String> formatKeys(List<Long> orderedKeys) {
        List<String> fk = new ArrayList<String>();
        Long k0 = orderedKeys.get(0);
        for (Long k : orderedKeys) {
            fk.add(new Long(k.longValue() - k0.longValue()).toString());
        }
        return fk;
    }

    public JComponent renderGraphics(double[][] data, String[] xAxisLabels) {
        graph.setTitle("JMX Memory Usage (" + memType + ")");
        graph.setData(data);
        graph.setXAxisLabels(xAxisLabels);
        graph.setXAxisTitle("elapsed time");
        graph.setYAxisTitle("Kb");
        graph.setWidth(800);
        graph.setHeight(600);
        graph.setMaximumSize(new Dimension(800, 600));
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        graph.paintComponent(img.createGraphics());
        return graph;
    }
}
