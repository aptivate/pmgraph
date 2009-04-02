<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrl"%>
<%@ page import="org.aptivate.bmotools.pmgraph.Configuration"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrl.View" %>
<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
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
	
	// Validate Parameters
	try
	{ 
		 pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}	
	
   	// Change start and end Time
	long startTime = pageUrl.getStartTime();
	long endTime = pageUrl.getEndTime();

    scrollAmount =  pageUrl.getScrollAmount();  
    zoomAmount = pageUrl.getZoomAmount(); 
/*    
    newZoomInStart = ((startTime + zoomAmount/2) / 6000);
    newZoomInEnd = ((endTime - zoomAmount/2) / 6000);
    
    newZoomOutStart = ((startTime - zoomAmount) / 6000);
    newZoomOutEnd = ((endTime + zoomAmount) / 6000);*/
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <script type="text/javascript">       
       
       // <![CDATA[		       					
			function onLoad()
			{
				<% if (errorMsg != null) { %>
					alert("<%=errorMsg%>");
					// Set focus
					document.getElementById ("fromDate").focus();
				<% } %>
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
						<td class="align_right">
						<input type="text" id="resultLimit"  name="resultLimit"   value="<%=pageUrl.getResultLimit()%>" size="3" /></td><td> Results</td>
					</tr>
					<% if (!pageUrl.isEspecificPortIpQuery()) { %>
					<tr>  
						<td>View </td>   
						<td class="align_right">
						<select id="view"  name="view" >
							<option value="IP" >IP</option>
							<% if (pageUrl.getView() == View.PORT) { %>
								<option value="PORT" selected="selected" >Local Port</option>							
							<%} else { %>											
								<option value="PORT">Local Port</option>														
							<%} %>												
						</select>
						</td>
					</tr>	
					<%} %>				
					<tr>  
						<td> </td>   
						<td colspan="2" class="center"><input type="submit" value="Draw Graph" id="Go" name="Go" /> </td>
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

                       	<a name="zoomOut"
                       	   href="<%=pageUrl.getZoomOutURL(report, graph, startTime, endTime)%>" 
                       	   class="control">Zoom -</a>     
                       		
                       	<%if (pageUrl.showZoomIn(startTime,endTime)) {%>
                        	<a	name="zoomIn"
                        		href="<%=pageUrl.getZoomInURL(report, graph, startTime, endTime)%>" 
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