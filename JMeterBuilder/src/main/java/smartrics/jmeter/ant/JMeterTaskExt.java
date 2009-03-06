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
package smartrics.jmeter.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.soap.providers.com.Log;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.programmerplanet.ant.taskdefs.jmeter.JMeterTask;

public class JMeterTaskExt extends JMeterTask {


    private String failThreshold;

    public void setFailThreshold(String t) {
        this.failThreshold = t;
    }

    public String getFailThreshold() {
        return this.failThreshold;
    }

    private File chartsOutputDir;

    public File getChartsOutputDir() {
        return chartsOutputDir;
    }

    public void setChartsOutputDir(File chartsOutputDir) {
        this.chartsOutputDir = chartsOutputDir;
    }

    public int getFailThresholdAsInt() {
        try {
            return Integer.parseInt(getFailThreshold());
        } catch (NumberFormatException e) {
            Log.msg(Log.WARNING, "Unable to parse failThreshold. Use default of 100%");
            return 100;
        }
    }

    public void execute() throws BuildException {
        try {
            super.execute();
            checkForFailure();
            generateCharts();
        } catch (RuntimeException e) {
            throw new BuildException("Unexpected exception: " + e.getMessage());
        }
    }

    private void generateCharts() throws BuildException {
        if (chartsOutputDir != null) {
            AbstractList<File> resultFiles = getResultLogFiles();
            for (File result : resultFiles) {
                String name = result.getName();
                int pos = name.indexOf(".jtl");
                name = name.substring(0, pos);
                File jmxChartFile = new File(chartsOutputDir, name + "_jmxChart.png");
                File perfChartFile = new File(chartsOutputDir, name + "_perfChart.png");
                ChartGenerator dataExtractor = new ChartGenerator(result, jmxChartFile, perfChartFile);
                log("Generating charts with data extracted from " + result.getAbsolutePath(), Project.MSG_VERBOSE);
                dataExtractor.generate();
                log("Charts generated in '" + jmxChartFile.getAbsolutePath() + "' and '" + perfChartFile.getAbsolutePath() + "'", Project.MSG_VERBOSE);
            }
        }
    }

    private ArrayList<File> getResultLogFiles() {
        ArrayList<File> resultLogFiles = new ArrayList<File>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jtl");
            }
        };
        String[] files = getResultLogDir().list(filter);
        for (String s : files) {
            resultLogFiles.add(new File(s));
        }
        return resultLogFiles;
    }

    private void checkForFailure() throws BuildException {
        // as check for failure is private, it's unset and then re-set using
        // threshold
        unsetFailure(getFailureProperty());
        if (getFailureProperty() != null && getFailureProperty().trim().length() > 0) {
            AbstractList<File> resultFiles = getResultLogFiles();
            for (Iterator<File> i = resultFiles.iterator(); i.hasNext();) {
                File resultLogFile = (File) i.next();
                log("Checking result log file " + resultLogFile.getName() + ".", Project.MSG_VERBOSE);
                SuccessParser parser = new SuccessParser(resultLogFile);
                parser.handleResults();
                int totals = parser.getTotal();
                int successes = parser.getSuccesses();
                double perc = 100 * successes / totals;
                if (perc > getFailThresholdAsInt()) {
                    setFailure(getFailureProperty());
                }
                if (perc != 100.0) {
                    log("Failures detected above the set threshold: " + Math.round(perc) + "%");
                }
            }
        }
    }

    private void unsetFailure(String failureProperty) {
        getProject().setProperty(failureProperty, null);
    }

}
