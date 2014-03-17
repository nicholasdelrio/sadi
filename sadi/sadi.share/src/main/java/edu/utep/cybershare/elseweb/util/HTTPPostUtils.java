package edu.utep.cybershare.elseweb.util;

import java.net.URL;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HTTPPostUtils {

	private static HTTPPostUtils instance;
	
	private CloseableHttpClient client;
	private RequestConfig config;
	public static HTTPPostUtils getInstance(){
		if(instance == null)
			instance = new HTTPPostUtils();
		
		return instance;
	}
	
	private void setConfiguration(){
		config = RequestConfig.custom()
			    .setSocketTimeout(50 * 1000)
			    .setConnectTimeout(50 * 1000)
			    .build();
	}
	
	private HTTPPostUtils(){
		client = HttpClients.createDefault();
		this.setConfiguration();
	}
	
	
	public boolean postText(URL textURL, String text, String username, String password){
		boolean success = false;
		
		try{
			
			StringEntity stringEntity = new StringEntity(text, ContentType.create("text/turtle", Consts.UTF_8));
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username + ":" + password));
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setCredentialsProvider(credentialsProvider);
		
			HttpPost httppost = new HttpPost(textURL.toString());
			httppost.setConfig(config);
			httppost.setEntity(stringEntity);
			
			System.out.println(httppost.toString());
		
			String content = null;
		
			System.out.println("calling...");
			CloseableHttpResponse response = client.execute(httppost, localContext);
			System.out.println(response.getStatusLine().getStatusCode());
		    HttpEntity entity = response.getEntity();
		    if (entity != null){
		        content = EntityUtils.toString(entity);
		        System.out.println("returned content: " + content);
		    }
		    success = (response.getStatusLine().getStatusCode() == 200)? true : false;
		    response.close();
		} catch(Exception e){e.printStackTrace();}
	
		return success;
	}
	
	public static String getRDFContent(){
		String queryString = 
				"@prefix foo: <http://example.org/ns#>.\n" + 
				"foo:owl23 <http://poop.owl#has232> <http://poop.owl#poop11223>.";
		return queryString;
	}
	
	public static void main(String[] args){
		String graphURI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/sadi-prov.owl";
		
		try{
			String urlString = "http://visko.cybershare.utep.edu/sparql-graph-crud-auth?graph=" + graphURI;

			System.out.println(urlString);
			
			URL url = new URL(urlString);
			boolean success = HTTPPostUtils.getInstance().postText(url, getRDFContent(), "elseweb", "elseweb1");
			System.out.println(success);
		}catch(Exception e){e.printStackTrace();}
		
	}
}