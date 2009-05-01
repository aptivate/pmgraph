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
	 * Just check if the donwloaded and uploaded values of a legend tambel match
	 * the values especified in the parameters.
	 * 
	 * @param table
	 * @param downloaded
	 * @param uploaded
	 * @param ipPort
	 * @throws IOException
	 * @throws SAXException
	 */
	private void checkUploadDownloadLegendTable(WebTable table,
			long downloaded[], long uploaded[], String ipPort[])
			throws IOException, SAXException
	{

		// Check the table data
		for (int i = 2; i < table.getRowCount(); i++)
		{
			assertEquals("Check the IP Or Port Address", ipPort[i - 2], table
					.getCellAsText(i, 1));
			assertEquals("Check the Downloaded Value", String
					.valueOf(downloaded[i - 2]), table.getCellAsText(i, 2));
			assertEquals("Check the Uploaded Value", String
					.valueOf(uploaded[i - 2]), table.getCellAsText(i, 3));
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
		long downloaded[] = { 9, 4, 1 };
		long uploaded[] = { 5, 6, 9 };
		String ports[] = { "110", "443", "80" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports);

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
						+ "index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15&view=LOCAL_PORT");
		WebResponse response = wc.getResponse(request);
		WebLink link = response.getLinkWithName("downloaded");
		// the default is 'sort by download DESC', the sortLink is opposite to
		// the DESC
		String sortLink = "index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC&resultLimit=15&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC&resultLimit=15&view=LOCAL_PORT");
		response = wc.getResponse(request);

		// Get the table data from the page
		WebTable table = (WebTable) response
				.getElementWithID(TestUtils.LEGEND_TBL);

		long uploaded[] = { 9, 6, 5 };
		long downloaded[] = { 1, 4, 9 };
		String ports[] = { "80", "443", "110" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports);

		request = new GetMethodWebRequest(
				m_testUtils.getUrlPmgraph()
						+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC&resultLimit=15&view=LOCAL_PORT");
		response = wc.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC&resultLimit=15&view=LOCAL_PORT";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		table = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);

		uploaded = new long[] { 9, 6, 5 };
		downloaded = new long[] { 1, 4, 9 };
		ports = new String[] { "80", "443", "110" };
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports);

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
		checkUploadDownloadLegendTable(table, downloaded, uploaded, ports);
	}

	public static Test suite()
	{
		return new TestSuite(LegendTestPortView.class);
	}

}
