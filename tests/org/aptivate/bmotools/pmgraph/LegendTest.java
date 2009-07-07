package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * Test the legend and each parameter within it (hostname, order by, limit...)
 * 
 * @author noeg
 * 
 */
public class LegendTest extends LegendTestBase
{
	private static Logger m_logger = Logger.getLogger(LegendTest.class.getName());

	private long timeInMinutes = (System.currentTimeMillis() / 60000);

	public LegendTest() throws InstantiationException, IllegalAccessException,
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
			// TODO why -5?
			testUtils.insertNewRow(500000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.255", "10.0.156.10");
			testUtils.insertNewRow(500000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.10", "224.0.0.255");

			testUtils.insertNewRow(100000, new Timestamp((timeInMinutes - 5) * 60000),
					"224.0.0.251", "10.0.156.1");
			testUtils.insertNewRow(100000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.1", "224.0.0.251");

			testUtils.insertNewRow(500000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.110", "10.0.156.120");
			testUtils.insertNewRow(500000, new Timestamp((timeInMinutes - 5) * 60000),
					"10.0.156.120", "10.0.156.110");
		}

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on the website
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph());
		WebResponse response = wc.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		long uploaded[] = { 47, 9 };
		long downloaded[] = { 47, 9 };
		String ips[] = { "10.0.156.10", "10.0.156.1" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_IP);

	}

	/**
	 * This test tests that there is not crash in legend.jsp when working with
	 * large numbers
	 * 
	 * @throws Exception
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
				// 1 << 30 means that the int 1 is being diplaced 30 bit to the
				// left (it is a fast way to get huge numbers, for example 1<<10
				// =
				// 1024)
				testUtils.insertNewRow(1 << 30, new Timestamp((timeInMinutes - 5) * 60000),
						"224.0.0.251", "10.0.156.1");
				testUtils.insertNewRow(1 << 30, new Timestamp((timeInMinutes - 5) * 60000),
						"10.0.156.1", "224.0.0.251");
			}

			// Open a graph page
			// Create a conversation
			WebConversation wc = new WebConversation();

			// Obtain the upload page on web site
			WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph());
			WebResponse response = wc.getResponse(request);
			String path = response.getImageWithAltText("Bandwidth Graph").getSource();
			URL urlObj = new URL(testUtils.getUrlPmgraph());
			urlObj = new URL(urlObj, path);
			request = new GetMethodWebRequest(urlObj.toString());
			response = wc.getResponse(request);
			assertEquals("image/png", response.getContentType());
		} catch (HttpInternalErrorException e)
		{
			m_logger.error("Problem with legend.jsp. There is value too big for integer.");
			throw (e);
		}
	}

	/**
	 * Test hostname for different IPs
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */

	public void testHostName() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException, SAXException
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(500000, testUtils.t1, "224.0.0.255", "10.0.156.10");
			testUtils.insertNewRow(300000, testUtils.t1, "10.0.156.10", "224.0.0.254");

			testUtils.insertNewRow(100000, testUtils.t1, "224.0.0.251", "10.0.156.22");
			testUtils.insertNewRow(150000, testUtils.t2, "10.0.156.22", "224.0.0.251");

			testUtils.insertNewRow(400000, testUtils.t2, "224.0.0.255", "10.0.156.33");
			testUtils.insertNewRow(1140000, testUtils.t2, "10.0.156.33", "224.0.0.255");
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
		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

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

	/**
	 * This test test the "sort by" option
	 * 
	 * @throws Exception
	 */
	public void testSorter() throws Exception
	{

		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			testUtils.insertNewRow(500000, testUtils.t1, "224.0.0.255", "10.0.156.10");
			testUtils.insertNewRow(300000, testUtils.t1, "10.0.156.10", "224.0.0.254");

			testUtils.insertNewRow(100000, testUtils.t1, "224.0.0.251", "10.0.156.1");
			testUtils.insertNewRow(150000, testUtils.t2, "10.0.156.1", "224.0.0.251");

			testUtils.insertNewRow(400000, testUtils.t2, "224.0.0.255", "10.0.156.120");
			testUtils.insertNewRow(1140000, testUtils.t2, "10.0.156.120", "224.0.0.255");
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
		String sortLink = "index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_IP";

		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15");
		response = wc.getResponse(request);
		link = response.getLinkWithName("downloaded");
		sortLink = "index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_IP";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		// Get the table data from the page
		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		if (table != null)
		{
			long downloaded[] = { 9, 38, 47 };
			long uploaded[] = { 14, 108, 28 };
			String ips[] = { "10.0.156.1", "10.0.156.120", "10.0.156.10" };
			checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_IP);

		}

		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC&resultLimit=15");
		response = wc.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC&resultLimit=15&dynamic=false&view=LOCAL_IP";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);
		if (table != null)
		{

			long downloaded[] = { 38, 47, 9 };
			long uploaded[] = { 108, 28, 14 };
			String ips[] = { "10.0.156.120", "10.0.156.10", "10.0.156.1" };
			checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_IP);
			// Check the table data
			// .10 47 = 500000*100/1024/1024 28
			// .120 38 = 400000*100/1024/1024 108
			// .1 9 = 100000*100/1024/1024 14

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
	public void testLimitResults() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException, SAXException
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
		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		if (table != null)
		{
			assertEquals("Check the number of rows is limited to default value.", Configuration
					.getResultLimit(), (Integer) (table.getRowCount() - 3));

			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded, average downloaded, average uploaded
			// (the last two are checked in other test)
			// Get data from the last row and check if it contains the
			// information for the resta of Ip's
			String hostIP = table.getCellAsText(Configuration.getResultLimit() + 2, 1);
			String downloaded = table.getCellAsText(Configuration.getResultLimit() + 2, 3);
			String uploaded = table.getCellAsText(Configuration.getResultLimit() + 2, 4);
			assertEquals("Check the IP Address", "Others", hostIP);
			assertEquals("Check the Downloaded Value", "<1", downloaded);
			assertEquals("Check the Upload Value", "23", uploaded);
		}
		
		// Check a result limit defined by the user
		request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=8");
		response = wc.getResponse(request);
		// get the table
		table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		if (table != null)
		{
			assertEquals("Check the number of rows is limited to default value.", (Integer) 8,
					(Integer) (table.getRowCount() - 3));

			// Columns in the table are Color,Host IP,Host
			// Name,Downloaded,Uploaded
			// Get data from the last row and check if it contains the
			// information for the rest of Ip's
			String hostIP = table.getCellAsText(8 + 2, 1);
			String downloaded = table.getCellAsText(8 + 2, 3);
			String uploaded = table.getCellAsText(8 + 2, 4);
			assertEquals("Check the IP Address", "Others", hostIP);
			assertEquals("Check the Downloaded Value", "<1", downloaded);
			assertEquals("Check the Upload Value", "8", uploaded);
		}

	}

	/**
	 * test the case where you have limit=1 when you are looking into a specific port
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	public void testLimitResultsSpecificPort() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException, SQLException, SAXException
	{
		WebConversation wc = new WebConversation();
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=1&port=110");
		WebResponse response = wc.getResponse(request);

		WebTable table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);
		long uploaded[] = { 4, 0 };
		long downloaded[] = { 5, 4 };
		String ips[] = { "10.0.156.110", "Others" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ips, View.LOCAL_PORT);
	}

	public static Test suite()
	{
		return new TestSuite(LegendTest.class);
	}

}
