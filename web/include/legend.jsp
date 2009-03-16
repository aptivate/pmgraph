<!-- Table which reads database to produce legend corresponding to graph -->

<%@ page import="java.awt.Color" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="org.aptivate.bmotools.pmgraph.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.InetAddress"  %>
<%@ page import="java.net.UnknownHostException" %>
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%
    long startP = Long.parseLong(request.getParameter("start"));
    long endP = Long.parseLong(request.getParameter("end"));
    
    // Round our times to the nearest minute
    long start = startP - (startP % 60000);
    long end = endP - (endP % 60000);
    
    //sortBy = downloaded | uploaded |total_byties
	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");
	String orderN = order.equals("DESC") ? "ASC" : "DESC";

	String arrow = order.equals("DESC")?" &#8681":" &#8679";
	String col1 = "Downloaded";
	String col2 = "Uploaded";
	String col3 = "Totals (MB)";
	if(sortBy.equals("downloaded"))
	col1 = col1 + arrow;
	if(sortBy.equals("uploaded"))
	col2 = col2 + arrow;	
	if(sortBy.equals("bytes_total"))
	col3 = col3 + arrow;
	

    String indexURL = "/pmgraph/index.jsp";
    DataAccess dataAccess = new DataAccess();
	List<GraphData> ipResults = dataAccess.getThroughputPerIP(start, end, sortBy, order);
	
%>

<table>
	<thead>
		<tr>
		    <th></th>
            <th rowspan="2">Host IP</th>
            <th rowspan="2">Host Name</th>
            <th colspan="2">
             <a name="bytes_total" 
                       href="<%=indexURL +
                                    "?start=" + startP +
                                    "&end=" + endP +
                                    "&sortBy=" + "bytes_total" +
                                    "&order=" + orderN%>"> <%=col3%></a> 
           </th>
		</tr>
		
		<tr>
		    <th></th>
		    <th>
		    <a name="downloaded" 
                       href="<%=indexURL +
                                    "?start=" + startP +
                                    "&end=" + endP +
                                    "&sortBy=" + "downloaded" +
                                    "&order=" + orderN%>"><%=col1%></a>
		    </th>
		    <th>
		     <a name="uploaded" 
                       href="<%=indexURL +
                                    "?start=" + startP +
                                    "&end=" + endP +
                                    "&sortBy=" + "uploaded" +
                                    "&order=" + orderN%>"><%=col2%></a>
		    </th>
		</tr>
	</thead>
	<%
			int i= 0;
			GraphFactory graphFactory = new GraphFactory();
		    for (GraphData ipResult : ipResults) 
		    {
		    	String ip = ipResult.getLocalIp();	    	
		        Color c = graphFactory.getSeriesColor(ip);
		        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
		HostResolver hostResolver = new HostResolver();
		        String hostName = hostResolver.getHostname(ip);
	%>
		    <tr class="row<%=i % 2%>">
		        <td style="background-color: <%=fillColour%>; width: 5px;"></td>
		        <td><%=ip%></td>
		        <td><%=hostName%></td>        
		
		        <td class="numval"><%=(ipResult.getDownloaded() / 1048576)%></td>
		        <td class="numval"><%=(ipResult.getUploaded() / 1048576)%></td>
		    </tr>
	    	<%
		    i++;
	    }
	%>
</table>