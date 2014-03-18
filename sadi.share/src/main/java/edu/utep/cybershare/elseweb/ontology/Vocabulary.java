package edu.utep.cybershare.elseweb.ontology;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Vocabulary {
	
	private static final Model model = ModelFactory.createDefaultModel();
	
	private static final String service = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-service.owl#";
	private static final String provo = "http://www.w3.org/ns/prov#";
	private static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	//rdf properties
	public static final Property type = model.createProperty(rdf + "type");
	
	//service agents
	public static final Resource SADIService = model.createResource(service + "SADIService");
	public static final Resource SHAREClient = model.createResource(service + "SHAREClient");
	public static final Resource ELSEWebUser = model.createResource(service + "ELSEWebUser");

	//service activities
	public static final Resource SADIServiceExecutionActivity = model.createResource(service + "SADIServiceExecutionActivity");
	public static final Resource InvokeServiceActivity = model.createResource(service + "InvokeServiceActivity");
	public static final Resource GenerateQueryPlanActivity = model.createResource(service + "GenerateQueryPlanActivity");
	public static final Resource ExecuteQueryPlanActivity = model.createResource(service + "ExecuteQueryPlanActivity");
	
	//service entities
	public static final Resource SparqlQuery = model.createResource(service + "SPARQLQuery");
	public static final Resource SADIServiceInput = model.createResource(service + "SADIServiceInput");
	public static final Resource SADIServiceOutput = model.createResource(service + "SADIServiceOutput");
	public static final Resource CardioSHAREQueryPlan = model.createResource(service + "CardioSHAREQueryPlan");
	
	//provo classes
	public static final Property Association = model.createProperty(provo + "Association");	
	
	//provo properties
	public static final Property wasAssociatedWith = model.createProperty(provo + "wasAssociatedWith");
	public static final Property startedAtTime = model.createProperty(provo + "startedAtTime");
	public static final Property endedAtTime = model.createProperty(provo + "endedAtTime");
	public static final Property agent = model.createProperty(provo + "agent");
	public static final Property hadPlan = model.createProperty(provo + "hadPlan");
	
	//service properties
	public static final Property hadInput = model.createProperty(service + "hadInput");
	public static final Property wasOutputBy = model.createProperty(service + "wasOutputBy");
	public static final Property wasInvokedBy = model.createProperty(service + "wasInvokedBy");
}
