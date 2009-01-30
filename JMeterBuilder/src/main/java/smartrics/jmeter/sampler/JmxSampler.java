package smartrics.jmeter.sampler;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import smartrics.jmeter.sampler.jmx.MemUsageJmxClient;
import smartrics.jmeter.sampler.jmx.MemUsageJmxClient.MemoryData;

public class JmxSampler extends AbstractSampler {
    public static String HEAP_MEM = "heap";
    public static String NON_HEAP_MEM = "non heap";
    private static final long serialVersionUID = -5877623539165274730L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String JMX_URI = "JmxSampler.jmx_uri";

    public static final String JMX_MEM_TYPE = "JmxSampler.jmx_mem_type";

    public JmxSampler() {
    }

    public void setJmxUri(String data) {
        setProperty(JMX_URI, data);
    }

    public String getJmxUri() {
        return getPropertyAsString(JMX_URI);
    }

    public void setJmxMemType(String data) {
        setProperty(JMX_MEM_TYPE, data);
    }

    public String getJmxMemType() {
        return getPropertyAsString(JMX_MEM_TYPE);
    }

    public String toString() {
        return "Jmx uri: " + getJmxUri() + ", mem: " + getJmxMemType();
    }

    public JmxSampleResult sample(Entry e) {
        return sample();
    }

    public JmxSampleResult sample() {
        JmxSampleResult newRes = new JmxSampleResult();
        long startdate = System.currentTimeMillis();
        MemUsageJmxClient c = new MemUsageJmxClient();
        c.setUrl(getJmxUri());
        MemoryData d = c.getData();
        newRes.setSampleLabel("jmx");
        long mem = byte2Kbyte(d.getUsedNonHeap());
        if (HEAP_MEM.equals(getJmxMemType())) {
            mem = byte2Kbyte(d.getUsedHeap());
        }
        newRes.setMemType(getJmxMemType());
        newRes.setValue(mem);
        newRes.setSamplerData(Long.toString(mem));
        long enddate = System.currentTimeMillis();
        newRes.setStampAndTime(enddate, enddate - startdate);
        newRes.setSuccessful(true);
        return newRes;
    }

    private long byte2Kbyte(long n) {
        return n / 1024;
    }
}
