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
	
	//Validate Parameters
	try
	{ 
	 	pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}	
	
    if (pageUrl.getParams().getAddSubnet() != null)
    	update = true;
    
    if (pageUrl.getParams().getDelSubnets() != null)
    	if (!pageUrl.getParams().getDelSubnets().isEmpty())
    		update = true;
   
    if (update) {
    	String newSubnets = pageUrl.getParams().getAddSubnet();
		goodSubnet = true;
		if (!newSubnets.equals("")) {
			Pattern p = Pattern.compile("(([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.){3}$");
	    	Matcher m = p.matcher(newSubnets);
			goodSubnet = m.find();
		}
		if (goodSubnet) {
			result = Configuration.updateConf(pageUrl.getParams().getAddSubnet(), pageUrl.getParams().getDelSubnets());
			wrongSubnet = "";
		}
		else 
			wrongSubnet = newSubnets;
	}
	
%>
    
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
   		<title>pmGraph</title>
   		<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
   		<link href="styles/main.css" media="screen" type="text/css" rel="stylesheet" />   		   		
   </head>
   <body>
     <div id="conf_container">     	
     <div id="conf_header">
     	<img id="logo" alt="Logo Banner" src="images/logo.png"/> 
     	<div id="options">	
	     	<a class="change" title="Home" href="<%= response.encodeURL(request.getContextPath() +"/index.jsp") %>">Home</a>      
	     	<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html">Help</a>
	    </div>	 
	    <%if (update) { 
    		if (result) {%>
    	  		<div id="successResult">
	    			<p>Update Done</p>
    			</div>
	    	<%} else { %>
	    		<div id="unsuccessResult">	    		
    				<%if (!goodSubnet) { %>
    					<p>Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255.</p>
    				<%}else {%>
	    				<p>The new subnet is already in the configure file</p>
    				<%}%>
    			</div>
    	    <%}
    			update = false;
    		}%>
	    <div id="conf_form">	
			<form id="config" action="">	
				<fieldset id="configuration">		
				<legend>Configuration Parameters</legend>	
				<div id="scrolltable">
             		<table id="TableLocalSubnets" class="scroll_table" width="80%" border="1" cellpadding="0" cellspacing="0">               		          
                 		<thead>
               		     	<tr>
               		          	<th>Local Subnets</th>
            	             	<th>Delete</th>
            	         	</tr>
            	    	</thead>
         		       	<tbody>
         		       		<%
            	            	String [] subnets = Configuration.getLocalSubnet();
                 
                            	for (int i = 0; i < subnets.length; i++)
                          	{
                        	%>   
                        		<tr> 
                            		<td id="localSubnet<%=(i+1)%>"> <p><%=subnets[i]%></p></td>
                               		<td> <input type="checkbox" id="delSubnet<%=(i+1)%>" name="delSubnet<%=(i+1)%>" value="delSubnet<%=(i+1)%>"/></td>
                            	</tr>   
                        	<%
                          	}   
                        	%>                        	
                		</tbody>                		
            		</table>
            		<input type="hidden" id="numSubnets"  name="numSubnets" title="numSubnets"  value="<%=subnets.length%>" size="8" />   
           		</div>
           		<div id="uoptions">
           			<p>
           				<label for="newSubnet">Add new Subnet</label>
           				<input type="text" id="newSubnet"  name="newSubnet" title="newSubnet"  value="<%=wrongSubnet%>" size="11" />	             	             	             	
           				<label for="save_conf"> </label>
          				<input type="submit" value="Save configuration" id="save_conf" name="Go" />
           			</p>
           		</div>  
				</fieldset>        
			</form>   
        	<a class="left" title="Click here to go to the previous view" href="javascript:history.back(1);">Back</a>                                                          
    	</div>     	    	        	    	
    </div>
    </div>
    </body>   
</html>											