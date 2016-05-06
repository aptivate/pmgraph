package org.aptivate.bmotools.pmgraph;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class UrlBuilderTest extends PmGraphTestBase
{
	private TestUtils m_testUtils;
	private long m_timeInMinutes;
	private WebRequest m_request;
	private WebResponse m_response;
	private long m_startTime;
	private long m_endTime;
	
	public UrlBuilderTest () throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, SAXException
	{
		super();
		m_timeInMinutes = (System.currentTimeMillis() / 60000);
		m_startTime = (m_timeInMinutes - 180) * 60000;
		m_endTime = m_timeInMinutes * 60000;
		m_testUtils = new TestUtils();		
		m_testUtils.CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++)
		{
			// Set the values
			m_testUtils.insertNewRow(250000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"224.0.0.255", "10.0.156.10", false);
			m_testUtils.insertNewRow(500000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"10.0.156.10", "224.0.0.255", false);

			m_testUtils.insertNewRow(200000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"224.0.0.251", "10.0.156.1", false);
			m_testUtils.insertNewRow(100000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"10.0.156.1", "224.0.0.251", false);			
			
			m_testUtils.insertNewRow(200000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"224.0.0.251", "10.0.223.15", false);
			m_testUtils.insertNewRow(100000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"10.0.223.15", "224.0.0.251", false);	
			
			m_testUtils.insertNewRow(200000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"224.0.0.255", "10.1.223.8", false);
			m_testUtils.insertNewRow(100000, new Timestamp((m_timeInMinutes - 5) * 60000),
					"10.1.223.8", "224.0.0.255", false);	
		}
	}

	public void setUp() throws Exception
	{
		super.setUp();
		Configuration.addIpGroupConf("Test", "10.0.156.1");
		m_request = new GetMethodWebRequest(m_testUtils.getUrlPmgraph() + 
				"?start=" + m_startTime + "&end=" + 
				m_endTime + "&width=780" +
			"&height=350&resultLimit=5&view=LOCAL_IP" +
			"&selectGroupIndex=Test");
		m_response = m_conversation.getResponse(m_request);
	}

	public void tearDown () throws Exception
	{
		m_request = null;
		m_response = null;
		Configuration.delGroup("Test");
		super.tearDown();
	}
	
	public void testImageUrlWithGroups() throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, SAXException
	{

		HTMLElement theImage = m_response.getElementWithID("graphimage");
		String imageUrl = theImage.getAttribute("src");
		String expectedUrl = "graphservlet?start=" + m_startTime + "&end=" + m_endTime + "&width=780" +
						"&height=350&resultLimit=5&view=LOCAL_IP" +
						"&selectGroupIndex=Test";
		assertEquals(expectedUrl, imageUrl);
	}
	
	public void testGroupsPreservedOnZoom() throws IOException, SAXException
	{
		WebLink theLink = m_response.getLinkWithName("zoomIn");
		String linkUrl = theLink.getURLString();
		String endOfExpectedUrl = "&resultLimit=5&dynamic=false&view=LOCAL_IP" +
				"&selectGroupIndex=Test";
		String endOfLinkUrl = linkUrl.substring(linkUrl.indexOf("&res"));
		assertEquals(endOfExpectedUrl, endOfLinkUrl);
		
		theLink = m_response.getLinkWithName("zoomOut");
		linkUrl = theLink.getURLString();
		endOfLinkUrl = linkUrl.substring(linkUrl.indexOf("&res"));
		
		endOfExpectedUrl = "&resultLimit=5&dynamic=false&view=LOCAL_IP" +
				"&selectGroupIndex=Test";
		
		assertEquals(endOfExpectedUrl, endOfLinkUrl);
	}
	
	public void testGroupsPreservedOnNextAndPrevious() throws IOException, SAXException
	{
		long scrollAmount = (m_endTime - m_startTime) / 2;
		
		WebLink prevLink = m_response.getLinkWithName("prev");
		String linkUrl = prevLink.getURLString();
		String endOfExpectedUrl = "&resultLimit=5" +
					"&dynamic=false&view=LOCAL_IP&selectGroupIndex=Test";
		String endOfLinkUrl = linkUrl.substring(linkUrl.indexOf("&res"));
		assertEquals(endOfExpectedUrl, endOfLinkUrl);
		
		long now = new Date().getTime();
		WebLink nextLink;
		if((m_endTime + scrollAmount) > now)
		{
			nextLink = m_response.getLinkWithName("current");
		}
		else
		{
			nextLink = m_response.getLinkWithName("next");
		}
		
		linkUrl = nextLink.getURLString();
		endOfExpectedUrl = "&resultLimit=5&dynamic=false" +
						"&view=LOCAL_IP&selectGroupIndex=Test";
		endOfLinkUrl = linkUrl.substring(linkUrl.indexOf("&res"));
		assertEquals(endOfExpectedUrl, endOfLinkUrl);
	}
	
	public void testGroupsPreservedOnChangeView() throws SAXException, IOException
	{
		checkUrlForView("LOCAL_IP");
		checkUrlForView("LOCAL_PORT");
		checkUrlForView("REMOTE_IP");
		checkUrlForView("REMOTE_PORT");
	}
	
	public void testGroupsPreservedOnSorting() throws SAXException
	{
		checkUrlForSorting("bytes_total");
		checkUrlForSorting("downloaded");
		checkUrlForSorting("uploaded");
	}
	
	private void checkUrlForView(String view) throws IOException, SAXException
	{
		WebResponse response;
		WebForm form = m_response.getFormWithID("SetDateAndTime");
		form.setParameter("view", view);
		response = form.submit();
		String url = response.getURL().toString();
		
		String expectedUrl; 
		StringBuffer tempUrl = new StringBuffer(m_testUtils.getUrlPmgraph() +"?start=" + m_startTime
		+ "&end=" + m_endTime + "&width=780&height=350&fromDate=");
		tempUrl.append(insertDate(m_startTime));
		tempUrl.append("&toDate=");
		tempUrl.append(insertDate(m_endTime));
		tempUrl.append("&fromTime="); 
		tempUrl.append(insertTime(m_startTime));
		tempUrl.append("&toTime=");
		tempUrl.append(insertTime(m_endTime));
		tempUrl.append("&resultLimit=5&view=" + view + 
				"&selectGroupIndex=Test&Go=Draw+Graph");
		expectedUrl = tempUrl.toString();
		assertEquals(expectedUrl, url);
	}
	
	private String insertDate(long date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String result = dateFormat.format(new Date(date));
		result = result.replace("/", "%2F");
		return result;
	}
	
	private String insertTime(long time)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String result = dateFormat.format(new Date(time));
		result = result.replace(":", "%3A");
		return result;
	}
	
	private void checkUrlForSorting(String sortBy) throws SAXException
	{
		WebLink link = m_response.getLinkWithName(sortBy);
		String expectedUrl = "index.jsp?start=" + m_startTime
		+ "&end=" + m_endTime + "&sortBy=" + sortBy + "&order=DESC" +
				"&resultLimit=5&dynamic=false" +
				"&view=LOCAL_IP&selectGroupIndex=Test";
		assertEquals(expectedUrl, link.getURLString());
	}

	public static Test suite()
	{
		return new TestSuite(UrlBuilderTest.class);
	}
}
