package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * Test for the legend table when a port view has been chosen
 * 
 * @author Noe Andres Rodriguez Gonzalez
 * 
 */
public class LegendTestPortView extends PmGraphTestBase
{
	protected TestUtils m_testUtils;
	final long bitsConversion = 1024*8;
	// time period in seconds from milliseconds
	final long time = 300000/1000;
	
	public LegendTestPortView() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{

		m_testUtils = new TestUtils();
	}

	public void setUp() throws Exception
	{
		m_testUtils.CreateTable();
		m_testUtils.InsertPortsSampleData();
		m_testUtils.InsertLongPortsSampleData();
	}

	/**
	 * Just check if the donwloaded and uploaded values of a legend table match
	 * the values specified by the parameters.
	 * 
	 * @param table
	 * @param downloaded
	 * @param uploaded
	 * @param ipPort
	 * @throws IOException
	 * @throws SAXException
	 */
	private void checkUploadDownloadLegendTable(WebTable table,
			long downloaded[], long uploaded[], String ipPort[],
			String serviceHostName[], String services[], long averageDownloaded[], long averageUploaded[])
			throws IOException, SAXException
	{

		// Check the table data
		String upload, download, averageUpload, averageDownload;
		// It is i - 2 to avoid the headers in the table
		for (int i = 2; i < table.getRowCount(); i++)
		{
			int column= 1;
			assertEquals("Check the IP Or Port Address", ipPort[i - 2], table
					.getCellAsText(i, column++));
			
			if (table.getColumnCount() == 8) {
				assertEquals("Check the protocol", String
						.valueOf( services[i - 2]), table.getCellAsText(i, column++));				
			}
			
			
			assertEquals("Check the service or Host Name", String
					.valueOf( serviceHostName[i - 2]), table.getCellAsText(i, column++));
			
			
			
			if (downloaded[i - 2] == 0)
				download ="<1";
			else
				download =String.valueOf (downloaded[i - 2]); 
			assertEquals("Check the Downloaded Value", 
					download, table.getCellAsText(i, column++));

			if (uploaded[i - 2] == 0)
				upload ="<1";
			else
				upload =String.valueOf (uploaded[i - 2]);
			
			assertEquals("Check the Uploaded Value", 
					upload, table.getCellAsText(i, column++));
			
			if (averageDownloaded[i - 2] == 0)
				averageDownload ="<1";
			else
				averageDownload =String.valueOf (averageDownloaded[i - 2]); 
			assertEquals("Check the Downloaded average Value", 
					averageDownload, table.getCellAsText(i, column++));

			if (averageUploaded[i - 2] == 0)
				averageUpload ="<1";
			else
				averageUpload =String.valueOf (averageUploaded[i - 2]);
			
			assertEquals("Check the Uploaded average Value", 
					averageUpload, table.getCellAsText(i, column));
		}
	}

	/* This test tests the legend table in the pmGraph page */
	public void testLegendPortView() throws Exception
	{
		// port graph		
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&resultLimit=15&view=LOCAL_PORT");
		WebResponse response = m_conversation.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);
		
		long[] downloaded = { 9750, 1687, 4125, 562};
		long[] uploaded = { 6000, 9375, 6750, 0};

		
		/* The values for upload and download shown on the screen and tested for above have 
		 been truncated so using these values to calculate the average does not work
		 (is 9 really 9.3 or 9.6 when used in the calculation ?).  So for the purposes of having 
		 a test that checks against future changes, the expected values have been set to match the actual 
		 values we get when the test is run
		*/
		
		long[] averageDownloaded= { 260, 45, 110 ,15};
		long[] averageUploaded = { 160, 250, 180, 0};

		String ports[] = { "110",  "80", "443", "443"};
		String portName[] = { "pop3", "http", "https", "https"};
		String services[] = {"tcp", "tcp", "tcp", "udp"};
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports,portName, services, averageDownloaded, averageUploaded);		
	}

	/**
	 * Check if the sorting works when the view is a port view
	 * 
	 * @throws Exception
	 */
	public void testSorterPortView() throws Exception
	{		
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		WebResponse response = m_conversation.getResponse(request);
		WebLink link = response.getLinkWithName("downloaded");
		// the default is 'sort by download DESC', the sortLink is opposite to
		// the DESC
		String sortLink = "index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		response = m_conversation.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);

		long uploaded[] = { 0, 9375, 6750, 6000 };
		long downloaded[] = { 562, 1687, 4125, 9750 };
		String ports[] =  { "443", "80", "443", "110" };
		String portName[] = {"https","http", "https", "pop3" };
		String services[] = {"udp", "tcp", "tcp", "tcp"};
		
		/* The values for upload and download shown on the screen and tested for above have 
		 been truncated so using these values to calculate the average does not work
		 (is 9 really 9.3 or 9.6 when used in the calculation ?).  So for the purposes of having 
		 a test that checks against future changes, the expected values have been set to match the actual 
		 values we get when the test is run
		*/
		
		long[] averageDownloaded= { 15, 45, 110,260};
		long[] averageUploaded = {0, 250, 180, 160};
		
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services, averageDownloaded, averageUploaded);

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		response = m_conversation.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC&resultLimit=15&dynamic=false&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		uploaded = new long[] {9375, 6750, 6000, 0};
		downloaded = new long[] { 1687, 4125, 9750, 562 };
		
		ports = new String[] { "80", "443", "110", "443" };
		portName = new String[] {"http","https", "pop3", "https" };
		services = new String[] {"tcp", "tcp", "tcp", "udp"};
		
		averageDownloaded= new long[] {45, 110,260, 15};
		averageUploaded = new long[] {250, 180, 160, 0};

		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services, averageDownloaded, averageUploaded);

	}

	/**
	 * Check if the limit results works when the view is a port view.
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
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&resultLimit=1&view=LOCAL_PORT");
		WebResponse response = m_conversation.getResponse(request);

		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);
		long uploaded[] = { 6000, 16125 };
		long downloaded[] = { 9750, 6375 };
		String ports[] = { "110", "Others" };
		String portName[] = {"pop3", "" };
		String services[] = {"tcp", ""};
		
		/* The values for upload and download shown on the screen and tested for above have 
		 been truncated so using these values to calculate the average does not work
		 (is 9 really 9.3 or 9.6 when used in the calculation ?).  So for the purposes of having 
		 a test that checks against future changes, the expected values have been set to match the actual 
		 values we get when the test is run
		*/
		
		long averageDownloaded[] = {260, 170};
		long averageUploaded[] = {160, 430};

		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services, averageDownloaded, averageUploaded);
	}

	public static Test suite()
	{
		return new TestSuite(LegendTestPortView.class);
	}

}
