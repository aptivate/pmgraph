package org.aptivate.pmGraph.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.nutrun.xhtml.validator.XhtmlValidator;

/**
 * Checks for W3C Compliance 
 * 
 * @author Noe A. Rodriguez Glez. 
 *
 *
 *
 */
public class W3cValidationTest extends TestCase
{
	

	private static Logger m_logger = Logger.getLogger(W3cValidationTest.class.getName());
	

	/**
	 * 	Check if the returned Web page is a valid XHTML page according
	 *  to the W3C standar. 
	 *  
	 *  	If there are any error it is written in the logger establiced
	 *  in the logger properties file.
	 *  
	 * @throws IOException
	 * @throws SAXException
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */	
	public void testw3cValidator () throws IOException, SAXException, InstantiationException,
		IllegalAccessException, ClassNotFoundException, SQLException {
		
		TestUtils testUtils= new TestUtils();
	
		WebConversation wc = new WebConversation();
		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(testUtils.getUrlPmgraph()
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000");
		WebResponse response = wc.getResponse(request);
			
		XhtmlValidator validator = new XhtmlValidator();
 		String docText = response.getText();
 		
 		if (!validator.isValid(new ByteArrayInputStream(docText.getBytes())));
 			String errors[] = validator.getErrors();
 			for (String error: errors) {
 				m_logger.warn(error);
 			}
 		 assertTrue(validator.isValid(new ByteArrayInputStream(docText.getBytes())));
	}
	
	public static Test suite()
	{
		return new TestSuite(W3cValidationTest.class);
	}	
}
