package ca.wilkinsonlab.cardioshare.test;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.Map;

import ca.wilkinsonlab.cardioshare.CardioSHAREQueryClient;
import ca.wilkinsonlab.sadi.utils.SPARQLStringUtils;

public class TestPredicatesSkeleton {

	final String PREFIX_SHARE = "http://es-01.chibi.ubc.ca/~benv/predicates.owl#";
	final String PREFIX_UNIPROT = "http://uniprot/";
	final String PREFIX_MOBY = "http://moby/";
	final String PREFIX_NAMESPACE = "http://biordf.net/moby/";
	final String PREFIX_ONT = "http://ontology.dumontierlab.com/";
	
	final String exampleKeywordURI = PREFIX_NAMESPACE + "Global_Keyword/transcription";
	// "golgi lumen"
	// We want this to be a fairly obscure term, so we don't have to wait
	// to retrieve a huge list of results. -- B.V.
	final String exampleGOTermURI = PREFIX_NAMESPACE + "GO/0005796";
	// "Response to dietary excess"
	final String notInSeqHoundGOTermURI = PREFIX_NAMESPACE + "GO/0002021";
	
	final String exampleUniProtURI = PREFIX_NAMESPACE + "UniProt/O14733";
	final String exampleUniProtURI2 = PREFIX_NAMESPACE + "UniProt/P06744";
	final String exampleUniProtURI3 = PREFIX_NAMESPACE + "UniProt/P04637";
	final String examplePubMedURI = PREFIX_NAMESPACE + "PMID/14633995";
	final String exampleKeywordURI2 = PREFIX_NAMESPACE + "Global_Keyword/parkinson";
	final String exampleKEGGURI = PREFIX_NAMESPACE + "KEGG/hsa:2821";
	final String examplePDBURI = PREFIX_NAMESPACE + "PDB/3SAK";
	final String exampleKEGGPathwayURI = PREFIX_NAMESPACE + "KEGG_PATHWAY/hsa00010";
	final String examplePatientURI = PREFIX_NAMESPACE + "Dummy/496858";
	final String exampleSlopeURI = PREFIX_NAMESPACE + "keyword/increasing";

	protected void testPredicate(String subject, String predicate)
	{
		List<Map<String, String>> results = null;
		try
		{
			URL queryTemplate = TestPredicates_DemoCluster.class.getResource("/org/moby2/resources/SPARQL/test.predicate.template.sparql");
			String query = SPARQLStringUtils.strFromTemplate(queryTemplate, subject, predicate);

			printPredTestHeader(predicate, query);
			
			results = new CardioSHAREQueryClient().synchronousQuery(query);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// If we get any results, I call that success.
		assertTrue(results != null);
		assertTrue(!results.isEmpty());
		
		printPredTestSuccess(predicate);
	}
	
	protected void printPredTestHeader(String predicate, String query)
	{
		String header =
			"\n" +
			"-------------------------------------------------------------------\n" +
			"Testing predicate: " + predicate + "\n" +
			"-------------------------------------------------------------------\n" +
			"\n" +
			"Running test query:\n\n" +
			query + "\n\n";
		
		System.out.println(header);
	}
	
	protected void printPredTestSuccess(String predicate)
	{
		String success =
			"\n" +
			"--------------------------------------------------------------------\n" +
			"Predicate test for " + predicate + " PASSED\n" +
			"--------------------------------------------------------------------\n";
		
		System.out.println(success);
	}

}