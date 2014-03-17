package ca.wilkinsonlab.cardioshare.test;

import org.junit.Test;

public class TestPredicates_DemoCluster extends TestPredicatesSkeleton {

	
	// GO => GO
	@Test
	public void testPred_hasAncestorTerm()
	{
		testPredicate(exampleGOTermURI, PREFIX_SHARE + "hasAncestorTerm");
	}

	// GO => GO
	@Test
	public void testPred_hasDescendantTerm()
	{
		testPredicate(exampleGOTermURI, PREFIX_SHARE + "hasDescendantTerm");
	}

	// GO => GO
	@Test
	public void testPred_hasParentTerm()
	{
		testPredicate(exampleGOTermURI, PREFIX_SHARE + "hasParentTerm");
	}

	// GO => GO
	@Test
	public void testPred_hasChildTerm()
	{
		testPredicate(exampleGOTermURI, PREFIX_SHARE + "hasChildTerm");
	}

	@Test
	public void testPred_belongsToOrganism()
	{
		testPredicate(exampleUniProtURI, PREFIX_SHARE + "belongsToOrganism");
	}

	@Test
	public void testPred_hasDescription()
	{
		testPredicate(exampleUniProtURI, PREFIX_SHARE + "hasDescription");
	}

	@Test
	public void testPred_hasProteinName()
	{
		testPredicate(exampleUniProtURI, PREFIX_SHARE + "hasProteinName");
	}
	
	@Test
	public void testPred_isPaperAboutProtein()
	{
		testPredicate(examplePubMedURI, PREFIX_SHARE + "isPaperAboutProtein"); 
	}
	
	@Test
	public void testPred_isKeywordFor()
	{
		testPredicate(exampleKeywordURI2, PREFIX_SHARE + "isKeywordFor");
	}

	@Test
	public void testPred_hasGOTerm()
	{
		testPredicate(exampleUniProtURI, PREFIX_SHARE + "hasGOTerm");
	}
	
	@Test
	public void testPred_hasProteinSequence()
	{
		testPredicate(exampleUniProtURI, PREFIX_SHARE + "hasProteinSequence");
	}

	@Test
	public void testPred_codedByGene()
	{
		testPredicate(exampleUniProtURI2, PREFIX_SHARE + "codedByGene");
	}
	
	@Test
	public void testPred_codesForProtein()
	{
		testPredicate(exampleKEGGURI, PREFIX_SHARE + "codesForProtein");
	}
	
	@Test
	public void testPred_has3DStructure()
	{
		testPredicate(exampleUniProtURI3, PREFIX_SHARE + "has3DStructure");
	}
	
	@Test
	public void testPred_is3DStructureFor()
	{
		testPredicate(examplePDBURI, PREFIX_SHARE + "is3DStructureFor");
	}

	@Test 
	public void testPred_belongsToPathway()
	{
		testPredicate(exampleKEGGURI, PREFIX_SHARE + "belongsToPathway");
	}

	@Test 
	public void testPred_hasPathwayGene()
	{
		testPredicate(exampleKEGGPathwayURI, PREFIX_SHARE + "hasPathwayGene");
	}
	
	@Test 
	public void testPred_hasProteinMotif()
	{
		testPredicate(exampleUniProtURI3, PREFIX_SHARE + "hasProteinMotif");
	}
	
	@Test 
	public void testPred_hasTermName()
	{
		testPredicate(exampleGOTermURI, PREFIX_SHARE + "hasTermName");
	}
	
	@Test 
	public void testPred_creatinineSlopeInverse()
	{
		testPredicate(exampleSlopeURI, PREFIX_SHARE + "creatinineSlopeInverse");
	}

	@Test 
	public void testPred_latestCreatinine()
	{
		testPredicate(examplePatientURI, PREFIX_SHARE + "latestCreatinine");
	}

	@Test 
	public void testPred_latestBUN()
	{
		testPredicate(examplePatientURI, PREFIX_SHARE + "latestBUN");
	}

}
