package edu.utep.cybershare.elseweb.prov;

import java.net.URL;

import edu.utep.cybershare.elseweb.util.HTTPPostUtils;

public class ELSEWebSPARQLEndpoint {
	
	private static final String ENDPOINT_URL = "http://visko.cybershare.utep.edu/sparql";
	private static final String PROVENANCE_GRAPH_URI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/sadi-prov.owl";	
	
	private String endpoint;
	private String endpointAuth;
	
	public ELSEWebSPARQLEndpoint(){
		this.endpoint = ENDPOINT_URL;
		this.endpointAuth = this.endpoint + "-graph-crud-auth";
	}
	
	public void updateProvenance(String turtleContent){
		try{
			String requestString = this.endpointAuth + "?graph=" + PROVENANCE_GRAPH_URI;
			
			System.out.println("updating prov graph");
			System.out.println(requestString);
			
			URL requestURL = new URL(requestString);
			HTTPPostUtils.getInstance().postText(requestURL, turtleContent, "elseweb", "elseweb1");
		}catch(Exception e){e.printStackTrace();}
	}
}