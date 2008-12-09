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
        private long heap;
        private long noHeap;
        public long getHeapData() {
            return heap;
        }

        public long getNoHeapData() {
            return noHeap;
        }

        public MemoryData(long h, long noh) {
            this.heap = h;
            this.noHeap = noh;
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
        MemoryData data = new MemoryData(memoryMbean.getHeapMemoryUsage().getUsed(), memoryMbean.getNonHeapMemoryUsage().getUsed());
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
