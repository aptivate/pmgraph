package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Tests that the error messages are shown correctly
 * @author sylviaw 
 *
 */

public class ErrorMessageTest extends TestCase {

	private TestUtils m_testUtils;

	WebConversation m_wc;

	WebRequest m_request;

	WebResponse m_response;

	WebForm m_theForm;

	SubmitButton m_subButton;

	public ErrorMessageTest() throws Exception {
		m_testUtils = new TestUtils();
		m_testUtils.CreateTable();
		m_testUtils.InsertSampleData();
		m_wc = new WebConversation();
		try {
			//This is necessary to ensure that the value in pmacctd.conf is reset correctly
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=0&end=300000");
		m_response = m_wc.getResponse(m_request);
		m_theForm = m_response.getFormWithID("SetDateAndTime");
		m_subButton = m_theForm.getSubmitButton("Go");
				
	}

	/**
	 * Check we get an error alert if the input date/time is in the wrong format
	 */
	public void testTimeFormatErrorMessage() throws IOException, SAXException {
	
		m_response.getElementWithID("fromDate").setAttribute("value", "02/0p/2009");
		m_subButton.click();

		assertEquals("Check from date format alert.",
					ErrorMessages.DATE_TIME_FORMAT_ERROR, m_wc.popNextAlert());
		
	}

	/**
	 * test error alert if we input a future time
	 * @throws Exception
	 */
	public void testTimeExtendNowErrorMessage() throws Exception {

		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm:ss");
		LocalDate toDate = new LocalDate();
		LocalTime currentTime = new LocalTime();

		//set endtime to future time only
		m_response.getElementWithID("toDate").setAttribute("value",
				dateFormat.print(toDate));
		m_response.getElementWithID("toTime").setAttribute("value",
				timeFormat.print(currentTime.plusMinutes(1)));

		m_subButton.click();
		assertEquals("Time in future.", ErrorMessages.TIME_IN_FUTURE, m_wc.popNextAlert());

	}

	/**
	 * test we get the error alert when the span of to and from time is less than 1 minute 
	 * @throws Exception
	 */
	public void testShortSpanErrorMessage() throws Exception {
		//set endtime to future time only
		m_response.getElementWithID("toDate").setAttribute("value",	"01/01/1970");
		m_response.getElementWithID("toTime").setAttribute("value", "01:00:58");

		m_subButton.click();
		assertEquals("Test Short Span", ErrorMessages.TIME_NOT_ENOUGH, m_wc.popNextAlert());

	}
	
	/**
	 * test error message for invalid value in start
	 * @throws Exception
	 */
	public void testStartEndErrorMessage() throws Exception {
		
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=gdg&end=300000");
		m_response = m_wc.getResponse(m_request);
		
		assertEquals("Test Start and End", ErrorMessages.START_END_FORMAT_ERROR , m_wc.popNextAlert());
	}
	
	/**
	 * test error message for invalid limit number
	 * @throws Exception
	 */
	public void testLimitNumberErrorMessage() throws Exception {
		
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=0&end=300000");
		//wrong setting of result limit
		m_response.getElementWithID("resultLimit").setAttribute("value", "N");
		m_subButton.click();

		assertEquals("Test Short Span", ErrorMessages.RESULT_LIMIT_FORMAT_ERROR, m_wc.popNextAlert());
	}

	/**
	 * 
	 * test error message for invalid port number
	 * @throws Exception
	 */
	public void testPortNumberErrorMessage() throws Exception{
		//test wrong port number
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?port=9p");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", ErrorMessages.PORT_FORMAT_ERROR, m_wc.popNextAlert());
		
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?port=-9");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", ErrorMessages.NEGATIVE_PORT_NUMBER, m_wc.popNextAlert());
		
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?port=99999");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", ErrorMessages.PORT_NUMBER_TOO_BIG, m_wc.popNextAlert());
		
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?port=Others");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", "", m_wc.popNextAlert());
	}
	
	/**
	 * 
	 * test error message for invalid IP address
	 * @throws Exception
	 */
	public void testIPErrorMessage() throws Exception{
		//test wrong port number
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?ip=3.3.y.9");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", ErrorMessages.IP_FORMAT_ERROR, m_wc.popNextAlert());
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?ip=Others");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", "", m_wc.popNextAlert());		
	}

	/**
	 * test error message for invalid View
	 * @throws Exception
	 */
	public void testViewErrorMessage() throws Exception{
		//test for wrong view parameter
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + "?view=XXXX");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test wrong port Number", ErrorMessages.VIEW_FORMAT_ERROR, m_wc.popNextAlert());
	}
	
	public static Test suite()
	{
		return new TestSuite(ErrorMessageTest.class);
	}

}
