package org.aptivate.bmotools.pmgraph;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sylviaw
 * create URL and check if the inputed URL is valid
 * 
 */
public class PageUrl {
	
	private final String m_indexURL = "/pmgraph/index.jsp";
    private final String m_servletURL = "/pmgraph/graphservlet";
    private final String m_legendURL = "/include/legend.jsp";      
    private Date m_fromDateAndTime;
    private Date m_toDateAndTime;
    final String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !\\n The time format should be : hh:mm:ss !";
    
    public PageUrl () 
    {
    	
    }
    
    public void getDatesFromRequest (HttpServletRequest request) throws  PageUrlException, java.text.ParseException, NumberFormatException
    {
    	// set to default to have a value even if any Exception happens
    	setDatesDefault();    	
    	
    	// try to get date time defined by user
    	try {
	    	if(request.getParameter("fromDate") != null) {	
	    		m_fromDateAndTime = setDateTimeFromFromData(request, "from");
				m_toDateAndTime = setDateTimeFromFromData(request, "to");
	    	} else {	// if user has not defined date time get it from start and end parameters
	    		setDatesFromStartEnd(request);
	    	}
    	} catch (PageUrlException e) {
    		setDatesFromStartEnd(request);
    		throw e;
    	} 
    	if((m_fromDateAndTime.getTime() >= m_toDateAndTime.getTime()) || 
		  ((m_toDateAndTime.getTime() - m_fromDateAndTime.getTime()) < 60000))
		{
   			throw new PageUrlException("The From Date and Time have to be at least 1 minute before the To Date and Time.");
		}    	
    	if(m_toDateAndTime.getTime() > new Date().getTime())
		{
    		throw new PageUrlException("The From and To Date and Time cannot be in the future.");
		}
    }
    
    /**
     * Assign the start and end dates for the graph using start and end values
     * @param request
     * @param name
     * @return A Date created from the toDate or from date parameters of the request.
     * @throws java.text.ParseException
     * @throws PageUrlException
     */
    private Date setDateTimeFromFromData(HttpServletRequest request, String name) throws java.text.ParseException, PageUrlException {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		Date date;
		
		if ((request.getParameter(name+"Time") != null) && (request.getParameter(name+"Time").length() == 8) &&
			(request.getParameter(name+"Date") != null) && (request.getParameter(name+"Date").length() == 10)){
			date = dateTimeFormat.parse(request.getParameter(name+"Date")+"-"+request.getParameter(name+"Time"));
			if ((date == null) || 
					(dateTimeFormat.format(date).equals(request.getParameter(name+"Date")+
							"-"+request.getParameter(name+"Time")) == false))
			{
				throw new PageUrlException(DATE_TIME_FORMAT_ERROR);
			}
			return date;
		} else {
			throw new PageUrlException(DATE_TIME_FORMAT_ERROR);
		}
    }

    /**
     *  Assign the start and end dates for the graph using start and end  request values
     *  assuming they are timestamps. If that is not possible a default start and end time
     *  are assigned.
     *  
     * @param request
     * @throws Exception
     */
    private void setDatesFromStartEnd(HttpServletRequest request) throws NumberFormatException {
		
    	
		if ((request.getParameter("start") != null) && (request.getParameter("end")!= null)){
			m_fromDateAndTime = new Date (Long.valueOf (request.getParameter("start")));
			m_toDateAndTime = new Date (Long.valueOf (request.getParameter("end")));
		} else {
			setDatesDefault();
		}    
    }
    
    
    private void setDatesDefault() {
		
    	long now = new Date().getTime();
		m_fromDateAndTime = new Date (now - 240 * 60000);
		m_toDateAndTime = new Date(now);   
    }
    
    
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
	
	public String getFromDateAsString () {
		
		return (getDateAsString(m_fromDateAndTime));
	}
	public String getToDateAsString () {
		
		return (getDateAsString(m_toDateAndTime));
	}
	public String getFromTimeAsString () {
		
		return (getTimeAsString(m_fromDateAndTime));
	}
	public String getToTimeAsString () {
		
		return (getTimeAsString(m_toDateAndTime));
	}
	
	

}
