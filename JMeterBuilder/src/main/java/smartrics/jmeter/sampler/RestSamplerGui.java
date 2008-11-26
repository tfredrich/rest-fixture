package smartrics.jmeter.sampler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextArea;

public class RestSamplerGui extends AbstractSamplerGui {
    private JCheckBox useKeepAlive;
	private JLabeledTextArea xmlData;
    private JLabeledChoice httpMethods = null;

	public RestSamplerGui() {
		init();
	}

	public String getLabelResource() {
		return "rest_sampler_title"; //$NON-NLS-1$
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
			sampler.setUseKeepAlive(useKeepAlive.isSelected());
		}
	}

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();
        
        xmlData.setText(""); //$NON-NLS-1$
        useKeepAlive.setSelected(false);
    }    

	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());

		add(makeTitlePanel(), BorderLayout.NORTH);

		xmlData = new JLabeledTextArea(JMeterUtils.getResString("soap_data_title")); //$NON-NLS-1$
		useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$

		JPanel mainPanel = new JPanel(new BorderLayout());
	    JPanel mainDataPanel = new JPanel();
	    mainDataPanel.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridwidth = 2;
	    c.gridy = 2;
	    c.gridx = 0;
		mainDataPanel.add(useKeepAlive, c);
		
		JLabel selectLabel = new JLabel("Http Methods");
		httpMethods = new JLabeledChoice();
		httpMethods.setValues(new String[]{"Get", "Post", "Put", "Delete"});
		mainDataPanel.add(selectLabel);
		mainDataPanel.add(httpMethods);
		
		mainPanel.add(mainDataPanel, BorderLayout.NORTH);
		mainPanel.add(xmlData, BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);
	}

	public void configure(TestElement el) {
		super.configure(el);
		RestSampler sampler = (RestSampler) el;
		xmlData.setText(sampler.getXmlData());
        useKeepAlive.setSelected(sampler.getUseKeepAlive());
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
}