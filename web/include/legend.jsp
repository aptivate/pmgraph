<!-- Table which reads database to produce legend corresponding to graph -->

<%@ page import="java.awt.Color" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="org.aptivate.bmotools.pmgraph.*" %>

<%
    long start = Long.parseLong(request.getParameter("start"));
    long end = Long.parseLong(request.getParameter("end"));
    
    // Round our times to the nearest minute
    start = start - (start % 60000);
    end = end - (end % 60000);
    
    // Get database connection and network properties
    Connection conn = GraphUtilities.getConnection();
    String localSubnet = GraphUtilities.getProperties();
    
    // Prepare and execute the query to find all active IPs on the network
    PreparedStatement ipStatement = 
    	   conn.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP);
    ipStatement.setString(1, localSubnet + "%");
    ipStatement.setString(2, localSubnet + "%");
    ipStatement.setString(3, localSubnet + "%");
    ipStatement.setString(4, localSubnet + "%");
    ipStatement.setString(5, localSubnet + "%");
    ipStatement.setTimestamp(6, new Timestamp(start));
    ipStatement.setTimestamp(7, new Timestamp(end));
    System.out.println(ipStatement);
    ResultSet ipResults = ipStatement.executeQuery();
    ipResults.beforeFirst();
%>

<table>
	<thead>
		<tr>
		    <th></th>
            <th rowspan="2">Host IP</th>
            <th colspan="2">Totals (MB)</th>
		</tr>
		
		<tr>
		    <th></th>
		    <th>Downloaded</th>
		    <th>Uploaded</th>
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