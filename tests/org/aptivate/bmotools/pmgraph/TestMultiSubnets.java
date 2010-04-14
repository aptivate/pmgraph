package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.meterware.httpunit.FormControl;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * Test the functionality of multiple subnets
 * 
 * @author franciscor and pablob
 * 
 */
public class TestMultiSubnets extends TestCase 
{
	final long HOUR = 60 * 60 * 1000;
	final long DAY = 24 * HOUR;
	
	private static final int SIZE_HEADS = 1;
	
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
	
	// ---------------------------------------------------
	
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
	
	// ------------------------------------------------------
	
	public void testCheckSubnet() throws InstantiationException, IllegalAccessException, 
	        ClassNotFoundException, SQLException, IOException, SAXException
	{
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = initConversation (theTestUtils);  
		String subnet1 = "10.0.156.";
		String subnet2 = "10.21.21.";
		
		boolean isSubnet1 = false;
		boolean isSubnet2 = false;
	    WebTable table = response.getTableWithID("TableLocalSubnets");
		for (int i = 1; i < table.getRowCount(); i++)
		{
			String currentSubnet = table.getCellAsText(i, 0);
			if (currentSubnet.equals(subnet1))
				isSubnet1 = true;
			if (currentSubnet.equals(subnet2))
				isSubnet2 = true;
		}
		
		assertTrue(isSubnet1);
		assertFalse(isSubnet2);
	}
	
	// ---------------------------------------------------------------
	
	public void testCompareSubnets() throws InstantiationException, IllegalAccessException, 
    ClassNotFoundException, SQLException, IOException, SAXException
    {
		String currentSubnets = Configuration.getLocalSubnet();
		String[] vectSubnets = currentSubnets.split(" ");
		
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = initConversation (theTestUtils);
		WebTable table = response.getTableWithID("TableLocalSubnets");
		int numSubnet = table.getRowCount() - SIZE_HEADS;
        assertEquals(numSubnet, vectSubnets.length);
        
        if (numSubnet == vectSubnets.length)
        	for (int i = 0; i < numSubnet; i++)
        	{
        		String currentSubnet = table.getCellAsText(i+1, 0);
        		assertTrue(currentSubnet.equals(vectSubnets[i]));
        	}
    }
	
	// -----------------------------------------------------------------
	
	public void testAddSubnet() throws InstantiationException, IllegalAccessException, 
            ClassNotFoundException, SQLException, IOException, SAXException
	{
		testCompareSubnets();
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = initConversation (theTestUtils);
		WebForm configurationForm = response.getFormWithID("config");
		WebTable table = response.getTableWithID("TableLocalSubnets");
		int oldNumSubnets = table.getRowCount() - SIZE_HEADS;
		String newSubnet = "99.99.99.";
		configurationForm.setParameter("newSubnet", newSubnet);
		configurationForm.submit();
		
		theTestUtils = new TestUtils();
		response = initConversation (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		int numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue((numSubnets > oldNumSubnets));
     	assertEquals(table.getCellAsText(numSubnets,0), newSubnet);
     	
     	configurationForm = response.getFormWithID("config");
	    FormControl aux = configurationForm.getControlWithID("delSubnet"+numSubnets);
		aux.toggle();
		configurationForm.submit();	
		
		response = initConversation (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue((numSubnets == oldNumSubnets));
	}

	// -----------------------------------------------------------------
	
	
	private WebResponse initConversation (TestUtils theTestUtils) throws IOException, SAXException
	{
		WebConversation wc = new WebConversation();
	    WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp");
	    WebResponse response = wc.getResponse(request);
	    return response;
	}
	
	// ------------------------------------------------------------------
	

	public void testFormatSubnet() throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, SQLException, IOException, SAXException
	{	
		TestUtils theTestUtils = new TestUtils();
		WebConversation wc = new WebConversation();
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp");
		WebResponse response = wc.getResponse(request);
		WebForm configurationForm = response.getFormWithID("config");
		
		int i = Integer.parseInt(configurationForm.getParameterValue("numSubnets"));
		String addSubnet[] = {"10.40.255.", "10.1A.123.", "1001.1.3"};
		
		String Url[] = new String[3];
        for (int j = 0; j < 3; j++)
            Url[j] = theTestUtils.getUrlPmgraph() + "configure.jsp?newSubnet="+addSubnet[j]+"&selectSubnet=10.0.156.&numSubnets="+i+"&Go=Save+configuration";				       	      
	    request = new GetMethodWebRequest(Url[2]);
	    response = wc.getResponse(request);
	    HTMLElement result = response.getElementWithID("unsuccessResult");
	    String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255."));
	       
	    request = new GetMethodWebRequest(Url[1]);
	    response = wc.getResponse(request);
	    result = response.getElementWithID("unsuccessResult");
	    resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255."));
	       
	    request = new GetMethodWebRequest(Url[0]);
	    response = wc.getResponse(request);
	    result = response.getElementWithID("successResult");
	    resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Update Done"));
	       
	    Hashtable<String,Integer> delSubnet = new Hashtable<String,Integer>();
	    delSubnet.put("LocalSubnet"+(i + 1),(i + 1));
	    Configuration.delSubnetConf(delSubnet);
	} 		
	
    // ------------------------------------------------------------------
	
	public static Test suite()
	{
		return new TestSuite(TestMultiSubnets.class);
	}
}
