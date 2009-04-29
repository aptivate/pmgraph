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

		// Check valid values
		response.getElementWithID("resultLimit").setAttribute("value", "6");
		theForm.submit();

		assertEquals("Check no alert.", "", wc.popNextAlert());

		// Check non-numeric results values
		// AC There is an error in HttpUnit v1.7 (fixed in next version but not released) that causes a double 
		// submit hence 2 errors messages are obtained if we use Submit hence we are using URL to set invalid value
		
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=p");
		response = wc.getResponse(request);
		
		assertEquals("Check result alert.", ErrorMessages.RESULT_LIMIT_FORMAT_ERROR, wc.popNextAlert());
		assertEquals("Check no more alerts.", "", wc.popNextAlert());
		
		// Should now be set to default value and give no error when submitted
        assertEquals("Check the results default used.", defaultResults, response
						.getElementWithID("resultLimit").getAttribute("value"));
        
        theForm = response.getFormWithID("SetDateAndTime");
        response = theForm.submit();
        
        assertEquals("Check no alert.", "", wc.popNextAlert());
    
		// Check negative results value
        request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=-6");
		response = wc.getResponse(request);
		
		assertEquals("Check -ve result alert.", ErrorMessages.RESULT_LIMIT_FORMAT_ERROR, wc.popNextAlert());
		
    	assertEquals("Check no more alerts.", "", wc.popNextAlert());

	}

	public static Test suite()
	{
		return new TestSuite(ResultEntryTest.class);
	}
}
