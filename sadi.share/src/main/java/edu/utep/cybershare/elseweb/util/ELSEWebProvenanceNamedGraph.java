package edu.utep.cybershare.elseweb.util;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class ELSEWebProvenanceNamedGraph {

	private static final String graphURI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/sadi-prov.owl";
	private static final String virtuosoURLString = "http://visko.cybershare.utep.edu/";
	private static final String crudPath = "sparql-graph-crud-auth";
	private static final String graphParameter = "?graph=";
			
	private static ELSEWebProvenanceNamedGraph instance;
	
	private DefaultHttpClient httpClient;
	private URL postURL;
	
	public static ELSEWebProvenanceNamedGraph getInstance(){
		if(instance == null)
			instance = new ELSEWebProvenanceNamedGraph();
		
		return instance;
	}
	
	private ELSEWebProvenanceNamedGraph(){
		httpClient = new DefaultHttpClient();
		try{postURL = new URL(virtuosoURLString + crudPath + graphParameter + graphURI);}
		catch(Exception e){e.printStackTrace();}
	}
	
	public boolean postProvenance(String text, String username, String password){
		boolean success = false;
		httpClient.getCredentialsProvider().setCredentials(
	                new AuthScope(postURL.getHost(), AuthScope.ANY_PORT), 
	                new UsernamePasswordCredentials(username, password));
		
		try{
			StringEntity stringEntity = new StringEntity(text, "text/turtle", HTTP.ASCII);
		
			HttpPost httppost = new HttpPost(postURL.toString());
			httppost.setEntity(stringEntity);
		
			String content = null;
		
			System.out.println("calling...");
			HttpResponse response = httpClient.execute(httppost);
			
			System.out.println(response.getStatusLine().getStatusCode());
		    HttpEntity entity = response.getEntity();
		    if (entity != null){
		        content = EntityUtils.toString(entity);
		        System.out.println("returned content: " + content);
		    }
		    success = (response.getStatusLine().getStatusCode() == 200)? true : false;
	
		} catch(Exception e){e.printStackTrace();}
	
		return success;
	}
	
	public static String getRDFContent(){
		String queryString = 
				"@prefix foo: <http://example.org/ns#>.\n" + 
				"foo:owl232d34 <http://poop.owl#has23sd2> <http://poop.owl#poop11222342343>.";
		return queryString;
	}
	
	public static void main(String[] args){			
		boolean success = ELSEWebProvenanceNamedGraph.getInstance().postProvenance(getRDFContent(), "elseweb", "elseweb1");
		System.out.println(success);
	}
}