/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;


/**
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 * This configuration class for the application is used to keep all
 * access to configuration data in a single place. 
 * 
 * Addition: RuchiR
 * 
 * Updated for accessing and modifying local subnet in configuration 
 * files "database.properties" and "pmacctd.conf". 
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
		InputStream stream = DataAccess.class.getResourceAsStream(CONFIGURATION_FILE);		
		properties.loadFromXML(stream);
		stream.close();
		return properties;
	}

	public static String getLocalSubnet() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("LocalSubnet");
	}
	
	
	public static String getBandwidth() throws IOException
	{

		Properties properties = readConfiguration();
		return properties.getProperty("TotalBandwidth");
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
	
	public static String getResultDatabaseLongTable() throws IOException
	{
		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseLongTable");
	}
	
	public static String getResultDatabaseVeryLongTable() throws IOException
	{
		Properties properties = readConfiguration();
		return properties.getProperty("DatabaseVeryLongTable");
	}
	
	public static String getTimespansForLongGraph() throws IOException
	{
		Properties properties = readConfiguration();
		return properties.getProperty("TimespansForLongGraph");
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
	
	
	private static void setConfiguration(String localSubnet) throws IOException
	{
		Properties properties = readConfiguration();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		properties.setProperty("LocalSubnet", localSubnet);
		properties.storeToXML(out, "");				
		out.close();	
	 }	    
	
	
	
	public static String updateConf(String localSubnet) throws IOException
	{	
		String result = "false";
		String oldSubnet = getLocalSubnet();	
		setConfiguration(localSubnet);
		processLineByLine(localSubnet, oldSubnet);
		result = "true";			
		return result;
	}
	
	public static void processLine(String aLine, String localSubnet, String oldSubnet, StringBuilder contents) throws IOException
	{
	    //use a second Scanner to parse the content of each line 
	    Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("\n");
	    if ( scanner.hasNext() ){
	      String name = scanner.next();
	      if(name.contains("pcap_filter"))
	      {	   
	    	  String oldLine = "pcap_filter: not (src and dst net " + oldSubnet + "0/24)";	    	  
	    	  String newLine = "pcap_filter: not (src and dst net " + localSubnet + "0/24)";	    	  
	    	  name = name.replace(oldLine, newLine);
	      }	
	      contents.append(name);
	      contents.append("\n");
		  
	    }
	    else {	    	
	    	contents.append("\n");
	    }	    	    
	    //(no need for finally here, since String is source)
	    scanner.close();
	  }
	
	 public static void processLineByLine(String localSubnet, String oldSubnet) throws IOException {
		 
		    String fullPath = (DataAccess.class.getResource(CONFIGURATION_FILE)).getPath();
		    String pmacctPath = fullPath.replace("/web/WEB-INF/classes/database.properties", "/config/pmacctd.conf");		    		    		    
		    File fFile = new File(pmacctPath);
		    Scanner scanner = new Scanner(fFile);
		    StringBuilder contents = new StringBuilder();
		    try {
		      //first use a Scanner to get each line
		      while ( scanner.hasNextLine() ){
		        processLine( scanner.nextLine(), localSubnet, oldSubnet, contents );
		      }
		    }
		    finally {
		      //ensure the underlying stream is always closed
		      scanner.close();
		    }
		    
//		  use buffering
		    Writer output = new BufferedWriter(new FileWriter(pmacctPath));
		    try {
		      //FileWriter always assumes default encoding is OK!
		      output.write( contents.toString() );
		    }
		    finally {
		      output.close();
		    }		    
		  }	 
	
}