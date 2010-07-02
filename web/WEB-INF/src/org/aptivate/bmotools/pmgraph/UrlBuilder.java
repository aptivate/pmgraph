package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.SAXException;

/**
 * This class creates the different URLs using the parameters on the screen
 * 
 * @author sylviaw Create URL and check if the entered URL is valid - History:
 *         Noe A. Rodriguez Glez. 18-03-2009 W3C URL compilance Page Date time
 *         Validation moved to this class. RuchiR. 12-06-2009 Changes made for
 *         dynamic update feature.
 */
public class UrlBuilder
{

	private final String m_indexURL = "index.jsp";

	private final String m_servletURL = "graphservlet";

	private final String m_legendURL = "/include/legend.jsp";

	RequestParams m_params;

	public UrlBuilder() {
		m_params = new RequestParams();
	}

	public RequestParams getParams()
	{
		return m_params;
	}

	public void setParameters(HttpServletRequest request) throws PageUrlException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException, SAXException
	{
		m_params.setParameters(request);
	}

	/**
	 * Just create a String with the parameters for the URL using the parameters
	 * for ip, port and view.
	 * 
	 * @param jspInclude is this a URL for a JSP include? if false '&amp;' is used to
	 *            separate parameters
	 * @return String to add to a URL in order to include View parameter
	 *         depending to the the IP, port or view parameters.
	 */
	private String buildIpPortViewParameters(boolean jspInclude)
	{
		String newUrl = "";
		String separator = "&amp;";

		if (jspInclude)
			separator = "&";

		// key are the keys for the hashMap
		for (String key : m_params.m_reqParams.keySet())
		{
			if (m_params.m_reqParams.get(key) != null)
			{
				newUrl += separator + key + "=" + m_params.m_reqParams.get(key);
			}
		}

		if (m_params.getView() != null)
			newUrl += separator + "view=" + m_params.getView();

		return newUrl;

	}

	private String buildSortParameters(boolean jspInclude)
	{
		String newUrl = "";
		String separator = "&amp;";

		if (jspInclude)
			separator = "&";

		if (m_params.getOrder() != null)
			newUrl += separator + "order=" + m_params.getOrder();

		if (m_params.getSortBy() != null)
			newUrl += separator + "sortBy=" + m_params.getSortBy();

		return newUrl;

	}

	/**
	 * The legend is displayed by a separate call to the server using legend.jsp
	 * in the URL
	 * 
	 * @return URL to get the legend
	 */
	public String getLegendURL()
	{
		return m_legendURL;
	}

	/**
	 * The graph is displayed by a separate call to the server using
	 * graphServlet in the URL
	 * 
	 * @return Servlet URL to get the graph
	 */
	public String getServletURL()
	{
		String newURL = m_servletURL + "?start=" + m_params.getStartTime() + "&amp;end="
				+ m_params.getEndTime() + "&amp;width=780" + "&amp;height=350"
				+ "&amp;resultLimit=" + m_params.getResultLimit();

		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	/**
	 * IndexURL is used for the initial call to the server
	 * 
	 * @return URL to get the main page
	 */
	public String getIndexURL()
	{
		String newURL = m_indexURL + "?start=" + m_params.getStartTime() + "&amp;end="
				+ m_params.getEndTime() + "&amp;resultLimit=" + m_params.getResultLimit()
				+ "&amp;dynamic=" + m_params.getDynamic();
		newURL += buildIpPortViewParameters(false);
		newURL += buildSortParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end, boolean dynamicFlag)
	{
		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end + "&amp;resultLimit="
				+ m_params.getResultLimit() + "&amp;dynamic=" + dynamicFlag;
		newURL += buildSortParameters(false);
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end)
	{
		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end + "&amp;resultLimit="
				+ m_params.getResultLimit() + "&amp;dynamic=" + m_params.getDynamic();
		newURL += buildSortParameters(false);
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end, boolean resetFlag, boolean dynamicFlag)
	{
		String newURL = "";
		if (resetFlag)
		{
			newURL = m_indexURL + "?start=" + start + "&amp;end=" + end + "&amp;dynamic="
					+ dynamicFlag;
		} else
		{
			newURL = m_indexURL + "?start=" + start + "&amp;end=" + end + "&amp;resultLimit="
					+ m_params.getResultLimit() + "&amp;dynamic=" + dynamicFlag;
			newURL += buildSortParameters(false);
			newURL += buildIpPortViewParameters(false);
		}
		return newURL;
	}

	public String getIndexURL(String sortBy)
	{
		String order = "DESC";
		// change sorting order if the result has been ordered by this column
		// else keep default sorting == DESC

		if (sortBy.equalsIgnoreCase(m_params.getSortBy()))
		{
			if ("DESC".equalsIgnoreCase(m_params.getOrder()))
				order = "ASC";
		}

		String newURL = m_indexURL + "?start=" + m_params.getStartTime() + "&amp;end="
				+ m_params.getEndTime() + "&amp;sortBy=" + sortBy + "&amp;order=" + order
				+ "&amp;resultLimit=" + m_params.getResultLimit() + "&amp;dynamic="
				+ m_params.getDynamic();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	// Read through the hashMap using the keys to extract the parameters
	private String getCurrentParams()
	{

		String paramsUrl = "";

		for (String key : m_params.m_reqParams.keySet())
		{
			Object param = m_params.m_reqParams.get(key);
			if (param != null)
			{
				paramsUrl += "&amp;" + key + "=" + param;
			}
		}
		return paramsUrl;
	}

	/**
	 * This method builds the URL that will be followed by clicking on a link
	 * 
	 * @param paramValue Value of the element the user clicked on
	 * @return URL followed in the links
	 */
	public String getLinkUrl(String paramValue)
	{

		// not all the parameters have been selected
		if ((m_params.getParams().size() < 3) && (!"Other".equalsIgnoreCase(paramValue)))
		{

			// Round our times to the nearest minute
			long start = m_params.getStartTime() - (m_params.getStartTime() % 60000);
			long end = m_params.getEndTime() - (m_params.getEndTime() % 60000);
			String newURL = "";
			View view;

			// get current selected params
			String extra = getCurrentParams();
			// add new selected parameter depending of the view
			switch (m_params.getView())
			{
			// select a different view because you have already selected a
			// parameter and the level of the current view is no longer
			// appropriate
			case LOCAL_PORT:
				extra += "&amp;port=" + paramValue;
				view = View.getNextView(m_params, "port");
				break;
			case REMOTE_PORT:
				extra += "&amp;remote_port=" + paramValue;
				view = View.getNextView(m_params, "remote_port");
				break;
			default:
			case LOCAL_IP:
				extra += "&amp;ip=" + paramValue;
				view = View.getNextView(m_params, "ip");
				break;
			case REMOTE_IP:
				extra += "&amp;remote_ip=" + paramValue;
				view = View.getNextView(m_params, "remote_ip");
				break;
			}
			newURL = m_indexURL + "?start=" + start + "&amp;end=" + end + "&amp;resultLimit="
					+ m_params.getResultLimit() + extra + "&amp;view=" + view;
			return newURL;
		}
		return (null);
	}

	/**
	 * This method is used to get a new URL if the user has clicked on the
	 * zoom-in button
	 * 
	 * @return URL to zoom in
	 */
	public String getZoomInURL()
	{
		long newZoomInStart = m_params.getStartTime() + m_params.getZoomAmount() / 2;
		long newZoomInEnd = m_params.getEndTime() - m_params.getZoomAmount() / 2;

		return getIndexURL(newZoomInStart, newZoomInEnd);
	}

	/**
	 * This method is used to get a new URL if the user has clicked on the
	 * zoom-out button
	 * 
	 * @return URL to zoom in
	 */
	public String getZoomOutURL()
	{
		long newZoomOutStart = m_params.getStartTime() - m_params.getZoomAmount();
		long newZoomOutEnd = m_params.getEndTime() + m_params.getZoomAmount();
		long temp = new Date().getTime();

		// limit the zoom if it creates a date in the future
		if (newZoomOutEnd > temp)
		{
			newZoomOutStart -= (newZoomOutEnd - temp);
			newZoomOutEnd = temp;
		}

		return getIndexURL(newZoomOutStart, newZoomOutEnd);
	}

	/**
	 * Returns true if we can zoom in, or false if we are already at maximum
	 * zoom
	 * 
	 * @return boolean
	 */
	public boolean showZoomIn()
	{
		long newZoomInStart = ((m_params.getStartTime() + m_params.getZoomAmount() / 2) / 6000);
		long newZoomInEnd = ((m_params.getEndTime() - m_params.getZoomAmount() / 2) / 6000);

		return ((newZoomInEnd - newZoomInStart) > 15);
	}

	/**
	 * Test if scrolling forward would take us past the current time. If that is
	 * the case we will show a "Current" button instead of the "Next" button
	 * 
	 * @return boolean
	 */
	public boolean isShowCurrent()
	{
		long now = new Date().getTime();
		return (m_params.getEndTime() + m_params.getScrollAmount() >= now);
	}

}
