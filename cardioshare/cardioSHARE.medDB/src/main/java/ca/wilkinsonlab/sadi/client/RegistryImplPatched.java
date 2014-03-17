package ca.wilkinsonlab.sadi.client;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import ca.wilkinsonlab.sadi.utils.QueryExecutorFactory;
import ca.wilkinsonlab.sadi.utils.RdfUtils;

/**
 * File-based SADI registry that is able to search the
 * CLASSPATH or retrieve the registry file from an URL.
 * Currently, RegistryImpl only allows system file
 * paths to locate the registry file, which is not
 * portable in the context of a servlet.
 */
public class RegistryImplPatched extends RegistryImpl
{
   public RegistryImplPatched(Configuration config) throws IOException
   {
       super(QueryExecutorFactory.createJenaModelQueryExecutor(createModel(config.getString("url"))));
   }
   private static Model createModel(String url) throws IOException
   {
       Model model = ModelFactory.createDefaultModel();
       RdfUtils.loadModelFromString(model, url, RegistryImplPatched.class);
       return model;
   }
}
