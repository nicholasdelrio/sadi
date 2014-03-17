package edu.utep.cybershare.elseweb.prov;

import java.io.StringWriter;
import java.util.UUID;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.utep.cybershare.elseweb.ontology.Vocabulary;

public class ServiceExecution {
	
	public enum AgentType {LM_SERIVCE, WCS_EXEC, PAYLOAD_EXT, SPEC_GEN};
	
	private static String LIFEMAPPER_SERVICE_URI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/lifemapperService";
	private static String WCS_EXECUTOR_URI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/wcsExecutor";	
	private static String PAYLOAD_EXTRACTOR_URI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/payloadExtractor";
	private static String SPECIFICATION_GENERATOR_URI = "http://ontology.cybershare.utep.edu/ELSEWeb/linked-data/sadi/provenance/specificationGenerator";
	
	private Resource agent;
	private Resource input;
	private Resource output;
	private Resource activity;
	
	private Model model;
	
	public ServiceExecution(AgentType agentType, Resource input, Resource output){
		model = ModelFactory.createDefaultModel();
		
		if(agentType == AgentType.LM_SERIVCE){
			agent = model.createResource(LIFEMAPPER_SERVICE_URI, Vocabulary.SADIService);
			activity = model.createResource(getActivityURI(LIFEMAPPER_SERVICE_URI), Vocabulary.SubmitToLifemapper);
		}
		else if(agentType == AgentType.WCS_EXEC){
			agent = model.createResource(WCS_EXECUTOR_URI,  Vocabulary.SADIService);
			activity = model.createResource(getActivityURI(WCS_EXECUTOR_URI), Vocabulary.ExecuteWCSService);
		}
		else if(agentType == AgentType.PAYLOAD_EXT){
			agent = model.createResource(PAYLOAD_EXTRACTOR_URI, Vocabulary.SADIService);
			activity = model.createResource(getActivityURI(PAYLOAD_EXTRACTOR_URI), Vocabulary.ExtractPayload);
		}
		else{
			agent = model.createResource(SPECIFICATION_GENERATOR_URI, Vocabulary.SADIService);
			activity = model.createResource(getActivityURI(SPECIFICATION_GENERATOR_URI), Vocabulary.GenerateSpecification);
		}

		this.input = input;
		this.output = output;
	}
	
	private static String getActivityURI(String agentURI){
		return agentURI + "-" + UUID.randomUUID().toString();
	}
	
	public void dump(){
		this.connectResources();
		
		this.model.add(input.getModel());
		this.model.add(output.getModel());
		
		StringWriter wtr = new StringWriter();
		this.model.write(wtr, "TURTLE");

		System.out.println("PROVENANCE---------------------");
		System.out.println(wtr.toString());
		
		ELSEWebSPARQLEndpoint endpoint = new ELSEWebSPARQLEndpoint();
		endpoint.updateProvenance(wtr.toString());
	}
	
	private void connectResources(){
		this.activity.addProperty(Vocabulary.hadInput, this.input);
		this.output.addProperty(Vocabulary.wasOutputBy, this.activity);
		this.activity.addProperty(Vocabulary.wasAssociatedWith, this.agent);
	}
}