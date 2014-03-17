package ca.wilkinsonlab.cardioshare;

import org.apache.log4j.Logger;

import ca.wilkinsonlab.sadi.tasks.QueryTask;

import com.hp.hpl.jena.rdf.model.Model;

import ca.wilkinsonlab.sadi.share.SHAREQueryClient;

public class CardioSHAREQueryTask extends QueryTask
{
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(CardioSHAREQueryTask.class);
	
	SHAREQueryClient client;
	
	public CardioSHAREQueryTask(String queryString)
	{
		super(queryString);
        client = new CardioSHAREQueryClient();
	}
	
	public Model getDataModel()
	{
		return client.getDataModel();
	}
	
	public void run()
	{
		results = client.synchronousQuery(queryString);
        success();
	}
	
	public void dispose()
	{
		client.getKB().dispose();
		super.dispose();
	}
}
