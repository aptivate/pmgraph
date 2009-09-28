package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Utilities class
 *
 */

public class UtilitiesTest extends TestCase {
	public void testTimeSpanResults()
	{
		long[] TimePeriods = {86400000, 5180400000L}; //, 5184000000L, 124416000000L
		int[] results = {3600000, 3600000}; //, 86400000, 86400000 
		int resolution;
		for(int i = 0; i < TimePeriods.length; i++)
		{
			long time = TimePeriods[i];
			resolution = Utilities.getResolution(true, time);
			assertEquals(results[i], resolution);
			resolution = Utilities.getResolution(false, time);
			assertEquals(60000, resolution);
		}
	}
	
	public void testFindTable()
	{
		long[] TimePeriods = {86300000, 86400000, 5180400000L}; //, 5184000000L, 124416000000L
		String[] tables = {"acct_v6", "acct_v6_long", "acct_v6_long"};
		String table;
		for(int i = 0; i < TimePeriods.length; i++)
		{
			try
			{
				long time = TimePeriods[i];
				table = Utilities.findTable(time);
				assertEquals(tables[i], table);
			}
			catch(IOException e)
			{
				
			}
		}
	}

	public static Test suite()
	{
		return new TestSuite(UtilitiesTest.class);
	}
}
