package edu.utep.cybershare.elseweb.prov;

import java.io.StringWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;

import ca.wilkinsonlab.sadi.client.Service;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.utep.cybershare.elseweb.ontology.Vocabulary;
import edu.utep.cybershare.elseweb.util.ELSEWebProvenanceNamedGraph;
import edu.utep.cybershare.elseweb.util.ResourceURI;

public class ServiceExecution {
		
	private Calendar startTime;
	private Calendar endTime;
	
	private Resource shareClientAgent;	
	private ResourceURI resourceURI;
	private Resource sadiServiceAgent;
	private Service service;
	private Model model;
	private Query query;
	
	public ServiceExecution(Service service, Calendar startTime, Calendar endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.model = ModelFactory.createDefaultModel();
		this.service = service;
		this.resourceURI = new ResourceURI();
		this.sadiServiceAgent = model.createResource(service.getURI(), Vocabulary.SADIService);
		this.shareClientAgent = model.createResource(resourceURI.getPreditableURI("cardioSHARE-elseweb").toASCIIString(), Vocabulary.SHAREClient);
	}
	
	public void setResponsibleQuery(Query query){
		this.query = query;
	}
	
	public void logExecution(Collection<Resource> inputs, Model outputs){
		//add output model to this provenance model
		this.model.add(outputs);
		
		for(Resource input : inputs){
			//create cardioShare activity
			Resource aCardioSHAREInvokeServiceActivity = this.getCardioSHAREInvokeServiceActivity();
			aCardioSHAREInvokeServiceActivity.addProperty(Vocabulary.wasAssociatedWith, this.shareClientAgent);
			
			//create sadi service activity
			Resource aSADIServiceActivity = this.getSADIServiceActivity(input);
			aSADIServiceActivity.addProperty(Vocabulary.wasAssociatedWith, this.sadiServiceAgent);
			aSADIServiceActivity.addProperty(Vocabulary.hadInput, input);
			aSADIServiceActivity.addProperty(Vocabulary.wasInvokedBy, aCardioSHAREInvokeServiceActivity);
						
			//set objects of properties as output(s) to activity
			Resource output = outputs.getResource(input.getURI());
			StmtIterator iterator = output.listProperties();
			while(iterator.hasNext()){
				Statement statement = iterator.next();
				Property prop = statement.getPredicate();
			
				//don't consider the type predicates, we want fresh stuff added by the SADI service business logic
				if(!prop.getURI().equals(Vocabulary.type.getURI())){		
					
					RDFNode node = statement.getObject();
					System.out.println("Adding " + node.toString() + " as an output!");
				
					//skip literals since they can't be the subject of a statement
					if(!node.isLiteral()){
						Resource replicatedOutput = this.model.getResource(node.asResource().getURI());
						replicatedOutput.addProperty(Vocabulary.wasOutputBy, aSADIServiceActivity);
					}
				}
			}
		}
		dump();
	}
	
	private Resource getCardioSHAREInvokeServiceActivity(){
		String cardioSHAREInvokeServiceActivityName = "invoke-" + service.getName() + "-activity";
		URI aCardioSHAREInvokeServiceActivityURI = resourceURI.getRandomURI(cardioSHAREInvokeServiceActivityName);
		Resource aCardioSHAREInvokeServiceActivity = model.createResource(aCardioSHAREInvokeServiceActivityURI.toASCIIString(), Vocabulary.InvokeServiceActivity);
		
		XSDDateTime startDateTime = new XSDDateTime(startTime);
		XSDDateTime endDateTime = new XSDDateTime(endTime);
		
		aCardioSHAREInvokeServiceActivity.addLiteral(Vocabulary.startedAtTime, startDateTime);
		aCardioSHAREInvokeServiceActivity.addLiteral(Vocabulary.endedAtTime, endDateTime);
		return aCardioSHAREInvokeServiceActivity;
	}
	
	private Resource getSADIServiceActivity(Resource input){
		String sadiServiceActivityName = service.getName() + "-activity";
		URI aSADIServiceActivityURI = resourceURI.getRandomURI(sadiServiceActivityName);		
		Resource aSADIServiceActivity = model.createResource(aSADIServiceActivityURI.toASCIIString(), Vocabulary.SADIServiceExecutionActivity);
		
		return aSADIServiceActivity;
	}
	
	private void dump(){
		StringWriter wtr = new StringWriter();
		this.model.write(wtr, "TURTLE");
		
		ELSEWebProvenanceNamedGraph.getInstance().postProvenance(wtr.toString(), "elseweb", "elseweb1");
	}
}