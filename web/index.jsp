<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrl"%>
<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long startTime = (param = request.getParameter("start")) != null ? Long.parseLong(param) : now - 240 * 60000;
    long endTime = (param = request.getParameter("end")) != null ? Long.parseLong(param) : now;
    
    long scrollAmount = (endTime - startTime) / 2;  
    long zoomAmount = (endTime - startTime) / 2; 
    
    long newZoomInStart = ((startTime + zoomAmount/2) / 6000);
    long newZoomInEnd = ((endTime - zoomAmount/2) / 6000);
    
    long newZoomOutStart = ((startTime - zoomAmount) / 6000);
    long newZoomOutEnd = ((endTime + zoomAmount) / 6000);
        
         //the sort parameters
    //sortBy: bytes_total | downloaded | uploaded
	String sortBy = (param = request.getParameter("sortBy")) != null ? param : "bytes_total";
	//order: DESC | ASC
	String order = (param = request.getParameter("order")) != null ? param : "DESC";
    
    //methods to get new URL
    PageUrl pageUrl = new PageUrl();

    // Input Validation
    String errorMsg = null;
    if(request.getParameter("fromDate") != null)
    {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date theFromDate = null;
		Date theToTime = null;
		Date theToDate = null;
		Date theFromTime = null;
		Date fromDateAndTime = null;
		Date toDateAndTime = null;
		final String DATE_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !";
		final String TIME_FORMAT_ERROR = "The time format should be : hh:mm:ss !";
				
		// Validate fromDate
		try
		{ 
			theFromDate = dateFormat.parse(request.getParameter("fromDate"));
		}
		catch (ParseException e)
		{
			errorMsg = DATE_FORMAT_ERROR;
		}

		if (theFromDate == null)
		{
			errorMsg = DATE_FORMAT_ERROR;
		}
		else
		{
			if ((dateFormat.format(theFromDate).equals(request.getParameter("fromDate")) == false) || 
			   ((request.getParameter("fromDate")).length() != 10))
    		{
    			errorMsg = DATE_FORMAT_ERROR;
    		}
    	}
    	// --------------------------------- //
    			
    	if(errorMsg == null)
    	{
    		// Validate toDate
			try
			{ 
				theToDate = dateFormat.parse(request.getParameter("toDate"));

			}
			catch( ParseException e)
			{
				errorMsg = DATE_FORMAT_ERROR;
			}

			if (theToDate == null)
			{
				errorMsg = DATE_FORMAT_ERROR;
			}
			else
			{
				if ((dateFormat.format(theToDate).equals(request.getParameter("toDate")) == false) || 
				   ((request.getParameter("toDate")).length() != 10))
    			{
    				errorMsg = DATE_FORMAT_ERROR;
    			}
    		}
    	}
    	// --------------------------------- //
    			
    	// Validate fromTime
    	if(errorMsg == null)
    	{
			try
			{ 
				theFromTime = timeFormat.parse(request.getParameter("fromTime"));
			}
			catch(ParseException e)
			{
    			errorMsg = TIME_FORMAT_ERROR;
			}
		
			if(theFromTime == null)
			{
				errorMsg = TIME_FORMAT_ERROR;
			}
			else
			{
				if ((timeFormat.format(theFromTime).equals(request.getParameter("fromTime")) == false) || 
				   ((request.getParameter("fromTime")).length() != 8))
    			{
    				errorMsg = TIME_FORMAT_ERROR;
    			}
    		}
    	}
    	// --------------------------------- //
    			
    	// Validate toTime
		if(errorMsg == null)
    	{
			try
			{ 
				theToTime = timeFormat.parse(request.getParameter("toTime"));
			}
			catch(ParseException e)
			{
    			errorMsg = TIME_FORMAT_ERROR;
			}
			
			if(theToTime == null)
			{
				errorMsg = TIME_FORMAT_ERROR;
			}
			else
			{
				if ((timeFormat.format(theToTime).equals(request.getParameter("toTime")) == false) || 
				   ((request.getParameter("toTime")).length() != 8))
    			{
    				errorMsg = TIME_FORMAT_ERROR;
    			}
    		}
    	}
    	// --------------------------------- //
    	
    	// Convert user input to date and time
    	if(errorMsg == null)
    	{
    		fromDateAndTime = new Date(theFromDate.getYear(), theFromDate.getMonth(), theFromDate.getDate(),
        					  theFromTime.getHours(), theFromTime.getMinutes(), theFromTime.getSeconds());
		
			toDateAndTime = new Date(theToDate.getYear(), theToDate.getMonth(), theToDate.getDate(),
        					theToTime.getHours(), theToTime.getMinutes(), theToTime.getSeconds());
    	
    		// Check that the fromDateAndTime and toDateAndTime aren't in the future
			Date currentDateTime = new Date();
			if((fromDateAndTime.getTime() > currentDateTime.getTime()) || 
			   (toDateAndTime.getTime() > currentDateTime.getTime()))
			{
    			errorMsg = "The From and To Date and Time cannot be in the future.";
			}
		}		
		
		// Check that the fromDateAndTime is lesser than the toDateAndTime and
		// check that the toDateAndTime - fromDateAndTime > 1 minutes
		if(errorMsg == null)
    	{
			if((fromDateAndTime.getTime() >= toDateAndTime.getTime()) || 
			  ((toDateAndTime.getTime() - fromDateAndTime.getTime()) < 60000))
			{
    			errorMsg = "The From Date and Time have to be at least 1 minute before the To Date and Time.";
			}
    	}
    	
    	// Change start and end Time
    	if(errorMsg == null)
    	{
    		startTime = fromDateAndTime.getTime();
    		endTime = toDateAndTime.getTime();
    	}
   	}
 %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <script type="text/javascript">
       // <![CDATA[							
			function onLoad()
			{
			<% System.out.println(errorMsg); %>
				if("<%=errorMsg%>"!="null")
				{
					alert("<%=errorMsg%>");
				}
				// Set focus
				document.getElementById ("fromDate").focus();
			}
		// ]]>	
        </script>
    </head>
    <body onload="onLoad();">
        <div id="container">
            <div id="header">
                <img id="logo" alt="Logo Banner" src="images/logo.png" />
            <div id="date_form">
            	<form id="SetDateAndTime"  action="">
            	<table class="layout_table" id="date_table">
					<tr>
						<th> </th>   
						<th class="th_class">From</th>
						<th class="th_class">To</th>
					</tr>				
					<tr>
						<td>Date (dd/mm/yyyy)</td>   
						<%
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
 					        Date date = new Date();	
							String toDate;	
							
							toDate = request.getParameter("toDate");
							if (toDate == null) 
							{
							    toDate = dateFormat.format(date);
							}
							
							// ----------------------------- //
							
							date.setDate(date.getDate()-1);
 					        String fromDate;
							
							fromDate = request.getParameter("fromDate");
							if (fromDate == null) 
							{
							    fromDate = dateFormat.format(date);
							}
						%>		
						
			   			<td> <input type="text" id="fromDate" name="fromDate" value="<%=fromDate%>" size="8" /> </td>
            		    <td> <input type="text" id="toDate"   name="toDate" value="<%=toDate%>"  size="8" /> </td>
				    </tr>
					<tr>
						<td>Time (hh:mm:ss)</td>  
						<%
							dateFormat = new SimpleDateFormat("HH:mm:ss");
							String toTime;	
							
							toTime = request.getParameter("toTime");
							if (toTime == null) 
							{
							    toTime= dateFormat.format(date);
							}
							
							// ----------------------------- //
							
 					        String fromTime;
							
							fromTime = request.getParameter("fromTime");
							if (fromTime == null) 
							{
							    fromTime= dateFormat.format(date);
							}
						%>			
						<td> <input type="text" id="fromTime" name="fromTime" value="<%=fromTime%>" size="8" /> </td>
						<td> <input type="text" id="toTime"   name="toTime"   value="<%=toTime%>" size="8" /> </td>	     
					</tr>
					<tr>  
						<td> </td>   
						<td colspan="2" class="center"><input type="submit" value="Go" id="Go" name="Go" /> </td>
					</tr>
				</table>   
				</form>
	            </div>         
            </div>
            <div style="clear:both;"></div>
            
            <div id="main">
                <!-- Graph parameter controls not yet functional -->
                <div id="graph">
                    <img id="graphimage" alt="Bandwidth Graph" 
                            src="<%=pageUrl.getServetURL(graph, startTime, endTime)%>"
                            width="760" height="350" />
                </div>
                
                <!-- Move back/forward or zoom in/out -->
                <div id="controls">
                    <a name="prev"
                       href="<%=pageUrl.getIndexURL(report, graph, (startTime - scrollAmount), (endTime - scrollAmount))%>"
                        class="control">Prev.</a>
                    <div id="controlscenter">
                    	<%if ((newZoomOutEnd - newZoomOutStart) < 357920) {%>
                        	<a name="zoomOut"
                        	   href="<%=pageUrl.getIndexURL(report, graph, (startTime - zoomAmount), (endTime + zoomAmount))%>" 
                        	   class="control">Zoom -</a>     
                       	<%}%>
                       		
                       	<%if ((newZoomInEnd - newZoomInStart) > 15) {%>
                        	<a	name="zoomIn"
                        		href="<%=pageUrl.getIndexURL(report, graph, (startTime + zoomAmount/2), (endTime - zoomAmount/2))%>" 
                        		class="control">Zoom +</a>
                      	<%}%>             
                    </div>
                    <a name="next" 
                       href="<%=pageUrl.getIndexURL(report, graph, (startTime + scrollAmount), (endTime + scrollAmount))%>" 
                       class="control">Next</a>
                </div>    
                <div id="legend">
                    <jsp:include page="<%=pageUrl.getLegendURL(startTime, endTime, sortBy, order)%>" />
                </div>
            </div>
 
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>