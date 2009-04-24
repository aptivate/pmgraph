package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * 
 * @author  Anne and Ida
 * 
 */
public class IPandPortEntryTest extends TestCase
{

	private TestUtils m_testUtil;

	public IPandPortEntryTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests Selected IP and Ports */
	public void testCheckIPEntry() throws Exception
	{
		final String IP_FORMAT_ERROR = "Invalid IP format used";

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=5&ip=10.0.156.120&port=90&view=REMOTE_IP");
		WebResponse response = wc.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box Selected Ip.", response
				.getElementWithID("ip"));
		
		assertEquals("Check the ip initial value.","10.0.156.120", response
				.getElementWithID("ip").getAttribute("value"));

		// Check valid values
		response.getElementWithID("ip").setAttribute("value", "10.0.156.121");
		theForm.submit();

		assertEquals("Check no alert.", "", wc.popNextAlert());

		// Check invalid Ip values  - IP address should have format n.n.n.n where n is in the range 0-255
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=5&ip=10.0.156.256&port=90&view=REMOTE_IP");
		response = wc.getResponse(request);
		
		assertEquals("Check IP alert1.", IP_FORMAT_ERROR, wc.popNextAlert());
		assertEquals("Check no more alerts.", "", wc.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=5&ip=10.0.1a6.250&port=90&view=REMOTE_IP");
		response = wc.getResponse(request);
		
		assertEquals("Check IP alert2.", IP_FORMAT_ERROR, wc.popNextAlert());
		assertEquals("Check no more alerts.", "", wc.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=5&ip=10.156.250&port=90&view=REMOTE_IP");
		response = wc.getResponse(request);
		
		assertEquals("Check IP alert3.", IP_FORMAT_ERROR, wc.popNextAlert());
		assertEquals("Check no more alerts.", "", wc.popNextAlert());

	}

	public static Test suite()
	{
		return new TestSuite(IPandPortEntryTest.class);
	}
}
