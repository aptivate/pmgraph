<!-- Table which reads database to produce legend corresponding to graph -->

<%@ page import="java.awt.Color" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="org.aptivate.bmotools.pmgraph.*" %>

<%
    long startP = Long.parseLong(request.getParameter("start"));
    long endP = Long.parseLong(request.getParameter("end"));
    
    // Round our times to the nearest minute
    long start = startP - (startP % 60000);
    long end = endP - (endP % 60000);
    

	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");
	String orderN = order.equals("DESC") ? "ASC" : "DESC";

    String indexURL = "/pmgraph/index.jsp";
    
    // Get database connection and network properties
    Connection conn = GraphUtilities.getConnection();
    String localSubnet = GraphUtilities.getProperties();
    
    // Prepare and execute the query to find all active IPs on the network
    String THROUGHPUT_PER_IP = GraphUtilities.THROUGHPUT_PER_IP;
    String tmp = " ORDER BY " + sortBy + " " + order +";";
    int lastC = THROUGHPUT_PER_IP.indexOf(";");
    THROUGHPUT_PER_IP = THROUGHPUT_PER_IP.substring(0, lastC);
    THROUGHPUT_PER_IP = THROUGHPUT_PER_IP + tmp;    
    
    PreparedStatement ipStatement = 
    	   conn.prepareStatement(THROUGHPUT_PER_IP);
    ipStatement.setString(1, localSubnet + "%");
    ipStatement.setString(2, localSubnet + "%");
    ipStatement.setString(3, localSubnet + "%");
    ipStatement.setString(4, localSubnet + "%");
    ipStatement.setString(5, localSubnet + "%");
    ipStatement.setString(6, localSubnet + "%");
    ipStatement.setString(7, localSubnet + "%");
    ipStatement.setTimestamp(8, new Timestamp(start));
    ipStatement.setTimestamp(9, new Timestamp(end));
    ResultSet ipResults = ipStatement.executeQuery();
%>

<table>
	<thead>
		<tr>
		    <th></th>
            <th rowspan="2">Host IP</th>
            <th colspan="2">
             <a name="bytes_total" 
                       href="<%=indexURL +
                                    "?start=" + startP +
                                    "&end=" + endP +
                                    "&sortBy=" + "bytes_total" +
                                    "&order=" + orderN%>"> Totals (MB)</a> 
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
                                    "&order=" + orderN%>">Downloaded</a>
		    </th>
		    <th>
		     <a name="uploaded" 
                       href="<%=indexURL +
                                    "?start=" + startP +
                                    "&end=" + endP +
                                    "&sortBy=" + "uploaded" +
                                    "&order=" + orderN%>">Uploaded</a>
		    </th>
		</tr>
	</thead>
	<%
    while(ipResults.next()) {
    	String ip = ipResults.getString("local_ip");
    	byte[] ipBytes = ip.getBytes();
        MessageDigest algorithm = MessageDigest.getInstance("SHA1");
        algorithm.reset();
        algorithm.update(ipBytes);
        byte sha1[] = algorithm.digest();
        Color c = new Color(sha1[0] & 0xFF, sha1[1] & 0xFF, sha1[2] & 0xFF);
        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
    %>
    <tr class="row<%=ipResults.getRow() % 2%>">
        <td style="background-color: <%=fillColour%>; width: 5px"> </td>
        <td><%=ip%></td>
        <td class="numval"><%=(ipResults.getLong("downloaded") / 1048576)%></td>
        <td class="numval"><%=(ipResults.getLong("uploaded") / 1048576)%></td>
    </tr>
    <%
    }
	%>
</table>