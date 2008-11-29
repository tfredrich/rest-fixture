package smartrics.jmeter.sampler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
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

import smartrics.jmeter.sampler.RestSampler;

public class RestGui extends AbstractSamplerGui {
    private static final long serialVersionUID = -5576774730632101012L;
    private JCheckBox useKeepAlive;
    private JLabeledTextArea body;
    private JLabeledTextArea headers;
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
        this.headers.setText("");
        this.resource.setText("");
        this.useKeepAlive.setSelected(true);
        this.body.setText("");
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
            sampler.setRequestBody(body.getText());
            sampler.setHttpMethod(httpMethods.getText());
            sampler.setUseKeepAlive(useKeepAlive.isSelected());
            sampler.setHostBaseUrl(hostBaseUrl.getText());
            sampler.setResource(resource.getText());
            sampler.setRequestHeaders(headers.getText());
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
        hostBaseUrl = new JLabeledTextField("Base Url");
        resource = new JLabeledTextField("Resource");
        httpMethods = new JLabeledChoice("Method", new String[] { "Get", "Post", "Put", "Delete" });
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(hostBaseUrl);
        panel.add(resource);
        panel.add(httpMethods);
        panel.add(useKeepAlive);
        return panel;
    }

    private JPanel getRequestPanel() {
        body = new JLabeledTextArea("Body"); //$NON-NLS-1$
        headers = new JLabeledTextArea("Headers"); //$NON-NLS-1$
        VerticalPanel panel = new VerticalPanel();
        panel.add(headers, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        VerticalPanel panel = new VerticalPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(getResourceConfigPanel(), BorderLayout.NORTH);
        panel.add(getRequestPanel(), BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    public void configure(TestElement el) {
        super.configure(el);
        RestSampler sampler = (RestSampler) el;
        body.setText(sampler.getRequestBody());
        headers.setText(sampler.getRequestHeaders());
        useKeepAlive.setSelected(sampler.getUseKeepAlive());
        httpMethods.setText(sampler.getHttpMethod());
        resource.setText(sampler.getResource());
        hostBaseUrl.setText(sampler.getHostBaseUrl());
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
}