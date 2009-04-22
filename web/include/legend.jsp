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
<%@ page import="org.aptivate.bmotools.pmgraph.View" %>
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%
    StringBuffer othersHostName = new StringBuffer("");
    StringBuffer othersIp = new StringBuffer("");
    long othersDownloaded = 0;
	long othersUploaded = 0;
	String othersFillColour="";
	List<GraphData> results ;
    
    //sortBy = downloaded | uploaded |total_byties
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
	
	LegendData legendData = new LegendData();
	results = legendData.getLegendData(sortBy, order,pageUrl.getParams());
%>
<table id="legend_tbl">
		<tr class="legend_th">
		    <td></td>
		    <%switch (pageUrl.getParams().getView()) {		
				case LOCAL_PORT:
				case REMOTE_PORT:		%>
	                <td rowspan="2">Port</td>	            
            <%break;
	            default:
				case LOCAL_IP:	
				case REMOTE_IP:	%>
	            <td rowspan="2">Host IP</td>
	            <td rowspan="2">Host Name</td>
            <%} %>
            <td colspan="2" class="center">
             <a name="bytes_total" 
                       href="<%=pageUrl.getIndexURL("bytes_total")%>"
                       > <%=col3%></a> 
           </td>
		</tr>
		
		<tr class="legend_th">
		    <td></td>
		    <td>
		    <a name="downloaded" 
                       href="<%=pageUrl.getIndexURL("downloaded")%>"
                       ><%=col1%></a>
		    </td>
		    <td>
		     <a name="uploaded" 
                       href="<%=pageUrl.getIndexURL("uploaded")%>"
                       ><%=col2%></a>
		    </td>
		</tr>
	<%
		int i= 0;
		GraphFactory graphFactory = new GraphFactory();
		
		switch (pageUrl.getParams().getView()) {
		
			case LOCAL_PORT:
			case REMOTE_PORT:		
			  for (GraphData result : results) 
			    {
					Integer port;
				  	if (pageUrl.getParams().getView() == View.REMOTE_PORT) 				  		
				  		port = result.getRemotePort();
				  	else
					  	port = result.getPort();
			        Color c = graphFactory.getSeriesColor(port);
			        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
		%>				    <tr class="row<%=i % 2%>">
					        <td style="background-color: <%=fillColour%>; width: 5px;"></td>
							<%   if (GraphFactory.OTHER_PORT == port) {
								%><td>Others</td><%
							} else {
								if (pageUrl.getParams().getView() == View.REMOTE_PORT) {
									%><td><a href="<%=pageUrl.getUrlGraph(port, "remote_port")%>" ><%=port%></a></td><%
								} else {
									%><td><a href="<%=pageUrl.getUrlGraph(port, "port")%>" ><%=port%></a></td><%
								}
							}
							%>
					        <td class="numval"><%=(result.getDownloaded() / 1048576)%></td>
					        <td class="numval"><%=(result.getUploaded() / 1048576)%></td>
					    </tr>
				   <%
		    		i++;
				}			  		   
			break;
			default:
			case LOCAL_IP:	
			case REMOTE_IP:				
			  for (GraphData result : results) 
			    {
					String ip;
				  	if (pageUrl.getParams().getView() == View.REMOTE_IP)
				    	ip = result.getRemoteIp();
				  	else
				  		ip = result.getLocalIp();	    	
			        Color c = graphFactory.getSeriesColor(ip);
			        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
					HostResolver hostResolver = new HostResolver();
			        String hostName = hostResolver.getHostname(ip);		
			        if (GraphFactory.OTHER_IP.equalsIgnoreCase(ip))
			        	ip = "Others";
		%>				    <tr class="row<%=i % 2%>">
					        <td style="background-color: <%=fillColour%>; width: 5px;"></td>
					       <%
					        if ((!"Others".equalsIgnoreCase(ip))) {
					        	if (pageUrl.getParams().getView() == View.REMOTE_IP) {
									%><td><a href="<%=pageUrl.getUrlGraph(ip, "remote_ip")%>" ><%=ip%></a></td><%
					        	} else {
									%><td><a href="<%=pageUrl.getUrlGraph(ip, "ip")%>" ><%=ip%></a></td><%
					        	}
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
			  break;
		  }
			%>
</table>