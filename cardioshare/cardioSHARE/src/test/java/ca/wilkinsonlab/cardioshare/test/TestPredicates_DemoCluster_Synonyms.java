package ca.wilkinsonlab.cardioshare.test;

import org.junit.Test;

public class TestPredicates_DemoCluster_Synonyms extends TestPredicatesSkeleton {
	
	// Synonym: SHARE:isPaperAboutProtein
	@Test
	public void testPred_isReferenceFor()
	{
		testPredicate(examplePubMedURI, PREFIX_ONT + "isReferenceFor"); 
	}
	
	// Synonym: SHARE:belongsToPathway
	@Test
	public void testPred_isParticipantIn()
	{
		testPredicate(exampleKEGGURI, PREFIX_ONT + "isParticipantIn"); 
	}

	// Synonym: SHARE:hasPathwayGene
	@Test
	public void testPred_hasParticipant()
	{
		testPredicate(exampleKEGGPathwayURI, PREFIX_ONT + "hasParticipant"); 
	}

	// Synonym: SHARE:codesForProtein
	@Test
	public void testPred_encodes()
	{
		testPredicate(exampleKEGGURI, PREFIX_SHARE + "encodes"); 
	}

	// Synonym: SHARE:codedByGene
	@Test
	public void testPred_isEncodedBy()
	{
		testPredicate(exampleKEGGURI, PREFIX_SHARE + "isEncodedBy"); 
	}
	
	// Synonym: SHARE:hasProteinMotif
	@Test
	public void testPred_hasMotif()
	{
		testPredicate(exampleUniProtURI3, PREFIX_SHARE + "hasMotif"); 
	}

	// Synonym: SHARE:hasProteinName
	@Test
	public void testPred_hasName()
	{
		testPredicate(exampleUniProtURI3, PREFIX_SHARE + "hasName"); 
	}

	// Synonym: SHARE:hasProteinSequence
	@Test
	public void testPred_hasSequence()
	{
		testPredicate(exampleUniProtURI3, PREFIX_SHARE + "hasSequence"); 
	}

	// Synonym: SHARE:isKeywordFor
	@Test
	public void testPred_isTagOf()
	{
		testPredicate(exampleKeywordURI2, PREFIX_ONT + "isTagOf"); 
	}
	
}
