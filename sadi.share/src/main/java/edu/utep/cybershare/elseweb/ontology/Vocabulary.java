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
	public static final Resource SHAREClientQueryPlanner = model.createResource(service + "SHAREClientQueryPlanner");
	public static final Resource SHAREClientQueryPlanExecutor = model.createResource(service + "SHAREClientQueryPlanExecutor");
	public static final Resource ELSEWebUser = model.createResource(service + "ELSEWebUser");

	//service activities
	public static final Resource SADIServiceExecutionActivity = model.createResource(service + "SADIServiceExecutionActivity");
	public static final Resource InvokeSADIServiceActivity = model.createResource(service + "InvokeSADIServiceActivity");
	public static final Resource GenerateQueryPlanActivity = model.createResource(service + "GenerateQueryPlanActivity");
	public static final Resource ExecuteQueryPlanActivity = model.createResource(service + "ExecuteQueryPlanActivity");
	
	//service entities
	public static final Resource SPARQLQuery = model.createResource(service + "SPARQLQuery");
	public static final Resource SADIServiceInput = model.createResource(service + "SADIServiceInput");
	public static final Resource SADIServiceOutput = model.createResource(service + "SADIServiceOutput");
	public static final Resource SHAREQueryPlan = model.createResource(service + "SHAREQueryPlan");
	
	//service associations
	public static final Property QueryPlanAssociation = model.createProperty(provo + "QueryPlanAssociation");	
	
	//provo properties
	public static final Property wasAssociatedWith = model.createProperty(provo + "wasAssociatedWith");
	public static final Property startedAtTime = model.createProperty(provo + "startedAtTime");
	public static final Property endedAtTime = model.createProperty(provo + "endedAtTime");
	public static final Property agent = model.createProperty(provo + "agent");
	public static final Property hadPlan = model.createProperty(provo + "hadPlan");
	public static final Property used = model.createProperty(provo + "used");
	public static final Property wasGeneratedBy = model.createProperty(provo + "wasGeneratedBy");
	public static final Property wasAttributedTo = model.createProperty(provo + "wasAttributedTo");
	public static final Property wasInformedBy = model.createProperty(provo + "wasInformedBy");
	
	//service properties
	public static final Property hadInput = model.createProperty(service + "hadInput");
	public static final Property wasOutputBy = model.createProperty(service + "wasOutputBy");
	public static final Property wasInvokedBy = model.createProperty(service + "wasInvokedBy");
	public static final Property hasQueryText = model.createProperty(service + "hasQueryText");
}
