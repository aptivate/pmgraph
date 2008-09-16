<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%
    String report = request.getParameter("report");
    int scale = Integer.parseInt(request.getParameter("scale"));
    int period = Integer.parseInt(request.getParameter("period")) * scale;
    
    String servletURL =
        "/pmgraph/graphservlet?"+
                "graph="+report+
                "&period="+period+
                "&width=760&height=350&max=16"; //Need to accommodate for different params
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
                <div id="params">
                    <form id="graphparams" action="/pmgraph/prototype.jsp" method="get">
                        <p>
                        <label for="report">Report type:</label>
                        <select id="report" name="report">
                            <option value="toplocalhosts">Top local hosts</option>
                            <option value="topremotehosts">Top remote hosts</option>
                            <option value="toplocalports">Top local ports</option>
                            <option value="topremoteports">Top remote ports</option>
                        </select>
                        
                        <label for="period">From</label>
                        <input  id="period" type="text" name="period" size="3" value="0" />
                        
                        <select name="scale">  <!-- check fitting of 'minute' option -->
                            <option value="1">minutes</option>
                            <option value="60">hours</option>
                            <option value="1440">days</option>
                        </select>
                        
                        ago to now
                        
                        <input type="submit" id="go" value="Update" />
                        </p>
                        
                        <p class="radios">
                        <label for="cumul">Cumulative</label>
                        <input id="cumul" type="radio" name="style" value="cumul" checked="checked" onclick="javascript:alert('Make this update the graph')" />
                        <label for="line">Line</label>
                        <input id="line" type="radio" name="style" value="line" onclick="javascript:alert('Make this update the graph')" />
                        <label for="pie">Pie</label>
                        <input id="pie" type="radio" name="style" value="pie" onclick="javascript:alert('Make this update the graph')" />
                        </p>
                    </form>
                </div>
                
                <div id="graph">
                    <img alt="Bandwith Graph" src="<%=servletURL%>" width="760" height="350" />
                </div>
                
                <div id="controls">
                    <input type="button" name="prev" value="Prev. x" />

                    <div id="controlscenter">
                        <input type="button" name="out" value="Zoom -" />
                        <input type="button" name="in" value="Zoom +" />
                    </div>
                    
                    <input type="button" name="next" value="Next x" />
                </div>
                
                <div id="legend">
                    <table>
                        <thead>
                            <tr>
                                <th>Host IP</th>
                                <th>Down</th>
                                <th>Up</th>
                                <th>Top ports</th>
                                <th>Up kB/s</th>
                                <th>Down kB/s</th>
                            </tr>
                        </thead>
                        <tr class="evenrow">
                            <td><a href="anotherquery">192.168.1.5</a></td>
                            <td>200</td>
                            <td>50</td>
                            <td><a href="anotherquery">80</a>, <a href="anotherquery">25</a></td>
                            <td>6</td>
                            <td>3</td>
                        </tr>
                        <tr class="oddrow">
                            <td><a href="anotherquery">192.168.1.6</a></td>
                            <td>598</td>
                            <td>13</td>
                            <td><a href="anotherquery">80</a>, <a href="anotherquery">25</a></td>
                            <td>23</td>
                            <td>3</td>
                        </tr>
                        <tr class="evenrow">
                            <td><a href="anotherquery">192.168.1.7</a></td>
                            <td>600</td>
                            <td>43</td>
                            <td><a href="anotherquery">80</a>, <a href="anotherquery">25</a></td>
                            <td>15</td>
                            <td>9</td>
                        </tr>
                    </table>
                </div> 
            </div>
            
            <div id="footer">
                <p>TODO: Complete legend w/ database read and colour coding</p>
                <p>TODO: Make graph params match the graph currently showing</p>
            </div>
        </div>
    </body>
</html>