package org.aptivate.bmotools.pmgraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebForm;
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
public class W3cValidationTest extends PmGraphTestBase
{

	private static Logger m_logger = Logger.getLogger(W3cValidationTest.class
			.getName());

	private TestUtils m_testUtils;

	public W3cValidationTest() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{

		m_testUtils = new TestUtils();
	}

	public void setUp() throws Exception
	{
		m_testUtils.CreateTable();
		m_testUtils.InsertSampleData();
	}

	/**
	 * Check if the response is W3C valid XHTML file.
	 * 
	 * @param response
	 * @throws IOException
	 */
	private void w3cValidator(WebResponse response) throws IOException
	{

		XhtmlValidator validator = new XhtmlValidator();
		String docText = response.getText();

		if (!validator.isValid(new ByteArrayInputStream(docText.getBytes())))
			;
		String errors[] = validator.getErrors();
		for (String error : errors)
		{
			m_logger.warn(error);
		}
		assertTrue(validator.isValid(new ByteArrayInputStream(docText
				.getBytes())));

	}

	/**
	 * * Check if the returned Web page is a valid XHTML page according to the
	 * W3C standar.
	 * 
	 * If there are any error it is written in the logger establiced in the
	 * logger properties file.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void testW3c() throws IOException, SAXException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException			 		
	{

		TestUtils testUtils = new TestUtils();
		
		// Main page
		WebRequest request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&limitResult=15");
		WebResponse response = m_conversation.getResponse(request);
		w3cValidator(response);

		// Port View.
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&view=LOCAL_PORT");
		response = m_conversation.getResponse(request);
		w3cValidator(response);

		// Specific IP View.
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&ip=10.0.156.110&view=LOCAL_PORT");
		response = m_conversation.getResponse(request);
		w3cValidator(response);

		// Specific Port.
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&port=90&view=LOCAL_IP");
		response = m_conversation.getResponse(request);
		w3cValidator(response);

		// Limitting results ip view.
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "index.jsp?report=totals&start=0&end=300000&limitResult=2&view=LOCAL_IP");
		response = m_conversation.getResponse(request);
		w3cValidator(response);
		
		// Configuration page.
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "configure.jsp");
		response = m_conversation.getResponse(request);
		w3cValidator(response);
												
		// Configuration page after adding a new subnet
		request = new GetMethodWebRequest(testUtils.getUrlPmgraph() + "configure.jsp");
		response = m_conversation.getResponse(request);
		WebForm configurationForm = response.getFormWithID("config");		
		int i = Integer.parseInt(configurationForm.getParameterValue("numSubnets"));
		
		request = new GetMethodWebRequest(
				testUtils.getUrlPmgraph()
						+ "configure.jsp?newSubnet=10.1A.123.&selectSubnet=10.0.156.&numSubnets="+i+"&Go=Save+configuration");
		response = m_conversation.getResponse(request);
		w3cValidator(response);		
	}

	public static Test suite()
	{
		return new TestSuite(W3cValidationTest.class);
	}
}
