package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * 
 * @author Noe Andres Rodriguez Gonzalez
 * 
 */
public class LegendTestPortView extends TestCase
{
	protected TestUtils m_testUtils;

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
			String serviceHostName[], String services[])
			throws IOException, SAXException
	{

		// Check the table data
		String upload, download;
		// It is i - 2 to avoid the headers in the table
		for (int i = 2; i < table.getRowCount(); i++)
		{
			int column= 1;
			assertEquals("Check the IP Or Port Address", ipPort[i - 2], table
					.getCellAsText(i, column++));
			
			if (table.getColumnCount() == 6) {
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
					upload, table.getCellAsText(i, column));
		}
	}

	/* This test tests the legend table in the pmGraph page */
	public void testLegentPortView() throws Exception
	{
		// port graph
		WebConversation wc = new WebConversation();
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15&view=LOCAL_PORT");
		WebResponse response = wc.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);
		long downloaded[] = { 9, 1, 4, 0};
		long uploaded[] = { 5, 9, 6, 0};
		String ports[] = { "110",  "80", "443", "443"};
		String portName[] = { "pop3", "http", "https", "https"};
		String services[] = {"tcp", "tcp", "tcp", "udp"};
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports,portName, services);		
	}

	/**
	 * Check if the sorting works when the view is a port view
	 * 
	 * @throws Exception
	 */
	public void testSorterPortView() throws Exception
	{
		WebConversation wc = new WebConversation();
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		WebResponse response = wc.getResponse(request);
		WebLink link = response.getLinkWithName("downloaded");
		// the default is 'sort by download DESC', the sortLink is opposite to
		// the DESC
		String sortLink = "index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		response = wc.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);

		long uploaded[] = { 0, 9, 6, 5 };
		long downloaded[] = { 0, 1, 4, 9 };
		String ports[] =  { "443", "80", "443", "110" };
		String portName[] = {"https","http", "https", "pop3" };
		String services[] = {"udp", "tcp", "tcp", "tcp"};
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services);

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC&resultLimit=15&dynamic=false&view=LOCAL_PORT");
		response = wc.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC&resultLimit=15&dynamic=false&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		uploaded = new long[] {9, 6, 5, 0};
		downloaded = new long[] { 1, 4, 9, 0 };
		ports = new String[] { "80", "443", "110", "443" };
		portName = new String[] {"http","https", "pop3", "https" };
		services = new String[] {"tcp", "tcp", "tcp", "udp"};
		
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services);

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
		WebConversation wc = new WebConversation();
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=1&view=LOCAL_PORT");
		WebResponse response = wc.getResponse(request);

		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);
		long uploaded[] = { 5, 15 };
		long downloaded[] = { 9, 6 };
		String ports[] = { "110", "Others" };
		String portName[] = {"pop3", "" };
		String services[] = {"tcp", ""};
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports, portName, services);
	}

	public static Test suite()
	{
		return new TestSuite(LegendTestPortView.class);
	}

}
