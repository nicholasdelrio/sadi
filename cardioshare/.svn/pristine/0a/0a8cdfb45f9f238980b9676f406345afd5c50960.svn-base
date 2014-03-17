package ca.wilkinsonlab.cardioshare.test;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import ca.wilkinsonlab.cardioshare.CardioSHAREQueryClient;
import ca.wilkinsonlab.sadi.share.SHAREQueryClient;
import ca.wilkinsonlab.sadi.test.ExampleQueries;

import com.hp.hpl.jena.ontology.OntDocumentManager;

public class SingleQueryTest
{
	private static final Logger log = Logger.getLogger( SingleQueryTest.class );
	
	private static final String OUTPUT_FILENAME = "/tmp/SingleQueryTest.rdf";
	
	public static void main(String[] args) throws Exception
	{
		OntDocumentManager.getInstance().setCacheModels(true);
		
		String query = ExampleQueries.getQueryByHtmlListIndex(13);
		
//		query = 
//			"PREFIX pred: <http://sadiframework.org/ontologies/predicates.owl#> " +
//			"PREFIX uniprot: <http://lsrn.org/UniProt:> " +
//			"SELECT ?name " +
//			"WHERE { " +
//				"uniprot:P15923 pred:hasName ?name " +
//			"}";
		
//		query = 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX patients: <http://sadiframework.org/ontologies/patients.owl#> " +
//			"PREFIX pred: <http://sadiframework.org/ontologies/predicates.owl#> " +
//			"SELECT ?patient ?bun ?creat " +
//			"FROM <http://sadiframework.org/ontologies/patients.rdf> " +
//			"WHERE { " +
//				"?patient rdf:type patients:ElevatedCreatininePatient . " +
//				"?patient pred:latestBUN ?bun . " +
//				"?patient pred:latestCreatinine ?creat . " +
//			"}";
		
//		query = 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX patients: <http://biordf.net/cardioSHARE/patients.owl#> " +
//			"PREFIX bmi: <http://sadiframework.org/examples/bmi.owl#> " +
//			"SELECT ?patient ?bmi " +
//			"FROM <http://biordf.net/cardioSHARE/patients.rdf> " +
//			"WHERE { " +
//			"   ?patient rdf:type patients:AtRiskPatient . " +
//			"	?patient bmi:BMI ?bmi " + 
//			"}";
		
//		query = 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX ss: <http://semanticscience.org/resource/> " +
//			"select ?s ?value " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl> " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinski_test.rdf> " +
//			"where { " +
//			"  ?s rdf:type <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl#lipinskismilesmolecule> . " +
//			"  ?s ss:SIO_000008 ?attr . " +
//			"  ?attr rdf:type ss:CHEMINF_000251 . " +
//			"  ?attr ss:SIO_000300 ?value . " +
//			"}";
		
//		query = 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX ss: <http://semanticscience.org/resource/> " +
//			"PREFIX lso: <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl#> " +
//			"SELECT ?s ?value " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl> " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinski_test.rdf> " +
//			"WHERE { " +
//			" ?s rdf:type lso:smilesmolecule . " +
//			" ?s lso:hasChemicalDescriptor ?attr . " +
//			" ?attr rdf:type ss:CHEMINF_000244 . " +
//			" ?attr ss:SIO_000300 ?value . " +
//			"}";
		
//		query = 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX ss: <http://semanticscience.org/resource/> " +
//			"SELECT ?s ?value " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinski_test.rdf> " +
//			"WHERE { " +
//			"  ?s ss:SIO_000008 ?attr . " +
//			"  ?attr rdf:type ss:CHEMINF_000245 . " +
//			"  ?attr ss:SIO_000300 ?value . " +
//			"}";
		
//		query =
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX lm: <http://bio2rdf.org/lipidmaps:> " +
//			"PREFIX sio: <http://semanticscience.org/resource/> " +
//			"PREFIX lmo: <http://unbsj.biordf.net/lipids/lipid-maps-ontology.owl#> " +
//			"PREFIX lco: <http://unbsj.biordf.net/lipids/lipid-classification-service-ontology.owl#> " +
//			"SELECT DISTINCT ?fg " +
//			"FROM <http://unbsj.biordf.net/lipids/service-data/LMFA03010001.rdf> " +
//			"WHERE { " +
//			"    <http://semanticscience.org/LMFA03010001>  sio:SIO_000053 ?fg . " +
//			"}";
		
//		query =
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//			"PREFIX ss: <http://semanticscience.org/resource/> " +
//			"PREFIX lso: <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl#> " +
//			"select ?s " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinskiserviceontology.owl> " +
//			"FROM <http://semanticscience.org/sadi/ontology/lipinski_test.rdf> " +
//			"where { " +
//			"	?s rdf:type lso:lipinskismilesmolecule . " +
//			"}";
		
//		query =
//			"PREFIX cardio: <http://es-01.chibi.ubc.ca/~soroush/framingham/cardiorisk.owl#>\n" + 
//			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
//			"PREFIX galen: <http://www.co-ode.org/ontologies/galen#>\n" + 
//			"PREFIX sio: <http://semanticscience.org/resource/>\n" + 
//			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
//			"\n" + 
//			"SELECT ?pressure1 ?val ?unit FROM <file:///Users/luke/Downloads/SingleQueryTest.rdf> \n" + 
//			"WHERE { \n" + 
//			"    ?pressure1 rdf:type cardio:SystolicBloodPressurex .\n" +
////			"    ?pressure1 rdf:type cardio:SystolicBloodPressurex FILTER regex(str(?pressure1), \"inch\") .\n" + 
//			"    ?pressure1 sio:SIO_000216 ?measurement1 .\n" + 
//			"    ?measurement1 sio:SIO_000300 ?val .\n" + 
//			"    ?measurement1 sio:SIO_000221 ?unit .\n" + 
////			"    ?measurement1 sio:SIO_000221 ?unit FILTER regex(str(?unit), \"milli\") .\n" + 
//			"    ?measurement1 sio:SIO_000221 cardio:milli-meter-of-mercury-column .\n" +
//			"}";
		
		log.info( String.format("executing query\n%s", query) );
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		SHAREQueryClient client = new CardioSHAREQueryClient();
//		QueryClient client = new ServletClient("http://localhost:8080/cardioSHARE/query");
		List<Map<String, String>> results = client.synchronousQuery(query);
		
		stopWatch.stop();
		
		StringBuffer buf = new StringBuffer("query finished in ");
		buf.append( DurationFormatUtils.formatDurationHMS(stopWatch.getTime()) );
		if (results.isEmpty())
			buf.append("\nno results");
		else
			for (Map<String, String> binding: results) {
				buf.append("\n");
				buf.append(binding);
			}
		log.info( buf.toString() );
		
		try {
			client.getDataModel().write(new FileOutputStream(OUTPUT_FILENAME));
		} catch (Exception e) {
			log.error( String.format("error writing to %s: %s", OUTPUT_FILENAME, e) );
		}
	}
}
