package edu.utep.cybershare.elseweb.util;

import java.net.URI;
import java.util.UUID;

public class ResourceURI {
	private static final String URI_PREFIX = "http://visko.cybershare.utep.edu/linked-data/elseweb/sadi/";

	private String uuid;
	
	public ResourceURI(){
		uuid = UUID.randomUUID().toString();
	}
	
	public URI getPreditableURI(String fragment){
		return createURI(fragment);
	}
	
	public URI getURI(String baseFragment, String id){
		String fullFragment = baseFragment + "-" + id;
		return createURI(fullFragment);
	}
	
	public URI getURI(String baseFragment){		
		String fullFragment = baseFragment + "-" + uuid.toString();
		return createURI(fullFragment);
	}
	
	public URI getRandomURI(String baseFragment){
		String fullFragment = baseFragment + "-" + UUID.randomUUID().toString();
		return createURI(fullFragment);
	}
	
	private static URI createURI(String fullFragment){
		URI uri = null;
		try{uri = new URI(URI_PREFIX + fullFragment);}
		catch(Exception e){e.printStackTrace();}
		return uri;
	}
}
