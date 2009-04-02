package org.aptivate.pmGraph.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.aptivate.bmotools.pmgraph.Configuration;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * 
 * @author  Anne and Ida
 * 
 */
public class ResultEntryTest extends TestCase
{

	private TestUtils m_testUtil;

	public ResultEntryTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests the No of Results Entry */
	public void testCheckResultsEntry() throws Exception
	{
		final String RESULT_LIMIT_FORMAT_ERROR = "ResultLimit parameter should by a number ! \n" + 
		" Default resultLimit value assumed.";
		final String defaultResults = "" + Configuration.getResultLimit();

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000");
		WebResponse response = wc.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is text box results.", response
				.getElementWithID("resultLimit"));
		
		assertEquals("Check the results initial value.", defaultResults, response
				.getElementWithID("resultLimit").getAttribute("value"));

		SubmitButton subButton = theForm.getSubmitButton("Go");

		// Check valid values
		response.getElementWithID("resultLimit").setAttribute("value", "6");
		subButton.click();

		assertEquals("Check no alert.", "", wc.popNextAlert());

		// Check non-numeric results values
		response.getElementWithID("resultLimit").setAttribute("value", "p");
		subButton.click();
		
		assertEquals("Check result alert.", RESULT_LIMIT_FORMAT_ERROR, wc.popNextAlert());
		// Should now be set to default value and not give an error
		
		// TODO Check default value is used - Not working the URL still has p while screen shows 5
		
//		response.getRefreshRequest();
//		subButton.click();
//		assertEquals("Check the results default used.", defaultResults, response
//						.getElementWithID("resultLimit").getAttribute("value"));
//		assertEquals("Check no alert.", "", wc.popNextAlert());		
//		
		// Check negative results value
		response.getElementWithID("resultLimit").setAttribute("value", "-2");
		subButton.click();
		
		assertEquals("Check -ve result alert.", RESULT_LIMIT_FORMAT_ERROR, wc.popNextAlert());
		
    	assertEquals("Check no more alerts.", "", wc.popNextAlert());

	}

	public static Test suite()
	{
		return new TestSuite(ResultEntryTest.class);
	}
}
