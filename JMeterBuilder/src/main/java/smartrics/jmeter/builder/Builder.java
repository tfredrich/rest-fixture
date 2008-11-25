package smartrics.jmeter.builder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler2;
import org.apache.jmeter.protocol.http.sampler.SoapSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;

public class Builder {
    private TestPlan testPlan;
    private ThreadGroup threadGroup;
    private ConfigTestElement configTestElement;

    public Builder(String testPlanName, String threadGroupName, int numThreads, int rampUp, int loops) {
        testPlan = new TestPlan(testPlanName);
        setThreadGroup(threadGroupName, numThreads, rampUp, loops);
    }

    public void setDefaults(String protocol, String host, String port, String basePath, Map<String, String> defHeaders) {
        if (configTestElement != null)
            return;
        configTestElement = new ConfigTestElement();
        threadGroup.addTestElement(configTestElement);
        configTestElement.setName("HTTP Request Defaults");
        configTestElement.setProperty(HTTPSampler.PROTOCOL, null2empty(protocol));
        configTestElement.setProperty(HTTPSampler.DOMAIN, null2empty(host));
        configTestElement.setProperty(HTTPSampler.PORT, null2empty(port));
        configTestElement.setProperty(HTTPSampler.PATH, null2empty(basePath));
        configTestElement.setProperty(HTTPSampler.CONTENT_ENCODING, "UTF-8");
        if (defHeaders != null) {
            HeaderManager headerManager = new HeaderManager();
            threadGroup.addTestElement(headerManager);
            for (Entry<String, String> e : defHeaders.entrySet()) {
                headerManager.add(new Header(e.getKey(), e.getValue()));
            }
        }
    }

    public void addRestRequest(String resourceUri, String body) {
        SoapSampler soapSampler = new SoapSampler();
        threadGroup.addTestElement(soapSampler);
        if (configTestElement != null)
            soapSampler.addTestElement(configTestElement);
        soapSampler.setProperty(HTTPSampler2.URL, resourceUri);
        soapSampler.setProperty(SoapSampler.XML_DATA, body);
        soapSampler.setProperty(SoapSampler.USE_KEEPALIVE, true);
        soapSampler.setProperty(SoapSampler.SEND_SOAP_ACTION, false);
        soapSampler.setProperty(SoapSampler.SOAP_ACTION, "");
    }

    public String build() {
        Writer stringWriter = new StringWriter();
        try {
            SaveService.saveElement(testPlan, System.out);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save test plan");
        }
    }

    private void setThreadGroup(String name, int numThreads, int rampUp, int loops) {
        threadGroup = new ThreadGroup();
        testPlan.addThreadGroup(threadGroup);
        LoopController loopController = new LoopController();
        loopController.setLoops(loops);
        threadGroup.setSamplerController(loopController);
        threadGroup.setName(name);
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        // TODO: check duration
        // threadGroup.setDuration(duration);
    }

    private static String null2empty(String s) {
        if (s == null)
            return "";
        return s;
    }

}
