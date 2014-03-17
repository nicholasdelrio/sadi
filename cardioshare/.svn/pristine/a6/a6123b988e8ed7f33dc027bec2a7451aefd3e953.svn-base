package ca.wilkinsonlab.cardioshare;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import ca.wilkinsonlab.sadi.share.SHAREKnowledgeBase;
import ca.wilkinsonlab.sadi.share.SHAREQueryClient;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CardioSHAREQueryClient extends SHAREQueryClient
{
	public static final Logger log = Logger.getLogger(CardioSHAREQueryClient.class);
	
	public static final boolean USE_PELLET = Config.getConfiguration().getBoolean(Config.USE_PELLET_KEY);
	
	public CardioSHAREQueryClient()
	{
		// don't allow ARQ SPARQL syntax extensions if we're using Pellet
		super( new SHAREKnowledgeBase(createReasoningModel(), (!USE_PELLET && Config.getConfiguration().getBoolean(ALLOW_ARQ_SYNTAX_CONFIG_KEY, true)))); 
	}

	private static OntModel createReasoningModel()
	{
		if (USE_PELLET) {
			log.debug("using Pellet reasoner in SHARE knowledge base");
			return ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		} else {
			return ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF );
		}
	}
	
	@Override
	protected QueryRunner getQueryRunner(String query, QueryClientCallback callback)
	{
		return new CardioSHAREQueryRunner(query, callback);
	}
	
	public class CardioSHAREQueryRunner extends SHAREQueryRunner
	{
		public CardioSHAREQueryRunner(String query, QueryClientCallback callback)
		{
			super(query, callback);
		}
		
		@Override
		protected QueryExecution getQueryExecution(String query, Model model)
		{
			if (USE_PELLET) {
				log.debug("using Pellet query execution environment");
				return SparqlDLExecutionFactory.create(QueryFactory.create(query), model);
			} else {
				return super.getQueryExecution(query, model);
			}
		}
	}
}
