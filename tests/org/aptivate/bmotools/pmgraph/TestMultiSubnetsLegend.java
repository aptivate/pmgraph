package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * Test the legend and each parameter within it (hostname, order by, limit...)
 * 
 * @author franciscor and pablob
 * 
 */
public class TestMultiSubnetsLegend extends LegendTestBase
{
	private long timeInMinutes = (System.currentTimeMillis() / 60000);

	public TestMultiSubnetsLegend() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		super();
	}

	/* This test tests the legend table in the pmGraph page */
	public void testCheckDataTranslationAndRepresentation() throws Exception
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(250000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.255", "10.0.156.10", false);
			testUtils.insertNewRow(500000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.10", "224.0.0.255", false);

			testUtils.insertNewRow(200000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.251", "10.0.156.1", false);
			testUtils.insertNewRow(100000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.1", "224.0.0.251", false);			
			
			testUtils.insertNewRow(200000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.251", "10.0.223.15", false);
			testUtils.insertNewRow(100000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.223.15", "224.0.0.251", false);	
			
			testUtils.insertNewRow(200000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.255", "10.1.223.8", false);
			testUtils.insertNewRow(100000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.1.223.8", "224.0.0.255", false);	
		}		

		// Obtain the upload page on the website
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph());
		WebResponse response = m_conversation.getResponse(request);
		
		// Get the table data from the page
		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		long uploaded[] = { 47, 9 };
		long downloaded[] = { 23, 19 };
		String ips[] = { "10.0.156.10", "10.0.156.1" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_IP);
	}
	
	public void testGroupsDataInLegend() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, SAXException
	{
		TestUtils theTestUtils = new TestUtils();
		// Add a group
		WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp?addGroup=Test");
	    WebResponse response = m_conversation.getResponse(request);
	    request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "/include/groups.jsp?Group=Test");
	    // Add IPs to group
	    response = m_conversation.getResponse(request);
	    WebForm form = response.getFormWithID("config");
	    form.setCheckbox("addIp1", true);
	    form.setCheckbox("addIp2", true);
	    Button submitButton = form.getButtonWithID("save_conf3");
	    submitButton.click();
	    
        // Load main page and get the legend table
		request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph());
	    response = m_conversation.getResponse(request);
	    WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);
	    
	    long uploaded[] = { 47, 9, 47, 9 };
		long downloaded[] = { 23, 19, 23, 19 };
		String ips[] = { "10.0.156.10", "10.0.156.1", "10.0.156.10", "10.0.156.1" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_IP);
				
		request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp");
		response = m_conversation.getResponse(request);
		form = response.getFormWithID("config");
	    form.setCheckbox("delGroup1", true);
	    form.submit();		
	}

	public static Test suite()
	{
		return new TestSuite(TestMultiSubnetsLegend.class);
	}

}