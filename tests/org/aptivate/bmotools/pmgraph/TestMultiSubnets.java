package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.meterware.httpunit.FormControl;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
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
public class TestMultiSubnets extends PmGraphTestBase
{
	public TestMultiSubnets() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		super();
	}

	final long HOUR = 60 * 60 * 1000;
	final long DAY = 24 * HOUR;
	
	private static final int SIZE_HEADS = 1;
	
	public void testCheckSubnet() throws InstantiationException, IllegalAccessException, 
	        ClassNotFoundException, SQLException, IOException, SAXException
	{
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = loadUrl(theTestUtils);  
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
		String[] vectSubnets = Configuration.getLocalSubnet();
		
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = loadUrl(theTestUtils);
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
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = loadUrl (theTestUtils);
		WebForm configurationForm = response.getFormWithID("config");
		WebTable table = response.getTableWithID("TableLocalSubnets");
		int oldNumSubnets = table.getRowCount() - SIZE_HEADS;
		String newSubnet = "99.99.99.";
		configurationForm.setParameter("newSubnet", newSubnet);
		configurationForm.submit();
		
		theTestUtils = new TestUtils();
		response = loadUrl (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		int numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue(numSubnets > oldNumSubnets);
     	assertEquals(table.getCellAsText(numSubnets,0), newSubnet);
     	
     	configurationForm = response.getFormWithID("config");
	    FormControl aux = configurationForm.getControlWithID("delSubnet"+numSubnets);
		aux.toggle();
		configurationForm.submit();	
		
		response = loadUrl (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue(numSubnets == oldNumSubnets);
	}

	// -----------------------------------------------------------------		
	

	public void testFormatSubnet() throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, SQLException, IOException, SAXException
	{	
		TestUtils theTestUtils = new TestUtils();		
		WebResponse response = loadUrl (theTestUtils);
		WebForm configurationForm = response.getFormWithID("config");
		
		int numSubnets = Integer.parseInt(configurationForm.getParameterValue("numSubnets"));
		String addSubnet[] = {"10.40.255.", "10.1A.123.", "1001.1.3"};
		
		String Url[] = new String[3];
        for (int i = 0; i < 3; i++)
            Url[i] = theTestUtils.getUrlPmgraph() + "configure.jsp?newSubnet="+addSubnet[i]+"&selectSubnet=10.0.156.&numSubnets="+numSubnets+"&Go=Save+configuration";				       	      
	    WebRequest request = new GetMethodWebRequest(Url[2]);
	    response = m_conversation.getResponse(request);
	    HTMLElement result = response.getElementWithID("unsuccessResult");
	    String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255."));
	       
	    request = new GetMethodWebRequest(Url[1]);
	    response = m_conversation.getResponse(request);
	    result = response.getElementWithID("unsuccessResult");
	    resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255."));
	       
	    request = new GetMethodWebRequest(Url[0]);
	    response = m_conversation.getResponse(request);
	    result = response.getElementWithID("successResult");
	    resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("Update Done"));
	       
	    configurationForm = response.getFormWithID("config");
	    FormControl aux = configurationForm.getControlWithID("delSubnet"+ (numSubnets + 1));
		aux.toggle();
		configurationForm.submit();			
	} 		
	
    // ------------------------------------------------------------------
	
	public void testFormatSubnetZeros() throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, SQLException, IOException, SAXException
	{	
		TestUtils theTestUtils = new TestUtils();		
		WebResponse response = loadUrl (theTestUtils);
		WebForm configurationForm = response.getFormWithID("config");
		WebTable table = response.getTableWithID("TableLocalSubnets");
		int oldNumSubnets = table.getRowCount() - SIZE_HEADS;
		String newSubnet = "99.99.99.";		
		configurationForm.setParameter("newSubnet", newSubnet);
		configurationForm.submit();		
		String newSubnet2 = "000.015.255.";
		configurationForm.setParameter("newSubnet", newSubnet2);
		configurationForm.submit();		
		
		theTestUtils = new TestUtils();
		response = loadUrl (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		int numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue(numSubnets > oldNumSubnets);
     	assertEquals(table.getCellAsText((numSubnets-1),0), newSubnet);
     	assertEquals(table.getCellAsText(numSubnets,0), "0.15.255.");
     	
     	WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp?newSubnet=099.099.099.&selectSubnet=10.0.156.&numSubnets="+numSubnets+"&Go=Save+configuration");
	    response = m_conversation.getResponse(request);
	    HTMLElement result = response.getElementWithID("unsuccessResult");
	    String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("The new subnet is already in the configure file"));
									     
     	configurationForm = response.getFormWithID("config");
	    FormControl aux = configurationForm.getControlWithID("delSubnet"+numSubnets);
		aux.toggle();
		aux = configurationForm.getControlWithID("delSubnet"+(numSubnets-1));
		aux.toggle();
		configurationForm.submit();	
		
		response = loadUrl (theTestUtils);
		table = response.getTableWithID("TableLocalSubnets");
		numSubnets = table.getRowCount() - SIZE_HEADS;
		assertTrue(numSubnets == oldNumSubnets);
	}	
	
	//------------------------------------------------------------------
	
	public void testDeleteAllSubnets() throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, SQLException, IOException, SAXException
	{
		TestUtils theTestUtils = new TestUtils();		
		WebResponse response = loadUrl (theTestUtils);
		WebForm configurationForm = response.getFormWithID("config");
		
		int numSubnets = Integer.parseInt(configurationForm.getParameterValue("numSubnets"));
		String aux = "?";
		for (int i = 1; i <= numSubnets; i++)
			aux += "delSubnet"+i+"=delSubnet"+i+"&";
		aux +="numSubnets="+numSubnets+"&newSubnet=&Go=Save+configuration";			
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp"+aux);
	    response = m_conversation.getResponse(request);
		
		configurationForm = response.getFormWithID("config");
		int newNumSubnets = Integer.parseInt(configurationForm.getParameterValue("numSubnets"));
		assertEquals(numSubnets, newNumSubnets);		
	    HTMLElement result = response.getElementWithID("unsuccessResult");
	    String resultString = result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("You can't delete all the subnets"));
	}
	
	// ------------------------------------------------------------------
	
	public static Test suite()
	{
		return new TestSuite(TestMultiSubnets.class);
	}
}
