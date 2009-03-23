package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sylviaw
 * create URL and check if the inputed URL is valid
 * 
 *- History:
 *		Noe A. Rodriguez Glez.
 *		18-03-2009 	W3C URL compilance 
 *					Page Date time Validation moved to this class.
 */
public class PageUrl {
	
	private final String m_indexURL = "/pmgraph/index.jsp";
    private final String m_servletURL = "/pmgraph/graphservlet";
    private final String m_legendURL = "/include/legend.jsp";      
    private Date m_fromDateAndTime;
    private Date m_toDateAndTime;
    private Integer m_resultLimit;
    final String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !\\n The time format should be : hh:mm:ss !";
    
    public PageUrl () 
    {
    	
    }
    
    public void setDatesFromRequest (HttpServletRequest request) throws  PageUrlException, java.text.ParseException, NumberFormatException
    {
    	// set to default to eer have a value even if any Exception happens
    	setDatesDefault();    	
    	
    	// try to get date tome defined by user
    	try {
	    	if(request.getParameter("fromDate") != null) {	
	    		m_fromDateAndTime = getDateTimeFromFromData(request, "from");
				m_toDateAndTime = getDateTimeFromFromData(request, "to");
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
    		setDatesDefault();   
   			throw new PageUrlException("The From Date and Time have to be at least 1 minute before the To Date and Time.");
		}    	
    	if(m_toDateAndTime.getTime() > new Date().getTime())
		{
    		setDatesDefault();
    		throw new PageUrlException("The From and To Date and Time cannot be in the future.");
		}
    }
    
    /**
     * Set the value of resultLimit to the user defined value or to default value
     * established in config file if user have not set a value. 
     * @param request
     * @throws NumberFormatException
     * @throws IOException
     */
    public void setResultLimitFromRequest (HttpServletRequest request) throws NumberFormatException, IOException
    {
    	if ((request.getParameter("resultLimit") != null)
    		&& (!"".equalsIgnoreCase(request.getParameter("resultLimit")))) {	
    		m_resultLimit = Integer.valueOf (request.getParameter("resultLimit"));

    	} else {	// if user has not defined date time get it from start and end parameters
    		m_resultLimit = Configuration.getResultLimit();
    	}
    }
    
    
    /**
     * Asign the start and end dates for the graph using start end values
     * @param request
     * @param  name prefix to be add to get infromation from request (from or to)
     * @return A Date time obtained from request totime todate and fromTime fromDate
     * @throws java.text.ParseException 
     * @throws PageUrlException 
     * @throws Exception
     */   
    private Date getDateTimeFromFromData(HttpServletRequest request, String name) throws java.text.ParseException, PageUrlException {
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
     *  Asign the star and aend dates for the graph using star end  request values
     *  asuming they are timestaps. If that is not posible a default star end time
     *  are asigned.
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
        "&order=" + order +
        "&resultLimit=" + getResultLimit();	
		return newURL;
	}

	public String getServetURL(String graph, long start, long end)
	{
		String newURL = m_servletURL + 
		"?graph=" + graph +
	    "&amp;start=" + start +
	    "&amp;end=" + end +
	    "&amp;width=780" +
	    "&amp;height=350" + 
	    "&amp;resultLimit=" + getResultLimit();		
		return newURL;		
	}
	
	
	public String getIndexURL(String report, String graph, long start, long end)
	{
		String newURL = m_indexURL + 
		"?report=" + report +
		"&amp;graph=" + graph +
	    "&amp;start=" + start +
	    "&amp;end=" + end +
	    "&amp;resultLimit=" + getResultLimit();		
		return newURL;
	}
	
	public String getIndexURL(long start, long end, String sortBy, String order)
	{
		String newURL = m_indexURL + 
		"?start=" + start +
		"&amp;end=" + end +
	    "&amp;sortBy=" + sortBy +
	    "&amp;order=" + order +
	    "&amp;resultLimit=" + getResultLimit();		
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

	public Integer getResultLimit()
	{
		return m_resultLimit;
	}

	public void setResultLimit(Integer resultLimit)
	{
		this.m_resultLimit = resultLimit;
	}
}
