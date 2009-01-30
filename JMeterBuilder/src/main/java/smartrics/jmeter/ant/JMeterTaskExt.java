package smartrics.jmeter.ant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
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

    public int getFailThresholdAsInt() {
        try {
            return Integer.parseInt(getFailThreshold());
        } catch (NumberFormatException e) {
            Log.msg(Log.WARNING, "Unable to parse failThreshold. Use default of 100%");
            return 100;
        }
    }

    public void execute() throws BuildException {
        super.execute();
        checkForFailures();
    }

    private void checkForFailures() throws BuildException {
        if (getFailureProperty() != null && getFailureProperty().trim().length() > 0) {
            ArrayList<File> resultLogFiles = new ArrayList<File>();
            if (getTestPlan() != null) {
                if (getResultLogDir() != null) {
                    String testPlanFileName = getTestPlan().getName();
                    String resultLogFilePath = getResultLogDir() + File.separator + testPlanFileName.replaceFirst("\\.jmx", "\\.jtl");
                    resultLogFiles.add(new File(resultLogFilePath));
                }
            }
            unsetFailure(getFailureProperty());
            double total = 0;
            double failures = 0;
            for (Iterator<File> i = resultLogFiles.iterator(); i.hasNext();) {
                File resultLogFile = (File) i.next();
                log("Checking result log file " + resultLogFile.getName() + ".", Project.MSG_VERBOSE);
                LineNumberReader reader = null;
                try {
                    reader = new LineNumberReader(new FileReader(resultLogFile));
                    // look for any success="false" (pre 2.1) or s="false" (post
                    // 2.1)
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        line = line.toLowerCase();
                        // set failure property if there are failures
                        if (line.indexOf("success=\"false\"") > 0 || line.indexOf(" s=\"false\"") > 0) {
                            log("Failure detected at line: " + reader.getLineNumber(), Project.MSG_VERBOSE);
                            failures++;
                        }
                        if (line.indexOf("success=\"") > 0 || line.indexOf(" s=\"") > 0) {
                            total++;
                        }
                    }
                    double perc = 100 * (total - failures) / total;
                    if (perc > getFailThresholdAsInt()) {
                        setFailure(getFailureProperty());
                    }
                } catch (IOException e) {
                    throw new BuildException("Could not read jmeter resultLog: " + e.getMessage());
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) { /* ignore */
                    }
                }
            }
        }
    }

    private void unsetFailure(String failureProperty) {
        getProject().setProperty(failureProperty, null);
    }

}
