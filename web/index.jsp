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
    
    long scrollAmount = (endTime - startTime) / 3;
    long zoomAmount = (endTime - startTime) / 4;
    
    // URLs to resources requiring further parameters
    String indexURL = "/pmgraph/index.jsp";
    String servletURL = "/pmgraph/graphservlet";
    String legendURL = "/include/legend.jsp";
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <script type="text/javascript">
			var dragging = false, drag_start_x = 0, drag_start_y = 0;
			function getevent(e)
			{
				if (!e)
				{
					e = window.event;
				}

				return e;
			}
			function x(e)
			{
				return e.pageX; // event.x;
			}
			function y(e)
			{
				return e.pageY; // event.y;
			}
			function target(e)
			{
				if (e.target)
				{
				  targ = e.target;
				}
				else if (e.srcElement)
				{
				  targ = e.srcElement;
				}
				if (targ.nodeType == 3) // defeat Safari bug
				{
				  targ = targ.parentNode;
				}
				return targ;
			}
			function mousedown(e)
			{
				e = getevent(e);
				dragging = true;
				drag_start_x = x(e);
				drag_start_y = y(e);
				return false; // stop drag
			}
			function mousemove(e)
			{
				e = getevent(e);

				if (dragging)
				{
					target(e).style.position = "relative";
					target(e).style.left = x(e) - drag_start_x;
				}
			}
			function mouseup(e)
			{
				e = getevent(e);
				if (!dragging) return true;
				var off_x = x(e) - drag_start_x;
				var off_y = y(e) - drag_start_y;
				//alert(off_x + "," + off_y);
				dragging = false;
				return true;
			}
        </script>
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
                                    "&height=350"%>"
                            width="760" height="350"
                            onmousedown="return mousedown(event);"
                            onmousemove="return mousemove(event);"
                            onmouseup="return mouseup(event);"
                            />
                </div>
                
                <!-- Move back/forward or zoom in/out by 2 hours-->
                <div id="controls">
                    <a name="prev"
                       href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime - scrollAmount) +
                                "&end=" + (endTime - scrollAmount)%>" class="control">Prev.(2h)</a>
                    <div id="controlscenter">
                        <a name="zoomOut"
                           href="<%=indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime - zoomAmount) +
                                    "&end=" + (endTime + zoomAmount)%>" class="control">Zoom -</a>
                        <a name="zoomIn"
                           href="<%=indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime + zoomAmount) +
                                    "&end=" + (endTime - zoomAmount)%>" class="control">Zoom +</a>
                    </div>
                    <a name="next" 
                       href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime + scrollAmount) +
                                "&end=" + (endTime + scrollAmount)%>" class="control">Next (2h)</a>
                </div>    
    
                <div id="legend">
                    <jsp:include page="<%=legendURL + "?start=" + startTime + "&end=" + endTime%>" />
                </div>
            </div>
            
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>