package ca.wilkinsonlab.cardioshare.test;

import ca.wilkinsonlab.sadi.client.QueryClient;
import ca.wilkinsonlab.sadi.test.QueryClientTest;

public class ServletClientTest extends QueryClientTest
{
	@Override
	public QueryClient getClient()
	{
//		return new ServletClient("http://biordf.net/cardioSHARE/query");
//		return new ServletClient("http://dev.biordf.net/cardioSHARE/query");
		return new ServletClient("http://localhost:8080/cardioSHARE/query");
	}
}
