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
			SQLException, ConfigurationException
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		testUtils.InsertSampleData();
		DataAccess dataAccess = new DataAccess();

		RequestParams requestParams = new RequestParams(0, 300000,
				View.LOCAL_IP, 10);
		List<GraphData> resultPerIP = dataAccess.getThroughput(requestParams,
				true);
		List<GraphData> resultPerIPPerMinute = dataAccess.getThroughput(
				requestParams, false);
		assertTrue("get result per IP", !resultPerIP.isEmpty());
		assertTrue("get result per IP", !resultPerIPPerMinute.isEmpty());
	}

	public static Test suite()
	{
		return new TestSuite(DataBaseTest.class);
	}
}
