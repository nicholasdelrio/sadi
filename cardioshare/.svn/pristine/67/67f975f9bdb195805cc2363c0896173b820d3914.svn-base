package ca.wilkinsonlab.sadi.service.proxy;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

public class LocalEntityRecognitionProxy extends EntityRecognitionProxyServlet
{
	private static final Logger log = Logger.getLogger(LocalEntityRecognitionProxy.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException
	{
		super.init();
		log.info(String.format("entity recognition proxy will use %s for redirects", getServiceURL()));
	}
}
