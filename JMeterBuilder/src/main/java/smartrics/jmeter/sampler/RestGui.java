package smartrics.jmeter.sampler;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

public class RestGui extends AbstractSamplerGui {
    private static final long serialVersionUID = -5576774730632101012L;
    private JCheckBox useKeepAlive;
    private JLabeledTextArea xmlData;
    private JLabeledTextField hostBaseUrl;
    private JLabeledTextField resource;
    private JLabeledChoice httpMethods;

    public RestGui() {
        init();
    }

    public String getLabelResource() {
        return "rest_sampler_title"; //$NON-NLS-1$
    }

    public String getStaticLabel() {
        return "Rest Sampler";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement() {
        RestSampler sampler = new RestSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    protected void configureTestElement(TestElement e) {
        e.setName(getName());
        e.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
        e.setProperty(TestElement.TEST_CLASS, e.getClass().getName());
    }

    public void clear() {
        this.httpMethods.setText("GET");
        this.hostBaseUrl.setText("");
        this.resource.setText("");
        this.useKeepAlive.setSelected(true);
        this.xmlData.setText("");
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     * 
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement s) {
        this.configureTestElement(s);
        if (s instanceof RestSampler) {
            RestSampler sampler = (RestSampler) s;
            sampler.setXmlData(xmlData.getText());
            sampler.setHttpMethod(httpMethods.getText());
            sampler.setUseKeepAlive(useKeepAlive.isSelected());
            sampler.setHostBaseUrl(hostBaseUrl.getText());
            sampler.setResource(resource.getText());
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();
        clear();
    }

    private JPanel getResourceConfigPanel() {
        useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
        hostBaseUrl = new JLabeledTextField("Base URL");
        resource = new JLabeledTextField("Resource");
        httpMethods = new JLabeledChoice("Method", new String[] { "Get", "Post", "Put", "Delete" });
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(hostBaseUrl);
        panel.add(resource);
        panel.add(httpMethods);
        panel.add(useKeepAlive);
        return panel;
    }

    private JPanel getXmlDataPanel() {
        xmlData = new JLabeledTextArea("Body"); //$NON-NLS-1$
        VerticalPanel panel = new VerticalPanel();
        panel.add(xmlData, BorderLayout.CENTER);
        return panel;
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(getXmlDataPanel(), BorderLayout.CENTER);
        add(getResourceConfigPanel(), BorderLayout.SOUTH);
    }

    public void configure(TestElement el) {
        super.configure(el);
        RestSampler sampler = (RestSampler) el;
        xmlData.setText(sampler.getXmlData());
        useKeepAlive.setSelected(sampler.getUseKeepAlive());
        httpMethods.setText(sampler.getHttpMethod());
        resource.setText(sampler.getResource());
        hostBaseUrl.setText(sampler.getHostBaseUrl());
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
}