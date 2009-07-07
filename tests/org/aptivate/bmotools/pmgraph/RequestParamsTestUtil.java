package org.aptivate.bmotools.pmgraph;

import java.util.Date;
import java.util.HashMap;

/**
 * Class used for RequestParamsTest to fill the hashmap containing the
 * parameters
 * 
 */
public class RequestParamsTestUtil
{

	private Date m_fromDateAndTime;

	private Date m_toDateAndTime;

	private Integer m_resultLimit;

	private View m_view;

	private String m_sortBy;

	private String m_order;

	private String m_ip;

	private Integer m_port;

	private String m_remoteIp;

	private Integer m_remotePort;

	HashMap<String, Object> hashmap = new HashMap<String, Object>();

	private RequestParamsTestUtil() {

	}

	public RequestParamsTestUtil(HashMap<String, String> hashMap) throws Exception {

		this();

		if (hashMap.get("start") != null)
			m_fromDateAndTime = new Date(Long.valueOf(hashMap.get("start")));
		if (hashMap.get("end") != null)
			m_toDateAndTime = new Date(Long.valueOf(hashMap.get("end")));
		if (hashMap.get("resultLimit") != null)
			m_resultLimit = Integer.valueOf(hashMap.get("resultLimit"));
		else
			m_resultLimit = Configuration.getResultLimit();
		if (hashMap.get("view") != null)
			m_view = View.valueOf(hashMap.get("view"));
		m_sortBy = hashMap.get("sortBy");
		m_order = hashMap.get("order");

		m_ip = hashMap.get("ip");
		if (hashMap.get("port") != null)
			m_port = Integer.valueOf(hashMap.get("port"));
		m_remoteIp = hashMap.get("remote_ip");
		if (hashMap.get("remote_port") != null)
			m_remotePort = Integer.valueOf(hashMap.get("remote_port"));

		hashmap.put("start", m_fromDateAndTime);
		hashmap.put("end", m_toDateAndTime);
		hashmap.put("resultLimit", m_resultLimit);
		hashmap.put("view", m_view);
		hashmap.put("sortBy", m_sortBy);
		hashmap.put("order", m_order);
		hashmap.put("ip", m_ip);
		hashmap.put("port", m_port);
		hashmap.put("remote_ip", m_remoteIp);
		hashmap.put("remote_port", m_remotePort);

	}

	public Object getParams(String paramsName)
	{
		Object obj = null;
		obj = hashmap.get(paramsName);
		return obj;
	}

}
