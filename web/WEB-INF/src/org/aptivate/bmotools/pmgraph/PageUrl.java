package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sylviaw create URL and check if the inputed URL is valid
 *  - History: Noe A. Rodriguez Glez. 18-03-2009 W3C URL compilance Page
 * Date time Validation moved to this class.
 */
public class PageUrl
{
	public enum View
	{
		PORT, IP
	}; // Show Ips or show ports in graph

	private final String m_indexURL = "/pmgraph/index.jsp";

	private final String m_servletURL = "/pmgraph/graphservlet";

	private final String m_legendURL = "/include/legend.jsp";

	private Date m_fromDateAndTime;

	private Date m_toDateAndTime;

	private Integer m_resultLimit;

	private String m_ip;

	private Integer m_port;

	private View m_view;

	private long scrollAmount;

	private long zoomAmount;

	public PageUrl()
	{

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
		zoomAmount = (getEndTime() - getStartTime()) / 2;
		scrollAmount = (getEndTime() - getStartTime()) / 2;
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
				m_resultLimit = Integer.parseInt(request.getParameter("resultLimit"));
			}
			catch (NumberFormatException e)
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
			}
			
			if (m_resultLimit < 0) 
			{
				m_resultLimit = Configuration.getResultLimit();
				throw new PageUrlException(ErrorMessages.RESULT_LIMIT_FORMAT_ERROR);
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
		if ((request.getParameter("ip") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("ip"))))
		{
			m_ip = request.getParameter("ip");

		}
		else
		{ // if user has not defined date time get it from start and end
			// parameters
			m_ip = null;
		}
		if ((request.getParameter("port") != null)
				&& (!"".equalsIgnoreCase(request.getParameter("port"))))
		{

			try
			{
				m_port = Integer.valueOf(request.getParameter("port"));
			}
			catch (NumberFormatException e)
			{
				throw new PageUrlException(ErrorMessages.PORT_FORMAT_ERROR);
			}
			if (m_port < 0)
			{
				m_port = null;
				throw new PageUrlException(ErrorMessages.NEGATIVE_PORT_NUMBER);
			}

		}
		else
		{ // if user has not defined date time get it from start and end
			// parameters
			m_port = null;
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
				m_view = View.IP; // Default view Value
				throw (new PageUrlException(ErrorMessages.VIEW_FORMAT_ERROR));
			}
		}
		else
		{
			m_view = View.IP; // Default view Value
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
	 * Assign the start and end dates for the graph using start end request values
	 * assuming they are timestamps. If that is not posible a default start and end
	 * time are assigned.
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

	private void setDatesDefault()
	{

		long now = new Date().getTime();
		m_fromDateAndTime = new Date(now - 240 * 60000);
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
		if (exception != null)
			throw exception;
	}

	public Date getFromDateAndTime()
	{
		return m_fromDateAndTime;
	}

	public void setFromDateAndTime(Date fromDateAndTime)
	{
		this.m_fromDateAndTime = fromDateAndTime;
	}

	public Date getToDateAndTime()
	{
		return m_toDateAndTime;
	}

	public void setToDateAndTime(Date toDateAndTime)
	{
		this.m_toDateAndTime = toDateAndTime;
	}

	public long getStartTime()
	{
		return m_fromDateAndTime.getTime();
	}

	public long getEndTime()
	{
		return m_toDateAndTime.getTime();
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

	public void setResultLimit(Integer resultLimit)
	{
		this.m_resultLimit = resultLimit;
	}

	public String getIp()
	{
		return m_ip;
	}

	public void setIp(String ip)
	{
		this.m_ip = ip;
	}

	public Integer getPort()
	{
		return m_port;
	}

	public void setPort(Integer port)
	{
		this.m_port = port;
	}

	public boolean isEspecificPortIpQuery()
	{
		if ((m_port == null) && (m_ip == null))
			return false;
		return true;
	}

	public View getView()
	{
		return m_view;
	}

	public void setView(View view)
	{
		this.m_view = view;
	}

	public long getScrollAmount()
	{
		return scrollAmount;
	}

	public void setScrollAmount(long scrollAmount)
	{
		this.scrollAmount = scrollAmount;
	}

	public long getZoomAmount()
	{
		return zoomAmount;
	}

	public void setZoomAmount(long zoomAmount)
	{
		this.zoomAmount = zoomAmount;
	}

	/**
	 * Just create a String with the parameters for the URL for parameters ip,
	 * port, View. the parameters have priority if and specific Ip querry is
	 * build the parameters port andt view are ignored, port have more priority
	 * than view, then if a port is selected the view is ignored.
	 * 
	 * @param jspInclude
	 *            is this a URL for a JSP include? if false &amp; used to
	 *            separate parameters
	 * @return
	 */
	private String buildIpPortViewParameters(boolean jspInclude)
	{
		String newUrl = "";
		String separator = "&amp;";

		if (jspInclude)
			separator = "&";

		if (isEspecificPortIpQuery()) // specific Queries dont mind in view
		// selection
		{
			if (m_ip != null)
			{
				newUrl = separator + "ip=" + m_ip;
			}
			else
			{
				newUrl = separator + "port=" + m_port;
			}
		}
		else
		{
			if (m_view != null)
				newUrl = separator + "view=" + m_view;
		}
		return newUrl;

	}

	public String getLegendURL(long start, long end, String sortBy, String order)
	{

		String newURL = m_legendURL + "?start=" + start + "&end=" + end
				+ "&sortBy=" + sortBy + "&order=" + order + "&resultLimit="
				+ getResultLimit();
		newURL += buildIpPortViewParameters(true);
		return newURL;
	}

	public String getServetURL(String graph, long start, long end)
	{
		String newURL = m_servletURL + "?graph=" + graph + "&amp;start="
				+ start + "&amp;end=" + end + "&amp;width=780"
				+ "&amp;height=350" + "&amp;resultLimit=" + getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(String report, String graph, long start, long end)
	{
		String newURL = m_indexURL + "?report=" + report + "&amp;graph="
				+ graph + "&amp;start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end, String sortBy, String order)
	{
		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;sortBy=" + sortBy + "&amp;order=" + order
				+ "&amp;resultLimit=" + getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getUrlOneIpGraph(long start, long end, String ip)
	{

		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + getResultLimit() + "&amp;ip=" + ip;
		return newURL;
	}

	public String getUrlOnePortGraph(long start, long end, Integer port)
	{

		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + getResultLimit() + "&amp;port=" + port;
		return newURL;
	}

	public String getZoomInURL(String report, String graph, long start, long end)
	{
		long newZoomInStart = start + zoomAmount / 2;
		long newZoomInEnd = end - zoomAmount / 2;

		return getIndexURL(report, graph, newZoomInStart, newZoomInEnd);
	}

	public String getZoomOutURL(String report, String graph, long start,
			long end)
	{
		long newZoomOutStart = start - zoomAmount;
		long newZoomOutEnd = end + zoomAmount;
		long temp = new Date().getTime();

		// limit the zoom if it create a date in the future
		if (newZoomOutEnd > temp)
		{
			newZoomOutStart -= (newZoomOutEnd - temp);
			newZoomOutEnd = temp;
		}

		return getIndexURL(report, graph, newZoomOutStart, newZoomOutEnd);
	}

	public boolean showZoomIn(long start, long end)
	{
		long newZoomInStart = ((start + zoomAmount / 2) / 6000);
		long newZoomInEnd = ((end - zoomAmount / 2) / 6000);

		return ((newZoomInEnd - newZoomInStart) > 15);
	}

}
