package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

abstract class PmGraphTestBase extends TestCase
{
	protected WebConversation m_conversation;

	public PmGraphTestBase() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		m_conversation = new WebConversation();
	}
	
	public WebResponse loadUrl (TestUtils theTestUtils) throws IOException, SAXException
	{				
	    WebRequest request = new GetMethodWebRequest(theTestUtils.getUrlPmgraph() + "configure.jsp");
	    WebResponse response = m_conversation.getResponse(request);
	    return response;
	}
	
	public void TearDown() 
	{
		m_conversation = null;
	}
}