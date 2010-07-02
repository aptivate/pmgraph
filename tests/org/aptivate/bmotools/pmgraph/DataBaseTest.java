package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.aptivate.bmotools.pmgraph.DataAccess;
import org.aptivate.bmotools.pmgraph.DataPoint;

public class DataBaseTest extends TestCase
{

	public void testDataAccesss() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException, ConfigurationException
	{		
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		testUtils.InsertSampleData();
		testUtils.InsertLongSampleData();
		DataAccess dataAccess = new DataAccess();

		RequestParams.setSelectSubnetIndex("all");
		RequestParams requestParams = new RequestParams(0, 300000, View.LOCAL_IP, 10);
		Hashtable<Integer,List<DataPoint>> thrptResults = dataAccess.getThroughput(requestParams, true, false);
		Hashtable<Integer,List<DataPoint>> thrptResults2 = dataAccess.getThroughput(requestParams, false, false);
		for (Enumeration e = thrptResults.keys (); e.hasMoreElements ();)
		{
			int key = (Integer) e.nextElement();
			List<DataPoint> resultPerIP = thrptResults.get(key);
			assertTrue("get result per IP", !resultPerIP.isEmpty());
		}
		for (Enumeration e = thrptResults2.keys (); e.hasMoreElements ();)
		{
			int key = (Integer) e.nextElement();
			List<DataPoint> resultPerIPPerMinute = thrptResults.get(key);
			assertTrue("get result per IP", !resultPerIPPerMinute.isEmpty());
		}
		
		
		requestParams = new RequestParams(0, 345600000, View.LOCAL_IP, 10);
		thrptResults = dataAccess.getThroughput(requestParams, true, true);
		thrptResults2 = dataAccess.getThroughput(requestParams, false, true);
		for (Enumeration e = thrptResults.keys (); e.hasMoreElements ();)
		{
			int key = (Integer) e.nextElement();
			List<DataPoint> resultPerIP = thrptResults.get(key);
			assertTrue("get result per IP", !resultPerIP.isEmpty());
		}
		for (Enumeration e = thrptResults2.keys (); e.hasMoreElements ();)
		{
			int key = (Integer) e.nextElement();
			List<DataPoint> resultPerIPPerMinute = thrptResults.get(key);
			assertTrue("get result per IP", !resultPerIPPerMinute.isEmpty());
		}
	}

	public static Test suite()
	{
		return new TestSuite(DataBaseTest.class);
	}
}
