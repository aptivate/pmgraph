package org.aptivate.pmGraph.test;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.aptivate.bmotools.pmgraph.Configuration;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * 
 * @author noeg
 * 
 */
public class LegendTest extends TestCase
{
	private static Logger m_logger = Logger.getLogger(LegendTest.class
			.getName());
	private long l = (System.currentTimeMillis() / 60000);
	
	

	/* This test tests the legend table in the pmGraph page */
	public void testCheckDataTranslationAndRepresentation() throws Exception
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"224.0.0.255", "10.0.156.10");
			testUtils.insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"10.0.156.10", "224.0.0.255");

			testUtils.insertNewRow(100000, new Timestamp((l - 5) * 60000),
					"224.0.0.251", "10.0.156.1");
			testUtils.insertNewRow(100000, new Timestamp((l - 5) * 60000),
					"10.0.156.1", "224.0.0.251");

			testUtils.insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"10.0.156.110", "10.0.156.120");
			testUtils.insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"10.0.156.120", "10.0.156.110");
		}

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph());
		WebResponse response = wc.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response.getElementWithID("legend_tbl");

		// Row 2
		// Columns in the table are Color,Host IP,Host Name,Downloaded,Uploaded
		String hostIP1 = table.getCellAsText(2, 1);
		String downloaded1 = table.getCellAsText(2, 3);
		String uploaded1 = table.getCellAsText(2, 4);

		// Row 3
		String hostIP2 = table.getCellAsText(3, 1);
		String downloaded2 = table.getCellAsText(3, 3);
		String uploaded2 = table.getCellAsText(3, 4);

		// Check the table data
		assertEquals("Check the IP Address", hostIP1, "10.0.156.10");
		assertEquals("Check the Downloaded Value", downloaded1, "47");
		assertEquals("Check the Downloaded Value", uploaded1, "47");
		assertEquals("Check the IP Address", hostIP2, "10.0.156.1");
		assertEquals("Check the Downloaded Value", downloaded2, "9");
		assertEquals("Check the Downloaded Value", uploaded2, "9");
	}

	/*
	 * This test tests that there is not crash in legend.dsp when working with
	 * large numbers
	 */
	public void testLargeValuesDoNotCrashLegendJsp() throws Exception
	{
		try
		{
			TestUtils testUtils = new TestUtils();
			testUtils.CreateTable();

			// Insert rows into table
			for (int i = 0; i < 2; i++)
			{
				// Set the values
				testUtils.insertNewRow(1 << 30, new Timestamp((l - 5) * 60000),
						"224.0.0.251", "10.0.156.1");
				testUtils.insertNewRow(1 << 30, new Timestamp((l - 5) * 60000),
						"10.0.156.1", "224.0.0.251");
			}

			// Open a graph page
			// Create a conversation
			WebConversation wc = new WebConversation();

			// Obtain the upload page on web site
			WebRequest request = new GetMethodWebRequest(testUtils
					.getUrlPmgraph());
			WebResponse response = wc.getResponse(request);
			String path = response.getImageWithAltText("Bandwidth Graph")
					.getSource();
			URL urlObj = new URL(testUtils.getUrlPmgraph());
			urlObj = new URL(urlObj, path);
			request = new GetMethodWebRequest(urlObj.toString());
			response = wc.getResponse(request);
			assertEquals("image/png", response.getContentType());
		}
		catch (HttpInternalErrorException e)
		{
			m_logger
					.error("Problem with legend.jsp. There is value too big for integer.");
			throw (e);
		}
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	public void testHostName() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException, SAXException
	{

		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(500000, testUtils.t1, "224.0.0.255",
					"10.0.156.10");
			testUtils.insertNewRow(300000, testUtils.t1, "10.0.156.10",
					"224.0.0.254");

			testUtils.insertNewRow(100000, testUtils.t1, "224.0.0.251",
					"10.0.156.22");
			testUtils.insertNewRow(150000, testUtils.t2, "10.0.156.22",
					"224.0.0.251");

			testUtils.insertNewRow(400000, testUtils.t2, "224.0.0.255",
					"10.0.156.33");
			testUtils.insertNewRow(1140000, testUtils.t2, "10.0.156.33",
					"224.0.0.255");
		}

		String hostname1 = "fen-ndiyo3.fen.aptivate.org.";
		String hostname2 = "ap.int.aidworld.org.";
		String hostname3 = "Unknown Host";
		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15");
		WebResponse response = wc.getResponse(request);

		// get the table
		WebTable table = (WebTable) response.getElementWithID("legend_tbl");

		if (table != null)
		{
			// Row 2
			String hostN1 = table.getCellAsText(2, 2);
			assertEquals("Check the host name", hostname1, hostN1);
			// Row 3
			String hostN2 = table.getCellAsText(3, 2);
			assertEquals("Check the host name", hostname2, hostN2);
			// Row 4
			String hostN3 = table.getCellAsText(4, 2);
			assertEquals("Check the host name", hostname3, hostN3);
		}
	}

	public void testSorter() throws Exception
	{

		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(500000, testUtils.t1, "224.0.0.255",
					"10.0.156.10");
			testUtils.insertNewRow(300000, testUtils.t1, "10.0.156.10",
					"224.0.0.254");

			testUtils.insertNewRow(100000, testUtils.t1, "224.0.0.251",
					"10.0.156.1");
			testUtils.insertNewRow(150000, testUtils.t2, "10.0.156.1",
					"224.0.0.251");

			testUtils.insertNewRow(400000, testUtils.t2, "224.0.0.255",
					"10.0.156.120");
			testUtils.insertNewRow(1140000, testUtils.t2, "10.0.156.120",
					"224.0.0.255");
		}

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15");
		WebResponse response = wc.getResponse(request);

		WebLink link = response.getLinkWithName("downloaded");

		// the default is 'sort by download DESC', the sortLink is opposite to
		// the DESC
		String sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15";

		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15");
		response = wc.getResponse(request);
		link = response.getLinkWithName("downloaded");
		sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		// Get the table data from the page
		WebTable table = (WebTable) response.getElementWithID("legend_tbl");

		if (table != null)
		{
			// Row 2
			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded
			String hostIP1 = table.getCellAsText(2, 1);
			String downloaded1 = table.getCellAsText(2, 3);
			String uploaded1 = table.getCellAsText(2, 4);
			// Row 3
			String hostIP2 = table.getCellAsText(3, 1);
			String downloaded2 = table.getCellAsText(3, 3);
			String uploaded2 = table.getCellAsText(3, 4);
			// Row 4
			String hostIP3 = table.getCellAsText(4, 1);
			String downloaded3 = table.getCellAsText(4, 3);
			String uploaded3 = table.getCellAsText(4, 4);

			// Check the table data
			// .10 47 = 500000*100/1024/1024 28
			// .120 38 = 400000*100/1024/1024 108
			// .1 9 = 100000*100/1024/1024 14
			assertEquals("Check the IP Address", "10.0.156.10", hostIP3);
			assertEquals("Check the Downloaded Value, r1", "47", downloaded3);
			assertEquals("Check the Downloaded Value", "28", uploaded3);
			assertEquals("Check the IP Address", "10.0.156.120", hostIP2);
			assertEquals("Check the Downloaded Value r2", "38", downloaded2);
			assertEquals("Check the Downloaded Value", "108", uploaded2);
			assertEquals("Check the IP Address", "10.0.156.1", hostIP1);
			assertEquals("Check the Downloaded Value r3", "9", downloaded1);
			assertEquals("Check the Downloaded Value", "14", uploaded1);
		}

		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC&resultLimit=15");
		response = wc.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC&resultLimit=15";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		table = (WebTable) response.getElementWithID("legend_tbl");
		if (table != null)
		{
			// Row 2
			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded
			String hostIP1 = table.getCellAsText(2, 1);
			String downloaded1 = table.getCellAsText(2, 3);
			String uploaded1 = table.getCellAsText(2, 4);
			// Row 3
			String hostIP2 = table.getCellAsText(3, 1);
			String downloaded2 = table.getCellAsText(3, 3);
			String uploaded2 = table.getCellAsText(3, 4);
			// Row 4
			String hostIP3 = table.getCellAsText(4, 1);
			String downloaded3 = table.getCellAsText(4, 3);
			String uploaded3 = table.getCellAsText(4, 4);

			// Check the table data
			// .10 47 = 500000*100/1024/1024 28
			// .120 38 = 400000*100/1024/1024 108
			// .1 9 = 100000*100/1024/1024 14
			assertEquals("Check the IP Address", "10.0.156.120", hostIP1);
			assertEquals("Check the Downloaded Value, r1", "38", downloaded1);
			assertEquals("Check the Uploaded Value", "108", uploaded1);
			assertEquals("Check the IP Address", "10.0.156.10", hostIP2);
			assertEquals("Check the Downloaded Value r2", "47", downloaded2);
			assertEquals("Check the Uploaded Value", "28", uploaded2);
			assertEquals("Check the	IP Address", "10.0.156.1", hostIP3);
			assertEquals("Check the Downloaded Value r3", "9", downloaded3);
			assertEquals("Check the Uploaded Value", "14", uploaded3);
		}
	}
	
	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	public void testLimitResults() throws ClassNotFoundException,
	IllegalAccessException, InstantiationException, IOException,
	SQLException, SAXException
		{
		
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		testUtils.InsertSampleData();
		
		// check the default limit of result
		
		// Create a conversation
		WebConversation wc = new WebConversation();
		
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000");
		WebResponse response = wc.getResponse(request);
		
		// get the table
		WebTable table = (WebTable) response.getElementWithID("legend_tbl");
		
		if (table != null)
		{
			assertEquals("Check the number of rows is limited to default value.", Configuration.getResultLimit(),(Integer)(table.getRowCount()-3));
			
			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded
			// Get data from the last row and check if it contains the information for the resta of Ip's 
			String hostIP = table.getCellAsText(Configuration.getResultLimit() + 2, 1);
			String downloaded = table.getCellAsText(Configuration.getResultLimit() + 2, 3);
			String uploaded = table.getCellAsText(Configuration.getResultLimit() + 2, 4);
			assertEquals("Check the IP Address", "255.255.255.255", hostIP);
			assertEquals("Check the Downloaded Value", "0", downloaded);
			assertEquals("Check the Upload Value", "23", uploaded);
		}
		// Check a user defined limit of results.

		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=8");
		response = wc.getResponse(request);
//		 get the table
		table = (WebTable) response.getElementWithID("legend_tbl");
		
		if (table != null)
		{
			assertEquals("Check the number of rows is limited to default value.",(Integer)8,(Integer)(table.getRowCount()-3));
			
			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded
			// Get data from the last row and check if it contains the information for the resta of Ip's 
			String hostIP = table.getCellAsText(8 + 2, 1);
			String downloaded = table.getCellAsText(8 + 2, 3);
			String uploaded = table.getCellAsText (8 + 2, 4);
			assertEquals("Check the IP Address", "255.255.255.255", hostIP);
			assertEquals("Check the Downloaded Value", "0", downloaded);
			assertEquals("Check the Upload Value", "8", uploaded);
		}
		
	}
	

	public static Test suite()
	{
		return new TestSuite(LegendTest.class);
	}

}
