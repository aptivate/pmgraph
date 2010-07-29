<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="org.aptivate.bmotools.pmgraph.UrlBuilder"%>
<%@ page import="org.aptivate.bmotools.pmgraph.Configuration"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.View" %>
<%@ page import="org.aptivate.bmotools.pmgraph.DataAccess" %>
<%@ page import="org.aptivate.bmotools.pmgraph.*" %>
<%
    //	methods to get new URL
    UrlBuilder pageUrl = new UrlBuilder();

	//Input Validation
	String errorMsg = null;
	String indexURL = "configure.jsp";	
	String wrongSubnet = "";
	boolean result = false;
	boolean goodSubnet = true;
	boolean update = false;
	boolean oneSubnet = false;	
	DataAccess dataAccess = new DataAccess();
	long now = new java.util.Date().getTime();
	long start = now - 180 * 60000;
	long end = now;
	List<String> dataIps;
	boolean updateGroups = false;
	boolean resultGroup = false;
	List<String> Groups = Configuration.getGroups();
	List<String> delGroups = null;
	String group = "";	
	
	//Validate Parameters
	try
	{ 
	 	pageUrl.setParameters(request);
	 	start = pageUrl.getParams().getStartTime();
		end = pageUrl.getParams().getEndTime();
		group = pageUrl.getParams().getGroup();
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}	
				
	if (pageUrl.getParams().getDelIpGroup() != null)
    	if (!pageUrl.getParams().getDelIpGroup().isEmpty())
    		updateGroups = true;   	  
    		
    if (pageUrl.getParams().getAddIpGroup() != null)
    	if (!pageUrl.getParams().getAddIpGroup().isEmpty())
    		updateGroups = true;         
    
    if (updateGroups) 
    {   		
		resultGroup = Configuration.updateGroups(null, delGroups, pageUrl.getParams().getDelIpGroup(), Groups, pageUrl.getParams().getAddIpGroup());    		
		Groups = Configuration.getGroups();
	}		
%>
    
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
   		<title>pmGraph</title>
   		<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
   		<link href="../styles/main.css" media="screen" type="text/css" rel="stylesheet" />   		   		   		
   </head>
   <body>
     <div class="container">     	
     <div class="conf_header">
     	<img id="logo" alt="Logo Banner" src="../images/logo.png"/> 
     	<div class="options">	
	     	<a class="change" title="Home" href="<%= response.encodeURL(request.getContextPath() +"/index.jsp") %>">Home</a>      
	     	<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html">Help</a>
	    </div>	 
    	
    	<%if (updateGroups == true) {%>
	    		<div class="successResult" id="successResult">			    		
  					<p>Update Done</p>
    			</div>
		<%}updateGroups = false;%>	      
    	
	    <div class="left_float_clear conf_form">	
			<form id="config" action="" method="post">	
				<fieldset class="configuration2">		
				<legend>Configuration Group "<%=group%>"</legend>
				<div id="saveConf2">
					<p><input type="submit" value="Update configuration" id="save_conf3" name="Go" /></p>
				</div>
					<fieldset class="actConfigGroups">	
					<legend>Actual IPs</legend>	
						<div class="scrolltableGroups"> 				
    	         		<table id="IpsGroup" class="table" width="80%" border="1" cellpadding="0" cellspacing="0">               		          
        	         		<thead>
            	   		     	<tr>
               			          	<th>IPs</th>
            	    	         	<th>Delete</th>
            	        	 	</tr>
	            	    	</thead>
    	     		       	<tbody>
    	     		       	<%
    	     		       		dataIps = dataAccess.getThroughputAll(pageUrl.getParams(),false,Configuration.needsLongGraph(start, end));
    	     		       		List<String> IpsGroup = Configuration.getIpsGroup(group);
    	     		       		int i = 1;
    	     		       		if (!IpsGroup.isEmpty())
    	     		       		{    	     		       		
    	     		       			for (String currentIp: IpsGroup)
    	     		       			{
    	     		       			%>
    	     		       				<tr>
    	     		       				<td class="center"> <p> <%=currentIp%> </p> <input type="hidden" id="Ip<%=i%>"  name="Ip<%=i%>" title="Ip<%=i%>"  value="<%=currentIp%>" size="8" /></td>
    	     		       				<td class="center"> <input type="checkbox" id="delIp<%=i%>" name="delIp<%=i%>" value="delIp<%=i%>"/></td>
    	     		       				</tr>    	     		       			
    	     		       			<%
    	     		       				i++;	     			    	     		       			    	     		       			    	     		       			
    	     		       			}
    	     		       		}
    	     		       		else
    	     		       		{
    	     		       		%>   
    	     		       			<tr><td class="empty"></td><td class="empty"></td></tr> 	          		        	     		       		     	                    		        	    	     		       	        	 		                            	
    	     		       		<%}%>    	     		       		
    	            		</tbody>                		
        	    		</table>    		
           				</div>           	
           			</fieldset>
           			<fieldset class="addConfigGroups">	
					<legend>Add IPs</legend>	
						<div class="scrolltableGroups">
						<table id="nIpsGroup" class="table" width="80%" border="1" cellpadding="0" cellspacing="0">               		          
        	         		<thead>
            	   		     	<tr>
               			          	<th>IPs</th>
            	    	         	<th>Add</th>
            	        	 	</tr>
	            	    	</thead>
    	     		       	<tbody>       	     		   	         	     		       	
    	     		       	 	<%
    	     		       		List<String> nIpsGroup = Configuration.getNIpsGroup(IpsGroup, dataIps);
    	     		       		if (!nIpsGroup.isEmpty())
    	     		       		{
    	     		       			i = 1;
    	     		       			for (String currentGroup : nIpsGroup) 
    	     		       			{
    	     		       			%>
    	     		       				<tr>    	     	
    	     		       				<td class="center"> <p><%=currentGroup%></p> <input type="hidden" id="newIp<%=i%>"  name="newIp<%=i%>" title="newIp<%=i%>"  value="<%=currentGroup%>" size="8" /></td>
    	     		       				<td class="center"> <input type="checkbox" id="addIp<%=i%>" name="addIp<%=i%>" value="addIp<%=i%>"/></td>
    	     		       				</tr>        	     		       		
    	     		       			<%
    	     		       				i++;	     			    	     		       			    	     		       			    	     		       			
    	     		       			}
    	     		       		}
    	     		       		else
    	     		       		{
    	     		       		%>     	     		       		
	           		    			<tr><td class="empty"></td><td class="empty"></td></tr>
    	     		       		<%}%>	       	    	     		       	
    	            		</tbody>                		
        	    		</table>        	         		
           				</div>
					</fieldset>	
					<input type="hidden" id="numIps"  name="numIps" title="numIps"  value="<%=dataIps.size()%>" size="8" />   	
					<input type="hidden" id="Group"  name="Group" title="Group"  value="<%=group%>" size="8" />				  
				</fieldset>        
			</form>  
			<div class="left_float_clear Back2"> 			
        	<p><a class="left" title="Click here to go to the previous view" href="javascript:history.back(1);">Back</a></p>                                                          
        	</div>
    	</div>     	    	        	    	
    </div>
    </div>
    </body>   
</html>		