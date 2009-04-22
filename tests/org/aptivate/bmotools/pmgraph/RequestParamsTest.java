package org.aptivate.bmotools.pmgraph;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *  test class RequestParams. 
 *  read request information from XML and create mock HttpServletRequest
 *  get RequestParams object
 *  get mock object wich contain the expect value
 *  compare RequestParams object and the expect value
 *  
 * @author sylviaw
 *
 */
public class RequestParamsTest extends TestCase{
	
	//RequestParams is the class we are going to test

	//a mock object of HttpServletRequest will be created
	private HttpServletRequest m_request;
	
	private SAXReader m_reader = new SAXReader();
	private Document m_doc = null;

	public RequestParamsTest() throws Exception{
				
		// create mock HttpServletRequest object 
		m_request = createNiceMock(HttpServletRequest.class);
	}
	
	/**
	 * Get the RequestParams object, using the mock HttpServletRequest object
	 * @return RequestParams
	 * @throws Exception
	 */
	private RequestParams getRequestParamsFromMockRequest(HashMap<String, String> hashMap) throws Exception{
		//create mock request
		for(String key : hashMap.keySet()){
			expect(m_request.getParameter(key)).andReturn(hashMap.get(key)).anyTimes();
			}
	    replay(m_request);
	    
	    //get RequestParams and call public method 'setParameters'.
	    //m_request is the mock httpServletRequest
		RequestParams requestParams = new RequestParams(); 
		requestParams.setParameters(m_request);
		reset(m_request);
		
		return requestParams;
	}
		
    /**
     * get expect mock object
     * @param hashMap
     * @return RequestParamsTestUtil
     * @throws Exception
     */
	private RequestParamsTestUtil getExpectObj(HashMap<String, String> hashMap) throws Exception{
		RequestParamsTestUtil expectObj = new RequestParamsTestUtil(hashMap);
		return expectObj;
	
	}
	
	/**
	 * read XML and test all the testcases from the XML
	 * @throws Exception
	 */
	public void testSetParameters() throws Exception{		
		m_doc = m_reader.read(new File("./tests/org/aptivate/bmotools/pmgraph/testRequestParamsData.xml"));
		Element rootData = m_doc.getRootElement();
		int listIndex = 1;
		for (Iterator i = rootData.elementIterator("servletRequest"); i
				.hasNext(); listIndex++) {
			
			Element servletRequest = (Element) i.next();			
			Element request = servletRequest.element("request");
			HashMap<String, String> hashMapReq = new HashMap<String, String>(); 
			for (Iterator j = request.elementIterator(); j
			.hasNext();) {
				Element reqData = (Element) j.next();
				hashMapReq.put(reqData.getName(), reqData.getTextTrim());
			}
			
			RequestParams requestParams = getRequestParamsFromMockRequest(hashMapReq);
			
			Element expectResult = servletRequest.element("expectResult");
			HashMap<String, String> hashMapExpect = new HashMap<String, String>(); 
			for (Iterator j = expectResult.elementIterator(); j
			.hasNext();) {
				Element reqData = (Element) j.next();
				hashMapExpect.put(reqData.getName(), reqData.getTextTrim());
			}
			
			RequestParamsTestUtil expectObj = getExpectObj(hashMapExpect);
			
			checkEachParams(expectObj, requestParams, listIndex);
			
		}
				
	}
	
	/**
	 * compare the RequestParams object and the mock object
	 * @param expectObj
	 * @param requestParams
	 * @param listIndex
	 * @throws Exception
	 */
	private void checkEachParams(RequestParamsTestUtil expectObj, RequestParams requestParams, int listIndex) throws Exception{

		assertEquals("test No." + listIndex + " request. test startTime", expectObj.getParams("start"), requestParams.getFromDateAndTime());
		assertEquals("test No." + listIndex + " request. test endTime", expectObj.getParams("end"), requestParams.getToDateAndTime());
		assertEquals("test No." + listIndex + " request. test resultLimit", expectObj.getParams("resultLimit"), requestParams.getResultLimit());
		assertEquals("test No." + listIndex + " request. test view", expectObj.getParams("view"), requestParams.getView());
		assertEquals("test No." + listIndex + " request. test sortBy", expectObj.getParams("sortBy"), requestParams.getSortBy());
		assertEquals("test No." + listIndex + " request. test order", expectObj.getParams("order"), requestParams.getOrder());
		assertEquals("test No." + listIndex + " request. test ip", expectObj.getParams("ip"), requestParams.getParams().get("ip"));
		assertEquals("test No." + listIndex + " request. test port", expectObj.getParams("port"), requestParams.getParams().get("port"));
		assertEquals("test No." + listIndex + " request. test remoteIp", expectObj.getParams("remote_ip"), requestParams.getParams().get("remote_ip"));
		assertEquals("test No." + listIndex + " request. test remotePort", expectObj.getParams("remote_port"), requestParams.getParams().get("remote_port"));
	}
	
	public static Test suite()
	{
		return new TestSuite(RequestParamsTest.class);
	}

}
