package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Utilities class
 *
 */

public class ConfigurationTest extends TestCase 
{
	final long HOUR = 60 * 60 * 1000;
	final long DAY = 24 * HOUR;
	
	public void testTimeSpanResults()
	{
		long[] TimePeriods = {DAY, 30 * DAY - HOUR};
		long[] results = {HOUR, HOUR};
		int resolution;
		for(int i = 0; i < TimePeriods.length; i++)
		{
			long time = TimePeriods[i];
			resolution = Configuration.getResolution(true, time);
			assertEquals(results[i], resolution);
			resolution = Configuration.getResolution(false, time);
			assertEquals(60000, resolution);
		}
	}
	
	public void testFindTable() throws IOException
	{
		long[] timePeriods = {DAY - HOUR, DAY, 30L * DAY - HOUR};
		String[] tables = {"acct_v6", "acct_v6_long", "acct_v6_long"};
		String table;
		for(int i = 0; i < timePeriods.length; i++)
		{
			long time = timePeriods[i];
			table = Configuration.findTable(time);
			assertEquals(tables[i], table);
		}
	}
	
	public void testDeleteIpsFromGroups() throws IOException
	{
		Properties props = new Properties();
		InputStream stream = DataAccess.class.getResourceAsStream(Configuration.CONFIGURATION_FILE);
		props.loadFromXML(stream);
		Hashtable<String, String> ipsForGroup = new Hashtable<String, String>();
		ArrayList<String> groups = new ArrayList<String>();
		ipsForGroup.put("10.0.1.1", "Test");
		ipsForGroup.put("10.0.1.2", "Test");
		ipsForGroup.put("10.0.1.3", "Test");
		Configuration.updateGroups("Test", null, null, groups, null);
		groups.add("Test");
		Configuration.addIpGroupConf(ipsForGroup, props);
		Configuration.delIpGroup(ipsForGroup, groups, props);
		for(Object propKey : props.keySet())
		{
			String key = propKey.toString();
			boolean hasGroup = key.contains("G1-Test") || 
				key.contains("G2-Test") || key.contains("G3-Test");
			assertTrue("Group Test still contains IP: " + props.getProperty(key),
					!hasGroup);
		}
		Configuration.delGroup("Test");
	}
	
	public static Test suite()
	{
		return new TestSuite(ConfigurationTest.class);
	}
}
