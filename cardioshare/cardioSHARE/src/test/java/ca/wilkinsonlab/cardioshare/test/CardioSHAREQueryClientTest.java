package ca.wilkinsonlab.cardioshare.test;

import ca.wilkinsonlab.cardioshare.CardioSHAREQueryClient;
import ca.wilkinsonlab.sadi.client.QueryClient;
import ca.wilkinsonlab.sadi.test.QueryClientTest;

public class CardioSHAREQueryClientTest extends QueryClientTest
{
	@Override
	public QueryClient getClient()
	{
		return new CardioSHAREQueryClient();
	}
}
