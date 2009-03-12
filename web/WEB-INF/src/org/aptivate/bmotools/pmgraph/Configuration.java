/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author noeg That the configuration class of the aplication is a way to make
 *         all access to configuration data in a single place reducing the
 *         problems of changing where the configuration data is stored
 * 
 */
public class Configuration {

	private static final String CONFIGURATION_FILE = "/database.properties";

	/**
	 * read the content of teh properties file and return it in a Properties
	 * object
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Properties readConfiguration() throws IOException {
		Properties properties = new Properties();
		InputStream stream = DataAccess.class
				.getResourceAsStream(CONFIGURATION_FILE);
		properties.load(stream);
		stream.close();
		return properties;
	}

	public static String getLocalSubnet() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("LocalSubnet");
	}

	public static String getDatabaseURL() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseURL");
	}

	public static String getDatabaseUser() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseUser");
	}

	public static String getDatabasePass() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DatabasePass");
	}

	public static String getDHCPAddress() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPAdress");
	}

	public static String getDHCPName() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPName");
	}

	public static String getDHCPPass() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("DHCPPass");
	}
	
	
	public static String getJdbcDriver() throws IOException {

		Properties properties = readConfiguration();
		return properties.getProperty("JdbcDriver");
	}
	public static Integer getDHCPPort() throws IOException {

		Properties properties = readConfiguration();
		return Integer.valueOf (properties.getProperty("DHCPPort"));
	}
	
	
	
	
	
}
