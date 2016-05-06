package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;

/**
 * Validate content of URL for start and end parameters
 * @author  Anne and Ida
 * 
 */
public class StartAndEndEntryTest extends PmGraphTestBase
{

	private TestUtils m_testUtil;

	public StartAndEndEntryTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests the Start Entry */
	public void testCheckStartEntry() throws Exception
	{	
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0end=300000");
		m_conversation.getResponse(request);
	
		// Attempt to obtain the upload page on web site with invalid start
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=d0&end=300000");
		m_conversation.getResponse(request);
		
		assertEquals("Check start alert.", ErrorMessages.START_END_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
		
		// Attempt to obtain the upload page on web site with invalid end
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000trgbgfhgfh");
		m_conversation.getResponse(request);
 	
		assertEquals("Check end alert.", ErrorMessages.START_END_FORMAT_ERROR, m_conversation.popNextAlert());
		assertEquals("Check no more alerts.", "", m_conversation.popNextAlert());
	}

	public static Test suite()
	{
		return new TestSuite(StartAndEndEntryTest.class);
	}
}
