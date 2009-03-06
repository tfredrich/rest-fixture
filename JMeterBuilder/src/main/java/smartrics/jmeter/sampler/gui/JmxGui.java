/*  Copyright 2009 Fabrizio Cannizzo
 *
 *  This file is part of JMeterRestSampler.
 *
 *  JMeterRestSampler (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  JMeterRestSampler is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JMeterRestSampler.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please see http://smartrics.blogspot.com
 */
package smartrics.jmeter.sampler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import smartrics.jmeter.sampler.JmxSampler;

/**
 * Frontend for the JmxSampler
 */
public class JmxGui extends AbstractSamplerGui {
    private static final long serialVersionUID = -5576774730632101012L;
    private JmxPanel panel;

    public JmxGui() {
        init();
    }

    public String getLabelResource() {
        return "jmx_sampler_title"; //$NON-NLS-1$
    }

    public String getStaticLabel() {
        return "Jmx Sampler";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement() {
        JmxSampler sampler = new JmxSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    public void clear() {
        resetJmxPanel();
    }

    private void resetJmxPanel() {
        panel.setUrl("service:jmx:rmi:///jndi/rmi://<host>:<port>/jmxrmi");
        panel.setUsedMemoryType(JmxSampler.HEAP_MEM);
        panel.setSaveGraph(false);
        panel.setSaveFileTo(null);
        panel.setSamplingFrequency(5);
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement s) {
        this.configureTestElement(s);
        if (s instanceof JmxSampler) {
            JmxSampler sampler = (JmxSampler) s;
            try {
                GuiPackage.getInstance().getReplacer().replaceValues(sampler);
            } catch (InvalidVariableException e) {
                e.printStackTrace();
            }
            sampler.setSampleFrequency(panel.getSamplingFrequency());
            sampler.setJmxUri(panel.getUrl());
            sampler.setJmxMemType(panel.getUsedMemoryType());
            sampler.setSaveGraph(panel.isSaveGraph());
            sampler.setGraphFileName(panel.getSaveFileTo());
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    public void clearGui() {
        super.clearGui();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        VerticalPanel p = new VerticalPanel();
        add(p, BorderLayout.NORTH);
        p.add(makeTitlePanel());
        panel = new JmxPanel();
        resetJmxPanel();
        p.add(panel);
    }

    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof JmxSampler) {
            JmxSampler sampler = (JmxSampler) el;
            panel.setUrl(sampler.getJmxUri());
            panel.setUsedMemoryType(sampler.getJmxMemType());
            int sf = sampler.getSampleFrequency();
            panel.setSamplingFrequency(sf);
            String fname = sampler.getGraphFileName();
            if (fname != null && !"".equals(fname)) {
                panel.setSaveGraph(true);
                panel.setSaveFileTo(fname);
            } else {
                panel.setSaveFileTo(null);
            }
        }
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

}