package org.aptivate.bmotools.pmgraph;

/**
 * @author sylviaw
 * creat URL and check if the inputed URL is valid
 * 
 *- History:
 *		Noe A. Rodriguez Glez.
 *		18-03-2009 	W3C URL compilance 
 */
public class pageURL {
	
    private final String m_indexURL = "/pmgraph/index.jsp";
    private final String m_servletURL = "/pmgraph/graphservlet";
    private final String m_legendURL = "/include/legend.jsp";      

	public String getLegendURL(long start, long end, String sortBy, String order)
	{
		
		String newURL = m_legendURL + 
	    "?start=" + start +
	    "&end=" + end +
        "&sortBy=" + sortBy +
        "&order=" + order;
		return newURL;
	}

	public String getServetURL(String graph, long start, long end)
	{
		String newURL = m_servletURL + 
		"?graph=" + graph +
	    "&amp;start=" + start +
	    "&amp;end=" + end +
	    "&amp;width=780" +
	    "&amp;height=350";		
		return newURL;		
	}
	
	
	public String getIndexURL(String report, String graph, long start, long end)
	{
		String newURL = m_indexURL + 
		"?report=" + report +
		"&amp;graph=" + graph +
	    "&amp;start=" + start +
	    "&amp;end=" + end;
		return newURL;
	}
	
	public String getIndexURL(long start, long end, String sortBy, String order)
	{
		String newURL = m_indexURL + 
		"?start=" + start +
		"&amp;end=" + end +
	    "&amp;sortBy=" + sortBy +
	    "&amp;order=" + order;
		return newURL;
	}


}
