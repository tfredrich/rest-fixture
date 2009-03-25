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
package smartrics.jmeter.sampler;

import org.apache.jmeter.samplers.SampleResult;

/**
 * The result of a sample on a JMX server.
 * 
 * It's a hack as it contains not only the results but also some other data like
 * the path to the file where to save the graph. the reason for that is that the
 * file name (and the saveGraph flag) must be set in the sampler to be
 * persisted. they are passed to the visualiser via this result object.
 */
public class JmxSampleResult extends SampleResult {

    private static final long serialVersionUID = 527766459330955540L;
    private String memType;
    private long value;
    private String jmxUri;

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

    public String getJmxUri() {
        return jmxUri;
    }

    public void setJmxUri(String uri) {
        this.jmxUri = uri;
    }
}
