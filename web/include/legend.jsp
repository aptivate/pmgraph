<!-- Table which reads database to produce legend corresponding to graph -->

<%@ page import="java.awt.Color"%>
<%@ page import="java.security.MessageDigest"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="org.aptivate.bmotools.pmgraph.*"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.net.UnknownHostException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.View"%>
<%@ page import="org.aptivate.bmotools.pmgraph.ConfigurationException"%>
<%@ page pageEncoding="utf-8" language="java"
	contentType="text/html; charset=utf-8"%>
<%
	List<DataPoint> results = new ArrayList<DataPoint>();
	
	ArrayList<ArrayList<LegendTableEntry>> headers = new ArrayList<ArrayList<LegendTableEntry>>();
	ArrayList<ArrayList<LegendTableEntry>> rows = new ArrayList<ArrayList<LegendTableEntry>>();
    
    //sortBy = downloaded | uploaded |total_byties
	String sortBy = request.getParameter("sortBy");
	//order: DESC | ASC
	String order = request.getParameter("order");
    
	 //methods to get new URL
    UrlBuilder pageUrl = new UrlBuilder();

    // Input Validation
    String errorMsg = null, configError= null;   

    // Validate Parameters
	try
	{ 
		pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getLocalizedMessage();
	}	
	
	//time in seconds
	long bitsConversion = 8;
	long kbitsConversion = 1024;
	RequestParams param = pageUrl.getParams();
    long time = (param.getRoundedEndTime()-param.getRoundedStartTime())/1000;
	
	try {
		LegendData legendData = new LegendData();
		results = legendData.getLegendData(sortBy, order,pageUrl.getParams());
	} catch (	ConfigurationException e)
	{
		configError = e.getLocalizedMessage();
		if (e.getCause() != null)
	configError += "<p>" +e.getCause().getLocalizedMessage() + "</p>";
	}	
	
	String arrow = "ASC".equals(pageUrl.getParams().getOrder())?" &#8679;":" &#8681;";
	String col1 = "Down";
	String col2 = "Up";
	String col3 = "Totals (MB)";
	
	if("downloaded".equals(pageUrl.getParams().getSortBy()))
		col1 = col1 + arrow;
	if("uploaded".equals(pageUrl.getParams().getSortBy()))
		col2 = col2 + arrow;	
	if ("bytes_total".equals(pageUrl.getParams().getSortBy()))
		col3 = col3 + arrow;
%>
<%@page import="java.util.ArrayList"%>
<table id="legend_tbl">
	<%
	LegendTable table = View.getLegendTable(pageUrl, results);
	headers = table.getHeaders(); 
	rows = table.getRows();
	for (ArrayList<LegendTableEntry> row: headers)
	{
	%>
	<tr class="legend_th">
	<%
		for (LegendTableEntry column: row)
		{
		%><th<%			
			if (column.isDoubleColSpan())
			{%> 
				colspan="2"
			<%}
			if (column.isDoubleRowSpan()) 
			{%> 
				rowspan="2" 
			<%}	%>>
			<%
			if (column.getLink()!=null)
			{%> 
			<a href="<%=column.getLink()%>" name="<%=column.getName()%>"><%=column.getValue()%></a>
			<%} else
			{
				%> <%=column.getValue()%> <%
			 } %>
			 </th>
		<%}	%>
	</tr>
	<%} 
	int i = 0;
	for (ArrayList<LegendTableEntry> row: rows)
	{%>
		<tr class="row<%=i % 2%>">
		<td style="background-color: <%=row.get(0).getValue()%>; width: 5px;"></td>
		<%
		int j = 0; 
		for (LegendTableEntry column: row)
		{
			if(column != row.get(0))
			{ %>
				<td <%if (j>2)
				{%> 
					class="numval"
				<%}
					if (column.isDoubleColSpan())
					{%> 
					colspan="2"
					<%}
					if (column.isDoubleRowSpan()) 
					{%> 
						rowspan="2" 
					<%} %>>
					<%if (column.getLink()!=null)
					{%> 
						<a href="<%=column.getLink()%>" name="<%=column.getName() %>"><%=column.getValue()%></a>
					<% } else
					{
					%> <%=column.getValue()%> <%
					} %>
					</td>
				<%
				}
				j++;
			}
		%>
		</tr>
		<%
			i++;
		}
	%>
</table>
<%
if (pageUrl.getParams().getView() == View.REMOTE_PORT) {
%>
<a href="javascript:window.open('port_assignment.html');void(0);">Well
known ports list</a>
<%
}
%>
<%
if (configError != null) {
%>
<div class="error_panel"><%=configError%></div>
<%
}
%>
