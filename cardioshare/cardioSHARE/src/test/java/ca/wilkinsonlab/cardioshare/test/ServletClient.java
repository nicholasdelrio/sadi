package ca.wilkinsonlab.cardioshare.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.stringtree.json.JSONReader;

import ca.wilkinsonlab.sadi.client.QueryClient;

public class ServletClient extends QueryClient
{
	public static String DEFAULT_SERVLET_URI = "http://biordf.net/cardioSHARE/query";
	
	private final static Logger LOGGER = Logger.getLogger(ServletClient.class);
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final int POLL_MAX = 720;
	private static final long POLL_DELAY = 5000;
	
	private URI servletUri;
	
	public ServletClient()
	{
		this(DEFAULT_SERVLET_URI);
	}
	
	public ServletClient(String servletUri)
	{
		this( URI.create(servletUri) );
	}
	
	public ServletClient(URI servletUri)
	{
		this.servletUri = servletUri;
	}
	
	@Override
	protected QueryRunner getQueryRunner(String query, QueryClientCallback callback)
	{
		return new ServletClientQueryRunner(query, callback);
	}
	
	private Object postToServlet(String data) throws IOException
	{
		URLConnection conn = servletUri.toURL().openConnection();

		LOGGER.trace("sending data to servlet");
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		wr.close();
	    
		LOGGER.trace("reading servlet response");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder buf = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null)
			buf.append(line);
		rd.close();

		// key names must be quoted to avoid an infinite loop in JSONReader.
		// MobyServlet ensures this by using JSONWriter to write all output, but be careful.
		LOGGER.trace("converting JSON object");
		return new JSONReader().read(buf.toString());
	}
	
	private static String getPostData(String key, String value)
	{
		StringBuilder buf = new StringBuilder();
		try {
			buf.append(URLEncoder.encode(key, DEFAULT_CHARSET));
			buf.append("=");
			buf.append(URLEncoder.encode(value, DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException e) {
			// this shouldn't happen...
			throw new RuntimeException(e);
		}
		return buf.toString();
	}
	
	private class ServletClientQueryRunner extends QueryRunner
	{
		String taskId;
		String statusMessage;
		String errorMessage;
		
		public ServletClientQueryRunner(String query, QueryClientCallback callback)
		{
			super(query, callback);
		}
		
		public void run()
		{
			try {
				taskId = (String)((Map<?, ?>)postToServlet(getPostData("query", query))).get("taskId");
				LOGGER.debug("query submitted with taskId " + taskId);
			} catch (IOException e) {
				failure(e.getMessage());
			}
			
			int pollCount=0;
			while ( ++pollCount < POLL_MAX ) {
				try {
					LOGGER.trace(String.format("sleeping %dms before poll #%d", POLL_DELAY, pollCount));
					Thread.sleep(POLL_DELAY);
				} catch (InterruptedException e) {
				}
				try {
					LOGGER.trace("polling servlet with taskId " + taskId);
					Object result = postToServlet(getPostData("poll", taskId));
					LOGGER.trace("received response " + result);
					if (result instanceof Map<?, ?>) {
						Map<?, ?> resultMap = (Map<?, ?>)result;
						try {
						if (!((List<?>)resultMap.get("rows")).isEmpty())
							success(resultMap.get("rows"), resultMap.get("fields"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						Object error = resultMap.get("error");
						if (error instanceof Collection<?>)
							errorMessage = StringUtils.join(((Collection<?>)error).toArray(), "; ");
						else if (error instanceof String)
							errorMessage = (String)error;
						else
							errorMessage = "";
						if (!StringUtils.isEmpty(errorMessage))
							System.err.println(errorMessage);
						break;
					} else if (result instanceof String) {
						statusMessage = (String)result;
					}
				} catch (IOException e) {
					failure(e.getMessage());
					break;
				}
			}
			
			if (pollCount >= POLL_MAX)
				failure(String.format("servlet stopped responding; last status was '%s'", statusMessage));
		}
		
		private void failure(String errorMessage)
		{
			this.errorMessage = errorMessage;
			if (callback != null)
				callback.onFailure(this.errorMessage);
			else
				throw new RuntimeException(this.errorMessage);
		}
		
		@SuppressWarnings("unchecked")
		private void success(Object rows, Object fields)
		{
			successCast((List<List<String>>)rows, (List<String>)fields);
		}
		
		private void successCast(List<List<String>> rows, List<String> fields)
		{
			results = new ArrayList<Map<String, String>>(rows.size());
			for (List<String> row: rows) {
				Map<String, String> binding = new HashMap<String, String>();
				for (int i=0; i<row.size(); ++i)
					binding.put(fields.get(i), row.get(i));
				results.add(binding);
			}
			
			if (callback != null)
				callback.onSuccess(results);
		}
	}
}