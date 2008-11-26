package smartrics.jmeter.sampler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.httpclient.methods.RequestEntity;

public class MyRequestEntity implements RequestEntity {

    private String data;

    public MyRequestEntity(String data) {
        this.data = data;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream out) throws IOException {
        PrintWriter printer = new PrintWriter(out);
        printer.print(data);
        printer.flush();
    }

    public long getContentLength() {
        return data.getBytes().length;// so we don't generate
        // chunked encoding
    }

    public String getContentType() {
        return "text/xml";
    }
}
