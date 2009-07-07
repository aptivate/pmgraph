package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.aptivate.bmotools.pmgraph.DataAccess;

/**
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 * That is the configuration class for the Test classes of the application. It is a
 * way to make all the access to configuration data in a single place reducing the
 * problems of changing where the configuration data is stored
 * 
 */
class TestConfiguration
{

	private static final String TEST_CONFIGURATION_FILE = "/tests.properties";

	/**
	 * read the content of the properties file and return it in a Properties
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

	static String getDatabaseURL() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseURL");
	}

	static String getDatabaseUser() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseUser");
	}

	static String getDatabasePass() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabasePass");
	}

	static String getJdbcDriver() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("JdbcDriver");
	}

	static Integer getPort() throws IOException
	{

		Properties properties = readConfiguration();
		return Integer.valueOf(properties.getProperty("Port"));
	}
}
