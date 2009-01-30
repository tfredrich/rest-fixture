package smartrics.jmeter.sampler.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.JPanel;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jCharts.axisChart.AxisChart;
import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelPosition;
import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelRenderer;
import org.jCharts.chartData.AxisChartDataSet;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.DataSeries;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.DataAxisProperties;
import org.jCharts.properties.LabelAxisProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.LineChartProperties;
import org.jCharts.properties.PropertyException;
import org.jCharts.types.ChartType;

public class JmxGraph extends JPanel {

    private static final long serialVersionUID = 8263438417891736098L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String ELLIPSIS = "..."; //$NON-NLS-1$
    private static final int ELLIPSIS_LEN = ELLIPSIS.length();

    protected double[][] data = null;
    protected String title, xAxisTitle, yAxisTitle, yAxisLabel;
    protected int maxLength;
    protected String[] xAxisLabels;
    protected int width, height;

    /**
     *
     */
    public JmxGraph() {
        super();
    }

    /**
     * @param layout
     */
    public JmxGraph(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public JmxGraph(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setXAxisTitle(String title) {
        this.xAxisTitle = title;
    }

    public void setYAxisTitle(String title) {
        this.yAxisTitle = title;
    }

    public void setXAxisLabels(String[] labels) {
        this.xAxisLabels = labels;
    }

    public void setYAxisLabels(String label) {
        this.yAxisLabel = label;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void paintComponent(Graphics g) {
        if (data != null && this.title != null && this.xAxisLabels != null && this.xAxisTitle != null && this.yAxisLabel != null && this.yAxisTitle != null) {
            drawSample(this.title, this.maxLength, this.xAxisLabels, this.xAxisTitle, this.yAxisTitle, this.data, this.width, this.height, g);
        }
    }

    private String squeeze(String input, int _maxLength) {
        if (input.length() > _maxLength) {
            String output = input.substring(0, _maxLength - ELLIPSIS_LEN) + ELLIPSIS;
            return output;
        }
        return input;
    }

    private void drawSample(String _title, int _maxLength, String[] _xAxisLabels, String _xAxisTitle, String _yAxisTitle, double[][] _data, int _width, int _height, Graphics g) {
        try {
            /**
             * These controls are already done in StatGraphVisualizer if (_width
             * == 0) { _width = 450; } if (_height == 0) { _height = 250; }
             **/
            if (_maxLength < 5) {
                _maxLength = 5;
            }
            // if the "Title of Graph" is empty, we can assume some default
            if (_title.length() == 0) {
                _title = "Graph";
            }
            // if the labels are too long, they'll be "squeezed" to make the
            // chart viewable.
            for (int i = 0; i < _xAxisLabels.length; i++) {
                String label = _xAxisLabels[i];
                _xAxisLabels[i] = squeeze(label, _maxLength);
            }
            this.setPreferredSize(new Dimension(_width, _height));
            DataSeries dataSeries = new DataSeries(_xAxisLabels, _xAxisTitle, _yAxisTitle, _title);

            String[] legendLabels = { yAxisLabel };
            Paint[] paints = new Paint[] { Color.yellow };
            Stroke[] strokes = { LineChartProperties.DEFAULT_LINE_STROKE };
            Shape[] shapes = { new java.awt.geom.Ellipse2D.Double(0.0D, 0.0D, 2D, 2D) };
            LineChartProperties lineChartProperties = new LineChartProperties(strokes, shapes);
            ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer(false, false, true, 0);
            valueLabelRenderer.setValueLabelPosition(ValueLabelPosition.AT_TOP);
            valueLabelRenderer.useVerticalLabels(true);
            lineChartProperties.addPostRenderEventListener(valueLabelRenderer);
            AxisChartDataSet axisChartDataSet = new AxisChartDataSet(_data, legendLabels, paints, ChartType.LINE, lineChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);

            ChartProperties chartProperties = new ChartProperties();
            LabelAxisProperties xaxis = new LabelAxisProperties();
            DataAxisProperties yaxis = new DataAxisProperties();
            yaxis.setShowGridLines(1);
            xaxis.setShowGridLines(1);
            AxisProperties axisProperties = new AxisProperties(xaxis, yaxis);
            axisProperties.setXAxisLabelsAreVertical(true);
            LegendProperties legendProperties = new LegendProperties();
            AxisChart axisChart = new AxisChart(dataSeries, chartProperties, axisProperties, legendProperties, _width, _height);
            axisChart.setGraphics2D((Graphics2D) g);
            axisChart.render();
        } catch (ChartDataException e) {
            log.warn("", e);
        } catch (PropertyException e) {
            log.warn("", e);
        }
    }
}
