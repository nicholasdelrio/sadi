package edu.utep.cybershare.elseweb.prov;

import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;

import ca.wilkinsonlab.sadi.client.Service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.utep.cybershare.elseweb.ontology.Vocabulary;
import edu.utep.cybershare.elseweb.util.ELSEWebProvenanceNamedGraph;
import edu.utep.cybershare.elseweb.util.Printing;
import edu.utep.cybershare.elseweb.util.ResourceURI;

public class ServiceExecution {
		
	private ResourceURI resourceURI;
	private Resource agent;
	private Service service;
	private Model model;
	
	public ServiceExecution(Service service){
		this.model = ModelFactory.createDefaultModel();
		this.service = service;
		this.resourceURI = new ResourceURI();
		this.agent = model.createResource(service.getURI(), Vocabulary.SADIService);
	}
	
	public void logExecution(Collection<Resource> inputs, Model outputs){

		System.out.println("printing set of outputs from service:");
		Printing.print(outputs);
		
		Resource anActivity;
		String activityName = service.getName() + "-activity";
		URI activityURI;
		Resource output;
		for(Resource input : inputs){
			activityURI = resourceURI.getRandomURI(activityName);
			anActivity = model.createResource(activityURI.toASCIIString(), Vocabulary.SADIActivity);
			anActivity.addProperty(Vocabulary.wasAssociatedWith, this.agent);
			
			//add triples of input and output to the provenance model
			//this.model.add(input.getModel());
			this.model.add(outputs);
			
			//set input to activity
			System.out.println("adding as input to activity: " + input.getURI());
			anActivity.addProperty(Vocabulary.hadInput, input);
			
			//set objects of properties as output(s) to activity
			output = outputs.getResource(input.getURI());
			
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
						replicatedOutput.addProperty(Vocabulary.wasOutputBy, anActivity);
					}
				}
			}
		}
		
		dump();
	}
	
	private void dump(){
		
		StringWriter wtr = new StringWriter();
		this.model.write(wtr, "TURTLE");

		System.out.println("PROVENANCE---------------------");
		System.out.println(wtr.toString());
		
		ELSEWebProvenanceNamedGraph.getInstance().postProvenance(wtr.toString(), "elseweb", "elseweb1");
	}
}