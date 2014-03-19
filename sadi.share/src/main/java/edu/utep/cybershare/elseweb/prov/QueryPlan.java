package edu.utep.cybershare.elseweb.prov;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import ca.wilkinsonlab.sadi.client.Service;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.utep.cybershare.elseweb.ontology.Vocabulary;
import edu.utep.cybershare.elseweb.util.ELSEWebProvenanceNamedGraph;
import edu.utep.cybershare.elseweb.util.ResourceURI;

public class QueryPlan {
	//the rdf model
	private Model queryPlanModel;
	
	//the input sparql query
	private Query query;
	
	//query plan related activites
	private Resource executeQueryPlanActivity;
	private Resource generateQueryPlanActivity;

	//query plan related agents
	private Resource shareClientQueryPlannerAgent;
	private Resource shareClientQueryPlanExecutorAgent;
	private Resource elsewebUserAgent;
	
	//query plan association
	private Resource queryPlanAssociation;
	
	//the query plan and query
	private Resource shareQueryPlan;
	private Resource sparqlQuery;
	
	//URI generator
	ResourceURI resourceURI;
	
	public QueryPlan(Query inputQuery){
		this.query = inputQuery;
		this.queryPlanModel = ModelFactory.createDefaultModel();
		this.resourceURI = new ResourceURI();
		
		this.initializeResource();
		this.linkResources();
	}
	
	private void linkResources(){
		//link up query planning activity
		this.generateQueryPlanActivity.addProperty(Vocabulary.wasAssociatedWith, this.shareClientQueryPlannerAgent);
		this.generateQueryPlanActivity.addProperty(Vocabulary.used, this.sparqlQuery);
		
		//link up sparql query
		this.sparqlQuery.addProperty(Vocabulary.wasAttributedTo, this.elsewebUserAgent);
		this.sparqlQuery.addLiteral(Vocabulary.hasQueryText, this.query.toString());
		
		//link up query plan
		this.shareQueryPlan.addProperty(Vocabulary.wasGeneratedBy, this.generateQueryPlanActivity);
		
		//link up execute query plan activity
		this.executeQueryPlanActivity.addProperty(Vocabulary.wasAssociatedWith, this.shareClientQueryPlanExecutorAgent);
		this.executeQueryPlanActivity.addProperty(Vocabulary.wasInformedBy, this.generateQueryPlanActivity);
		this.executeQueryPlanActivity.addProperty(Vocabulary.used, this.shareQueryPlan);
		
		//link up query plan association
		this.queryPlanAssociation.addProperty(Vocabulary.agent, this.shareClientQueryPlanExecutorAgent);
		this.queryPlanAssociation.addProperty(Vocabulary.hadPlan, this.shareQueryPlan);
	}
	
	
	private void initializeResource(){
		//initialize activities (these have dynamic URIs based on execution instance)
		this.executeQueryPlanActivity = this.queryPlanModel.createResource(resourceURI.getURI("executeQueryPlan").toASCIIString(), Vocabulary.ExecuteQueryPlanActivity);
		this.generateQueryPlanActivity = this.queryPlanModel.createResource(resourceURI.getURI("generateQueryPlan").toASCIIString(), Vocabulary.GenerateQueryPlanActivity);
		
		//initialize association (this URI is dynamic based on execution instance)
		this.queryPlanAssociation = this.queryPlanModel.createResource(resourceURI.getURI("queryPlanAssociation").toASCIIString(), Vocabulary.QueryPlanAssociation);
		
		//initialize query plan and sparql query (these are dynamic based on execution instance)
		this.shareQueryPlan = this.queryPlanModel.createResource(resourceURI.getURI("queryPlan").toASCIIString(), Vocabulary.SHAREQueryPlan);
		this.sparqlQuery = this.queryPlanModel.createResource(resourceURI.getURI("sparqlQuery").toASCIIString(), Vocabulary.SPARQLQuery);
			
		//initialize agents (these have static URIs)
		this.shareClientQueryPlannerAgent = this.queryPlanModel.createResource(resourceURI.getPreditableURI("queryPlanner").toASCIIString(), Vocabulary.SHAREClientQueryPlanner);
		this.shareClientQueryPlanExecutorAgent = this.queryPlanModel.createResource(resourceURI.getPreditableURI("queryPlanExecutor").toASCIIString(), Vocabulary.SHAREClientQueryPlanExecutor);
		this.elsewebUserAgent = this.queryPlanModel.createResource(resourceURI.getPreditableURI("nicholas-del-rio").toASCIIString(), Vocabulary.ELSEWebUser);
	}
	
	public void addServiceExecution(Service service, Collection<Resource> inputs, Model outputs, Calendar startTime, Calendar endTime){
		ServiceExecution execution = new ServiceExecution(service, startTime, endTime, this.queryPlanModel);
		
		//the invoke service activities invoked by the execute query plan activity
		List<Resource> invokeSADIServiceActivities = execution.getInvokeServiceActivity(inputs, outputs);
		for(Resource invokeSADIServiceActivity : invokeSADIServiceActivities)
			invokeSADIServiceActivity.addProperty(Vocabulary.wasInvokedBy, this.shareClientQueryPlanExecutorAgent);
		
		//This is a hack.  We are adding many duplicate triples unnecessarily, but because I don't know where in the code the execution ends,
		//I have to do this hack.
		
		dump();
	}
	
	private void dump(){
		StringWriter wtr = new StringWriter();
		this.queryPlanModel.write(wtr, "TURTLE");
		
		ELSEWebProvenanceNamedGraph.getInstance().postProvenance(wtr.toString(), "elseweb", "elseweb1");
	}
}
