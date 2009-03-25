package smartrics.jmeter.sampler;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jCharts.Chart;
import org.jCharts.encoders.PNGEncoder;

public class SaveGraphUtil {

    private static Log log = LogFactory.getLog(SaveGraphUtil.class);

    public synchronized static void saveGraph(String fileName, String fileIdx, Chart chart) {
        String extension = ".png";
        FileOutputStream fileOutputStream = null;
        if (fileName != null && !("".equals(fileName.trim()))) {
            fileName = fileName.trim();
            if (fileName.toLowerCase().endsWith(extension)) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            try {

                fileOutputStream = new FileOutputStream(fileName + fileIdx + extension);
                PNGEncoder.encode(chart, fileOutputStream);
                fileOutputStream.flush();
            } catch (Throwable throwable) {
                log.warn("Unable to save graph in " + fileName, throwable);
                throw new IllegalArgumentException("Unable to save graph in " + fileName, throwable);
            } finally {
                if (fileOutputStream != null)
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        log.warn("Unable to close " + fileName, e);
                    }
            }
        }
    }

    public synchronized static void saveGraph(String fileName, Chart chart) {
        saveGraph(fileName, "", chart);
    }

}
