package smartrics.jmeter.sampler;

import org.apache.jmeter.samplers.SampleResult;

public class JmxSampleResult extends SampleResult {

    private static final long serialVersionUID = 527766459330955540L;
    private String memType;
    private long value;

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
