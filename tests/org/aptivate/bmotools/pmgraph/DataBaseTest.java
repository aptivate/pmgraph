package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.aptivate.bmotools.pmgraph.DataAccess;
import org.aptivate.bmotools.pmgraph.GraphData;

public class DataBaseTest extends TestCase
{

	public void testDataAccesss() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		testUtils.InsertSampleData();
		DataAccess dataAccess = new DataAccess();
		List<GraphData> resultPerIP = dataAccess.getThroughputPerIP(0, 300000);
		List<GraphData> resultPerIPSort = dataAccess.getThroughputPerIP(0,
				300000);
		List<GraphData> resultPerIPPerMinute = dataAccess
				.getThroughputPIPPMinute(0, 300000);
		assertTrue("get result per IP", !resultPerIP.isEmpty());
		assertTrue("get result per IP", !resultPerIPSort.isEmpty());
		assertTrue("get result per IP", !resultPerIPPerMinute.isEmpty());
	}

	public static Test suite()
	{
		return new TestSuite(DataBaseTest.class);
	}
}
