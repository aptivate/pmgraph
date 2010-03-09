package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import org.xml.sax.SAXException;

import com.meterware.httpunit.*;


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

	public void testUpdateConfiguration() throws InstantiationException, IllegalAccessException, 
			ClassNotFoundException, SQLException, IOException, SAXException
	{
		TestUtils theTestUtils = new TestUtils();
		WebConversation wc = new WebConversation();
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp");
		String oldSubnet = Configuration.getLocalSubnet();
		String newSubnet = "0.123.255.";
		WebResponse response = wc.getResponse(request);
		replaceSubnet(response, newSubnet);
		assertTrue(Configuration.getLocalSubnet().equals(newSubnet));
		try {
			//This is necessary to ensure that the value in pmacctd.conf is reset correctly
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		// Ensure there are no problems caused by tomcat reloading.
		wc = new WebConversation();
		response = wc.getResource(request);
		replaceSubnet(response, oldSubnet);
		assertTrue(Configuration.getLocalSubnet().equals(oldSubnet));
	}
	
	private void replaceSubnet(WebResponse response, String newSubnet) throws IOException, SAXException
	{
		WebForm configurationForm = response.getFormWithID("config");
		String currentSubnet = configurationForm.getParameterValue("localSubnet");
		assertTrue(currentSubnet.equals(Configuration.getLocalSubnet()));
		configurationForm.setParameter("localSubnet", newSubnet);
		WebResponse formResult = configurationForm.submit();
		HTMLElement result = formResult.getElementWithID("result");
		String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
		assertTrue(resultString.equals(" Update Done "));
		// Update the client side configuration.
		Configuration.forceConfigReload();
	}
	
	public static Test suite()
	{
		return new TestSuite(ConfigurationTest.class);
	}
}
