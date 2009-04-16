package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestParams
{

	private Date m_fromDateAndTime;

	private Date m_toDateAndTime;

	private Integer m_resultLimit;

	private View m_view;

	private long m_scrollAmount;

	private long m_zoomAmount;

	private long m_graphSpan;

	private String m_sortBy;

	private String m_order;

	Map<String, Object> m_params;

	public RequestParams()
	{
		m_params = new HashMap<String, Object>();
	}
	
	public RequestParams(long start, long end, View view, int resultLimit,
			Integer port)
	{
		this(start, end, view, resultLimit);
		m_params.put("port", port);
	}
	public RequestParams(long start, long end, View view, int resultLimit,
			String ip)
	{
		this(start, end, view, resultLimit);
		m_params.put("ip", ip);
	}
	
	public RequestParams(long start, long end, View view, int resultLimit)
	{
		this();
		m_fromDateAndTime = new Date (start);
		m_toDateAndTime = new Date (end);
		m_view = view;
		m_resultLimit = resultLimit;
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
		return (String) m_params.get("ip");
	}

	public Integer getPort()
	{
		return (Integer) m_params.get("port");
	}

	public boolean isIpAndPortSelected()
	{
		return ((m_params.get("ip") != null) && (m_params.get("port") != null));
	}

	public View getView()
	{
		return m_view;
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
		return (String) m_params.get("remote_ip");
	}

	public Integer getRemotePort()
	{
		return (Integer) m_params.get("remote_port");
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

	// Round our times to the nearest minute
	public long getRoundedStartTime()
	{
		return m_fromDateAndTime.getTime()
				- (m_fromDateAndTime.getTime() % 60000);
	}

	// Round our times to the nearest minute
	public long getRoundedEndTime()
	{
		return m_toDateAndTime.getTime() - (m_toDateAndTime.getTime() % 60000);
	}
	

	public Map<String, Object> getParams()
	{
		return m_params;
	}

	private void setDatesFromRequest(HttpServletRequest request)
			throws PageUrlException
	{
		// set to default to eer have a value even if any Exception happens
		setDatesDefault();

		// try to get date time defined by user
		try
		{
			if (request.getParameter("fromDate") != null)
			{
				m_fromDateAndTime = getDateTimeFromFromData(request, "from");
				m_toDateAndTime = getDateTimeFromFromData(request, "to");
			}
			else
			{ // if user has not defined date time get it from start and end
				// parameters
				setDatesFromStartEnd(request);
			}
		}
		catch (PageUrlException e)
		{
			setDatesFromStartEnd(request);
			throw e;
		}
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
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws PageUrlException
	 */
	private void setResultLimitFromRequest(HttpServletRequest request)
			throws IOException, PageUrlException
	{
		if ((request.getParameter("resultLimit") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("resultLimit"))))
		{
			try
			{
				m_resultLimit = Integer.parseInt(request
						.getParameter("resultLimit"));
			}
			catch (NumberFormatException e)
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(
						ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
			}

			if (m_resultLimit < 0)
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(
						ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
			}
		}
		else
		{ // if user has not defined resultLimit get it from default
			m_resultLimit = Configuration.getResultLimit();
		}
	}

	/**
	 * If a Ip parameter is set un the request the ip value is set.
	 * 
	 * @param request
	 * @throws NumberFormatException
	 * @throws PageUrlException
	 * @throws IOException
	 */
	private void setIpPortFromRequest(HttpServletRequest request)
			throws PageUrlException
	{
		Integer port;

		if ((request.getParameter("ip") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("ip"))))
		{
			m_params.put("ip", request.getParameter("ip"));
		}

		if ((request.getParameter("port") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("port"))))
		{

			try
			{
				port = Integer.valueOf(request.getParameter("port"));
				m_params.put("port", Integer.valueOf(request
						.getParameter("port")));
			}
			catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.PORT_FORMAT_ERROR);
			}
			if (port < 0)
			{
				throw new PageUrlException(ErrorMessages.NEGATIVE_PORT_NUMBER);
			}

		}
	}

	private void setRemoteIpPortFromRequest(HttpServletRequest request)
			throws PageUrlException
	{
		Integer port;

		if ((request.getParameter("remote_ip") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("remote_ip"))))
		{
			m_params.put("remote_ip", request.getParameter("remote_ip"));
		}

		if ((request.getParameter("remote_port") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("remote_port"))))
		{

			try
			{
				port = Integer.valueOf(request.getParameter("remote_port"));
				m_params.put("remote_port", Integer.valueOf(request
						.getParameter("remote_port")));
			}
			catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.PORT_FORMAT_ERROR);
			}
			if (port < 0)
			{
				throw new PageUrlException(ErrorMessages.NEGATIVE_PORT_NUMBER);
			}

		}
	}

	/**
	 * Set the view selected by the user, to a Port view or to a Ip port, Port
	 * view show in the graph the throughput per port. Ip view show throughput
	 * per IP. The views are ommited when a especific Ip or Port is selected.
	 * 
	 * @param request
	 * @throws PageUrlException
	 */
	private void setViewFromRequest(HttpServletRequest request)
			throws PageUrlException
	{

		if ((request.getParameter("view") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("view"))))
		{
			try
			{
				m_view = View.valueOf(request.getParameter("view"));
			}
			catch (IllegalArgumentException e)
			{
				m_view = View.LOCAL_IP; // Default view Value
				throw (new PageUrlException(ErrorMessages.VIEW_FORMAT_ERROR));
			}
		}
		else
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
	 * @throws java.text.ParseException
	 * @throws PageUrlException
	 * @throws Exception
	 */
	private Date getDateTimeFromFromData(HttpServletRequest request, String name)
			throws PageUrlException
	{
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
				"dd/MM/yyyy-HH:mm:ss");
		Date date;

		if ((request.getParameter(name + "Time") != null)
				&& (request.getParameter(name + "Time").length() == 8)
				&& (request.getParameter(name + "Date") != null)
				&& (request.getParameter(name + "Date").length() == 10))
		{
			try
			{
				date = dateTimeFormat.parse(request.getParameter(name + "Date")
						+ "-" + request.getParameter(name + "Time"));
			}
			catch (ParseException e)
			{
				throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
			}
			if ((date == null)
					|| (dateTimeFormat.format(date).equals(
							request.getParameter(name + "Date") + "-"
									+ request.getParameter(name + "Time")) == false))
			{
				throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
			}
			return date;
		}
		else
		{
			throw new PageUrlException(ErrorMessages.DATE_TIME_FORMAT_ERROR);
		}
	}

	/**
	 * Assign the start and end dates for the graph using start end request
	 * values assuming they are timestamps. If that is not posible a default
	 * start and end time are assigned.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void setDatesFromStartEnd(HttpServletRequest request)
			throws NumberFormatException
	{
		if ((request.getParameter("start") != null)
				&& (request.getParameter("end") != null))
		{
			m_fromDateAndTime = new Date(Long.valueOf(request
					.getParameter("start")));
			m_toDateAndTime = new Date(Long
					.valueOf(request.getParameter("end")));
		}
		else
		{
			setDatesDefault();
		}
	}

	private void setSortByFromStartEnd(HttpServletRequest request)
			throws NumberFormatException
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
	 * Set all the parameters of the request necesary to build new URLs
	 * 
	 * @param request
	 * @throws PageUrlException
	 * @throws IOException
	 */
	public void setParameters(HttpServletRequest request)
			throws PageUrlException, IOException
	{
		PageUrlException exception = null;
		setSortByFromStartEnd(request);

		try
		{
			setDatesFromRequest(request);
		}
		catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " "
						+ e.getMessage());
		}
		try
		{
			setResultLimitFromRequest(request);
		}
		catch (PageUrlException e)
		{
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " "
						+ e.getMessage());
		}

		try
		{
			setViewFromRequest(request);
		}
		catch (PageUrlException e)
		{
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " "
						+ e.getMessage());
		}

		try
		{
			setIpPortFromRequest(request);
		}
		catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " "
						+ e.getMessage());
		}
		try
		{
			setRemoteIpPortFromRequest(request);
		}
		catch (PageUrlException e)
		{ // Catch exception in order to continue setting parameters
			if (exception == null)
				exception = e;
			else
				exception = new PageUrlException(exception.getMessage() + " "
						+ e.getMessage());
		}

		if (exception != null)
			throw exception;
	}

}
