package smartrics.jmeter.sampler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler2;
import org.apache.jmeter.protocol.http.util.EncoderCache;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

public class RestSampler extends HTTPSampler2 {
    private static final long serialVersionUID = -5877623539165274730L;

    private static final String DEFAULT_URL = "http://localhost:8080";

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String REQUEST_BODY = "RestSampler.request_body";

    public static final String QUERY_STRING = "RestSampler.query_string";

    public static final String REQUEST_HEADERS = "RestSampler.request_headers";

    public RestSampler() {
    }

    public void setRequestBody(String data) {
        setProperty(REQUEST_BODY, data);
    }

    public void setRequestHeaders(String headers) {
        setProperty(REQUEST_HEADERS, headers);
    }

    public String getRequestBody() {
        return getPropertyAsString(REQUEST_BODY);
    }

    public String getRequestHeaders() {
        return getPropertyAsString(REQUEST_HEADERS);
    }

    public void setResource(String data) {
        setProperty(PATH, data);
    }

    public String getResource() {
        return getPropertyAsString(PATH);
    }

    public void setQueryString(String data) {
        setProperty(QUERY_STRING, data);
        getArguments().clear();
        parseArguments(data, EncoderCache.URL_ARGUMENT_ENCODING);
    }

    public String getQueryString() {
        return getPropertyAsString(QUERY_STRING);
    }

    public void setHostBaseUrl(final String data) {
        String d = data;
        if (data != null || "".equals(data.trim())) {
            d = DEFAULT_URL;
        }
        try {
            URL u = new URL(data);
            setProperty(PROTOCOL, u.getProtocol());
            setProperty(DOMAIN, u.getHost());
            setProperty(PORT, Integer.toString(u.getPort()));
            setProperty(URL, u.toString());

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url " + data, e);
        }
    }

    public String getHostBaseUrl() {
        try {
            URL u = new URL(getPropertyAsString(PROTOCOL), getPropertyAsString(DOMAIN), getPropertyAsInt(PORT), "");
            return u.toString();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid data to build url", e);
        }
    }

    public String toString() {
        return "Base host url: " + getHostBaseUrl() + ", resource: " + getResource() + ", Method: " + getMethod();
    }

    private void overrideHeaders(HttpMethodBase httpMethod) {
        String headers = getRequestHeaders();
        String[] header = headers.split(System.getProperty("line.separator"));
        for (String kvp : header) {
            int pos = kvp.indexOf(':');
            if (pos > 0) {
                String k = kvp.substring(0, pos).trim();
                String v = "";
                if (kvp.length() > pos + 1)
                    v = kvp.substring(pos + 1).trim();
                httpMethod.addRequestHeader(k, v);
            }
        }
    }

    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {

        String urlStr = url.toString();

        log.debug("Start : sample " + urlStr);
        log.debug("method " + method);

        HttpMethodBase httpMethod = null;

        HTTPSampleResult res = new HTTPSampleResult();
        res.setMonitor(isMonitor());

        res.setSampleLabel(urlStr); // May be replaced later
        res.setHTTPMethod(method);
        res.sampleStart(); // Count the retries as well in the time
        HttpClient client = null;
        InputStream instream = null;
        try {
            httpMethod = createHttpMethod(method, urlStr);
            // Set any default request headers
            setDefaultRequestHeaders(httpMethod);
            // Setup connection
            client = setupConnection(url, httpMethod, res);
            // Handle the various methods
            if (httpMethod instanceof EntityEnclosingMethod) {
                String postBody = sendData((EntityEnclosingMethod) httpMethod);
                res.setResponseData(postBody.getBytes());
            }
            res.setRequestHeaders(getConnectionHeaders(httpMethod));

            int statusCode = client.executeMethod(httpMethod);

            // Request sent. Now get the response:
            instream = httpMethod.getResponseBodyAsStream();

            if (instream != null) {// will be null for HEAD

                Header responseHeader = httpMethod.getResponseHeader(HEADER_CONTENT_ENCODING);
                if (responseHeader != null && ENCODING_GZIP.equals(responseHeader.getValue())) {
                    instream = new GZIPInputStream(instream);
                }
                res.setResponseData(readResponse(res, instream, (int) httpMethod.getResponseContentLength()));
            }

            res.sampleEnd();
            // Done with the sampling proper.

            // Now collect the results into the HTTPSampleResult:

            res.setSampleLabel(httpMethod.getURI().toString());
            // Pick up Actual path (after redirects)

            res.setResponseCode(Integer.toString(statusCode));
            res.setSuccessful(isSuccessCode(statusCode));

            res.setResponseMessage(httpMethod.getStatusText());

            String ct = null;
            org.apache.commons.httpclient.Header h = httpMethod.getResponseHeader(HEADER_CONTENT_TYPE);
            if (h != null)// Can be missing, e.g. on redirect
            {
                ct = h.getValue();
                res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                res.setEncodingAndType(ct);
            }

            String responseHeaders = getResponseHeaders(httpMethod);
            res.setResponseHeaders(responseHeaders);
            if (res.isRedirect()) {
                final Header headerLocation = httpMethod.getResponseHeader(HEADER_LOCATION);
                if (headerLocation == null) { // HTTP protocol violation, but
                                              // avoids NPE
                    throw new IllegalArgumentException("Missing location header");
                }
                res.setRedirectLocation(headerLocation.getValue());
            }

            // If we redirected automatically, the URL may have changed
            if (getAutoRedirects()) {
                res.setURL(new URL(httpMethod.getURI().toString()));
            }

            // Store any cookies received in the cookie manager:
            saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());

            // Save cache information
            final CacheManager cacheManager = getCacheManager();
            if (cacheManager != null) {
                cacheManager.saveDetails(httpMethod, res);
            }

            // Follow redirects and download page resources if appropriate:
            res = resultProcessing(areFollowingRedirect, frameDepth, res);

            log.debug("End : sample");
            httpMethod.releaseConnection();
            return res;
        } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
        {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } catch (IOException e) {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } finally {
            JOrphanUtils.closeQuietly(instream);
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    private HttpMethodBase createHttpMethod(String method, String urlStr) {
        HttpMethodBase httpMethod;
        // May generate IllegalArgumentException
        if (method.equals(POST)) {
            httpMethod = new PostMethod(urlStr);
        } else if (method.equals(PUT)) {
            httpMethod = new PutMethod(urlStr);
        } else if (method.equals(HEAD)) {
            httpMethod = new HeadMethod(urlStr);
        } else if (method.equals(TRACE)) {
            httpMethod = new TraceMethod(urlStr);
        } else if (method.equals(OPTIONS)) {
            httpMethod = new OptionsMethod(urlStr);
        } else if (method.equals(DELETE)) {
            httpMethod = new DeleteMethod(urlStr);
        } else if (method.equals(GET)) {
            httpMethod = new GetMethod(urlStr);
        } else {
            log.error("Unexpected method (converted to GET): " + method);
            httpMethod = new GetMethod(urlStr);
        }
        return httpMethod;
    }

    /**
     * Set up the PUT/POST data
     */
    private String sendData(EntityEnclosingMethod method) throws IOException {
        method.setRequestEntity(new MyRequestEntity(getRequestBody()));
        overrideHeaders(method);
        return getRequestBody();
    }
}
