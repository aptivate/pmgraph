package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private static final long DAY = 86400000;

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
	
	
	
	public static boolean updateConf(String localSubnet) throws IOException
	{	
		boolean result = false;
		String oldSubnet = getLocalSubnet();	
		setConfiguration(localSubnet);
		processLineByLine(localSubnet, oldSubnet);
		result = true;			
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
	 
	 	/**
		 * Gets the time period for each step on th x-axis of the graph
		 * @param isLong Whether or not a long time period is covered
		 * @return The value of each step on the x-axis of the graph as a long value in milliseconds
		 */
		public static int getResolution(boolean isLong, long timeSpan)
		{
			int requiredTimePeriod = TimePeriod.MINUTE.getTimePeriod();
			if(isLong)
			{
				int[] timePeriods = TimePeriod.getTimePeriodLengths();
				boolean found = false;
				long[] times = getTimeSpanLengths();
				if(times.length != 0)
				{
					for(int period : timePeriods)
					{
						// Ensure that the time period divided by the timespan is less than the number of minutes in a day
						// If it isn't then there's no real point in using it.
						for(long time : times)
						{
							if(timeSpan >= time)
							{
								if(timeSpan/(long)period < (60 * 24))
								{
									requiredTimePeriod = period;
									found = true;
									break;
								}
							}
						}
						if(!found)
						{
							requiredTimePeriod = timePeriods[timePeriods.length - 1];
						}
						else
						{
							break;
						}
					}
				}
			}
			return requiredTimePeriod;
		}
		
		/**
		 * This gets the table in which the data is stored
		 * @param timeSpan The length of time covered by the graph
		 * @return The name of the table
		 * @throws IOException
		 */
		public static String findTable(long timeSpan) throws IOException
		{
			long[] timeSpans = getTimeSpanLengths();
			for(int i = timeSpans.length - 1; i >= 0; i--)
			{
				if(timeSpan >= timeSpans[i])
				{
					switch(i){
						case 0:
							return Configuration.getResultDatabaseLongTable();
						case 1:
							String table = Configuration.getResultDatabaseVeryLongTable();
							if(table == null)
							{
								table = Configuration.getResultDatabaseLongTable();
							}
							return table;					
						default:
							return Configuration.getResultDatabaseLongTable();
					}
				}
			}
			return Configuration.getResultDatabaseTable();
		}
		
		/**
		 * This gets the different timespans for determining the period of data covered in the graph in ascending order
		 * @return An array of long values representing the timespans
		 */
		public static long[] getTimeSpanLengths()
		{
			long[] values;
			List<Long> temp = new ArrayList<Long>();
			try
			{
				String[] timeSpans = Configuration.getTimespansForLongGraph().split(",");
				for(String timeSpan : timeSpans)
				{
					try
					{
						long tempVal = Long.parseLong(timeSpan.trim()) * 60 * 60 * 1000;
						if(tempVal >= DAY)
						{
							temp.add(new Long(tempVal));
						}
					}
					catch(NumberFormatException e)
					{
						
					}
				}
				values = new long[temp.size()];
				for(int i = 0; i < values.length; i++)
				{
					values[i] = temp.get(i).longValue();
				}
				Arrays.sort(values);
				return values;
			}
			catch(IOException e)
			{
				values = new long[0];
				return values;
			}
			catch(NullPointerException e)
			{
				values = new long[0];
				return values;
			}
		}
		
		/**
		 * Determines if the graph should display data over a long time period
		 * @param start The time the graph shows data from
		 * @param end The time the graph shows data to
		 * @return a value saying whether or not the graph data should cover a long time period
		 */
		public static boolean needsLongGraph(long start, long end)
		{
			long[] timeSpans = getTimeSpanLengths();
			for(long timeSpan : timeSpans)
			{
				if(end - start >= timeSpan)
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Check that the displaying of long graphs has been enabled
		 * @return A boolean saying whether or not long graphs are enabled
		 */
		public static boolean longGraphIsAllowed()
		{
			try
			{
				String[] timeSpans = Configuration.getTimespansForLongGraph().split(",");
				for(String timeSpan : timeSpans)
				{
					long aTimeSpan = 0;
					try
					{
						aTimeSpan = Long.parseLong(timeSpan.trim());
					}
					catch(NumberFormatException e)
					{
						
					}
					// You can't display a long graph with time periods of less than one day.
					if(aTimeSpan >= 24)
					{
						return true;
					}
				}
				return false;
			}
			catch(IOException e)
			{
				return false;
			}
			catch(NullPointerException e)
			{
				return false;
			}
		}
}