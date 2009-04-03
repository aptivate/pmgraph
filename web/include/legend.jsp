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
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrl.View" %>
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%
    long startP = Long.parseLong(request.getParameter("start"));
    long endP = Long.parseLong(request.getParameter("end"));
    StringBuffer othersHostName = new StringBuffer("");
    StringBuffer othersIp = new StringBuffer("");
    long othersDownloaded = 0;
	long othersUploaded = 0;
	String othersFillColour="";
	List<GraphData> results ;
    
    // Round our times to the nearest minute
    long start = startP - (startP % 60000);
    long end = endP - (endP % 60000);
    
    //sortBy = downloaded | uploaded |total_byties
	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");

	String arrow = order.equals("ASC")?" &#8679;":" &#8681;";
	String col1 = "Downloaded";
	String col2 = "Uploaded";
	String col3 = "Totals (MB)";
	
	if("downloaded".equals(sortBy))
		col1 = col1 + arrow;
	if("uploaded".equals(sortBy))
		col2 = col2 + arrow;	
	if ("bytes_total".equals(sortBy))
		col3 = col3 + arrow;
    //methods to get new URL
    PageUrl pageUrl = new PageUrl();
	try
	{ 
		 pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		e.printStackTrace();
	}		
	
	LegendData legendData = new LegendData();

	// Legend for a especific IP it's a port graph
	if (pageUrl.getIp() != null) {
		results = legendData.getLegendDataOneIp(start, end, sortBy, order,pageUrl);		
	} else {
		if (pageUrl.getPort() != null) {
			results = legendData.getLegendDataOnePort(start, end, sortBy, order,pageUrl);				
		} else {
			if (pageUrl.getView() == View.PORT) {
				results = legendData.getLegendDataPerPort(start, end, sortBy, order,pageUrl);		
			} else  {		// Vista de  IPs vista por defecto
				results = legendData.getLegendData(start, end, sortBy, order,pageUrl);
			}
		}
	}
		    
%>
<table id="legend_tbl">
		<tr class="legend_th">
		    <td></td>
		    <%if ((pageUrl.getIp() == null) && (pageUrl.getView() != View.PORT)) { %>
	            <td rowspan="2">Host IP</td>
	            <td rowspan="2">Host Name</td>
            <%} else { %>
                <td rowspan="2">Port</td>
            <%} %>
            <td colspan="2" class="center">
             <a name="bytes_total" 
                       href="<%=pageUrl.getIndexURL(startP, endP, "bytes_total")%>"
                       > <%=col3%></a> 
           </td>
		</tr>
		
		<tr class="legend_th">
		    <td></td>
		    <td>
		    <a name="downloaded" 
                       href="<%=pageUrl.getIndexURL(startP, endP, "downloaded")%>"
                       ><%=col1%></a>
		    </td>
		    <td>
		     <a name="uploaded" 
                       href="<%=pageUrl.getIndexURL(startP, endP, "uploaded")%>"
                       ><%=col2%></a>
		    </td>
		</tr>
	<%
			int i= 0;
			GraphFactory graphFactory = new GraphFactory();
			
			if ((pageUrl.getIp() != null) || (pageUrl.getView() == View.PORT)) {
			  for (GraphData result : results) 
			    {
			    	Integer port  = result.getPort();	    	
			        Color c = graphFactory.getSeriesColor(port);
			        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
		%>				    <tr class="row<%=i % 2%>">
					        <td style="background-color: <%=fillColour%>; width: 5px;"></td>
							<%   if (GraphFactory.OTHER_PORT == port) {
								%><td>Others</td><%
							} else {
								if (!pageUrl.isEspecificPortIpQuery()) {
									%><td><a href="<%=pageUrl.getUrlOnePortGraph(start, end, port)%>" ><%=port%></a></td><%
								} else {
									%><td><%=port%></td><%
								}
							}
							%>
					        <td class="numval"><%=(result.getDownloaded() / 1048576)%></td>
					        <td class="numval"><%=(result.getUploaded() / 1048576)%></td>
					    </tr>
				   <%
		    		i++;
				}			  		   
			} 
		  else {
			  for (GraphData result : results) 
			    {
			    	String ip = result.getLocalIp();	    	
			        Color c = graphFactory.getSeriesColor(ip);
			        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
					HostResolver hostResolver = new HostResolver();
			        String hostName = hostResolver.getHostname(ip);		
			        if (GraphFactory.OTHER_IP.equalsIgnoreCase(ip))
			        	ip = "Others";
		%>				    <tr class="row<%=i % 2%>">
					        <td style="background-color: <%=fillColour%>; width: 5px;"></td>
					       <%
					        if ((!pageUrl.isEspecificPortIpQuery()) && (!"Others".equalsIgnoreCase(ip))) {
									%><td><a href="<%=pageUrl.getUrlOneIpGraph(start, end, ip)%>" ><%=ip%></a></td><%
								} else {
									%><td><%=ip%></td><%
							}%>					        
					        <td><%=hostName%></td>        					
					        <td class="numval"><%=(result.getDownloaded() / 1048576)%></td>
					        <td class="numval"><%=(result.getUploaded() / 1048576)%></td>
					    </tr>
				   <%
		    		i++;
				}			  
		  }
			%>
</table>