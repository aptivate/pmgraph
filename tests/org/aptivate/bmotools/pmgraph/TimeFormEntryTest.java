package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * 
 * @author noeg
 * 
 */
public class TimeFormEntryTest extends TestCase
{

	private TestUtils m_testUtil;

	public TimeFormEntryTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests the SetTime form uses the correct default values*/
	public void testCheckDefaults() throws Exception
	{
		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph());
		WebResponse response = wc.getResponse(request);
		
		checkDefaultValuesDisplayed(response);
	}
	
	/* This test tests the SetTime form uses the correct default values after invalid entry*/
	public void testCheckDefaultsAfterError() throws Exception
	{
		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site with invalid date
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?toDate=0a/03/2009");
		
		WebResponse response = wc.getResponse(request);
		
		checkDefaultValuesDisplayed(response);
	}

	private void checkDefaultValuesDisplayed(WebResponse response) throws SAXException 
	{
		// Check initial values
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");

		LocalDate toDate = new LocalDate();
		
		assertEquals("Check the toDate initial value.", dateFormat.print(toDate), response
				.getElementWithID("toDate").getAttribute("value"));

		assertEquals("Check the fromDate initial value.", dateFormat.print(toDate),
				response.getElementWithID("fromDate").getAttribute("value"));

		LocalTime currentTime = new LocalTime();
		LocalTime displayedTime = new LocalTime(response.getElementWithID("toTime").getAttribute("value"));
		long diff = currentTime.getMillisOfDay() - displayedTime.getMillisOfDay();
		assertTrue("Check the toTime initial value.", Math.abs(diff) < 60000);
		
		displayedTime = new LocalTime(response.getElementWithID("fromTime").getAttribute("value"));
		
		diff = (currentTime.minusHours(3)).getMillisOfDay() - displayedTime.getMillisOfDay();
		assertTrue("Check the fromTime initial value.", Math.abs(diff) < 60000);
	}
		
	
	/* This test tests the SetTime form */
	public void testCheckSetTimeForm() throws Exception
	{
		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000");
		WebResponse response = wc.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is form SetDateAndTime.", theForm);
		assertNotNull("Check if there is button Go.", theForm
				.getButtonWithID("Go"));
		assertNotNull("Check if there is text box fromDate.", response
				.getElementWithID("fromDate"));
		assertNotNull("Check if there is text box toDate.", response
				.getElementWithID("toDate"));
		assertNotNull("Check if there is text box fromTime.", response
				.getElementWithID("fromTime"));
		assertNotNull("Check if there is text box toTime.", response
				.getElementWithID("toTime"));

		// Check the SetTime form functionality

		String fromDateArray[] = { "02:03/2009", "002/03/2009", "02/03/20a",
				"32/03/2009", "02/13/2009", "02/03/20009" };
		String toDateArray[] = { "02:03/2009", "002/03/2009", "02/03/20a",
				"32/03/2009", "02/13/2009", "02/03/20009" };
		String fromTimeArray[] = { "15/54:32", "15:504:32", "b15:54:32",
				"25:54:32", "15:60:32", "15:54:61" };
		String toTimeArray[] = { "15/54:32", "15:504:32", "b15:54:32",
				"25:54:32", "15:60:32", "15:54:61" };
		int noElements = 6;

		// Check initial values
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm:ss");

		LocalDate toDate = new LocalDate();
		LocalTime currentTime = new LocalTime();

		assertEquals("Check the toDate initial value.", "01/01/1970", response
				.getElementWithID("toDate").getAttribute("value"));

		assertEquals("Check the fromDate initial value.", "01/01/1970",
				response.getElementWithID("fromDate").getAttribute("value"));

		assertEquals("Check the toTime initial value.", "01:05:00", response
				.getElementWithID("toTime").getAttribute("value"));

		assertEquals("Check the fromTime initial value.", "01:00:00", response
				.getElementWithID("fromTime").getAttribute("value"));

		SubmitButton subButton = theForm.getSubmitButton("Go");

		// Check valid values
		response.getElementWithID("toDate").setAttribute("value", "03/03/2009");
		response.getElementWithID("fromDate").setAttribute("value",
				"02/03/2009");
		response.getElementWithID("toTime").setAttribute("value", "15:54:32");
		response.getElementWithID("fromTime").setAttribute("value", "15:54:32");

		// Load the page press Go button
		subButton.click();

		assertEquals("Check no alert.", "", wc.popNextAlert());

		// Check wrong toDate values
		for (int i = 0; i < noElements; i++)
		{
			response.getElementWithID("toDate").setAttribute("value",
					toDateArray[i]);
			subButton.click();

			assertEquals("Check to date format alert.", ErrorMessages.DATE_TIME_FORMAT_ERROR, wc.popNextAlert());
		}
		// Restore to valid
		response.getElementWithID("toDate").setAttribute("value", "03/03/2009");

		// Check wrong fromDate values
		for (int i = 0; i < noElements; i++)
		{
			response.getElementWithID("fromDate").setAttribute("value", fromDateArray[i]);
			subButton.click();

			assertEquals("Check from date format alert.", ErrorMessages.DATE_TIME_FORMAT_ERROR, wc.popNextAlert());
		}
		// Restore to valid
		response.getElementWithID("fromDate").setAttribute("value", "02/03/2009");

		// Check wrong toTime values
		for (int i = 0; i < noElements; i++)
		{
			response.getElementWithID("toTime").setAttribute("value", toTimeArray[i]);
			subButton.click();

			assertEquals("Check to time format alert.", ErrorMessages.DATE_TIME_FORMAT_ERROR, wc.popNextAlert());
		}
		// Restore to valid
		response.getElementWithID("toTime").setAttribute("value", "15:54:32");

		// Check wrong fromTime values
		for (int i = 0; i < noElements; i++)
		{
			response.getElementWithID("fromTime").setAttribute("value", fromTimeArray[i]);
			subButton.click();

			assertEquals("Check from time format alert.", ErrorMessages.DATE_TIME_FORMAT_ERROR, wc.popNextAlert());
		}

		assertEquals("Check no more alerts.", "", wc.popNextAlert());

		// Restore to valid
		response.getElementWithID("fromTime").setAttribute("value", "15:54:32");

		// The To Date and Time cannot be in the future
		response.getElementWithID("toDate").setAttribute("value",
				dateFormat.print(toDate.plusDays(1)));
		subButton.click();
		assertEquals("Date in future.", ErrorMessages.TIME_IN_FUTURE, wc.popNextAlert());

		// Future time only
		response.getElementWithID("toDate").setAttribute("value",
				dateFormat.print(toDate));
		response.getElementWithID("toTime").setAttribute("value",
				timeFormat.print(currentTime.plusMinutes(1)));

		subButton.click();
		assertEquals("Time in future.", ErrorMessages.TIME_IN_FUTURE, wc.popNextAlert());

		// The From Date and Time have to be at least 1 minute before the To
		// Date and Time
		response.getElementWithID("toDate").setAttribute("value",
				dateFormat.print(toDate));
		response.getElementWithID("toTime").setAttribute("value",
				timeFormat.print(currentTime));
		response.getElementWithID("fromDate").setAttribute("value",
				dateFormat.print(toDate));
		response.getElementWithID("fromTime").setAttribute("value",
				timeFormat.print(currentTime));
		subButton.click();
		assertEquals("From equals To.", ErrorMessages.TIME_NOT_ENOUGH, wc.popNextAlert());
	}

	public static Test suite()
	{
		return new TestSuite(TimeFormEntryTest.class);
	}
}
