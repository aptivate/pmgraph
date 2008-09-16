<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page import="java.util.Date" %>

<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long startTime = (param = request.getParameter("start")) != null ? Long.parseLong(param) : now - 240 * 60000;
    long endTime = (param = request.getParameter("end")) != null ? Long.parseLong(param) : now;
    
    // URLs to resources requiring further parameters
    String indexURL = "/pmgraph/index.jsp";
    String servletURL = "/pmgraph/graphservlet";
    String legendURL = "/include/legend.jsp";
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <title>pmGraph</title>
      <link rel="Stylesheet" href="styles/main.css" type="text/css" />
    </head>
  
    <body>
        <div id="container">
            <div id="header">
                <img alt="Logo Banner" src="images/header.png" width="760" height="75" />
            </div>
            
            <div id="main">
                <!-- Graph parameter controls not yet functional -->
                <!-- <jsp:include page="/include/params.jsp" /> -->
                
                <div id="graph">
                    <img id="graphimage" alt="Bandwith Graph" 
                            src="<%=servletURL +
                                    "?graph=" + graph +
                                    "&start=" + startTime +
                                    "&end=" + endTime +
                                    "&width=760" +
                                    "&height=350"%>" width="760" height="350" />     
                </div>
                
                <!-- Move back/forward or zoom in/out by 2 hours-->
                <div id="controls">
                    <a href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime - 120 * 60000) +
                                "&end=" + (endTime - 120 * 60000)%>" class="control">Prev.</a>
                    <div id="controlscenter">
                        <a href="<%=indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime - 60 * 60000) +
                                    "&end=" + (endTime + 60 * 60000)%>" class="control">Zoom -</a>
                        <a href="<%=indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime + 60 * 60000) +
                                    "&end=" + (endTime - 60 * 60000)%>" class="control">Zoom +</a>
                    </div>
                    <a href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime + 120 * 60000) +
                                "&end=" + (endTime + 120 * 60000)%>" class="control">Next</a>
                </div>    
    
                <div id="legend">
                    <jsp:include page="<%=legendURL + "?start=" + startTime + "&end=" + endTime%>" />
                </div>
            </div>
            
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>