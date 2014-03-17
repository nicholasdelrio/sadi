package ca.wilkinsonlab.cardioshare;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtree.json.JSONWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import ca.wilkinsonlab.sadi.service.ServiceServlet;
import ca.wilkinsonlab.sadi.utils.ContentType;

public class DatasetDeliveryServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final Pattern N3_PATTERN = Pattern.compile(".*\\.(n3|ttl)", Pattern.CASE_INSENSITIVE);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Model model = ModelFactory.createDefaultModel();
		String lang = "RDF/XML";
		String path = request.getPathInfo();
		if (N3_PATTERN.matcher(path).matches()) {
			lang = "N3";
		}
		try {
			model.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(request.getPathInfo()), "", lang);
		} catch (NullPointerException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		model.removeAll(null, Vocab.rdf_type_inverse, null);
		
		ContentType contentType = ServiceServlet.getContentType(request);
		String callback = request.getParameter("callback");
		if (callback != null) {
			response.setContentType("application/javascript");
			StringWriter buf = new StringWriter();
			ContentType.RDF_XML.writeModel(model, buf, "");
			response.getWriter().format("%s(%s);", callback, 
					new JSONWriter().write(buf.toString()));
		} else {
			response.setContentType(contentType.getHTTPHeader());
			contentType.writeModel(model, response.getOutputStream(), "");
		}
	}
	
	private static final class Vocab
	{
		public static final Property rdf_type_inverse = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type-inverse");
	}
}
