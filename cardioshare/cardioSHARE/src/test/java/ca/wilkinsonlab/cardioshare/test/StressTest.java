package ca.wilkinsonlab.cardioshare.test;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import ca.wilkinsonlab.sadi.test.ExampleQueries;

public class StressTest
{
	private final static Logger log = Logger.getLogger(StressTest.class);
	
	public static void main(String[] args) throws Exception
	{
		ServletClient helper = new ServletClient("http://dev.biordf.net/cardioSHARE/query");
		StopWatch stopWatch = new StopWatch();
		for (int n=0; true; ) {
			for (String query: ExampleQueries.getQueries()) {
				log.info(String.format("sending query %d:\n%s", ++n, query));
				try {
					stopWatch.start();
					List<Map<String, String>> results = helper.synchronousQuery(query);
					stopWatch.stop();
					if (results.isEmpty())
						log.warn(String.format("query returned no results after %d seconds", stopWatch.getTime()/1000));
					else
						log.info(String.format("query executed in %d seconds", stopWatch.getTime()/1000));
				} catch (Exception e) {
					log.error("error executing query", e);
				}
				stopWatch.reset();
			}
		}
	}
}
