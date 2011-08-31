package org.aptivate.bmotools.pmgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static Properties s_properties;


	/**
	 * Read the content of the properties file and return it in a Properties
	 * object
	 * 
	 * @return java.utils.Properties - object created after reading the properties
	 *         file.
	 * @throws IOException 
	 */		
	private static void readConfiguration() throws IOException
	{
		if(s_properties == null)
		{
			s_properties = new Properties();
			InputStream stream = DataAccess.class.getResourceAsStream(CONFIGURATION_FILE);		
			s_properties.loadFromXML(stream);
			stream.close();
		}
	}
	
	/**
	 * Force a reload of the configuration (testing purposes only).
	 * @throws IOException 
	 * 
	*/ 
	public static void forceConfigReload() throws IOException
	{
		s_properties = null;
		readConfiguration();
	}

	public static String [] getLocalSubnet() throws IOException
	{

		readConfiguration();
		int i = 2;
		String AllSubnets = s_properties.getProperty("LocalSubnet");
		String subnet = s_properties.getProperty("LocalSubnet" + i);
		while (subnet != null)
		{
             AllSubnets += " " + subnet; 
             i++;
             subnet = s_properties.getProperty("LocalSubnet" + i);
		}		
		return (AllSubnets.split(" "));
	}
	
	public static String getBandwidth() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("TotalBandwidth");
	}
	
	public static Properties getProperties() throws IOException
	{
		return s_properties;
	}
	public static String getDatabaseURL() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DatabaseURL");
	}

	public static String getDatabaseUser() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DatabaseUser");
	}

	public static String getResultDatabaseTable() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DatabaseTable");
	}
	
	public static String getDatabasePass() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DatabasePass");
	}
	

	public static String getDHCPAddress() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DHCPAddress", "");
	}

	public static String getDHCPName() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DHCPName");
	}

	public static String getDHCPPass() throws IOException
	{

		readConfiguration();
		return s_properties.getProperty("DHCPPass");
	}
	
	public static String getResultDatabaseLongTable() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("DatabaseLongTable");
	}
	
	public static String getResultDatabaseVeryLongTable() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("DatabaseVeryLongTable");
	}
	
	public static String getTimespansForLongGraph() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("TimespansForLongGraph");
	}

	public static String getJdbcDriver() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("JdbcDriver");
	}

	public static Integer getDHCPPort() throws IOException
	{
		readConfiguration();
		return Integer.valueOf(s_properties.getProperty("DHCPPort"));
	}
	
	public static Integer getResultLimit() throws IOException
	{
		readConfiguration();
		return Integer.valueOf(s_properties.getProperty("ResultLimit", "5"));
	}

	public static String getMikrotikAddress() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("MikrotikAddress", "");
	}

	public static String getMikrotikUser() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("MikrotikUser", "admin");
	}

	public static String getMikrotikPass() throws IOException
	{
		readConfiguration();
		return s_properties.getProperty("MikrotikPass", "");
	}

	public static Integer getMikrotikApiPort() throws IOException
	{
		readConfiguration();
		return Integer.valueOf(s_properties.getProperty("MikrotikApiPort",
				"8728"));
	}

	public static boolean updateConf(RequestParams requestParams) throws IOException
	{	
		String newSubnet = requestParams.getAddSubnet();
		Hashtable<String, Integer> hashDelSubnets = requestParams.getDelSubnets();
		readConfiguration();
		boolean result = true;        
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());
		if (hashDelSubnets != null)
			delSubnetConf(hashDelSubnets, tempProps);
		if ((newSubnet != null) && (!newSubnet.equals(""))) 
		{ 
			newSubnet = requestParams.setAddSubnet(newSubnet);
			result = addSubnetConf(newSubnet, tempProps);
		}
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		return result;
	}
	
	public static boolean updateGroups(String addGroup, List<String> delGroups, Hashtable<String,String> hashDelIpGroup, List<String> Groups, Hashtable<String,String> hashAddIpGroup) throws IOException 
	{
		readConfiguration();
		boolean result = true;        
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		if (delGroups != null)
		{
			for (String delG: delGroups)
				delGroup(delG, tempProps);
		}
		if (addGroup != null)
		{
			if (!Groups.contains(addGroup))
				tempProps.put("G-"+addGroup, "0.0.0.0");
			else
				result = false;
		}
		if (hashDelIpGroup != null)
		{
			if (!hashDelIpGroup.isEmpty())
				delIpGroup(hashDelIpGroup, Groups, tempProps);
		}
		if (hashAddIpGroup != null)
		{
			if (!hashAddIpGroup.isEmpty())
				addIpGroupConf(hashAddIpGroup,tempProps);
		}
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		return result;		
	}	
	
	public static boolean addSubnetConf(String newSubnet, Properties tempProps) throws IOException
	{	
		int i=2;
		boolean insert = false;
		if (tempProps.getProperty("LocalSubnet") != null)
			if (tempProps.getProperty("LocalSubnet").equals(newSubnet))
				insert = true;
		while ((tempProps.getProperty("LocalSubnet"+i) != null) && (!insert))
		{
			if (tempProps.getProperty("LocalSubnet"+i).equals(newSubnet))
				insert = true;
			else
				i++;
		}		
		if (!insert) 
			tempProps.put("LocalSubnet"+i, newSubnet);	
		
		return(!insert);
	}
	
	public static boolean addSubnetConf(String newSubnet) throws IOException
	{	
		readConfiguration();
		boolean result = false;
		// Copy to a temporary properties object to prevent error where the configuration file ends up
		// empty.
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		int i=2;
		boolean insert = false;
		if (tempProps.getProperty("LocalSubnet") != null)
			if (tempProps.getProperty("LocalSubnet").equals(newSubnet))
				insert = true;
		while ((tempProps.getProperty("LocalSubnet"+i) != null) && (!insert))
		{
			if (tempProps.getProperty("LocalSubnet"+i).equals(newSubnet))
				insert = true;
			else
				i++;
		}		
		if (!insert) 
		{
			tempProps.put("LocalSubnet"+i, newSubnet);
			result = true;	
		}
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		return result;
	}
	
	public static boolean delSubnetConf(Hashtable<String,Integer> hashDelSubnets, Properties tempProps) throws IOException
	{	
		boolean result = false;				
		for (Enumeration e = hashDelSubnets.keys (); e.hasMoreElements ();) 
		{			
			String key = (String) e.nextElement ();
			int value = hashDelSubnets.get (key);
			tempProps.remove(key);
			int i = value + 1;		    
			while (tempProps.getProperty("LocalSubnet"+i) != null) {
				if ((i-1) == 1)
					tempProps.setProperty("LocalSubnet", tempProps.getProperty("LocalSubnet"+i));
				else
					tempProps.setProperty("LocalSubnet"+(i-1), tempProps.getProperty("LocalSubnet"+i));
				i++;
			}
			tempProps.remove("LocalSubnet"+(i-1));
		}
		result = true;		
		return result;
	}
	
	public static boolean delSubnetConf(Hashtable<String,Integer> hashDelSubnets) throws IOException
	{	
		readConfiguration();
		boolean result = false;
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
			
		for (Enumeration e = hashDelSubnets.keys (); e.hasMoreElements ();) 
		{
			String key = (String) e.nextElement ();
			int value = hashDelSubnets.get (key);
			tempProps.remove(key);
			int i = value + 1;
			while (tempProps.getProperty("LocalSubnet"+i) != null) {
				if ((i-1) == 1)
					tempProps.setProperty("LocalSubnet", tempProps.getProperty("LocalSubnet"+i));
				else
					tempProps.setProperty("LocalSubnet"+(i-1), tempProps.getProperty("LocalSubnet"+i));
				i++;
			}
			tempProps.remove("LocalSubnet"+(i-1));
		}
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		result = true;		
		return result;
	}
	
	public static List<String> getGroups() throws IOException
	{
		readConfiguration();				
		Set<String> allContent = s_properties.stringPropertyNames();
		List<String> groups = new ArrayList<String>();
		Pattern p = Pattern.compile("G(\\d+)?-");
		Matcher m;
		String newGroup;
		for (String name : allContent) 
		{	
			if (!name.trim().equals("G-null"))
			{
				m = p.matcher(name);
				if (m.find()) 
				{
					newGroup = name.substring(name.indexOf("-") + 1);

					if (!groups.contains(newGroup))
						groups.add(newGroup);
				}
			}
		}			
		Collections.sort(groups);
		return (groups);
	}
	
	public static List<String> getIpsGroup(String group) throws IOException
	{		
		readConfiguration();
		int i = 1;
		List<String> IpsGroup = new ArrayList<String>();		
		while (s_properties.getProperty("G" + i + "-" + group) != null)
		{
			IpsGroup.add(s_properties.getProperty("G" + i + "-" + group));				 																	
			i++;						
		}
		Collections.sort(IpsGroup, new IpComparator());
		return (IpsGroup);
	}	
	
	
	public static List<String> getGroupsIp(String ip, List<String> groups) throws IOException
	{
		readConfiguration();
		List<String> groupsIp = new ArrayList<String>();
		for (String currentGroup : groups) {		
			int i = 1;
			boolean find = false;
			while ((s_properties.getProperty("G" + i + "-" + currentGroup) != null) && (!find))
			{
				if (s_properties.getProperty("G" + i + "-" + currentGroup).equals(ip)) 
				{
					groupsIp.add(currentGroup);
					find = true;
				}
				i++;			
			}	
		}		
		return (groupsIp);
	}	
	
	public static List<String> getGroupsIp(String ip, List<String> groups, Properties tempProps) throws IOException
	{		
		List<String> groupsIp = new ArrayList<String>();
		for (String currentGroup : groups) {		
			int i = 1;
			boolean find = false;
			while ((tempProps.getProperty("G" + i + "-" + currentGroup) != null) && (!find))
			{
				if (tempProps.getProperty("G" + i + "-" + currentGroup).equals(ip)) 
				{
					groupsIp.add(currentGroup);
					find = true;
				}
				i++;			
			}	
		}		
		return (groupsIp);
	}	
	
	public static List<String> getGroupsNIp(List<String> groups, List<String> groupsIp) throws IOException
	{
		readConfiguration();
		List<String> groupsNIp = new ArrayList<String>();
		for (String currentGroup : groups) {
			if (!groupsIp.contains(currentGroup))
				groupsNIp.add(currentGroup);
		}	
		return (groupsNIp);
	}
	
	public static List<String> getNIpsGroup(List<String> ipGroup, List<String> ips) throws IOException
	{
		List<String> nIpsGroup = new ArrayList<String>();
		for (String currentIp : ips) {
			if (!ipGroup.contains(currentIp))
				nIpsGroup.add(currentIp);
		}	
		return (nIpsGroup);
	}
	
	public static boolean addIpGroupConf(String group, String newIp) throws IOException
	{	
		readConfiguration();	
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		int i=1;
		boolean insert = false;
		if (tempProps.getProperty("G-"+group) != null)
		{
			tempProps.remove("G-"+group);
			tempProps.put("G" + i + "-" + group, newIp);
		}
		else
		{
			while((tempProps.getProperty("G" + i + "-" + group) != null) && (!insert))
			{
				if (tempProps.getProperty("G" + i + "-" + group).equals(newIp))
					insert = true;
				else
					i++;
			}		
			if (!insert) 
				tempProps.put("G" + i + "-" + group, newIp);
		}
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		return (!insert);
	}
	
	public static boolean addIpGroupConf(Hashtable<String,String> hashAddIpGroup, Properties tempProps) throws IOException
	{		
		boolean insert = false;
		for (Enumeration e = hashAddIpGroup.keys (); e.hasMoreElements ();)
		{
			String newIp = (String) e.nextElement ();
			String group = hashAddIpGroup.get (newIp);
			int i=1;
			insert = false;
			if (tempProps.getProperty("G-"+group) != null)
			{
				tempProps.remove("G-"+group);
				tempProps.put("G" + i + "-" + group, newIp);
			}
			else
			{
				while((tempProps.getProperty("G" + i + "-" + group) != null) && (!insert))
				{
					if (tempProps.getProperty("G" + i + "-" + group).equals(newIp))
						insert = true;
					else
						i++;
				}		
				if (!insert) 
					tempProps.put("G" + i + "-" + group, newIp);		
			}
		}
		return (!insert);	
	}
	
	public static boolean delGroup(String group) throws IOException
	{	
		readConfiguration();
		boolean result = false;
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		delGroup(group, tempProps);
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();
		result = true;			
		return result;
	}
	
	public static boolean delGroup(String group, Properties tempProps) throws IOException
	{	
		boolean result = false;				
		if (tempProps.getProperty("G-"+group) != null)
		{
			tempProps.remove("G-"+group);
		}
		else 
		{
			int i = 1;
			while (tempProps.getProperty("G" + i + "-" + group) != null)
			{
				tempProps.remove("G" + i + "-" + group);
				i++;			
			}
		}
		result = true;			
		return result;
	}
	
	public static boolean delIpGroup(String group, String delIp) throws IOException
	{	
		readConfiguration();
		Properties tempProps = (Properties)s_properties.clone();
		FileOutputStream out = new FileOutputStream((DataAccess.class.getResource(CONFIGURATION_FILE)).getPath());		
		int i = 1;
		boolean delete = false;
		while ((tempProps.getProperty("G" + i + "-" + group) != null) && (!delete))
		{
			if (tempProps.getProperty("G" + i + "-" + group).equals(delIp)) 
			{
				tempProps.remove("G" + i + "-" + group);				
				delete = true;
				int j = i + 1;
				while (tempProps.getProperty("G" + j + "-" + group) != null) {					
					tempProps.setProperty("G" + (j-1) + "-" + group, tempProps.getProperty("G" + j + "-" + group));
					j++;
				}
				tempProps.remove("G" + (j-1) + "-" + group);
			}
			i++;			
		}	
		s_properties = tempProps;
		tempProps.storeToXML(out, null);
		out.close();	
		return delete;
	}
	
	public static boolean delIpGroup(Hashtable<String,String> hashDelIpGroup, List<String> Groups, Properties tempProps) throws IOException
	{			
		boolean delete = false;		
		for (Enumeration e = hashDelIpGroup.keys (); e.hasMoreElements ();) 
		{
			String delIp = (String) e.nextElement ();
			String group = hashDelIpGroup.get (delIp);
			List<String> groupsIp = new ArrayList<String>();			
			if (group.equals("allIpGroup"))
				groupsIp = Configuration.getGroupsIp(delIp, Groups, tempProps);
			else						
				groupsIp.add(group);			
			for (String currentGroup : groupsIp)
			{
				int i = 1;
				delete = false;
				while ((tempProps.getProperty("G" + i + "-" + currentGroup) != null) && (!delete))
				{
					if (tempProps.getProperty("G" + i + "-" + currentGroup).equals(delIp)) 
					{
						tempProps.remove("G" + i + "-" + currentGroup);						
						delete = true;
						int j = i + 1;
						if (tempProps.getProperty("G" + j + "-" + currentGroup) != null)
						{
							while (tempProps.getProperty("G" + j + "-" + currentGroup) != null) {					
								tempProps.setProperty("G" + (j-1) + "-" + currentGroup, 
										tempProps.getProperty("G" + j + "-" + currentGroup));
								j++;
							}
							tempProps.remove("G" + (j-1) + "-" + currentGroup);
						}
						else if (i == 1)
						{
							tempProps.put("G-"+currentGroup, "0.0.0.0");
						}
					}
					i++;			
				}
			}
		}		
		return delete;
	}
	
	public static void processLine(String aLine, String localSubnet, String oldSubnet, StringBuilder contents) throws IOException
	{
	    // use a second Scanner to parse the content of each line 
	    Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("\n");
	    if ( scanner.hasNext() )
	    {
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
	    else
	    {	    	
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
		    try 
		    {
		      //first use a Scanner to get each line
		      while ( scanner.hasNextLine() )
		      {
		        processLine( scanner.nextLine(), localSubnet, oldSubnet, contents );
		      }
		    }
		    finally 
		    {
		      //ensure the underlying stream is always closed
		      scanner.close();
		    }
		    
		    // use buffering
		    Writer output = new BufferedWriter(new FileWriter(pmacctPath));
		    try 
		    {
		      //FileWriter always assumes default encoding is OK!
		      output.write( contents.toString() );
		    }
		    finally 
		    {
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