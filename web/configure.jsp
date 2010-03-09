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
    //	methods to get new URL
    UrlBuilder pageUrl = new UrlBuilder();

	//Input Validation
	String errorMsg = null;
	String indexURL = "configure.jsp";	

	//Validate Parameters
	try
	{ 
	 	pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
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
	     	<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html" target="_blank">Help</a>
	    </div>
		<div id="conf_form">		
			<form id="config" action="updateConf.jsp">	
			<fieldset id="configuration">		
			<legend>Configuration Parameters</legend>						
				<table id="conf_table" class="layout_table">			
					<tr>  
						<td>Local Subnet</td>   
						<td>
						<input type="text" id="localSubnet"  name="localSubnet" title="Local Subnet"  value="<%=Configuration.getLocalSubnet()%>" size="8" /></td>					
					</tr>				
				</table>			
					<input type="submit" value="Save configuration" id="save_conf" name="Go" />						
			</fieldset>
			</form>					
			<a class="left" title="Click here to go to the previous view" href="javascript:history.back(1);">Back</a> 	 				 									
		</div>  
	</div>
	</body>    
</html>


										