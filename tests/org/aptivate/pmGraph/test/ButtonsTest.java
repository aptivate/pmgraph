package org.aptivate.pmGraph.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class ButtonsTest extends TestCase
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
		WebConversation wc;
		WebRequest request;
		WebResponse response;
		String nextURL;
		WebLink link;

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=75000&end=225000&resultLimit=15");
		response = wc.getResponse(request);

		nextURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=150000&end=300000&resultLimit=15";

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

		// Load the page after press the Next Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=150000&end=300000&resultLimit=15");
		response = wc.getResponse(request);

		nextURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=225000&end=375000&resultLimit=15";

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

	}

	/* This test tests the prev button */
	public void testCheckPrevButton() throws Exception
	{
		WebConversation wc;
		WebRequest request;
		WebResponse response;
		String prevURL;
		WebLink link;


		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=150000&end=450000&resultLimit=15");
		response = wc.getResponse(request);

		prevURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15";

		// Find the "prev" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());

		// Load the page after press the Prev Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph() + "?start=0&end=300000&resultLimit=15");
		response = wc.getResponse(request);

		prevURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=150000&resultLimit=15";

		// Find the "next" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());
	}

	/* This test tests the zoom- button */
	public void testCheckZoomOutButton() throws Exception
	{
		WebConversation wc;
		WebRequest request;
		WebResponse response;
		String zoomURL;
		WebLink link;

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph() + "?start=0&end=300000&resultLimit=15");
		response = wc.getResponse(request);

		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=450000&resultLimit=15";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom- Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-150000&end=450000&resultLimit=15");
		response = wc.getResponse(request);

		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-450000&end=750000&resultLimit=15";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());

		/* Test if the Zoom- Button Disappear */
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=2147460000");
		response = wc.getResponse(request);

		// Check thar there isn't the Zoom- link in the page
		link = response.getLinkWithName("zoomOut");
		assertEquals("Check that the zoom- link is null.", null, link);
	}

	/* This test tests the zoom+ button */
	public void testCheckZoomInButton() throws Exception
	{
		WebConversation wc;
		WebRequest request;
		WebResponse response;
		String zoomURL;
		WebLink link;

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-450000&end=750000&resultLimit=15");
		response = wc.getResponse(request);
		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=450000&resultLimit=15";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=-150000&end=450000&resultLimit=15");
		response = wc.getResponse(request);

		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=0&end=300000&resultLimit=15";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph() + "?start=0&end=300000&resultLimit=15");
		response = wc.getResponse(request);

		/* Test if the Zoom+ Button Disappear */
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph() + "?start=0&end=84000&resultLimit=15");
		response = wc.getResponse(request);

		// Check thar there isn't the Zoom+ link in the page
		link = response.getLinkWithName("zoomIn");
		assertEquals("Check that the zoom- link is null.", null, link);
	}

	public static Test suite()
	{
		return new TestSuite(ButtonsTest.class);
	}

	
}
