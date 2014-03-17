package ca.wilkinsonlab.cardioshare.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import ca.wilkinsonlab.sadi.SADIException;
import ca.wilkinsonlab.sadi.utils.OwlUtils;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ReasonerTests
{
	private static final Logger log = Logger.getLogger( ReasonerTests.class );

	private static final String ONTOLOGY_PREFIX = "http://test.ontology";

	@SuppressWarnings("serial")
	public static void main(String[] args)
	{
		Map<OntModelSpec, String> specNames = new HashMap<OntModelSpec,String>() {{
			put(PelletReasonerFactory.THE_SPEC, "PelletReasonerFactory.THE_SPEC");
//			put(OntModelSpec.OWL_DL_MEM_RULE_INF, "OntModelSpec.OWL_DL_MEM_RULE_INF");
//			put(OntModelSpec.OWL_MEM_TRANS_INF, "OntModelSpec.OWL_MEM_TRANS_INF");
//			put(OntModelSpec.OWL_MEM_RDFS_INF, "OntModelSpec.OWL_MEM_RDFS_INF");
//			put(OntModelSpec.OWL_MEM_RULE_INF, "OntModelSpec.OWL_MEM_RULE_INF");
			put(OntModelSpec.OWL_MEM_MINI_RULE_INF, "OntModelSpec.OWL_MEM_MINI_RULE_INF");
			put(OntModelSpec.OWL_MEM_MICRO_RULE_INF, "OntModelSpec.OWL_MEM_MICRO_RULE_INF");
		}};

		for (OntModelSpec spec: specNames.keySet()) {
			log.info("running tests for " + specNames.get(spec));
			OntModel ontModel = ModelFactory.createOntologyModel(spec);

			// comment out these lines if you want to skip running certain ontology tests
			OntModelTest tests[] = new OntModelTest[] {
					new PropertyRangeTest(ontModel),
					new InversePropertiesTest(ontModel),
					new EquivalentPropertiesTest(ontModel),
					new TransitiveEquivalentPropertiesTest(ontModel),
					new MultipleEquivalentPropertiesTest(ontModel),
					new InferredInversePropertiesTest(ontModel),
					new InferredSubpropertiesFromEquivalentPropertiesTest(ontModel),
					new OneOfClassTest(ontModel),
					new OneOfAndComplementOfClassTest(ontModel),
					new OneOfAndDisjointWithClassTest(ontModel)
			};

			for(OntModelTest test : tests) {
				log.info("running " + test);
				if(test.runTest()) {
					log.info(test + ": PASSED");
				} else {
					log.info(test + ": FAILED");
				}
			}

		}
	}

	abstract public static class OntModelTest {

		OntModel ontModel;
		String testName;

		public OntModelTest(OntModel ontModel, String testName) {
			setOntModel(ontModel);
			setTestName(testName);
		}

		abstract public boolean runTest();

		public OntModel getOntModel() {
			return ontModel;
		}
		public void setOntModel(OntModel ontModel) {
			this.ontModel = ontModel;
		}
		public String getTestName() {
			return testName;
		}
		public void setTestName(String testName) {
			this.testName = testName;
		}
		public String toString() {
			return getTestName();
		}
	}

	public static class InversePropertiesTest extends OntModelTest {

		public InversePropertiesTest(OntModel ontModel) {
			super(ontModel, "inverse properties test");
		}

		public boolean runTest() {
			OntProperty belongsToPathway;
			try {
				belongsToPathway = OwlUtils.getOntPropertyWithLoad(getOntModel(), "http://sadiframework.org/ontologies/predicates.owl#belongsToPathway");
				OntProperty inverseProperty = belongsToPathway.getInverse();
				return inverseProperty != null;
			} catch (SADIException e) {
				log.error(e.getMessage());
				return false;
			}
		}
	}

	public static class PropertyRangeTest extends OntModelTest {

		public PropertyRangeTest(OntModel ontModel) {
			super(ontModel, "property range test");
		}

		public boolean runTest() {
			OntProperty p;
			try {
				p = OwlUtils.getOntPropertyWithLoad(getOntModel(), "http://sadiframework.org/ontologies/predicates.owl#has3DStructure");
				return p.getRange().getURI().equals("http://purl.oclc.org/SADI/LSRN/PDB_Record");
			} catch (SADIException e) {
				log.error(e.getMessage());
				return false;
			}
		}
	}

	public static class EquivalentPropertiesTest extends OntModelTest {

		public EquivalentPropertiesTest(OntModel ontModel) {
			super(ontModel, "equivalent properties test");
		}

		public boolean runTest() {

			OntModel model = getOntModel();

			OntProperty a = model.createOntProperty("a");
			OntProperty b = model.createOntProperty("b");

			a.setEquivalentProperty(b);

			boolean hasB = false;
			for(Iterator<? extends OntProperty> i = a.listEquivalentProperties(); i.hasNext(); ) {
				OntProperty synonym = i.next();
				if(synonym.equals(b)) {
					hasB = true;
				}
			}

			return hasB;
		}
	}

	public static class TransitiveEquivalentPropertiesTest extends OntModelTest {

		public TransitiveEquivalentPropertiesTest(OntModel ontModel) {
			super(ontModel, "transitive equivalent properties test");
		}

		public boolean runTest() {

			OntModel model = getOntModel();

			OntProperty a = model.createOntProperty("a");
			OntProperty b = model.createOntProperty("b");
			OntProperty c = model.createOntProperty("c");

			a.setEquivalentProperty(b);
			b.setEquivalentProperty(c);

			boolean hasC = false;
			for(Iterator<? extends OntProperty> i = a.listEquivalentProperties(); i.hasNext(); ) {
				OntProperty synonym = i.next();
				if(synonym.equals(c)) {
					hasC = true;
				}
			}

			return hasC;
		}
	}

	public static class MultipleEquivalentPropertiesTest extends OntModelTest {

		public MultipleEquivalentPropertiesTest(OntModel ontModel) {
			super(ontModel, "multiple equivalent properties test");
		}

		public boolean runTest() {

			OntModel model = getOntModel();

			OntProperty a = model.createOntProperty("a");
			OntProperty b = model.createOntProperty("b");
			OntProperty c = model.createOntProperty("c");

			a.addEquivalentProperty(b);
			a.addEquivalentProperty(c);

			boolean hasB = false;
			boolean hasC = false;

			for(Iterator<? extends OntProperty> i = a.listEquivalentProperties(); i.hasNext(); ) {
				OntProperty synonym = i.next();
				if(synonym.equals(b)) {
					hasB = true;
				} else if(synonym.equals(c)) {
					hasC = true;
				}
			}

			return (hasB && hasC);
		}
	}

	public static class InferredInversePropertiesTest extends OntModelTest {

		public InferredInversePropertiesTest(OntModel ontModel) {
			super(ontModel, "inferred inverse properties test");
		}

		@Override
		public boolean runTest() {

			OntModel model = getOntModel();

			/*
			 * Ontology contents:
			 *
			 * => property A is equivalent to property B
			 * => property A has inverse property C
			 * => property B has inverse property D
			 *
			 * Test: Is property D inferred to be an inverse of property A?
			 */


			String ontologyFile = InferredInversePropertiesTest.class.getResource("inferred.inverse.properties.test.owl").toString();
			model.read(ontologyFile, ONTOLOGY_PREFIX, "RDF/XML");

			OntProperty propertyA = model.getOntProperty(ONTOLOGY_PREFIX + "#A");
			OntProperty propertyD = model.getOntProperty(ONTOLOGY_PREFIX + "#D");

			for(OntProperty inverse : propertyA.listInverse().toList()) {
				if(inverse.equals(propertyD)) {
					return true;
				}
			}

			return false;
		}
	}

	public static class InferredSubpropertiesFromEquivalentPropertiesTest extends OntModelTest {

		public InferredSubpropertiesFromEquivalentPropertiesTest(OntModel ontModel) {
			super(ontModel, "inferred subproperties from equivalent properties test");
		}

		@Override
		public boolean runTest() {

			OntModel model = getOntModel();

			/*
			 * Ontology contents:
			 *
			 * => property A is equivalent to property B
			 * => property C is a subproperty of property B
			 *
			 * Test:
			 *
			 * => Is property B a subproperty of property A?
			 * => Is property C a subproperty of property A?
			 */


			String ontologyFile = InferredSubpropertiesFromEquivalentPropertiesTest.class.getResource("inferred.subproperties.from.equivalent.properties.test.owl").toString();
			model.read(ontologyFile, ONTOLOGY_PREFIX, "RDF/XML");

			OntProperty propertyA = model.getOntProperty(ONTOLOGY_PREFIX + "#A");
			OntProperty propertyB = model.getOntProperty(ONTOLOGY_PREFIX + "#B");
			OntProperty propertyC = model.getOntProperty(ONTOLOGY_PREFIX + "#C");

			boolean gotPropertyB = false;
			boolean gotPropertyC = false;

			for(OntProperty subproperty : propertyA.listSubProperties().toList()) {
				if(subproperty.equals(propertyB)) {
					gotPropertyB = true;
				} else if (subproperty.equals(propertyC)) {
					gotPropertyC = true;
				}
			}

			return (gotPropertyB && gotPropertyC);
		}
	}

	public static class OneOfClassTest extends OntModelTest {

		public OneOfClassTest(OntModel ontModel) {
			super(ontModel, "oneOf class test");
		}

		@Override
		public boolean runTest() {

			OntModel model = getOntModel();

			/*
			 * Ontology contents:
			 *
			 * => Class A is oneOf { individual A, individual A }
			 * => Individuals A, B, and C
			 *
			 * Expect:
			 *
			 * => Individuals A and B are instances of class A
			 * => Individual C is not a member of class A
			 */

			String ontologyFile = InferredSubpropertiesFromEquivalentPropertiesTest.class.getResource("oneof.class.test.owl").toString();
			model.read(ontologyFile, ONTOLOGY_PREFIX, "RDF/XML");

			OntClass classA = model.getOntClass(ONTOLOGY_PREFIX + "#ClassA");
			OntResource individualA = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualA");
			OntResource individualB = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualB");

			Set<OntResource> instances = new HashSet<OntResource>();

			for (OntResource instance : classA.listInstances().toList())
				instances.add(instance);

			if (instances.size() == 2 && instances.contains(individualA) && instances.contains(individualB))
				return true;
			else
				return false;

		}
	}


	public static class OneOfAndComplementOfClassTest extends OntModelTest {

		public OneOfAndComplementOfClassTest(OntModel ontModel) {
			super(ontModel, "oneOf/complementOf class test");
		}

		@Override
		public boolean runTest() {

			OntModel model = getOntModel();

			/*
			 * Ontology contents:
			 *
			 * => Class A is oneOf { individual A, individual B }
			 * => Class B is complementOf(Class A)
			 * => Individuals A, B, and C
			 *
			 * Expect:
			 *
			 * => Individuals A and B are instances of class A
			 * => Individual C is not a member of class A
			 * => Individual C is a member of class B
			 */

			String ontologyFile = InferredSubpropertiesFromEquivalentPropertiesTest.class.getResource("oneof.and.complementof.class.test.owl").toString();
			model.read(ontologyFile, ONTOLOGY_PREFIX, "RDF/XML");

			OntClass classA = model.getOntClass(ONTOLOGY_PREFIX + "#ClassA");
			OntClass classB = model.getOntClass(ONTOLOGY_PREFIX + "#ClassB");
			OntResource individualA = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualA");
			OntResource individualB = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualB");
			OntResource individualC = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualC");

			Set<OntResource> classAInstances = new HashSet<OntResource>();
			Set<OntResource> classBInstances = new HashSet<OntResource>();

			for (OntResource instance : classA.listInstances().toList())
				classAInstances.add(instance);

			for (OntResource instance : classB.listInstances().toList())
				classBInstances.add(instance);

			if (classAInstances.size() == 2 && classAInstances.contains(individualA) && classAInstances.contains(individualB)) {
				if (classBInstances.size() == 1 && classBInstances.contains(individualC))
					return true;
				else
					return false;
			}
			else
				return false;

		}
	}

	public static class OneOfAndDisjointWithClassTest extends OntModelTest {

		public OneOfAndDisjointWithClassTest(OntModel ontModel) {
			super(ontModel, "oneOf/disjointWith class test");
		}

		@Override
		public boolean runTest() {

			OntModel model = getOntModel();

			/*
			 * Ontology contents:
			 *
			 * => Class A is oneOf { individual A, individual B }
			 * => Class B is (disjointWith(Class A) and NamedIndividual)
			 * => Individuals A, B, and C
			 *
			 * Expect:
			 *
			 * => Individuals A and B are instances of class A
			 * => Individual C is not a member of class A
			 * => Individual C is a member of class B
			 */

			String ontologyFile = InferredSubpropertiesFromEquivalentPropertiesTest.class.getResource("oneof.and.complementof.class.test.owl").toString();
			model.read(ontologyFile, ONTOLOGY_PREFIX, "RDF/XML");

			OntClass classA = model.getOntClass(ONTOLOGY_PREFIX + "#ClassA");
			OntClass classB = model.getOntClass(ONTOLOGY_PREFIX + "#ClassB");
			OntResource individualA = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualA");
			OntResource individualB = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualB");
			OntResource individualC = model.getOntResource(ONTOLOGY_PREFIX + "#IndividualC");

			Set<OntResource> classAInstances = new HashSet<OntResource>();
			Set<OntResource> classBInstances = new HashSet<OntResource>();

			for (OntResource instance : classA.listInstances().toList())
				classAInstances.add(instance);

			for (OntResource instance : classB.listInstances().toList())
				classBInstances.add(instance);

			if (classAInstances.size() == 2 && classAInstances.contains(individualA) && classAInstances.contains(individualB)) {
				if (classBInstances.size() == 1 && classBInstances.contains(individualC))
					return true;
				else
					return false;
			}
			else
				return false;

		}
	}

}
