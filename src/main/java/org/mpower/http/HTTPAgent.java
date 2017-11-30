package org.mpower.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.mpower.form.SubmissionBuilder;
import org.mpower.opensrpadapter.Listener;
import org.mpower.properties.AdapterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
@ComponentScan
public class HTTPAgent {
	
	private final GZipEncodingHttpClient httpClient;

	private static final Logger logger = LoggerFactory.getLogger(HTTPAgent.class);
	private static final String OPENSRP_USER = "sohel";
	
	private static final String OPENSRP_PWD = "Sohel@123";
	

	public HTTPAgent() {
		BasicHttpParams basicHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
		HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		SingleClientConnManager connectionManager = new SingleClientConnManager(basicHttpParams, registry);
		
		httpClient = new GZipEncodingHttpClient(new DefaultHttpClient(connectionManager, basicHttpParams));
	}
	
	public Response<String> post(String postURLPath, String jsonPayload) {
		try {
			HttpPost httpPost = new HttpPost(postURLPath);
			httpPost.setHeader("Authorization", "Basic " + getEncodedCredentials());
			
			StringEntity entity = new StringEntity(jsonPayload, HTTP.UTF_8);
			entity.setContentType("application/json; charset=utf-8");
			httpPost.setEntity(entity);
			
			HttpResponse response = httpClient.postContent(httpPost);
			logger.info("HTTP status code:" + response.getStatusLine().getStatusCode());
			
			ResponseStatus responseStatus = response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED
			        ? ResponseStatus.success
			        : ResponseStatus.failure;
			response.getEntity().consumeContent();
			return new Response<String>(responseStatus, null);
		}
		catch (Exception e) {
			logger.warn("failed: " + e.getMessage());
			return new Response<String>(ResponseStatus.failure, null);
		}
	}
	
	public Response<String> fetch(String requestURLPath) {
		try {
			HttpGet request = new HttpGet(requestURLPath);
			request.setHeader("Authorization", "Basic " + getEncodedCredentials());
			String responseContent = IOUtils.toString(httpClient.fetchContent(request));
			return new Response<String>(ResponseStatus.success, responseContent);
		}
		catch (Exception e) {
			logger.warn("failed: " + e.getMessage());
			return new Response<String>(ResponseStatus.failure, null);
		}
	}
	
	
	private String getEncodedCredentials() {

		return new String(Base64.encodeBase64((OPENSRP_USER + ":" + OPENSRP_PWD).getBytes()));
	}

	
}
