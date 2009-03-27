/*  Copyright 2008 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A generic REST client based on {@code HttpClient}.
 */
public class RestClientImpl implements RestClient {

    private static Log LOG = LogFactory.getLog(RestClientImpl.class);

    private final HttpClient client;

    private String baseUrl;

    /**
     * Constructor allowing the injection of an {@code
     * org.apache.commons.httpclient.HttpClient}.
     * 
     * @param client
     *            the client
     * @see org.apache.commons.httpclient.HttpClient
     */
    public RestClientImpl(HttpClient client) {
        if (client == null)
            throw new IllegalArgumentException("Null HttpClient instance");
        this.client = client;
    }

    /**
     * @see {@link smartrics.rest.client.RestClient#setBaseUrl(java.lang.String)}
     */
    public void setBaseUrl(String bUrl) {
        this.baseUrl = bUrl;
    }

    /**
     * @see {@link smartrics.rest.client.RestClient#getBaseUrl()}
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the Http client instance used by this implementation.
     * 
     * @return the instance of HttpClient
     * @see org.apache.commons.httpclient.HttpClient
     * @see {@link
     *      smartrics.rest.client.RestClientImpl(org.apache.commons.httpclient.
     *      HttpClient)}
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * @see {@link smartrics.rest.client.RestClient#execute(smartrics.rest.client.RestRequest)}
     */
    public RestResponse execute(RestRequest request) {
        return execute(getBaseUrl(), request);
    }

    /**
     * @see {@link smartrics.rest.client.RestClient#execute(java.lang.String, smartrics.rest.client.RestRequest)}
     */
    public RestResponse execute(String hostAddr, final RestRequest request) {
        if (request == null || !request.isValid())
            throw new IllegalArgumentException("Invalid request " + request);
        if (request.getTransactionId() == null)
            request.setTransactionId(Long.valueOf(System.currentTimeMillis()));
        LOG.debug(request);
        HttpMethod m = createHttpClientMethod(request);
        configureHttpMethod(m, hostAddr, request);
        RestResponse resp = new RestResponse();
        resp.setTransactionId(request.getTransactionId());
        resp.setResource(request.getResource());
        try {
            client.executeMethod(m);
            for (Header h : m.getResponseHeaders()) {
                resp.addHeader(h.getName(), h.getValue());
            }
            resp.setStatusCode(m.getStatusCode());
            resp.setStatusText(m.getStatusText());
            resp.setBody(m.getResponseBodyAsString());
        } catch (HttpException e) {
            String message = "Http call failed for protocol failure";
            LOG.warn(message);
            throw new IllegalStateException(message, e);
        } catch (IOException e) {
            String message = "Http call failed for IO failure";
            LOG.warn(message);
            throw new IllegalStateException(message, e);
        } finally {
            m.releaseConnection();
        }
        LOG.debug(resp);
        return resp;
    }

    /**
     * Configures the instance of HttpMethod with the data in the request and
     * the host address.
     * 
     * @param m
     *            the method class to configure
     * @param hostAddr
     *            the host address
     * @param request
     *            the rest request
     */
    protected void configureHttpMethod(HttpMethod m, String hostAddr, final RestRequest request) {
        addHeaders(m, request);
        setUri(m, hostAddr, request);
        m.setQueryString(request.getQuery());
        if (m instanceof EntityEnclosingMethod) {
            RequestEntity requestEntity = null;
            String fileName = request.getFileName();
            if (fileName != null) {
                requestEntity = configureFileUpload(fileName);
            } else {
                fileName = request.getMultipartFileName();
                if (fileName != null) {
                    requestEntity = configureMultipartFileUpload(m, request, requestEntity, fileName);
                } else {
                    requestEntity = new RequestEntity() {
                        public boolean isRepeatable() {
                            return true;
                        }

                        public void writeRequest(OutputStream out) throws IOException {
                            PrintWriter printer = new PrintWriter(out);
                            printer.print(request.getBody());
                            printer.flush();
                        }

                        public long getContentLength() {
                            return request.getBody().getBytes().length;
                        }

                        public String getContentType() {
                            List<smartrics.rest.client.RestData.Header> values = request.getHeader("Content-Type");
                            String v = "text/xml";
                            if (values.size() != 0)
                                v = values.get(0).getValue();
                            return v;
                        }
                    };
                }
            }
            ((EntityEnclosingMethod) m).setRequestEntity(requestEntity);
        }
    }

    private RequestEntity configureMultipartFileUpload(HttpMethod m, final RestRequest request, RequestEntity requestEntity, String fileName) {
        File file = new File(fileName);
        try {
            requestEntity = new MultipartRequestEntity(new Part[] { new FilePart(request.getMultipartFileParameterName(), file) }, ((EntityEnclosingMethod) m).getParams());
        } catch (FileNotFoundException e) {
            LOG.error(String.format("File %s not found", fileName), e);
            throw new IllegalArgumentException(e);
        }
        return requestEntity;
    }

    private RequestEntity configureFileUpload(String fileName) {
        final File file = new File(fileName);
        if (!file.exists()) {
            String message = String.format("File %s not found", fileName);
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
        return new FileRequestEntity(file, "application/octet-stream");
    }

    public String getContentType(RestRequest request) {
        List<smartrics.rest.client.RestData.Header> values = request.getHeader("Content-Type");
        String v = "text/xml";
        if (values.size() != 0)
            v = values.get(0).getValue();
        return v;
    }

    private void setUri(HttpMethod m, String hostAddr, RestRequest request) {
        String host = hostAddr == null ? client.getHostConfiguration().getHost() : hostAddr;
        if (host == null)
            throw new IllegalStateException("hostAddress is null: please config httpClient host configuration or " + "pass a valid host address or config a baseUrl on this client");
        String uriString = host + request.getResource();
        try {
            m.setURI(new URI(uriString, false));
        } catch (URIException e) {
            throw new IllegalStateException("Problem when building URI: " + uriString, e);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Building URI with null string", e);
        }
    }

    /**
     * factory method that maps a string with a HTTP method name to an
     * implementation class in Apache HttpClient. Currently the name is mapped
     * to <code>org.apache.commons.httpclient.methods.%sMethod</code> where
     * <code>%s</code> is the parameter mName.
     * 
     * @param mName
     *            the method name
     * @return the method class
     */
    protected String getMethodClassnameFromMethodName(String mName) {
        return String.format("org.apache.commons.httpclient.methods.%sMethod", mName);
    }

    /**
     * Utility method that creates an instance of {@code
     * org.apache.commons.httpclient.HttpMethod}.
     * 
     * @param request
     *            the rest request
     * @return the instance of {@code org.apache.commons.httpclient.HttpMethod}
     *         matching the method in RestRequest.
     */
    @SuppressWarnings("unchecked")
    protected HttpMethod createHttpClientMethod(RestRequest request) {
        String mName = request.getMethod().toString();
        String className = getMethodClassnameFromMethodName(mName);
        try {
            Class<HttpMethod> clazz = (Class<HttpMethod>) Class.forName(className);
            HttpMethod m = clazz.newInstance();
            return m;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(className + " not found: you may be using a too old or " + "too new version of HttpClient", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("An object of type " + className + " cannot be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("The default ctor for type " + className + " cannot be invoked", e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Exception when instantiating: " + className, e);
        }
    }

    private void addHeaders(HttpMethod m, RestRequest request) {
        for (RestData.Header h : request.getHeaders()) {
            m.addRequestHeader(h.getName(), h.getValue());
        }
    }
}
