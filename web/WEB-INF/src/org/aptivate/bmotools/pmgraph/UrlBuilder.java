package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
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

	public UrlBuilder()
	{
		m_params = new RequestParams();
	}

	public RequestParams getParams()
	{
		return m_params;
	}

	public void setParameters(HttpServletRequest request)
			throws PageUrlException, IOException
	{

		m_params.setParameters(request);
	}

	/**
	 * Just create a String with the parameters for the URL using the parameters
	 * for ip, port and view.
	 * 
	 * @param jspInclude
	 *            is this a URL for a JSP include? if false '&amp;' is used to
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

		for (String key : m_params.m_params.keySet())
		{
			if (m_params.m_params.get(key) != null)
			{
				newUrl += separator + key + "=" + m_params.m_params.get(key);
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

	public String getLegendURL()
	{

		return m_legendURL;
	}

	public String getServetURL()
	{
		String newURL = m_servletURL + "?start=" + m_params.getStartTime()
				+ "&amp;end=" + m_params.getEndTime() + "&amp;width=780"
				+ "&amp;height=350" + "&amp;resultLimit="
				+ m_params.getResultLimit();

		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL()
	{
		String newURL = m_indexURL + "?start=" + m_params.getStartTime()
				+ "&amp;end=" + m_params.getEndTime() + "&amp;resultLimit="
				+ m_params.getResultLimit() + "&amp;dynamic="
				+ m_params.getDynamic();
		newURL += buildIpPortViewParameters(false);
		newURL += buildSortParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end, boolean dynamicFlag)
	{
		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit()
				+ "&amp;dynamic=" + dynamicFlag;
		newURL += buildSortParameters(false);
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end)
	{
		String newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit()
				+ "&amp;dynamic=" + m_params.getDynamic();
		newURL += buildSortParameters(false);
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL(long start, long end, boolean resetFlag,
			boolean dynamicFlag)
	{
		String newURL = "";
		if (resetFlag)
		{
			newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
					+ "&amp;dynamic=" + dynamicFlag;
		} else
		{
			newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
					+ "&amp;resultLimit=" + m_params.getResultLimit()
					+ "&amp;dynamic=" + dynamicFlag;
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

		String newURL = m_indexURL + "?start=" + m_params.getStartTime()
				+ "&amp;end=" + m_params.getEndTime() + "&amp;sortBy=" + sortBy
				+ "&amp;order=" + order + "&amp;resultLimit="
				+ m_params.getResultLimit() + "&amp;dynamic="
				+ m_params.getDynamic();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getUrlGraph(Object paramValue, String paramName)
	{
		// Round our times to the nearest minute
		long start = m_params.getStartTime()
				- (m_params.getStartTime() % 60000);
		long end = m_params.getEndTime() - (m_params.getEndTime() % 60000);
		String newURL = "";
		String extra = "";

		for (String key : m_params.m_params.keySet())
		{
			if (key != paramName)
			{
				Object param = m_params.m_params.get(key);
				if (param != null)
				{
					extra += "&amp;" + key + "=" + param;
				}
			}
		}
		extra += "&amp;" + paramName + "=" + paramValue;
		View view = View.getNextView(m_params, paramName);

		newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit() + extra
				+ "&amp;view=" + view;
		return newURL;
	}

	public String getZoomInURL()
	{
		long newZoomInStart = m_params.getStartTime()
				+ m_params.getZoomAmount() / 2;
		long newZoomInEnd = m_params.getEndTime() - m_params.getZoomAmount()
				/ 2;

		return getIndexURL(newZoomInStart, newZoomInEnd);
	}

	public String getZoomOutURL()
	{
		long newZoomOutStart = m_params.getStartTime()
				- m_params.getZoomAmount();
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
	 * Returns true if we can zoom in, or false if we are alreadt at maximum
	 * zoom
	 * 
	 * @param start
	 * @param end
	 * @return boolean
	 */
	public boolean showZoomIn()
	{
		long newZoomInStart = ((m_params.getStartTime() + m_params
				.getZoomAmount() / 2) / 6000);
		long newZoomInEnd = ((m_params.getEndTime() - m_params.getZoomAmount() / 2) / 6000);

		return ((newZoomInEnd - newZoomInStart) > 15);
	}

	/**
	 * Test if scrolling forward would take us past the current time. If that is
	 * the case we will show a "Current" button instead of the "Next" button
	 * 
	 * @param start
	 * @param end
	 * @return boolean
	 */
	public boolean isShowCurrent()
	{
		long now = new Date().getTime();
		return (m_params.getEndTime() + m_params.getScrollAmount() >= now);
	}

}
