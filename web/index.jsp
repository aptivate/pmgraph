<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrl"%>
<%@ page import="org.aptivate.bmotools.pmgraph.Configuration"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long startTime =  now - 240 * 60000;
    long endTime = now;    
    long scrollAmount, zoomAmount, newZoomInStart, newZoomInEnd, 
    	newZoomOutStart, newZoomOutEnd;
        
         //the sort parameters
    //sortBy: bytes_total | downloaded | uploaded
	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");
    
    //methods to get new URL
    PageUrl pageUrl = new PageUrl();

    // Input Validation
    String errorMsg = null;    
	final String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !\\n The time format should be : hh:mm:ss !";
	final String START_END_FORMAT_ERROR = "Start and End parameters Should be numbers ! \\n Default start end parameters assumed.";		
	final String RESULT_LIMIT_FORMAT_ERROR = "ResultLimit parameter should by a number ! \\n Default resultLimit value assumed.";	
	// Validate Dates
	try
	{ 
		 pageUrl.setDatesFromRequest(request);
	}
	catch (ParseException e)
	{
		errorMsg = DATE_TIME_FORMAT_ERROR;
	}
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}
	catch (NumberFormatException e)
	{
		errorMsg = START_END_FORMAT_ERROR;
	}
	
	try
	{ 
		 pageUrl.setResultLimitFromRequest(request);
	}	
	catch (NumberFormatException e)
	{
		errorMsg = RESULT_LIMIT_FORMAT_ERROR;
	}

	
	
   	// Change start and end Time
	startTime = pageUrl.getStartTime();
	endTime = pageUrl.getEndTime();

    scrollAmount = (endTime - startTime) / 2;  
    zoomAmount = (endTime - startTime) / 2; 
    
    newZoomInStart = ((startTime + zoomAmount/2) / 6000);
    newZoomInEnd = ((endTime - zoomAmount/2) / 6000);
    
    newZoomOutStart = ((startTime - zoomAmount) / 6000);
    newZoomOutEnd = ((endTime + zoomAmount) / 6000);
%>
<%@page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <script type="text/javascript">
       // <![CDATA[							
			function onLoad()
			{
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
			   			<td> <input type="text" id="fromDate" name="fromDate" value="<%=pageUrl.getFromDateAsString()%>" size="8" /> </td>
            		    <td> <input type="text" id="toDate"   name="toDate" value="<%=pageUrl.getToDateAsString()%>"  size="8" /> </td>
				    </tr>
					<tr>
						<td>Time (hh:mm:ss)</td>  
						<td> <input type="text" id="fromTime" name="fromTime" value="<%=pageUrl.getFromTimeAsString()%>" size="8" /> </td>
						<td> <input type="text" id="toTime"   name="toTime"   value="<%=pageUrl.getToTimeAsString()%>" size="8" /> </td>	     
					</tr>
					<tr>  
						<td>Show Top </td>   
						<td>
						<input type="text" id="resultLimit"  name="resultLimit"   value="<%=pageUrl.getResultLimit()%>" size="3" /> IP's</td>
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