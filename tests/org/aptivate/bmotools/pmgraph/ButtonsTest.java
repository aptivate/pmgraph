package org.aptivate.bmotools.pmgraph;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class ButtonsTest extends PmGraphTestBase
{
	private TestUtils m_testUtil;

	public ButtonsTest() throws Exception
	{
		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	/* This test tests the next button */
	public void testCheckNextButton() throws Exception
	{		
		WebRequest request;
		WebResponse response;
		String nextURL;
		WebLink link;
		nextURL = "index.jsp?start=150000&end=300000&resultLimit=15&dynamic=false&view=LOCAL_IP";
				
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=75000&end=225000&resultLimit=15");
		response = m_conversation.getResponse(request);

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

		// Load the page after press the Next Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=150000&end=300000&resultLimit=15");
		response = m_conversation.getResponse(request);
		
		nextURL = "index.jsp?start=225000&end=375000&resultLimit=15&dynamic=false&view=LOCAL_IP";
		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

		// Obtain the current page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph());
		response = m_conversation.getResponse(request);

		// Find the "next" link
		link = response.getLinkWithName("current");
		assertTrue("Found current button", link != null);
	}
	
	public void testCheckButtonsKeepSorting() throws Exception
	{		
		WebRequest request;
		WebResponse response;
		String nextURL;
		WebLink link;
		nextURL = "index.jsp?start=150000&end=450000&resultLimit=5&dynamic=false&order=DESC&sortBy=uploaded&view=LOCAL_PORT";
		String prevURL = "index.jsp?start=-150000&end=150000&resultLimit=5&dynamic=false&order=DESC&sortBy=uploaded&view=LOCAL_PORT";
		String zoomURL = "index.jsp?start=-150000&end=450000&resultLimit=5&dynamic=false&order=DESC&sortBy=uploaded&view=LOCAL_PORT";
		String zoomInURL = "index.jsp?start=75000&end=225000&resultLimit=5&dynamic=false&order=DESC&sortBy=uploaded&view=LOCAL_PORT";
		
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=5&dynamic=false&order=DESC&sortBy=uploaded&view=LOCAL_PORT");
		response = m_conversation.getResponse(request);

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());
		
		// Find the "prev" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());
		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());
		// Check that there isn't the Zoom+ link in the page
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom- link.", zoomInURL, link.getURLString());

	}

	/* This test tests the prev button */
	public void testCheckPrevButton() throws Exception
	{		
		WebRequest request;
		WebResponse response;
		String prevURL;
		WebLink link;

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=150000&end=450000&resultLimit=15");
		response = m_conversation.getResponse(request);

		prevURL = "index.jsp?start=0&end=300000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the "prev" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());

		// Load the page after press the Prev Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=15");
		response = m_conversation.getResponse(request);

		prevURL = "index.jsp?start=-150000&end=150000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the "next" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());
	}

	/* This test tests the zoom- button */
	public void testCheckZoomOutButton() throws Exception
	{		
		WebRequest request;
		WebResponse response;
		String zoomURL;
		WebLink link;

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=15");
		response = m_conversation.getResponse(request);

		zoomURL = "index.jsp?start=-150000&end=450000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom- Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-150000&end=450000&resultLimit=15");
		response = m_conversation.getResponse(request);

		zoomURL = "index.jsp?start=-450000&end=750000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());
		
		//test the zoom- link when use the current time
		long toDateAndTime = new Date().getTime(); //get current time
		long fromDateAndTime = toDateAndTime - 180 * 60000;
	    long zoomAmount = (toDateAndTime - fromDateAndTime) / 2;
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=" + fromDateAndTime + "&end=" + toDateAndTime + "&resultLimit=15");
		response = m_conversation.getResponse(request);
		long now = new Date().getTime();
		long newStart = now - (toDateAndTime - fromDateAndTime) - 2 * zoomAmount;
		link = response.getLinkWithName("zoomOut");
		String strTime[] = link.getParameterValues("start");
		long lZoomStart = Long.parseLong(strTime[0]);
		strTime = link.getParameterValues("end");
		long lZoomEnd = Long.parseLong(strTime[0]);
		//replace 1000*60 with the limit of response time 
		assertTrue("check the zoomOut on current time", (lZoomStart - newStart < 1000 * 60)&&(lZoomEnd - now) < 1000 * 60);
	}

	/* This test tests the zoom+ button */
	public void testCheckZoomInButton() throws Exception
	{		
		WebRequest request;
		WebResponse response;
		String zoomURL;
		WebLink link;

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-450000&end=750000&resultLimit=15");
		response = m_conversation.getResponse(request);
		zoomURL = "index.jsp?start=-150000&end=450000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-150000&end=450000&resultLimit=15");
		response = m_conversation.getResponse(request);

		zoomURL = "index.jsp?start=0&end=300000&resultLimit=15&dynamic=false&view=LOCAL_IP";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000&resultLimit=15");
		response = m_conversation.getResponse(request);

		/* Test if the Zoom+ Button Disappear */
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=84000&resultLimit=15");
		response = m_conversation.getResponse(request);

		// Check thar there isn't the Zoom+ link in the page
		link = response.getLinkWithName("zoomIn");
		assertEquals("Check that the zoom- link is null.", null, link);
	}

	public static Test suite()
	{
		return new TestSuite(ButtonsTest.class);
	}

}
