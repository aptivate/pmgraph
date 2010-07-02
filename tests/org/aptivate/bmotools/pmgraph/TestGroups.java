package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * Test the group funcionallity
 * 
 * @author pablob
 * 
 */
public class TestGroups extends PmGraphTestBase
{
	public TestGroups() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		super();
	}

	private static final int SIZE_HEADS = 1;
	List<String> Groups = Configuration.getGroups();
	
	public void testCheckGroups() throws Exception
	{						
		TestUtils testUtils = new TestUtils();
		WebResponse response = loadUrl(testUtils);
		WebTable table = response.getTableWithID("TableGroups");
		if (Groups.size() > 0)
		{
			for (int i = 1; i < table.getRowCount(); i++)		
				assertTrue(table.getCellAsText(i, 0).contains(Groups.get(i-1)));
		}
	}
	
	
	//---------------------------------------------------------------
	
	public void testNewGroup() throws Exception
	{
		TestUtils theTestUtils = new TestUtils();
		WebResponse response = loadUrl (theTestUtils);
		WebTable table = response.getTableWithID("TableGroups");
		int oldNumGroups;
		if (Groups.size() > 0)
			oldNumGroups = table.getRowCount() - SIZE_HEADS;
		else
			oldNumGroups = table.getRowCount() - SIZE_HEADS -1;
					
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp?addGroup=Test");
	    response = m_conversation.getResponse(request);
	    table = response.getTableWithID("TableGroups");	    
	    int newNumGroups = table.getRowCount() - SIZE_HEADS;
	    assertTrue(oldNumGroups < newNumGroups);	
	    
	    request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp?addGroup=Test");	    
	    response = m_conversation.getResponse(request);	   	    
	    HTMLElement result = response.getElementWithID("unsuccessResult");
	    String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
	    assertTrue(resultString.equals("The new group is already in the configure file"));
	    
	    request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp?Group1=Test&delGroup1=delGroup1&numGroups="+Groups.size());	    
	    response = m_conversation.getResponse(request);
	    table = response.getTableWithID("TableGroups");	
	    if (Groups.size() > 0)
	    	newNumGroups = table.getRowCount() - SIZE_HEADS;
		else
			newNumGroups = table.getRowCount() - SIZE_HEADS -1;	    
	    assertTrue(oldNumGroups == newNumGroups);	    	    	    
	}	
	
	//---------------------------------------------------------------
		
	public void testCheckIpGroup() throws Exception
	{		
		boolean deleteGroup = false;
		if (Groups.isEmpty())
		{
			deleteGroup = true;
			Configuration.updateGroups("Test", null, null, Groups, null);
			Groups.add("Test");
		}
		
		// Check Group's Ips		
		String group = Groups.get(0);		
		TestUtils theTestUtils = new TestUtils();
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "/include/groups.jsp?Group="+group);
	    WebResponse response = m_conversation.getResponse(request);
		List<String> IpsGroup = Configuration.getIpsGroup(group);
		WebTable table = response.getTableWithID("IpsGroup");
		if (IpsGroup.size() > 0)
			assertTrue(table.getRowCount() - SIZE_HEADS == IpsGroup.size());
		else
			assertTrue(table.getRowCount() - SIZE_HEADS -1 == IpsGroup.size());
		if (IpsGroup.size() > 0)
		{
			for (int i = 1; i < table.getRowCount(); i++)
				assertTrue(table.getCellAsText(i, 0).equals(IpsGroup.get(i-1)));
		}
		
		// Check the rest of Ips
		String[] Ips = {"224.0.0.255", "10.0.156.10", "224.0.0.251", "10.0.156.1", "10.0.223.15", "10.1.223.8"};
		List<String> allIps = new ArrayList<String>();
		for (int i = 0; i < Ips.length; i++)
			allIps.add(Ips[i]);		
		List<String> nIpsGroup = Configuration.getNIpsGroup(IpsGroup, allIps);
		table = response.getTableWithID("nIpsGroup");
		if (nIpsGroup.size() > 0)
			assertTrue(table.getRowCount() - SIZE_HEADS == nIpsGroup.size());
		else
			assertTrue(table.getRowCount() - SIZE_HEADS -1 == nIpsGroup.size());
		if (nIpsGroup.size() > 0)
		{
			for (int i = 1; i < table.getRowCount(); i++)
				assertTrue(table.getCellAsText(i, 0).equals(nIpsGroup.get(i-1)));
		}
		
		// Check "adding a new IP"
		WebForm configurationForm = response.getFormWithID("config");		
		int numIps = Integer.parseInt(configurationForm.getParameterValue("numIps"));
		int numIp;
		if (!nIpsGroup.isEmpty())
		{
			numIp = nIpsGroup.size();
			String newIp = nIpsGroup.get(numIp-1);			
			request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "/include/groups.jsp?newIp"+numIp+"="+newIp+"&addIp"+numIp+"=addIp"+numIp+"&numIps="+numIps+"&Group="+group);	    
			response = m_conversation.getResponse(request);	   	    
			HTMLElement result = response.getElementWithID("successResult");
			String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
			assertTrue(resultString.equals("Update Done"));
			table = response.getTableWithID("IpsGroup");			
			assertTrue(table.getRowCount() - SIZE_HEADS > IpsGroup.size());	
			IpsGroup.add(newIp);
		}
		
		// Check "deleting a IP"
		if (!IpsGroup.isEmpty()) {
			numIp = IpsGroup.size();
			String delIp = IpsGroup.get(numIp-1);
			request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "/include/groups.jsp?Ip"+numIp+"="+delIp+"&delIp"+numIp+"=delIp"+numIp+"&numIps="+numIps+"&Group="+group);				   
			response = m_conversation.getResponse(request);	   	    
			HTMLElement result = response.getElementWithID("successResult");
			String resultString =  result.getNode().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
			assertTrue(resultString.equals("Update Done"));			
			table = response.getTableWithID("nIpsGroup");
			assertTrue(table.getRowCount() - SIZE_HEADS == nIpsGroup.size());
		}
		
		if (deleteGroup) 
		{
			Configuration.delGroup("Test");
			Groups.clear();
		}
	}
	
	
//---------------------------------------------------------------
	
	public static Test suite()
	{
		return new TestSuite(TestGroups.class);
	}	
}