package ca.wilkinsonlab.cardioshare;

/**
 * CardioSHARE configuration class.  The defaults can be overridden in
 * cardioSHARE.properties. 
 * (see {@link ca.wilkinsonlab.sadi.common.Config} for details)
 * 
 * @author Luke McCarthy
 */
public class Config extends ca.wilkinsonlab.sadi.Config
{
//	private static final Logger log = Logger.getLogger(Config.class);
	
	protected static final String DEFAULT_PROPERTIES_FILENAME = "cardioSHARE-default.properties";
	protected static final String LOCAL_PROPERTIES_FILENAME = "cardioSHARE.properties";
	
	public static final String USE_PELLET_KEY = "usePellet";

	private static final Config theInstance = new Config(DEFAULT_PROPERTIES_FILENAME, LOCAL_PROPERTIES_FILENAME);
	
	public static Config getConfiguration()
	{
		return theInstance;
	}
	
	private Config(String defaultPropertiesFile, String localPropertiesFile)
	{
		super(defaultPropertiesFile, localPropertiesFile);
	}
}
