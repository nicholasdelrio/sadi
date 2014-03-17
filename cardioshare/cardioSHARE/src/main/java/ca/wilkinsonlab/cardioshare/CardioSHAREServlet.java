package ca.wilkinsonlab.cardioshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.stringtree.json.JSONWriter;

import ca.wilkinsonlab.sadi.tasks.QueryTask;
import ca.wilkinsonlab.sadi.tasks.Task;
import ca.wilkinsonlab.sadi.tasks.TaskManager;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

@SuppressWarnings("serial")
public class CardioSHAREServlet extends HttpServlet
{
	private final static Logger log = Logger.getLogger(CardioSHAREServlet.class);

	private final static TaskManager taskManager = TaskManager.getInstance();

	private final static JSONWriter jsonWriter = new JSONWriter();

	public CardioSHAREServlet()
	{
		com.hp.hpl.jena.ontology.OntDocumentManager.getInstance().setCacheModels(true);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String query = request.getParameter("query");
		if (query != null) {
			log.debug("query submitted: " + query);

			QueryTask queryTask = new CardioSHAREQueryTask(query);
			String taskId = taskManager.startTask(queryTask);
			outputTaskId(taskId, response.getWriter());
		} else {
			String taskId = request.getParameter("poll");
			if (taskId != null) {
				Task queryTask = taskManager.getTask(taskId);
				if (queryTask == null) {
					log.warn("attempt to poll non-existant query taskId " + taskId);
					return;
				} else if (queryTask.isFinished()) {
					List<Map<String, String>> results = ((QueryTask)queryTask).getResults();
					try {
						writeResultModel(taskId, ((CardioSHAREQueryTask)queryTask).getDataModel());
					} catch (NoSuchElementException e) {
						// no results, no model...
					} catch (IOException e) {
						log.error("error writing result model to disk", e);
					}
					outputResults(results, queryTask.getError(), queryTask.getWarnings(), response.getWriter());
					taskManager.disposeTask(taskId);
				} else {
					String status = queryTask.getStatus();
					outputStatus(status, response.getWriter());
				}
			} else {
				/* display the query input page...
				 */
				getServletConfig().getServletContext().getRequestDispatcher("/index.html").forward(request, response);
			}
		}
	}

	private void writeResultModel(String taskId, Model model) throws IOException
	{
		String jenaFileName = String.format("%s/%s", Config.getConfiguration().getString("outputRDFPath"), taskId);
		FileOutputStream fos = new FileOutputStream(jenaFileName);
		model.write(fos);
		fos.close();
	}

	@SuppressWarnings("unused")
	private Model prune(OntModel resultModel, String value) throws IOException
	{
		File tmp = File.createTempFile("cardioSHARE", null);
		resultModel.write(new FileOutputStream(tmp));
		OntModel staticModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF );
		staticModel.read(new FileInputStream(tmp), "");

		Resource r = resultModel.getResource(value);
		if (staticModel.containsResource(r)) {
			return ResourceUtils.reachableClosure(r);
		} else {
			for (StmtIterator i = staticModel.listStatements(null, null, staticModel.createLiteral(value)); i.hasNext(); )
				return ResourceUtils.reachableClosure(i.nextStatement().getSubject());
		}
		return ModelFactory.createDefaultModel();

//		if (true)
//			return resultModel.getRawModel();
//
//		Model model = ModelFactory.createDefaultModel();
//
//		try {
//			File tmp = File.createTempFile("cardioSHARE", null);
//			resultModel.write(new FileOutputStream(tmp));
//			OntModel staticModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF );
//			staticModel.read(new FileInputStream(tmp), "");
//			OntClass OwlProperty = staticModel.getOntClass("http://www.w3.org/2002/07/owl#Property");
//			OntClass RdfsProperty = staticModel.getOntClass("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
//			for (Iterator i = staticModel.listIndividuals(); i.hasNext(); ) {
//				Individual individual = (Individual)i.next();
//				if (individual.hasOntClass(OWL.Class) ||
//					individual.hasOntClass(OWL.Ontology) ||
//					individual.hasOntClass(OwlProperty) ||
//					individual.hasOntClass(RdfsProperty) ||
//					individual.hasOntClass(OWL.AnnotationProperty) ||
//					individual.hasOntClass(OWL.ObjectProperty) ||
//					individual.hasOntClass(OWL.DatatypeProperty))
//					continue;
//				log.debug("adding properties of individual " + individual);
//				model.add(individual.listProperties());
//	//			for (Iterator j = individual.listOntClasses(true); j.hasNext(); ) {
//	//				OntClass clazz = (OntClass)j.next();
//	//				model.add(clazz, RDF.type, OWL.Class);
//	//			}
//			}
//		} catch (Exception e) {
//			log.error("Failed to prune result model", e);
//		}
//		return model;

//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		URI jenaUri = URI.create("file://" + jenaFileName);
//		OWLOntology ontology = manager.loadOntologyFromPhysicalURI(jenaUri);
//		URI manUri = URI.create(jenaUri + ".owl");
//		URI outUri = URI.create(jenaUri + ".rdf");
//		SimpleURIMapper mapper = new SimpleURIMapper(jenaUri, manUri);
//		manager.addURIMapper(mapper);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

	private void outputTaskId(String taskId, PrintWriter out)
	{
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("success", "true");
		jsonMap.put("taskId", taskId);
		out.println(jsonWriter.write(jsonMap));
	}

	private void outputResults(List<Map<String, String>> results, Throwable error, Collection<String> warnings, PrintWriter out)
	{
		List<String> variableNames = new ArrayList<String>();
		List<List<String>> bindings = new ArrayList<List<String>>();
		if (!results.isEmpty()) {
			/* extract the variable names from the results and sort them;
			 * the reason why will be apparent shortly...
			 */
			variableNames.addAll(results.get(0).keySet());
			Collections.sort(variableNames);

			/* convert each binding map to an ordered list of values, whose
			 * order matches that of the variable names in the list above...
			 * I'm positive this is unnecessary, since Ext is just going to
			 * redo what I've undone here, but I couldn't figure it out
			 * last night...
			 */
			for (Map<String, String> result: results) {
				List<String> binding = new ArrayList<String>(variableNames.size());
				for (String var: variableNames) {
					binding.add(result.get(var));
				}
				bindings.add(binding);
			}
		}

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("fields", variableNames);
		jsonMap.put("rows", bindings);
		jsonMap.put("error", error != null ? Collections.singletonList(error.toString()) : Collections.emptyList());
		jsonMap.put("warnings", warnings);
		out.println(jsonWriter.write(jsonMap));
	}

	private void outputStatus(String status, PrintWriter out)
	{
		out.println(jsonWriter.write(status));
	}
}
