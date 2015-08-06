package org.mpower.http;


import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;



public class HTTPAgent {
    private final GZipEncodingHttpClient httpClient;

	private static final String OPENSRP_USER = "sohel";
	private static final String OPENSRP_PWD = "Sohel@123";
	

    public HTTPAgent() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //registry.register(new Scheme("https", sslSocketFactoryWithopensrpCertificate(), 443));

        SingleClientConnManager connectionManager = new SingleClientConnManager(basicHttpParams, registry);
        
        httpClient = new GZipEncodingHttpClient(new DefaultHttpClient(connectionManager, basicHttpParams));
    }

 

    public Response<String> post(String postURLPath, String jsonPayload) {
        try {
            setCredentials(OPENSRP_USER, OPENSRP_PWD);
            HttpPost httpPost = new HttpPost(postURLPath);
			String encoded = new String(Base64.encodeBase64((OPENSRP_USER+":"+OPENSRP_PWD).getBytes()));
            httpPost.setHeader("Authorization", "Basic "+encoded);

            StringEntity entity = new StringEntity(jsonPayload, HTTP.UTF_8);
            entity.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(entity);
            
            HttpResponse response = httpClient.postContent(httpPost);
            System.out.println("HTTP status code:" + response.getStatusLine().getStatusCode());

            ResponseStatus responseStatus = response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED ? ResponseStatus.success : ResponseStatus.failure;
            response.getEntity().consumeContent();
            return new Response<String>(responseStatus, null);
        } catch (Exception e) {
            //logWarn(e.toString());
            return new Response<String>(ResponseStatus.failure, null);
        }
    }

    public Response<String> fetch(String requestURLPath) {
    	 System.out.println("fetch url: " + requestURLPath);
        try {
        	setCredentials(OPENSRP_USER, OPENSRP_PWD);
            String responseContent = IOUtils.toString(httpClient.fetchContent(new HttpGet(requestURLPath)));
            System.out.println("responseContent: " + responseContent);
            return new Response<String>(ResponseStatus.success, responseContent);
        } catch (Exception e) {
            return new Response<String>(ResponseStatus.failure, null);
        }
    }


    private void setCredentials(String userName, String password) {
        httpClient.getCredentialsProvider().setCredentials(new AuthScope("http://192.168.21.87:8080/", -1, "OpenSRP"),
                new UsernamePasswordCredentials(userName, password));
    }
    
    private SocketFactory sslSocketFactoryWithopensrpCertificate() {
        try {
            KeyStore trustedKeystore = KeyStore.getInstance("BKS");
            InputStream inputStream = null;
            try {
                trustedKeystore.load(inputStream, "phone red pen".toCharArray());
            } finally {
                inputStream.close();
            }
            SSLSocketFactory socketFactory = new SSLSocketFactory(trustedKeystore);
            final X509HostnameVerifier oldVerifier = socketFactory.getHostnameVerifier();
            socketFactory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                    for (String cn : cns) {
                        if (!false || host.equals(cn)) {
                            return;
                        }
                    }
                    oldVerifier.verify(host, cns, subjectAlts);
                }
            });
            return socketFactory;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }




}
