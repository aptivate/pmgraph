package org.aptivate.bmotools.pmgraph;

/**
 * @author sylviaw
 * creat URL and check if the inputed URL is valid
 */
public class pageURL {
	
    private String m_indexURL = "/pmgraph/index.jsp";
    private String m_servletURL = "/pmgraph/graphservlet";
    private String m_legendURL = "/include/legend.jsp";      

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
	    "&start=" + start +
	    "&end=" + end +
	    "&width=780" +
	    "&height=350";		
		return newURL;		
	}
	
	
	public String getIndexURL(String report, String graph, long start, long end)
	{
		String newURL = m_indexURL + 
		"?report=" + report +
		"&graph=" + graph +
	    "&start=" + start +
	    "&end=" + end;
		return newURL;
	}
	
	public String getIndexURL(long start, long end, String sortBy, String order)
	{
		String newURL = m_indexURL + 
		"?start=" + start +
		"&end=" + end +
	    "&sortBy=" + sortBy +
	    "&order=" + order;
		return newURL;
	}


}
