package ca.wilkinsonlab.cardioshare;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import ca.wilkinsonlab.sadi.utils.http.HttpUtils;

public class PrecomputeDrugInteractions {

	public static void main(String[] argv) throws IOException {

		/*
		 * Fail fast if the cardioSHARE-medDB war isn't running
		 * on localhost:8080.  The war needs to be running
		 * so that the DrugDrugInteractionDiscovery is accessible
		 * at http://localhost:8080/DrugDrugInteractionDiscovery.
		 */

		URL serviceUrl = new URL("http://localhost:8080/DrugDrugInteractionDiscovery");
		try {
			HttpUtils.GET(serviceUrl);
		} catch(IOException e) {
			System.err.println(String.format("unable to contact %s; please deploy cardioSHARE-medDB war on localhost:8080 first", serviceUrl));
			System.exit(1);
		}

		/* pure OWL version */

		/*
		//Config.getConfiguration().setProperty("usePellet", true);
		CardioSHAREQueryClient client = new CardioSHAREQueryClient();
		URL medDB = PrecomputeDrugInteractions.class.getResource("/medDB.owl");
		String query = String.format("SELECT ?drug FROM <%s> WHERE { ?drug a <%s#DangerousDrug> }", medDB, medDB);
		*/

		/* SPARQL version */

		CardioSHAREQueryClient client = new CardioSHAREQueryClient();

		InputStream is = PrecomputeDrugInteractions.class.getResourceAsStream("/medDB.n3");
		client.getDataModel().read(is, "", "N3");
		is.close();

		is = PrecomputeDrugInteractions.class.getResourceAsStream("/precompute-interactions.sparql");
		String query = IOUtils.toString(is);
		is.close();

		List<Map<String,String>> results = client.synchronousQuery(query);
		for(Map<String,String> result : results)
			System.out.println(result);

		File outputFile = new File("src/main/resources/medDB-with-interactions.n3");
		OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
		client.getKB().getReasoningModel().writeAll(os, "N3", "");
		os.close();

	}

}
