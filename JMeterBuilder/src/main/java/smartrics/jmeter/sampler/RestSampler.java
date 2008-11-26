package smartrics.jmeter.sampler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler2;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

public class RestSampler extends HTTPSampler2 {
	private static final long serialVersionUID = -5877623539165274730L;

	private static final Logger log = LoggingManager.getLoggerForClass();

	public static final String XML_DATA = "RestSampler.xml_data";

	public RestSampler() {
	}

	public void setXmlData(String data) {
		setProperty(XML_DATA, data);
	}

	public String getXmlData() {
		return getPropertyAsString(XML_DATA);
	}

	protected int setEntityEnclosingMethodHeaders(EntityEnclosingMethod m) {
		int length = 0;// Take length from file
		if (getHeaderManager() != null) {
			// headerManager was set, so let's set the connection
			// to use it.
			HeaderManager mngr = getHeaderManager();
			int headerSize = mngr.size();
			for (int idx = 0; idx < headerSize; idx++) {
				Header hd = mngr.getHeader(idx);
				if (HEADER_CONTENT_LENGTH.equalsIgnoreCase(hd.getName())) {// Use
																			// this
																			// to
																			// override
																			// file
																			// length
					length = Integer.parseInt(hd.getValue());
				}
				// All the other headers are set up by
				// HTTPSampler2.setupConnection()
			}
		} else {
			// otherwise we use "text/xml" as the default
			m.addRequestHeader(HEADER_CONTENT_TYPE, "text/xml"); //$NON-NLS-1$
		}
		return length;
	}

	private HttpMethodBase createMethod(String method, String urlStr) {
		if ("get".equals(method.toLowerCase()))
			return new GetMethod(urlStr);
		if ("post".equals(method.toLowerCase()))
			return new PostMethod(urlStr);
		if ("put".equals(method.toLowerCase()))
			return new PutMethod(urlStr);
		if ("delete".equals(method.toLowerCase()))
			return new DeleteMethod(urlStr);
		throw new IllegalStateException("Method not supported: " + method);
	}

	/**
	 * Send POST data from <code>Entry</code> to the open connection.
	 * 
	 * @param post
	 * @throws IOException
	 *             if an I/O exception occurs
	 */
	private String sendEntityEnclosingMethodData(EntityEnclosingMethod m,
			final int length) {
		// Buffer to hold the post body, except file content
		StringBuffer postedBody = new StringBuffer(1000);
		postedBody.append(getXmlData());
		m.setRequestEntity(new RequestEntity() {
			public boolean isRepeatable() {
				return true;
			}

			public void writeRequest(OutputStream out) throws IOException {
				PrintWriter printer = new PrintWriter(out);
				printer.print(getXmlData());
				printer.flush();
			}

			public long getContentLength() {
				return getXmlData().getBytes().length;// so we don't generate
														// chunked encoding
			}

			public String getContentType() {
				return "text/xml";
			}
		});
		return postedBody.toString();
	}

	private boolean isEntityEnclosingMethod(HttpMethodBase m){
		return m instanceof EntityEnclosingMethod;
	}
	
	protected HTTPSampleResult sample(URL url, String method,
			boolean areFollowingRedirect, int frameDepth) {
		String urlStr = url.toString();

		log.debug("Start : sample " + urlStr);

		HttpMethodBase httpMethod;
		httpMethod = createMethod(method, urlStr);

		HTTPSampleResult res = new HTTPSampleResult();
		res.setMonitor(false);

		res.setSampleLabel(urlStr); // May be replaced later
		res.setHTTPMethod(HTTPConstants.POST);
		res.sampleStart(); // Count the retries as well in the time
		HttpClient client = null;
		InputStream instream = null;
		try {
			int content_len = 0;
			if(isEntityEnclosingMethod(httpMethod))
				setEntityEnclosingMethodHeaders((EntityEnclosingMethod)httpMethod);
			client = setupConnection(url, httpMethod, res);
			if(isEntityEnclosingMethod(httpMethod))
				res.setQueryString(sendEntityEnclosingMethodData((EntityEnclosingMethod)httpMethod, content_len));
			res.setRequestHeaders(getConnectionHeaders(httpMethod));
			int statusCode = client.executeMethod(httpMethod);

			// Request sent. Now get the response:
			instream = httpMethod.getResponseBodyAsStream();

			if (instream != null) {// will be null for HEAD

				org.apache.commons.httpclient.Header responseHeader = httpMethod
						.getResponseHeader(TRANSFER_ENCODING);
				if (responseHeader != null
						&& ENCODING_GZIP.equals(responseHeader.getValue())) {
					instream = new GZIPInputStream(instream);
				}

				// int contentLength = httpMethod.getResponseContentLength();Not
				// visible ...
				// TODO size ouststream according to actual content length
				ByteArrayOutputStream outstream = new ByteArrayOutputStream(
						4 * 1024);
				// contentLength > 0 ? contentLength :
				// DEFAULT_INITIAL_BUFFER_SIZE);
				byte[] buffer = new byte[4096];
				int len;
				boolean first = true;// first response
				while ((len = instream.read(buffer)) > 0) {
					if (first) { // save the latency
						res.latencyEnd();
						first = false;
					}
					outstream.write(buffer, 0, len);
				}

				res.setResponseData(outstream.toByteArray());
				outstream.close();

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
			org.apache.commons.httpclient.Header h = httpMethod
					.getResponseHeader(HEADER_CONTENT_TYPE);
			if (h != null)// Can be missing, e.g. on redirect
			{
				ct = h.getValue();
				res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
				res.setEncodingAndType(ct);
			}

			res.setResponseHeaders(getResponseHeaders(httpMethod));
			if (res.isRedirect()) {
				res.setRedirectLocation(httpMethod.getResponseHeader(
						HEADER_LOCATION).getValue());
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
			httpMethod.releaseConnection();
		}
	}
}
