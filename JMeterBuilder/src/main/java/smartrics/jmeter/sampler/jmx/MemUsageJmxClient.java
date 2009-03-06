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

/**
 * A JMX client wrapper.
 * 
 * @see MemoryMXBean
 */
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
