package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Utilities class
 *
 */

public class TimeSpanUtilsTest extends TestCase {
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
			resolution = TimeSpanUtils.getResolution(true, time);
			assertEquals(results[i], resolution);
			resolution = TimeSpanUtils.getResolution(false, time);
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
			table = TimeSpanUtils.findTable(time);
			assertEquals(tables[i], table);
		}
	}

	public static Test suite()
	{
		return new TestSuite(TimeSpanUtilsTest.class);
	}
}
