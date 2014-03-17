package edu.utep.cybershare.elseweb.util;

import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HTTPGetUtils {

	private static HTTPGetUtils instance;
	
	private CloseableHttpClient client;
	private RequestConfig config;
	public static HTTPGetUtils getInstance(){
		if(instance == null)
			instance = new HTTPGetUtils();
		
		return instance;
	}
	
	private void setConfiguration(){
		config = RequestConfig.custom()
			    .setSocketTimeout(50 * 1000)
			    .setConnectTimeout(50 * 1000)
			    .build();
	}
	
	private HTTPGetUtils(){
		client = HttpClients.createDefault();
		this.setConfiguration();
	}
	
	public String robustDownloadText(URL url, int numberOfTries){
		int counter = 0;
		String contents = null;
		while(contents == null && counter < numberOfTries){
			System.out.println("trial: " + counter);
			counter ++;
			contents = this.downloadText(url);	
		}
		
		return contents;
	}
	
	public byte[] robustDownloadBytes(URL url, int numberOfTries){
		int counter = 0;
		byte[] contents = null;
		while(contents == null && counter < numberOfTries){
			System.out.println("trial: " + counter);
			counter ++;
			contents = this.downloadBytes(url);	
		}
		
		return contents;
	}
	
	public String downloadText(URL url, String username, String password){
		System.out.println("downloading: " + url.toString());

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username + ":" + password));
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setCredentialsProvider(credentialsProvider);
		
		HttpGet httpget = new HttpGet(url.toString());
		httpget.setConfig(config);
		
		String content = null;
		
		try {
			System.out.println("calling the url");
			CloseableHttpResponse response = client.execute(httpget, localContext);
			System.out.println(response.getStatusLine().getStatusCode());
		    HttpEntity entity = response.getEntity();
		    if (entity != null)
		        content = EntityUtils.toString(entity);
		    response.close();
		} catch(Exception e){e.printStackTrace();}
	
		return content;
	}
	
	public String downloadText(URL url){
		System.out.println("downloading: " + url.toString());

		HttpGet httpget = new HttpGet(url.toString());
		httpget.setConfig(config);
		
		String content = null;
		
		try {
			System.out.println("calling the url");
			CloseableHttpResponse response = client.execute(httpget);
		    HttpEntity entity = response.getEntity();
		    if (entity != null)
		        content = EntityUtils.toString(entity);
		    response.close();
		} catch(Exception e){e.printStackTrace();}
	
		return content;
	}
	
	public byte[] downloadBytes(URL url){
		System.out.println("downloading: " + url.toString());

		HttpGet httpget = new HttpGet(url.toString());
		httpget.setConfig(config);
		
		byte[] content = new byte[0];
		
		try {
			System.out.println("calling the url");
			CloseableHttpResponse response = client.execute(httpget);
		    HttpEntity entity = response.getEntity();
		    if (entity != null)	
		    	content = EntityUtils.toByteArray(entity);
		    response.close();
		} catch(Exception e){e.printStackTrace();}
	
		return content;
	}
	
	public static String getInsertQuery(){
		String graphURI = "<http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/sadi-prov.owl>";
		String queryString = 
				"insert data { graph " + graphURI + " {<http://poop.owl> <http://poop.owl#has> <http://poop.owl#poop1>}}";
		return queryString;
	}
	
	public static void main(String[] args){
		try{
			String urlString = "http://visko.cybershare.utep.edu/sparql-auth?";
			urlString += "query=" + URLEncoder.encode(getInsertQuery(), "UTF-8");

			System.out.println(urlString);
			
			URL url = new URL(urlString);
			String response = HTTPGetUtils.getInstance().downloadText(url, "elseweb", "elseweb1");
			System.out.println(response);
		}catch(Exception e){e.printStackTrace();}
		
	}
}