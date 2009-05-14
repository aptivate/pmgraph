<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.UrlBuilder"%>
<%@ page import="org.aptivate.bmotools.pmgraph.Configuration"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.View" %>
<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long scrollAmount, zoomAmount, graphSpan;
    String colon="", alignPort="align_right", name="";
        
         //the sort parameters
    //sortBy: bytes_total | downloaded | uploaded
	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");
    
	 //methods to get new URL
    UrlBuilder pageUrl = new UrlBuilder();

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
	long startTime = pageUrl.getParams().getStartTime();
	long endTime = pageUrl.getParams().getEndTime();
	
    graphSpan =  pageUrl.getParams().getGraphSpan();  
    scrollAmount =  pageUrl.getParams().getScrollAmount();  
    zoomAmount = pageUrl.getParams().getZoomAmount();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <% if (errorMsg != null) { %>
        <script type="text/javascript"> 
        // <![CDATA[		       					
			function onLoad()
			{
				alert("<%=errorMsg%>");
				// Set focus
				document.getElementById ("fromDate").focus();
			}
		// ]]>	
        </script>
        <% } %>
    </head>
    <% if (errorMsg != null) { %>
    	<body onload="onLoad();">   
    <% } else { %>
    	<body>
    <% } %>
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
			   			<td class="align_right"> <input type="text" id="fromDate" name="fromDate" value="<%=pageUrl.getParams().getFromDateAsString()%>" size="8" /> </td>
            		    <td> <input type="text" id="toDate"   name="toDate" value="<%=pageUrl.getParams().getToDateAsString()%>"  size="8" /> </td>
				    </tr>
					<tr>
						<td>Time (hh:mm:ss)</td>  
						<td class="align_right"> <input type="text" id="fromTime" name="fromTime" value="<%=pageUrl.getParams().getFromTimeAsString()%>" size="8" /> </td>
						<td> <input type="text" id="toTime"   name="toTime"   value="<%=pageUrl.getParams().getToTimeAsString()%>" size="8" /> </td>	     
					</tr>
					<tr>  
						<td>Show Top </td>   
						<td class="align_right">
						<input type="text" id="resultLimit"  name="resultLimit"   value="<%=pageUrl.getParams().getResultLimit()%>" size="3" /></td><td> Results</td>
					</tr>
					<% if ((pageUrl.getParams().getIp() != null) || (pageUrl.getParams().getPort() != null)){	%>				
					<tr>  
					
					<% if ((pageUrl.getParams().getIp() != null) && (pageUrl.getParams().getPort() != null)){
						colon=":";
						alignPort = "align_left";
					%>
						<td>Selected Ip:port</td>   
					<%} else { %>	
						<% if (pageUrl.getParams().getIp() != null) { %>
							<td>Selected Ip</td>   
						<%} else  {%>
							<% if (pageUrl.getParams().getPort() != null) { %>
								<td>Selected Port</td>   
							<%} %>
						<%} %>
					<%} %>										
					<% if (pageUrl.getParams().getIp() != null) { %>
						<td class="align_left">
						<input type="text" id="ip"  name="ip"   value="<%=pageUrl.getParams().getIp()%>" size="12" /></td>
					<%} %>					
					<% if (pageUrl.getParams().getPort() != null) { %>
						<td class="<%=alignPort %>"><%=colon %>
						<input type="text" id="port"  name="port"   value="<%=pageUrl.getParams().getPort()%>" size="6" /></td>
					<%} %>					
					</tr>  
					<%}%>			
					<% if ((pageUrl.getParams().getRemoteIp() != null) || (pageUrl.getParams().getRemotePort() != null)){%>					
					<tr>  
					<% if ((pageUrl.getParams().getRemoteIp() != null) && (pageUrl.getParams().getRemotePort() != null)){
						colon=":";
						alignPort = "align_left";
					%>
						<td>Selected Remote Ip:port</td>   
					<%} else { %>	
						<% if (pageUrl.getParams().getRemoteIp() != null) { %>
							<td>Selected Remote Ip</td>   
						<%} else  {%>
							<% if (pageUrl.getParams().getRemotePort() != null) { %>
								<td>Selected Remote Port</td>   
							<%} %>
						<%} %>
					<%} %>										
					<% if (pageUrl.getParams().getRemoteIp() != null) { %>
						<td class="align_left">
						<input type="text" id="remote_ip"  name="remote_ip"   value="<%=pageUrl.getParams().getRemoteIp()%>" size="12" /></td>
					<%} %>					
					<% if (pageUrl.getParams().getRemotePort() != null) { %>
						<td class="<%=alignPort %>"><%=colon %>
						<input type="text" id="remote_port"  name="remote_port"   value="<%=pageUrl.getParams().getRemotePort()%>" size="6" /></td>
					<%} %>							
					</tr>  
					<%}%>
					
					<tr>  
						<td>View </td>   
						<td class="align_right">
						<select id="view"  name="view" >
							<% List<View> views = View.getAvailableViews(pageUrl.getParams());
							for (View view : views) {  // for each view available
								name = view.toString().toLowerCase().replace("_"," ");
								if (pageUrl.getParams().getView() == view) { %>
									<option value="<%=view %>" selected="selected" ><%=name %></option>		
								<%} else { %>											
									<option value="<%=view %>" ><%=name %></option>	
								<%} %>
							<% } %>
						</select>
						</td>
						<td  colspan="2" class="center"><input type="submit" value="Draw Graph" id="Go" name="Go" /> </td>
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
                            src="<%=pageUrl.getServetURL()%>"
                            width="760" height="350" />
                </div>
                
                <!-- Move back/forward or zoom in/out -->
                <div id="controls">
                    <a name="prev"
                       href="<%=pageUrl.getIndexURL((startTime - scrollAmount), (endTime - scrollAmount))%>"
                        class="control">Prev.</a>
                    <div id="controlscenter">

                       	<a name="zoomOut"
                       	   href="<%=pageUrl.getZoomOutURL()%>" 
                       	   class="control">Zoom -</a>                            		
                       	<%if (pageUrl.showZoomIn()) {%>
                        	<a	name="zoomIn"
                        		href="<%=pageUrl.getZoomInURL()%>" 
                        		class="control">Zoom +</a>
                      	<%}%>             
                    </div>
                    <%if (pageUrl.isShowCurrent()) {%>
                    <!-- show current -->
                    <a name="current" 
                       href="<%=pageUrl.getIndexURL((now - graphSpan), now)%>" 
                       class="control">Current</a>
                    <%} else {%>
                    <a name="next" 
                       href="<%=pageUrl.getIndexURL((startTime + scrollAmount), (endTime + scrollAmount))%>" 
                       class="control">Next</a>
                    <%}%>
                </div>  
                <div id="legend">
                    <jsp:include page="<%=pageUrl.getLegendURL()%>" />
                </div>
            </div>
 			<div class="center">
 				<a class="left" href="javascript:history.back(1);">Back</a> 	 				 				
 			    <a class="align_right" href="<%=pageUrl.getIndexURL(startTime, endTime, true)%>">Reset</a>			
 			</div> 			
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>