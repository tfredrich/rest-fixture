package smartrics.jmeter.sampler.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class MemUsageJmxClient {

    public static class MemoryData {
        private MemoryMXBean mBean;

        private MemoryData(MemoryMXBean mBean) {
            this.mBean = mBean;
        }

        public long getUsedHeap() {
            return mBean.getHeapMemoryUsage().getUsed();
        }

        public long getUsedNonHeap() {
            return mBean.getNonHeapMemoryUsage().getUsed();
        }
    }

    private JMXServiceURL url;
    private MemoryMXBean memoryMbean;

    public void setUrl(String u) {
        try {
            url = new JMXServiceURL(u);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to parse url: " + u);
        }
    }

    private void createMemoryMxBean() {
        try {
            JMXConnector connector = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            memoryMbean = ManagementFactory.newPlatformMXBeanProxy(mbsc, "java.lang:type=Memory", MemoryMXBean.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get to the MemoryMXBean", e);
        }
    }

    public MemoryData getData() {
        if (memoryMbean == null)
            createMemoryMxBean();
        MemoryData data = new MemoryData(memoryMbean);
        return data;
    }


    public static void main(String[] args) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9004/jmxrmi");
        JMXConnector connector = JMXConnectorFactory.connect(url);
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();
        MemoryMXBean memoryMbean = ManagementFactory.newPlatformMXBeanProxy(mbsc, "java.lang:type=Memory", MemoryMXBean.class);
        for (int i = 0; i < 100; i++) {
            MemoryUsage mu = memoryMbean.getHeapMemoryUsage();
            System.out.println(mu.getUsed());
            Thread.sleep(1000);
        }
    }
}
