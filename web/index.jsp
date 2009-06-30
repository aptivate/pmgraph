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
    
    //dynamic parameter
    boolean dynamicFlag=pageUrl.getParams().getDynamic(); 
      
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <% if (dynamicFlag) { %>
        <script type="text/javascript">  
        	function onReload()			
        	{	
        		document.getElementById("dynamic").checked="true";
				t=setTimeout('update()',180000);															
			}			
						
			function checkTimeDate(i)
			{
				if (i<10)
				{
					i="0" + i;
				}
				return i;
			}						
						
			function update()
			{			    
			    //date
			    var today=new Date();			    
			    var day=today.getDate();
			    var mon=today.getMonth();
			    var yr=today.getFullYear();
			    // add a zero in front of numbers<10
				day=checkTimeDate(day);
				mon=mon+1;
				mon=checkTimeDate(mon);
			    
				var toDate1 = document.getElementById('toDate').value=day+"/"+mon+"/"+yr;
				var fromDate1 = document.getElementById('fromDate').value=day+"/"+mon+"/"+yr;
				
				//time
				var h=today.getHours();
				var fh=h-3;
				
				// At midnight h = 0 & fh = -3, wrap fh to show correct time. 
				if( fh < 0 ) {
				   fh = 24 + fh;
				   var Yesterday = new date( today.getTime() - 86400000 ); 
				   var YesterdayMon = Yesterday.getMonth(); 
				   var YesterdayYear = Yesterday.getFullYear(); 
				   var YesterdayDate = Yesterday.getDate(); 
				   // add a zero in front of numbers<10
				   YesterdayDate=checkTimeDate(YesterdayDate);
				   YesterdayMon=YesterdayMon+1;
				   YesterdayMon=checkTimeDate(YesterdayMon);
				   fromDate1 = document.getElementById('fromDate').value=YesterdayDate+"/"+YesterdayMon+"/"+YesterdayYear;
				}				

				var m=today.getMinutes();
				var s=today.getSeconds();
				// add a zero in front of numbers<10
				h=checkTimeDate(h);
				fh=checkTimeDate(fh);
				m=checkTimeDate(m);
				s=checkTimeDate(s);	
				var toTime1 = document.getElementById('toTime').value=h+":"+m+":"+s;
				var fromTime1 = document.getElementById('fromTime').value=fh+":"+m+":"+s;
				var url = window.location.href;								
				var componentList = url.split('?');
				var newUrl = componentList[0];
				var szDocument = componentList[componentList.length-1];				
				var documentFilename = szDocument.split('&');	
				documentFilename[0] = "fromDate=" + fromDate1;
				documentFilename[1] = "toDate=" + toDate1;			
				documentFilename[2] = "fromTime=" + fromTime1;
				documentFilename[3] = "toTime=" + toTime1;	
				newUrl = newUrl + "?" + documentFilename[0];			
				var i;				
				for (i=1;i<=(documentFilename.length-1);i++)
				{
					newUrl = newUrl + "&" + documentFilename[i];
				}
				//console.log(newUrl);
				window.location.replace(newUrl);				
											
			}
        </script>
        <% } %>
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
    <% } else if (dynamicFlag) { %>
    	<body onload="onReload();">
    <% } else { %>
    	<body>
    <% } %>
        <div id="container">
            <div id="header">
                <img id="logo" alt="Logo Banner" src="images/logo.png" />
            <div id="date_form">
            	<form id="SetDateAndTime"  action="">
            	<script type="text/javascript">  
				        function check()
						{							
							alert("Click 'Draw Graph' button to enable Dynamic Update.");	
							document.getElementById( 'dynamic' ).value = "true";														
						}   
						function uncheck()
						{							
							alert("Click 'Draw Graph' button to disable Dynamic Update.");	
							document.getElementById( 'dynamic' ).value = "false";														
						}   
					</script>
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
						
					</tr>
					
					<tr>						
						<td>Dynamic Update: </td> 
						<td class="align_right">																
						<input type="checkbox" id="dynamic" name="dynamic" value="false" onclick="if (this.checked) {check();} else {uncheck();}" />						
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
                            src="<%=pageUrl.getServletURL()%>"
                            width="760" height="350" />
                </div>
                
                <!-- Move back/forward or zoom in/out -->
                <div id="controls">
                    <a name="prev"
                       href="<%=pageUrl.getIndexURL((startTime - scrollAmount), (endTime - scrollAmount), dynamicFlag)%>"
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
                       href="<%=pageUrl.getIndexURL((now - graphSpan), now, dynamicFlag)%>" 
                       class="control">Current</a>
                    <%} else {%>
                    <a name="next" 
                       href="<%=pageUrl.getIndexURL((startTime + scrollAmount), (endTime + scrollAmount), dynamicFlag)%>" 
                       class="control">Next</a>
                    <%}%>
                </div>  
                <div id="legend">
                    <jsp:include page="<%=pageUrl.getLegendURL()%>" />
                </div>
            </div>
 			<div class="center">
 				<a class="left" href="javascript:history.back(1);">Back</a> 	 				 				
 			    <a class="align_right" href="<%=pageUrl.getIndexURL(startTime, endTime, true, false)%>">Reset</a>			
 			</div> 			
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>