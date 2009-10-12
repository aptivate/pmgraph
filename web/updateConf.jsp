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
	String result = null;

	//Validate Parameters
	try
	{ 
	 	pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}	
	
	//	update configuration files
	result = Configuration.updateConf(pageUrl.getParams().getLocalSubnet());
	
        
%>
    
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
   		<title>pmGraph</title>
   		<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
   		<link href="styles/main.css" media="screen" type="text/css" rel="stylesheet" />
   		   		
   </head>
   <body>
     
     <div id="container">     	
     <div id="conf_header">
     	<img id="logo" alt="Logo Banner" src="images/logo.png"/>  
     	<div id="options">	
     		<a class="change" title="Conf" href="<%= response.encodeURL(request.getContextPath() +"/configure.jsp") %>">Configure</a>
	     	<a class="change" title="Home" href="<%= response.encodeURL(request.getContextPath() +"/index.jsp") %>">Home</a>      
	     	<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html" target="_blank">Help</a>
	    </div>
	 
     
	<div id="result">		
		<% if(result == "true"){%>
		
		<p> Update Done </p>
	
		
		<% } 
		else {%>	
		
		<form id=max_band" action="configure.jsp">
			<fieldset>								
			<p> Update Failed </p>
			<input type="submit" value="Retry" id="Done" name="Done" />
			</fieldset>
		</form>	
		
		
		<% } %>			
					
	</div>
	</div>
	</body>    
</html>


										