/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 * This configuration class for the application is used to keep all
 * access to configuration data in a single place. 
 * 
 */
public class Configuration
{
	
	static final String CONFIGURATION_FILE = "/database.properties";

	/**
	 * Read the content of the properties file and return it in a Properties
	 * object
	 * 
	 * @return java.utils.Properties - object created after reading the properties
	 *         file.
	 * @throws IOException
	 */
	static Properties readConfiguration() throws IOException
	{
		Properties properties = new Properties();
		InputStream stream = DataAccess.class
				.getResourceAsStream(CONFIGURATION_FILE);
		properties.load(stream);
		stream.close();
		return properties;
	}

	public static String getLocalSubnet() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("LocalSubnet");
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

	public static String getResultDatabaseTable() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseTable");
	}
	
	public static String getDatabasePass() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DatabasePass");
	}
	

	public static String getDHCPAddress() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPAddress");
	}

	public static String getDHCPName() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPName");
	}

	public static String getDHCPPass() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPPass");
	}

	public static String getJdbcDriver() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("JdbcDriver");
	}

	public static Integer getDHCPPort() throws IOException
	{

		Properties properties = readConfiguration();
		return Integer.valueOf(properties.getProperty("DHCPPort"));
	}
	public static Integer getResultLimit() throws IOException
	{

		Properties properties = readConfiguration();
		return Integer.valueOf(properties.getProperty("ResultLimit"));
	}
}
