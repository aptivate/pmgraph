package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sylviaw create URL and check if the inputed URL is valid - History:
 *         Noe A. Rodriguez Glez. 18-03-2009 W3C URL compilance Page Date time
 *         Validation moved to this class.
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
	 * Just create a String with the parameters for the URL for parameters ip,
	 * port, View. the parameters have priority if and specific Ip querry is
	 * build the parameters port andt view are ignored, port have more priority
	 * than view, then if a port is selected the view is ignored.
	 * 
	 * @param jspInclude
	 *            is this a URL for a JSP include? if false &amp; used to
	 *            separate parameters
	 * @return String to add to a URL in order to include View parameter
	 *         depending to de the IP or port parameters or the view parameter.
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
	
	public String getLegendURL() {
		String newURL = m_legendURL + "?start=" + m_params.getStartTime() + "&end=" + m_params.getEndTime()
				+ "&sortBy=" + m_params.getSortBy() + "&order=" + m_params.getOrder() + "&resultLimit="
				+ m_params.getResultLimit();
		newURL += buildIpPortViewParameters(true);
		return newURL;
	}

	public String getServetURL()
	{
		String newURL = m_servletURL + "?graph=" + m_params.getGraph() + "&amp;start="
				+ m_params.getStartTime() + "&amp;end=" + m_params.getEndTime() + "&amp;width=780"
				+ "&amp;height=350" + "&amp;resultLimit="
				+ m_params.getResultLimit();

		newURL += buildIpPortViewParameters(false);
		return newURL;
	}

	public String getIndexURL()
	{
		String newURL = m_indexURL + "?report=" + m_params.getReport() + "&amp;graph="
				+ m_params.getGraph() + "&amp;start=" + m_params.getStartTime() + "&amp;end=" + m_params.getEndTime()
				+ "&amp;resultLimit=" + m_params.getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}
		
	public String getIndexURL(long start, long end)
	{
		String newURL = m_indexURL + "?report=" + m_params.getReport() + "&amp;graph="
				+ m_params.getGraph() + "&amp;start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}
	
	public String getIndexURL(long start, long end, boolean resetFlag)
	{
		String newURL = m_indexURL + "?report=" + m_params.getReport() + "&amp;graph="
				+ m_params.getGraph() + "&amp;start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit();				
		return newURL;
	}

	

	public String getIndexURL(String sortBy)
	{
		String order = "DESC";
		// change sortin order just if the result is order by this column
		// else keep default sorting == DESC

		if (sortBy.equalsIgnoreCase(m_params.getSortBy()))
		{
			if ("DESC".equalsIgnoreCase(m_params.getOrder()))
				order = "ASC";
		}

		String newURL = m_indexURL + "?start=" + m_params.getStartTime() + "&amp;end=" + m_params.getEndTime()
				+ "&amp;sortBy=" + sortBy + "&amp;order=" + order
				+ "&amp;resultLimit=" + m_params.getResultLimit();
		newURL += buildIpPortViewParameters(false);
		return newURL;
	}
	
	public String getUrlGraph(Object paramValue, String paramName)
	{
		 // Round our times to the nearest minute
	    long start = m_params.getStartTime() - (m_params.getStartTime() % 60000);
	    long end = m_params.getEndTime() - (m_params.getEndTime() % 60000);
		String newURL = "";
		String extra = "";

		for (String key : m_params.m_params.keySet())
		{
			if (key != paramName) {
				Object param = m_params.m_params.get(key);
				if (param != null)
				{				
					extra += "&amp;"+ key +"=" + param;
				}
			}
		}	
		extra += "&amp;"+ paramName +"=" + paramValue;
		View view = View.getNextView (m_params, paramName);
		
		newURL = m_indexURL + "?start=" + start + "&amp;end=" + end
				+ "&amp;resultLimit=" + m_params.getResultLimit() + extra
				+ "&amp;view=" + view;
		return newURL;
	}
	
	public String getZoomInURL()
	{
		long newZoomInStart = m_params.getStartTime() + m_params.getZoomAmount() / 2;
		long newZoomInEnd = m_params.getEndTime() - m_params.getZoomAmount() / 2;

		return getIndexURL(newZoomInStart, newZoomInEnd);
	}


	public String getZoomOutURL()
	{
		long newZoomOutStart = m_params.getStartTime() - m_params.getZoomAmount();
		long newZoomOutEnd = m_params.getEndTime() + m_params.getZoomAmount();
		long temp = new Date().getTime();

		// limit the zoom if it create a date in the future
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
		long newZoomInStart = ((m_params.getStartTime() + m_params.getZoomAmount() / 2) / 6000);
		long newZoomInEnd = ((m_params.getEndTime() - m_params.getZoomAmount() / 2) / 6000);

		return ((newZoomInEnd - newZoomInStart) > 15);
	}

	/**
	 * test if scrolling forward would take us past the current time if that is
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
