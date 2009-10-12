package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is used to store and access the parameters entered by the user
 * 
 * RuchiR. 12-06-2009 Additions made for dynamic update feature.
 */

public class RequestParams
{

	private Date m_fromDateAndTime;

	private Date m_toDateAndTime;

	private Integer m_resultLimit;

	private boolean m_dynamic;

	private View m_view;

	private long m_scrollAmount;

	private long m_zoomAmount;

	private long m_graphSpan;

	private String m_sortBy;

	private String m_order;
	
	private String m_localSubnet;

	Map<String, Object> m_reqParams;

	public RequestParams() {
		m_reqParams = new HashMap<String, Object>();
	}

	RequestParams(Map<String, Object> params) {
		m_reqParams = params;
	}

	// Currently, this method is used in test code only
	RequestParams(long start, long end, View view, int resultLimit, Integer port) {
		this(start, end, view, resultLimit);
		m_reqParams.put("port", port);
	}

	// Currently, this method is used in test code only
	RequestParams(long start, long end, View view, int resultLimit, String ip) {
		this(start, end, view, resultLimit);
		m_reqParams.put("ip", ip);
	}

	// Currently, this method is used in test code only
	RequestParams(long start, long end, View view, int resultLimit) {
		this();
		m_fromDateAndTime = new Date(start);
		m_toDateAndTime = new Date(end);
		m_view = view;
		m_resultLimit = resultLimit;
	}

	// Currently, this method is used in test code only
	void setRemoteIp(String remoteIp)
	{

		m_reqParams.put("remote_ip", remoteIp);
	}

	// Currently, this method is used in test code only
	void setPort(Integer port)
	{
		m_reqParams.put("port", port);
	}

	void setView(View view)
	{
		m_view = view;
	}

	void setStart(long start)
	{
		m_fromDateAndTime = new Date(start);
	}

	void setEnd(long end)
	{
		m_toDateAndTime = new Date(end);
	}

	void setDynamic(boolean dynamic)
	{
		m_dynamic = dynamic;
	}
	
	void setSubnet(String localSubnet)
	{
		m_localSubnet = localSubnet;
	}

	public String getFromDateAsString()
	{

		return (getDateAsString(m_fromDateAndTime));
	}

	public String getToDateAsString()
	{
		return (getDateAsString(m_toDateAndTime));
	}

	public String getFromTimeAsString()
	{
		return (getTimeAsString(m_fromDateAndTime));
	}

	public String getToTimeAsString()
	{
		return (getTimeAsString(m_toDateAndTime));
	}

	public Integer getResultLimit()
	{
		return m_resultLimit;
	}

	public String getIp()
	{
		return (String) m_reqParams.get("ip");
	}

	public Integer getPort()
	{
		return (Integer) m_reqParams.get("port");
	}

	public boolean isIpAndPortSelected()
	{
		return ((m_reqParams.get("ip") != null) && (m_reqParams.get("port") != null));
	}

	public View getView()
	{
		return m_view;
	}

	public boolean getDynamic()
	{
		return m_dynamic;
	}

	public long getGraphSpan()
	{
		return m_graphSpan;
	}

	public long getScrollAmount()
	{
		return m_scrollAmount;
	}

	public long getZoomAmount()
	{
		return m_zoomAmount;
	}

	public String getOrder()
	{
		return m_order;
	}

	public String getSortBy()
	{
		return m_sortBy;
	}

	private String getDateAsString(Date date)
	{
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateTimeFormat.format(date);
	}

	private String getTimeAsString(Date date)
	{
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
		return dateTimeFormat.format(date);
	}

	public String getRemoteIp()
	{
		return (String) m_reqParams.get("remote_ip");
	}

	public Integer getRemotePort()
	{
		return (Integer) m_reqParams.get("remote_port");
	}

	public Date getFromDateAndTime()
	{
		return m_fromDateAndTime;
	}

	public Date getToDateAndTime()
	{
		return m_toDateAndTime;
	}

	public long getStartTime()
	{
		return m_fromDateAndTime.getTime();
	}

	public long getEndTime()
	{
		return m_toDateAndTime.getTime();
	}

	/**
	 * @return Start time rounded to the nearest minute
	 */
	public long getRoundedStartTime(long resolution)
	{
		return m_fromDateAndTime.getTime() - (m_fromDateAndTime.getTime() % resolution);
	}

	/**
	 * @return End time rounded to the nearest minute
	 */
	public long getRoundedEndTime(long resolution)
	{
		return m_toDateAndTime.getTime() - (m_toDateAndTime.getTime() % resolution);
	}
	
	public String getLocalSubnet()
	{
		return m_localSubnet;
	}

	public Map<String, Object> getParams()
	{
		return m_reqParams;
	}

	private void setDatesFromRequest(HttpServletRequest request) throws PageUrlException
	{
		// set to default to have a value even if any Exception occurs
		setDatesDefault();

		// try to get date time defined by user
		try
		{
			if (request.getParameter("fromDate") != null)
			{
				m_fromDateAndTime = setDateTimeFromFromData(request, "from");
				m_toDateAndTime = setDateTimeFromFromData(request, "to");
			} else
			{ // if user has not defined date time get it from start and end
				// parameters
				setDatesFromStartEnd(request);
			}
		} catch (PageUrlException e)
		{
			setDatesFromStartEnd(request);
			throw e;
		}

		// It has to be more than a minute in the future
		if ((m_fromDateAndTime.getTime() >= m_toDateAndTime.getTime())
				|| ((m_toDateAndTime.getTime() - m_fromDateAndTime.getTime()) < 60000))
		{
			setDatesDefault();
			throw new PageUrlException(ErrorMessages.TIME_NOT_ENOUGH);
		}
		if (m_toDateAndTime.getTime() > new Date().getTime())
		{
			setDatesDefault();
			throw new PageUrlException(ErrorMessages.TIME_IN_FUTURE);
		}
		m_graphSpan = getEndTime() - getStartTime();
		m_zoomAmount = m_graphSpan / 2;
		m_scrollAmount = m_zoomAmount;
	}

	/**
	 * Set the value of resultLimit to the user defined value or to default
	 * value established in config file if user have not set a value.
	 * 
	 * @param request
	 * @throws IOException
	 * @throws PageUrlException
	 */
	private void setResultLimitFromRequest(HttpServletRequest request) throws IOException,
			PageUrlException
	{
		if ((request.getParameter("resultLimit") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("resultLimit"))))
		{
			try
			{

				m_resultLimit = Integer.parseInt(request.getParameter("resultLimit"));
			} catch (NumberFormatException e)
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
			}

			if (m_resultLimit < 0)
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
			}
		} else
		{ // if user has not defined resultLimit get it from default
			m_resultLimit = Configuration.getResultLimit();
		}
	}

	/**
	 * If a Dynamic parameter is set in the request the checkbox value is set.
	 * 
	 * @param request
	 * @throws PageUrlException
	 */
	private void setDynamicFromRequest(HttpServletRequest request) throws PageUrlException
	{

		if (request.getParameter("dynamic") != null)
		{
			m_dynamic = Boolean.parseBoolean(request.getParameter("dynamic"));
		} else
			m_dynamic = false;
	}

	/**
	 * Set the IP value to match the request.
	 * 
	 * @param request
	 * @throws NumberFormatException
	 * @throws PageUrlException
	 */
	private void setIpPortFromRequest(HttpServletRequest request) throws PageUrlException,
			NumberFormatException
	{
		Integer port;

		if ((request.getParameter("ip") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("ip"))))
		{
			if (isValidIP(request.getParameter("ip")))
			{
				m_reqParams.put("ip", request.getParameter("ip"));
			} else
			{
				throw new PageUrlException(ErrorMessages.IP_FORMAT_ERROR);
			}
		}

		if ((request.getParameter("port") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("port"))))
		{
			try
			{
				port = Integer.valueOf(request.getParameter("port"));
				m_reqParams.put("port", Integer.valueOf(request.getParameter("port")));
			} catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.PORT_FORMAT_ERROR);
			}
			if (port < 0)
			{
				throw new PageUrlException(ErrorMessages.NEGATIVE_PORT_NUMBER);
			}
			if (port > 65535)
			{
				throw new PageUrlException(ErrorMessages.PORT_NUMBER_TOO_BIG);
			}
		}
	}
	
	
	/**
	 * The value of local subnet textbox is set as per request.
	 * 
	 * @param request
	 * @throws PageUrlException
	 */
	private void setSubnetFromRequest(HttpServletRequest request) throws PageUrlException
	{

		if (request.getParameter("localSubnet") != null)
		{
			m_localSubnet = request.getParameter("localSubnet");
		} else
			m_localSubnet = "10.0.156.";
	}

	private boolean isValidIP(String ip) throws NumberFormatException
	{
		// IP address should have format n.n.n.n where n is in the range 0-255
		StringTokenizer initial_st = new StringTokenizer(ip, ".", true);
		if (initial_st.countTokens() != 7)
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(ip, ".");
		if (st.countTokens() != 4)
		{
			return false;
		}

		while (st.hasMoreTokens())
		{
			try
			{
				int ipElement = Integer.valueOf(st.nextToken());
				if (ipElement < 0 || ipElement > 255)
				{
					return false;
				}
			} catch (NumberFormatException e)
			{
				return false;
			}
		}
		return true;
	}

	private void setRemoteIpPortFromRequest(HttpServletRequest request) throws PageUrlException,
			NumberFormatException
	{
		Integer port;

		if ((request.getParameter("remote_ip") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("remote_ip"))))
		{
			if (isValidIP(request.getParameter("remote_ip")))
			{
				m_reqParams.put("remote_ip", request.getParameter("remote_ip"));
			} else
			{
				throw new PageUrlException(ErrorMessages.IP_FORMAT_ERROR);
			}
		}

		if ((request.getParameter("remote_port") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("remote_port"))))
		{

			try
			{
				port = Integer.valueOf(request.getParameter("remote_port"));
				m_reqParams
						.put("remote_port", Integer.valueOf(request.getParameter("remote_port")));
			} catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.PORT_FORMAT_ERROR);
			}
			if (port < 0)
			{
				throw new PageUrlException(ErrorMessages.NEGATIVE_PORT_NUMBER);
			}
			if (port > 65535)
			{
				throw new PageUrlException(ErrorMessages.PORT_NUMBER_TOO_BIG);
			}

		}
	}

	/**
	 * Set the view selected by the user, to a Port view or to a Ip port. Port
	 * view shows in the graph the throughput per port. Ip view shows throughput
	 * per IP. The views are omitted when a specific Ip or Port is selected.
	 * 
	 * @param request
	 * @throws PageUrlException
	 */
	private void setViewFromRequest(HttpServletRequest request) throws PageUrlException
	{
		if ((request.getParameter("view") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("view"))))
		{
			try
			{
				m_view = View.valueOf(request.getParameter("view"));
			} catch (IllegalArgumentException e)
			{
				m_view = View.LOCAL_IP; // Default view Value
				throw (new PageUrlException(ErrorMessages.VIEW_FORMAT_ERROR));
			}
		} else
		{
			m_view = View.LOCAL_IP; // Default view Value
		}
	}

	/**
	 * Assign the start and end dates for the graph using start and end values
	 * 
	 * @param request
	 * @param name
	 *            prefix to be add to get infromation from request (from or to)
	 * @return A Date time obtained from request totime todate and fromTime
	 *         fromDate
	 * @throws PageUrlException
	 */
	private Date setDateTimeFromFromData(HttpServletRequest request, String name)
			throws PageUrlException
	{
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		Date date;

		String requestTime = request.getParameter(name + "Time");
		String requestDate = request.getParameter(name + "Date");

		// shortcut time automatically add the rest

		// add one zero at the beginning if the hour has only one digit
		if (requestTime.length() == 1)
		{
			requestTime = "0" + requestTime;
		} else
		{
			if (requestTime.charAt(1) == ':')
				requestTime = "0" + requestTime;
		}
		if (requestTime.length() == 2)
		{
			requestTime = requestTime + ":00:00";
		}

		if (requestTime.length() == 5)
		{
			requestTime = requestTime + ":00";
		}

		// zero padding if the date has only one digit in the day
		if ((requestDate.length() == 9) && (requestDate.charAt(1) == '/'))
		{
			requestDate = "0" + requestDate;
		}

		if ((requestTime != null) && (requestTime.length() == 8) && (requestDate != null)
				&& (requestDate.length() == 10))
		{
			try
			{
				date = dateTimeFormat.parse(requestDate + "-" + requestTime);
			} catch (ParseException e)
			{
				throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
			}
			if ((date == null)
					|| (dateTimeFormat.format(date).equals(requestDate + "-" + requestTime) == false))
			{
				throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
			}
			return date;
		} else
		{
			throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
		}
	}

	/**
	 * Assign the start and end dates for the graph using start end request
	 * values assuming they are timestamps. If that is not possible a default
	 * start and end time are assigned.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void setDatesFromStartEnd(HttpServletRequest request) throws PageUrlException
	{
		if ((request.getParameter("start") != null) && (request.getParameter("end") != null))
		{
			try
			{
				m_fromDateAndTime = new Date(Long.valueOf(request.getParameter("start")));
				m_toDateAndTime = new Date(Long.valueOf(request.getParameter("end")));
			} catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.START_END_FORMAT_ERROR);
			}
		} else
		{
			setDatesDefault();
		}
	}

	private void setSortByFromStartEnd(HttpServletRequest request) throws NumberFormatException
	{
		if ((request.getParameter("sortBy") != null))
		{
			m_sortBy = request.getParameter("sortBy");
		}
		if ((request.getParameter("order") != null))
		{
			m_order = request.getParameter("order");
		}
	}

	private void setDatesDefault()
	{
		long now = new Date().getTime();
		m_fromDateAndTime = new Date(now - 180 * 60000);
		m_toDateAndTime = new Date(now);
	}

	/**
	 * Set all the parameters of the request necessary to build new URLs
	 * 
	 * @param request
	 *            Request object
	 * @throws PageUrlException
	 * @throws IOException
	 */
	public void setParameters(HttpServletRequest request) throws PageUrlException, IOException
	{
		PageUrlException exception = null;
		setSortByFromStartEnd(request);

		try
		{
			setDatesFromRequest(request);
		} catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}
		try
		{
			setResultLimitFromRequest(request);
		} catch (PageUrlException e)
		{
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}

		try
		{
			setDynamicFromRequest(request);
		} catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}
		
		try
		{
			setSubnetFromRequest(request);
		} catch (PageUrlException e)
		{
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}	

		try
		{
			setViewFromRequest(request);
		} catch (PageUrlException e)
		{
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}

		try
		{
			setIpPortFromRequest(request);
		} catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}
		try
		{
			setRemoteIpPortFromRequest(request);
		} catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " " + e.getMessage());
		}

		if (exception != null)
			throw exception;
	}
}
