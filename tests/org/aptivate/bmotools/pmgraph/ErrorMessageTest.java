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
 * Test if the error messages could be shown
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
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=0&end=300000");
		m_response = m_wc.getResponse(m_request);
		m_theForm = m_response.getFormWithID("SetDateAndTime");
		m_subButton = m_theForm.getSubmitButton("Go");
				
	}

	public void setUp() throws Exception {

	}

	/**
	 * input time/date in wrong format, and see if we can get the error alert
	 */
	public void testTimeFormatErrorMessage() throws IOException, SAXException {
		//todo: set error format and catch the alert message	
		final String dateTimeError = "The date format should be : dd/mm/yyyy !\n The time format should be : hh:mm:ss !";
		
		String fromDateArray[] = { "02:03/2009", "002/00/2009", "02/03/20a",
				"32/03/2009", "02/13/2009", "02/03/20009" };
		String toDateArray[] = { "02:03/2009", "02/03u/2009", "02/03/20a",
				"32/03/2009", "02/13/2009", "02/03/20009" };
		String fromTimeArray[] = { "15/54:32", "15:504:32", "b15:54:32",
				"25:54:32", "15:60:32", "15:54:61" };
		String toTimeArray[] = { "15/54:32", "15:504:32", "b15:54:32",
				"25:54:32", "15:60:32", "15:54:61" };
		int noElements = 6;

		//		 Check wrong fromDate values
		for (int i = 0; i < noElements; i++) {
			m_response.getElementWithID("fromDate").setAttribute("value",
					fromDateArray[i]);
			m_subButton.click();

			assertEquals("Check from date format alert.",
					dateTimeError, m_wc.popNextAlert());
		}
		// Restore to valid
		m_response.getElementWithID("fromDate").setAttribute("value",
				"02/03/2009");

		//Check wrong toDate values
		for (int i = 0; i < noElements; i++) {
			m_response.getElementWithID("toDate").setAttribute("value",
					toDateArray[i]);
			m_subButton.click();

			assertEquals("Check to date format alert.",
					dateTimeError, m_wc.popNextAlert());
		}

		//	 Restore to valid
		m_response.getElementWithID("fromDate").setAttribute("value",
				"02/03/2009");

		// Check wrong toTime values
		for (int i = 0; i < noElements; i++) {
			m_response.getElementWithID("toTime").setAttribute("value",
					toTimeArray[i]);
			m_subButton.click();

			assertEquals("Check to time format alert.",
					dateTimeError, m_wc.popNextAlert());
		}
		// Restore to valid
		m_response.getElementWithID("toTime").setAttribute("value", "15:54:32");

		// Check wrong fromTime values
		for (int i = 0; i < noElements; i++) {
			m_response.getElementWithID("fromTime").setAttribute("value",
					fromTimeArray[i]);
			m_subButton.click();

			assertEquals("Check from time format alert.",
					dateTimeError, m_wc.popNextAlert());
		}

		assertEquals("Check no more alerts.", "", m_wc.popNextAlert());

		// Restore to valid
		m_response.getElementWithID("fromTime").setAttribute("value",
				"15:53:32");
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
		assertEquals("Time in future.", ErrorMessages.TIME_IN_FUTURE, m_wc
				.popNextAlert());
		m_response.getElementWithID("toTime").setAttribute("value", "15:54:32");

	}

	/**
	 * to test if we can get the error alert when the span of to and from time is less than 1 minute 
	 * @throws Exception
	 */
	public void testShortSpanErrorMessage() throws Exception {
		//set endtime to future time only
		m_response.getElementWithID("toDate").setAttribute("value",
				"01/01/1970");
		m_response.getElementWithID("toTime").setAttribute("value", "01:00:58");

		m_subButton.click();
		assertEquals("Test Short Span", ErrorMessages.TIME_NOT_ENOUGH, m_wc
				.popNextAlert());

		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=0&end=59000");
		m_response = m_wc.getResponse(m_request);
		assertEquals("test short span.", ErrorMessages.TIME_NOT_ENOUGH, m_wc
				.popNextAlert());
	}

	/**
	 * test error message for wrong input of limit number
	 * @throws Exception
	 */
	public void testLimitNumberErrorMessage() throws Exception {
		final String limitNumberError = "ResultLimit parameter should by a number ! \n Default resultLimit value assumed.";
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph()
				+ "?start=0&end=300000");
		//wrong setting of result limit
		m_response.getElementWithID("resultLimit").setAttribute("value", "N");
		m_subButton.click();

		assertEquals("Test Short Span", limitNumberError, m_wc.popNextAlert());
	}

	/**
	 * 
	 * test error message for wrong input of port number
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
				
	}

	/**
	 * test error message for wrong input of View
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
