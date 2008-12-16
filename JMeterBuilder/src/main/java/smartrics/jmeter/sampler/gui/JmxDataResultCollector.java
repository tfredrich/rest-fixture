package smartrics.jmeter.sampler.gui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.visualizers.Visualizer;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterError;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

@SuppressWarnings("serial")
public class JmxDataResultCollector extends ResultCollector {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private transient volatile PrintWriter out;

    @Override
    public void loadExistingFile() {
        final Visualizer visualizer = getVisualizer();
        if (visualizer == null) {
            return; // No point reading the file if there's no visualiser
        }
        String filename = getFilename();
        File file = new File(filename);
        if (file.exists()) {
            clearVisualizer();
            try {
                processCsv(filename, visualizer);
            } catch (IOException e) {
                log.warn("Problem reading file containing JMX samples: " + file);
            } catch (JMeterError e) {
                log.warn("Problem reading JTL file: " + file);
            } catch (RuntimeException e) { // e.g. NullPointerException
                log.warn("Problem reading file containing JMX samples: " + file, e);

            }
        }
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        log.warn("event occurred: " + event.getResult().getClass());
        SampleResult result = event.getResult();
        if (isSampleWanted(result.isSuccessful())) {
            sendToVisualizer(result);
            if (out != null) {// no point otherwise
                String line = processLine(event.getResult());
                out.println(line);
            }
        }
    }

    protected void processCsv(String filename, Visualizer visualizer) throws IOException {
        BufferedReader dataReader = null;
        try {
            dataReader = new BufferedReader(new FileReader(filename));
            String line = dataReader.readLine();
            while (line != null) {
                SampleResult sample = processLine(line);
                if (sample != null)
                    sendToVisualizer(sample);
            }
        } finally {
            JOrphanUtils.closeQuietly(dataReader);
        }
    }

    private String processLine(SampleResult res) {
        long start = res.getStartTime();
        long elaps = res.getTime();
        return String.format("%s,%s", start, elaps);
    }

    private SampleResult processLine(String line) {
        String data[] = line.split(",");
        if (data.length != 2)
            return null;
        try {
            long ret0 = Long.parseLong(data[0]);
            long ret1 = Long.parseLong(data[1]);
            SampleResult ret = new SampleResult(ret0, ret1);
            return ret;
        } catch (RuntimeException e) {
            // file corrupted
            return null;
        }
    }

    public synchronized void testStarted(String host) {
        super.testStarted(host);
        initializeFileOutput();
    }

    public synchronized void testEnded(String host) {
        super.testEnded(host);
        finalizeFileOutput();
    }

    public void clearVisualizer() {
        super.clearVisualizer();
        finalizeFileOutput();
    }

    private void initializeFileOutput() {

        String filename = getFilename();
        if (out == null && filename != null) {
            if (out == null) {
                try {
                    boolean trimmed = new File(filename).exists();
                    out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filename, trimmed)), SaveService.getFileEncoding("UTF-8")), true);
                } catch (IOException e) {
                    out = null;
                }
            }
        }
    }

    private synchronized void finalizeFileOutput() {
        if (out != null) {
            out.close();
            out = null;
        }
    }
}
