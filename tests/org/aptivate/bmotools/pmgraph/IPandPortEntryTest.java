package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Test the selected IP/port, remote or local
 * 
 * @author  Anne and Ida
 * 
 */
public class IPandPortEntryTest extends PmGraphTestBase
{

	private TestUtils m_testUtil;

	public IPandPortEntryTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests Selected Local IP */
	public void testCheckIPEntry() throws Exception
	{		
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&ip=10.0.156.120");
		WebResponse response = m_conversation.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box Selected Ip.", response.getElementWithID("ip"));
		
		assertEquals("Check the ip initial value.","10.0.156.120", response.getElementWithID("ip")
																		   .getAttribute("value"));

		// Check valid values
		response.getElementWithID("ip").setAttribute("value", "10.0.156.121");
		
		theForm.submit();

		assertEquals("Check no alert.", "", m_conversation.popNextAlert());

		// Check invalid Ip values  - IP address should have format n.n.n.n where n is in the range 0-255
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&ip=10.0.156.256");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check IP alert1.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&ip=10.0.1a6.250");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check IP alert2.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&ip=10.156.250");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check IP alert3.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&ip=10.0..156.250");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check IP alert4.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
	}
	
	/* This test tests Selected Remote IP */
	public void testCheckRemoteIPEntry() throws Exception
	{	

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_ip=4.2.2.10");
		WebResponse response = m_conversation.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box Selected Ip.", response
				.getElementWithID("remote_ip"));
		
		assertEquals("Check the ip initial value.","4.2.2.10", response
				.getElementWithID("remote_ip").getAttribute("value"));

		// Check valid values
		response.getElementWithID("remote_ip").setAttribute("value", "10.0.156.121");
		theForm.submit();

		assertEquals("Check no alert.", "", m_conversation.popNextAlert());

		// Check invalid Ip values  - IP address should have format n.n.n.n where n is in the range 0-255
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_ip=2.b.2.10");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote IP alert1.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_ip=4.256.2.10");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote IP alert2.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_ip=4.0");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote IP alert3.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_ip=4.250..2.10");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote IP alert4.", ErrorMessages.IP_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
	}
	
	/* This test tests Selected Local Port */
	public void testCheckPort() throws Exception
	{		

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&port=90");
		WebResponse response = m_conversation.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box Selected Port.", response
				.getElementWithID("port"));
		
		assertEquals("Check the port initial value.","90", response
				.getElementWithID("port").getAttribute("value"));

		// Check valid values
		response.getElementWithID("port").setAttribute("value", "1000");
		theForm.submit();

		assertEquals("Check no alert.", "", m_conversation.popNextAlert());

		// Check invalid port values  - Port should be positive no 
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&port=k90");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check port alert1.", ErrorMessages.PORT_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&port=-90");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check port alert2.", ErrorMessages.NEGATIVE_PORT_NUMBER, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&port=65536");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check port alert3.", ErrorMessages.PORT_NUMBER_TOO_BIG, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
	}

	/* This test tests Selected Remote Port */
	public void testCheckRemotePort() throws Exception
	{		

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_port=90");
		WebResponse response = m_conversation.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box Selected Remote Port.", response
				.getElementWithID("remote_port"));
		
		assertEquals("Check the remote port initial value.","90", response
				.getElementWithID("remote_port").getAttribute("value"));

		// Check valid values
		response.getElementWithID("remote_port").setAttribute("value", "1000");
		theForm.submit();

		assertEquals("Check no alert.", "", m_conversation.popNextAlert());

		// Check invalid remote_port values  - Port should be positive no 
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_port=k90");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote port alert1.", ErrorMessages.PORT_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_port=-90");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote port alert2.", ErrorMessages.NEGATIVE_PORT_NUMBER, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&remote_port=65536");
		response = m_conversation.getResponse(request);
		
		assertEquals("Check remote port alert3.", ErrorMessages.PORT_NUMBER_TOO_BIG, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
	}
	public static Test suite()
	{
		return new TestSuite(IPandPortEntryTest.class);
	}
}
