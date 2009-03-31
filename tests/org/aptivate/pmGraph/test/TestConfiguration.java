package org.aptivate.pmGraph.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.aptivate.bmotools.pmgraph.DataAccess;

/**
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 * That is the configuration class for teh Test classes of the application is a
 * way to make all access to configuration data in a single place reducing the
 * problems of changing where the configuration data is stored
 * 
 */
public class TestConfiguration
{

	private static final String TEST_CONFIGURATION_FILE = "/tests.properties";

	/**
	 * read the content of teh properties file and return it in a Properties
	 * object
	 * 
	 * @return java.utils.Properties object created after reading the properties
	 *         file.
	 * @throws IOException
	 */
	private static Properties readConfiguration() throws IOException
	{
		Properties properties = new Properties();
		InputStream stream = DataAccess.class
				.getResourceAsStream(TEST_CONFIGURATION_FILE);
		properties.load(stream);
		stream.close();
		return properties;
	}

	public static String getDatabaseURL() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseURL");
	}

	public static String getDatabaseUser() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseUser");
	}

	public static String getDatabasePass() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabasePass");
	}

	public static String getJdbcDriver() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("JdbcDriver");
	}

	public static Integer getPort() throws IOException
	{

		Properties properties = readConfiguration();
		return Integer.valueOf(properties.getProperty("Port"));
	}
}
