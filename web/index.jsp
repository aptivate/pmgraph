<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
	
	//scroll and zoom parameters
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
  		<link rel="stylesheet" type="text/css" media="all" href="styles/calendar-green.css" title="win2k-cold-1" />
		<script type="text/javascript" src="jsFiles/calendar.js"></script>
		<script type="text/javascript" src="jsFiles/calendar-en.js"></script>
		<script type="text/javascript" src="jsFiles/calendar-setup.js"></script>	
		<script type="text/javascript">
			function CalendarSetup(valInput, valButton)
			{
				Calendar.setup({
    				inputField:  valInput,     
				    ifFormat:    "%d/%m/%Y",     
				    button:      valButton
				});   
			}
		</script>	        
        <% if (dynamicFlag) { %>
        <script type="text/javascript">
        	//called everytime "dynamic update" feature is chosen, and page gets reloaded
        	function onReload()			
        	{	
        		document.getElementById("dynamic").checked="true";
				t=setTimeout('update()',180000);															
			}			
			
			//add a zero in front of numbers<10			
			function zeroPadTimeDate(i)
			{
				if (i<10)
				{
					i="0" + i;
				}
				return i;
			}						
			
			//This function performs the functionality of updating date and time every 3 minutes. 
			//Then new request URL is built, and windows location is updated to new URL.	
			function update()
			{			    
			    //get current date
			    var today=new Date();			    
			    var day=today.getDate();
			    var mon=today.getMonth();
			    var yr=today.getFullYear();

				day=zeroPadTimeDate(day);
				// one is added to change the returned index 0-11 to 1-12
				mon=mon+1;
				mon=zeroPadTimeDate(mon);
			    
			    //update "toDate" and "fromDate"
			    var toDate1 = document.getElementById('toDate').value=day+"/"+mon+"/"+yr;
				var fromDate1 = document.getElementById('fromDate').value=day+"/"+mon+"/"+yr;
				
				//time
				var h=today.getHours();
				var fh=h-3;
				
				// At midnight h = 0 & fh = -3, wrap fh to show correct time. 
				// also update "fromDate" value to date yesterday
				if( fh < 0 ) {
				   fh = 24 + fh;
				   var Yesterday = new date( today.getTime() - 86400000 ); 
				   var YesterdayMon = Yesterday.getMonth(); 
				   var YesterdayYear = Yesterday.getFullYear(); 
				   var YesterdayDate = Yesterday.getDate(); 
				   YesterdayDate=zeroPadTimeDate(YesterdayDate);
				   // one is added to change the returned index 0-11 to 1-12
				   YesterdayMon=YesterdayMon+1;
				   YesterdayMon=zeroPadTimeDate(YesterdayMon);
				   fromDate1 = document.getElementById('fromDate').value=YesterdayDate+"/"+YesterdayMon+"/"+YesterdayYear;
				}				

				//get current time
				var m=today.getMinutes();
				var s=today.getSeconds();
				h=zeroPadTimeDate(h);
				fh=zeroPadTimeDate(fh);
				m=zeroPadTimeDate(m);
				s=zeroPadTimeDate(s);	
				//update "toTime" and "fromTime"
				var toTime1 = document.getElementById('toTime').value=h+":"+m+":"+s;
				var fromTime1 = document.getElementById('fromTime').value=fh+":"+m+":"+s;
				
				//obtain the current url
				var url = window.location.href;
				
				//split the url at various points to update date and time								
				var componentList = url.split('?');
				var newUrl = componentList[0];
				var szDocument = componentList[componentList.length-1];				
				var documentFilename = szDocument.split('&');	
				documentFilename[0] = "fromDate=" + fromDate1;
				documentFilename[1] = "toDate=" + toDate1;			
				documentFilename[2] = "fromTime=" + fromTime1;
				documentFilename[3] = "toTime=" + toTime1;	
				
				//now build the new url using the latest date and time values
				newUrl = newUrl + "?" + documentFilename[0];			
				var i;				
				for (i=1;i<=(documentFilename.length-1);i++)
				{
					newUrl = newUrl + "&" + documentFilename[i];
				}
				//console.log(newUrl);
				//now reload the new url location
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
        <div class="container">
            <div id="header">
                <img id="logo" alt="Logo Banner" src="images/logo.png" />                
            <div id="date_form">
            	<form id="SetDateAndTime"  action="">
            	<script type="text/javascript"> 
            	// <![CDATA[ 
				        function check(isChecked)
						{
							var message = "";
							var propertyName = "dynamic";
							var startIndex = window.location.href.indexOf(propertyName);
							var dynamicParam;
							if(startIndex != -1)
							{
								dynamicParam = window.location.href.substr(startIndex + propertyName.length + 1);
								dynamicParam = dynamicParam.substr(0, dynamicParam.indexOf("&"));
								if(dynamicParam == "true")
								{
									if(!isChecked)
									{
										message = "Click 'Draw Graph' button to disable Dynamic Update.";
										document.getElementById( 'dynamic' ).value = "false"
									}
								}
								else
								{
									if(isChecked)
									{
										message = "Click 'Draw Graph' button to enable Dynamic Update.";
										document.getElementById( 'dynamic' ).value = "true";
									}
								}
							}
							else
							{
								if(isChecked)
								{
									message = "Click 'Draw Graph' button to enable Dynamic Update.";
									document.getElementById( 'dynamic' ).value = "true";
								}
							}
							if(message != "")
							{	
								alert(message);
							}
						}   	
						//]]>
					</script>

            	<table class="table" id="date_table">
					<tr>
						<th> </th>   
						<th class="th_class">From</th>
						<th class="th_class">To</th>
					</tr>				
					<tr>
						<td>Date (dd/mm/yyyy)</td>   
			   			<td class="align_right"> <input type="text" id="fromDate" name="fromDate" value="<%=pageUrl.getParams().getFromDateAsString()%>" size="8" />  <input type="button" class="fDate" id="fDate" style="background: url('./images/img.gif') no-repeat" onclick="CalendarSetup('fromDate','fDate')"/></td>			   			
            		    <td> <input type="text" id="toDate"   name="toDate" value="<%=pageUrl.getParams().getToDateAsString()%>"  size="8" /> <input type="button" class="tDate" id="tDate" style="background: url('./images/img.gif') no-repeat" onclick="CalendarSetup('toDate','tDate')"/></td>            		    
				    </tr>
					<tr>
						<td>Time (hh:mm:ss)</td>  
						<td class="align_left"> <input type="text" id="fromTime" name="fromTime" value="<%=pageUrl.getParams().getFromTimeAsString()%>" size="8" /> </td>
						<td> <input type="text" id="toTime"   name="toTime"   value="<%=pageUrl.getParams().getToTimeAsString()%>" size="8" /> </td>	     
					</tr>
					<tr>  
						<td>Show Top </td>   
						<td class="align_left">
						<input type="text" id="resultLimit"  name="resultLimit" title="Limits the number of results that will be shown"  value="<%=pageUrl.getParams().getResultLimit()%>" size="3" /> Results </td> 	
					</tr>
					<% //If a specific IP or port has been chosen, add a row/s showing the selected IP and/or port.
					// Local%>
					<% 
					if ((pageUrl.getParams().getIp() != null) || (pageUrl.getParams().getPort() != null)){	%>				
					<tr>  
					
					<% if ((pageUrl.getParams().getIp() != null) && (pageUrl.getParams().getPort() != null)){
						colon=":";
						alignPort = "align_left";
					%>
						<td>Selected IP:port</td>   
					<%} else { %>	
						<% if (pageUrl.getParams().getIp() != null) { %>
							<td>Selected IP</td>   
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
					<% //Remote 
					if ((pageUrl.getParams().getRemoteIp() != null) && (pageUrl.getParams().getRemotePort() != null)){
						colon=":";
						alignPort = "align_left";
					%>
						<td>Selected Remote IP:port</td>   
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
						<td class="align_left">
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
						<td>Select Group / Subnet</td>   
						<td class="align_left">
						<select id="selectGroupIndex"  name="selectGroupIndex" >
						<% 
							List<String> Groups = Configuration.getGroups();
							String [] vectSubnets = Configuration.getLocalSubnet();
							String selectGroup = pageUrl.getParams().getSelectGroupIndex();						
							if ((selectGroup != null) && (selectGroup.equals("all"))) {
							%>
								<option selected="selected" value="all">all</option>
							<% } else {%>
								<option value="all">all</option>
							<% }							
							for (String currentGroup: Groups) {  
								if ((selectGroup != null) && (selectGroup.equals(currentGroup))) {
								 %>
									<option selected="selected" value="<%=currentGroup%>"><%=currentGroup%></option>		
								<% } else {%>
									<option value="<%=currentGroup%>"><%=currentGroup%></option>	
				 	 	<%} }
				 	 		for (int i = 0; i < vectSubnets.length; i++) {  
								String subnet = vectSubnets[i]; 
								if ((selectGroup != null) && (selectGroup.equals(vectSubnets[i]))) {
								 %>
									<option selected="selected" value="<%=subnet%>"><%=subnet%></option>		
								<% } else { %>
									<option value="<%=subnet%>"><%=subnet%></option>	
				 	 	<%} }%>				 	 	
						</select>
						</td>	
					</tr>					
					<tr>						
						<td>Dynamic Update: </td> 
						<td class="align_left	">																
						<input type="checkbox" title="Click to enable/disable dynamic update" id="dynamic" name="dynamic" value="false" onclick="check(this.checked);" />						
						</td>	
						<td  colspan="2" class="align_left"><input type="submit" value="Draw Graph" id="Go" name="Go" /> </td>					
					</tr>											
				</table>   																		
				</form>																			
	            </div>   
	            <div class="more_options">				
					<a class="change" title="Conf" href="<%= response.encodeURL(request.getContextPath() +"/configure.jsp") %>">Configure</a>
					<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html" target="_blank">Help</a>	
				</div>      
            </div>
            <div style="clear:both;"></div>            	
            <div id="main">
                <!-- Include the graph (Graph parameter controls not yet functional) -->
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
 				<a class="left" title="Click here to go to the previous view" href="javascript:history.back(1);">Back</a> 	 				 				
 			    <a class="align_right" title="Click here to go to the default view keeping the time selected" href="<%=pageUrl.getIndexURL(startTime, endTime, true, false)%>">Reset</a>			
 			</div> 			
            <!-- <div id="footer"></div> -->            
        </div>    	
    </body>    	
</html>