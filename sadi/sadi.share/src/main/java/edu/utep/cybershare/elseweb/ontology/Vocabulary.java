package edu.utep.cybershare.elseweb.ontology;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Vocabulary {
	
	private static final Model model = ModelFactory.createDefaultModel();
	
	private static final String modelling = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-modelling.owl#";
	private static final String lifemapper = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-lifemapper.owl#";
	private static final String edac = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-edac.owl#";
	private static final String data = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-data.owl#";
	private static final String service = "http://ontology.cybershare.utep.edu/ELSEWeb/elseweb-service.owl#";
	
	//service classes
	public static final Resource SADIService = model.createResource(service + "SADIService");
	public static final Resource SADIActivity = model.createResource(service + "SADIActivity");
	public static final Resource ExecuteWCSService = model.createResource(service + "ExecuteWCSService");
	public static final Resource ExtractPayload = model.createResource(service + "ExtractPayload");
	public static final Resource GenerateSpecification = model.createResource(service + "GenerateSpecification");
	public static final Resource SubmitToLifemapper = model.createResource(service + "SubmitToLifemapper");

	//provo properties
	public static final Property hadInput = model.createProperty(service + "hadInput");
	public static final Property wasOutputBy = model.createProperty(service + "wasOutputBy");
	public static final Property wasAssociatedWith = model.createProperty(service + "wasAssociatedWith");
	
		
	//data Classes
	public static final Resource WCSResponseManifestation = model.createResource(data + "WCSResponseManifestation");
	
	//data Properties
	public static final Property hasManifestation = model.createProperty(data + "hasManifestation");
	public static final Property hasFileDownloadURL = model.createProperty(data + "hasFileDownloadURL");
	public static final Property hasCapabilitiesDocumentURL = model.createProperty(data + "hasCapabilitiesDocumentURL");
	public static final Property coversRegion = model.createProperty(data + "coversRegion");
	public static final Property hasLeftLongitude = model.createProperty(data + "hasLeftLongitude");
	public static final Property hasRightLongitude = model.createProperty(data + "hasRightLongitude");
	public static final Property hasLowerLatitude = model.createProperty(data + "hasLowerLatitude");
	public static final Property hasUpperLatitude = model.createProperty(data + "hasUpperLatitude");
	
	//EDAC Properties
	public static final Property hasJSONCapabilitiesDigestURL = model.createProperty(edac + "hasJSONCapabilitiesDigestURL");
	
	// Lifemapper Classes
	public static final Resource WCSResponse = model.createResource(lifemapper + "WCSResponse");
	public static final Resource UnpostedScenario = model.createResource(lifemapper + "UnpostedScenario");
	public static final Resource TIFFManifestation = model.createResource(lifemapper + "TIFFManifestation");
	public static final Resource TIFFDataset = model.createResource(lifemapper + "TIFFDataset");
	public static final Resource ExperimentResult = model.createResource(lifemapper + "ExperimentResult");
	public static final Resource ExecutableExperimentSpecification = model.createResource(lifemapper + "ExecutableExperimentSpecification");
	public static final Resource LifemapperAlgorithm = model.createResource(lifemapper + "LifemapperAlgorithm");
	public static final Resource LifemapperParameter = model.createResource(lifemapper + "LifemapperParameter");
	public static final Resource SpeciesOccurrenceSet = model.createResource(lifemapper + "SpeciesOccurrenceSet");
	
	//Lifemapper Properties
	public static final Property specifiesModellingScenario = model.createProperty(lifemapper + "specifiesModellingScenario");
	public static final Property specifiesModellingAlgorithm = model.createProperty(lifemapper + "specifiesModellingAlgorithm");
	public static final Property specifiesOccurrenceSet = model.createProperty(lifemapper + "specifiesOccurrenceSet");
	
	public static final Property isModellingScenarioOf = model.createProperty(lifemapper + "isModellingScenarioOf");
	public static final Property hasAlgorithmCode = model.createProperty(lifemapper + "hasAlgorithmCode");
	public static final Property hasWCSResponse = model.createProperty(lifemapper + "hasWCSResponse");
	public static final Property hasTypeCode = model.createProperty(lifemapper + "hasTypeCode");
	public static final Property hasLayerUnits = model.createProperty(lifemapper + "hasLayerUnits");
	public static final Property hasExtractedPayload = model.createProperty(lifemapper + "hasExtractedPayload");
	public static final Property hasJSONExperimentSpecificationURL = model.createProperty(lifemapper + "hasJSONExperimentSpecificationURL");
	public static final Property hasExperimentResult = model.createProperty(lifemapper + "hasExperimentResult");
	public static final Property hasExperimentResultURL = model.createProperty(lifemapper + "hasExperimentResultURL");
	public static final Property hasOccurrenceSetID = model.createProperty(lifemapper + "hasOccurrenceSetID");
	public static final Property hasInputWCSDataset = model.createProperty(lifemapper + "hasInputWCSDataset");
	public static final Property hasScenarioLayer = model.createProperty(lifemapper + "hasScenarioLayer");
	public static final Property isScenarioLayerOf = model.createProperty(lifemapper + "isScenarioLayerOf");	
	
	// Modelling Properties
	public static final Property behaviorControlledBy = model.createProperty(modelling + "behaviorControlledBy");
	public static final Property hasParameterMember = model.createProperty(modelling + "hasParameterMember");
	public static final Property boundToValue = model.createProperty(modelling + "boundToValue");
	public static final Property hasParameterName = model.createProperty(modelling + "hasParameterName");

}
