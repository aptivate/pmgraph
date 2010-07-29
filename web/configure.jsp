<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page pageEncoding="utf-8" language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="org.aptivate.bmotools.pmgraph.UrlBuilder"%>
<%@ page import="org.aptivate.bmotools.pmgraph.Configuration"%>
<%@ page import="org.aptivate.bmotools.pmgraph.PageUrlException"%>
<%@ page import="org.aptivate.bmotools.pmgraph.View" %>
<%@ page import="org.aptivate.bmotools.pmgraph.DataAccess" %>

<%
    //	methods to get new URL
    UrlBuilder pageUrl = new UrlBuilder();

	//Input Validation
	String errorMsg = null;
	String indexURL = "configure.jsp";	
	String wrongSubnet = "";
	boolean result = false;
	boolean goodSubnet = true;
	boolean update = false;
	boolean oneSubnet = false;
	DataAccess dataAccess = new DataAccess();
	long now = new java.util.Date().getTime();
	long start = now - 180 * 60000;
	long end = now;
	List<String> dataIps;
	boolean updateGroups = false;
	boolean resultGroups = false;
	List<String> Groups = Configuration.getGroups();
	
	//Validate Parameters
	try
	{ 
	 	pageUrl.setParameters(request);
	}	
	catch (PageUrlException e)
	{
		errorMsg = e.getMessage();
	}	
	
    if (pageUrl.getParams().getAddSubnet() != null)
    	update = true;
    	
    oneSubnet = false;
    if (pageUrl.getParams().getDelSubnets() != null)
    {
    	if (!pageUrl.getParams().getDelSubnets().isEmpty()) {
    		update = true;   	    
	    	if (pageUrl.getParams().getNumSubnets() == pageUrl.getParams().getDelSubnets().size())
    			oneSubnet = true;
    	}
    }
   
    if (update) 
    {
    	String newSubnets = pageUrl.getParams().getAddSubnet();
		goodSubnet = true;
		if (newSubnets != null) 
		{
			Pattern p = Pattern.compile("^(([1-9]?[0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}$");
	    	Matcher m = p.matcher(newSubnets);
			goodSubnet = m.find();
		}
		if (goodSubnet) 
		{
			if (!oneSubnet) 
			{
				result = Configuration.updateConf(pageUrl.getParams());
				wrongSubnet = "";
			}
		}
		else 
			wrongSubnet = newSubnets;
	}
	
	if ((pageUrl.getParams().getAddGroup() != null) || (pageUrl.getParams().getDelGroups() != null))
		updateGroups = true;				
    
    if (updateGroups) 
    {   		        
		resultGroups = Configuration.updateGroups(pageUrl.getParams().getAddGroup(), pageUrl.getParams().getDelGroups(), null, Groups, null);    		
		Groups = Configuration.getGroups();
	}
	
%>
    
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
   		<title>pmGraph</title>
   		<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
   		<link href="styles/main.css" media="screen" type="text/css" rel="stylesheet" />   		   		
   		<script type="text/javascript">
			function expand(group){
			    var link = document.getElementById("link." + group);
			    if (link.innerHTML == '[+]')
			    {
					link.innerHTML = '[-]';
				}
				else
				{
					link.innerHTML = '[+]';
				}
				var group = document.getElementById(group);
				if(group.style.display=='')
					group.style.display='none';
				else
					group.style.display='';
			}
		</script>
   </head>
   <body>
     <div class="container">     	
     <div class="conf_header">
     	<img id="logo" alt="Logo Banner" src="images/logo.png"/> 
     	<div class="options">	
	     	<a class="change" title="Home" href="<%= response.encodeURL(request.getContextPath() +"/index.jsp") %>">Home</a>      
	     	<a class="change" title="Help" href="http://www.aptivate.org/Projects.BMOTools.pmGraph.html">Help</a>
	    </div>	 
	    <%if (update) { 
    		if (result) {%>
    	  		<div class="successResult" id="successResult">
	    			<p>Update Done</p>
    			</div>
	    	<%} else { %>
	    		<div class="unsuccessResult" id="unsuccessResult">	    		
    				<%if (!goodSubnet) { %>
    					<p>Incorrect new subnet format. Please try again as follows: 0-255.0-255.0-255.</p>
    				<%}else {
    					if (oneSubnet) {%>
	    					<p>You can't delete all the subnets</p>
	    				<%}else {%>
	    					<p>The new subnet is already in the configure file</p>
    					<%}
    				}%>
    			</div>
    	    <%}
    			update = false;
    	}%>
    	
    	<%if (updateGroups == true) {    	
    		if (resultGroups) {%>    	
    			<div class="successResult" id="successResult">
	    			<p>Update Done</p>
    			</div>
	    	<%} else { %>
	    		<div class="unsuccessResult" id="unsuccessResult">	    		    			
	    			<p>The new group is already in the configure file</p>
    			</div>
    	    <%}
    			updateGroups = false;
    	}%>    	 
    	
	      <div class="left_float_clear conf_form">	
			<form id="config" action="">	
				<fieldset class="configuration">		
				<legend>Configuration Parameters</legend>
				<div class="left_float_clear saveConf" id="saveConf">
				    <p>
        				<label for="save_conf"> </label>
	          			<input type="submit" value="Update configuration" id="save_conf" name="Go" />         					
          			</p>
         		</div>
				<fieldset class="configurationSubnets">	
				<legend>Subnets</legend>	
				<div class="scrolltable">
             		<table id="TableLocalSubnets" class="scroll_table table" width="80%" border="1" cellpadding="0" cellspacing="0">               		          
                 		<thead>
               		     	<tr>
               		          	<th>Local Subnets</th>
            	             	<th>Delete</th>
            	         	</tr>
            	    	</thead>
         		       	<tbody>
         		       		<%
            	            	String [] subnets = Configuration.getLocalSubnet();
                 
                            	for (int i = 0; i < subnets.length; i++)
                          	{
                        	%>   
                        		<tr> 
                            		<td id="localSubnet<%=(i+1)%>"> <p><%=subnets[i]%></p></td>
                               		<td> <input type="checkbox" id="delSubnet<%=(i+1)%>" name="delSubnet<%=(i+1)%>" value="delSubnet<%=(i+1)%>"/></td>
                            	</tr>   
                        	<%
                          	}   
                        	%>                        	
                		</tbody>                		
            		</table>
            		<input type="hidden" id="numSubnets"  name="numSubnets" title="numSubnets"  value="<%=subnets.length%>" size="8" />   
           		</div>
           		<div class="left_float_clear uoptions">
           			<p>
           				<label for="newSubnet">Add new Subnet</label>
           				<input type="text" id="newSubnet"  name="newSubnet" title="newSubnet"  value="<%=wrongSubnet%>" size="11" />	             	             	             	
           			</p>
           		</div>
           		</fieldset>
           		<fieldset class="configurationGroups">	
					<legend>Groups</legend>	
					<div class="scrolltable">
    	         		<table id="TableGroups" class="table" width="80%" border="1" cellpadding="0" cellspacing="0">               		          
        	         		<thead>
            	   		     	<tr>
               			          	<th>Groups</th>
            	    	         	<th>Delete</th>
            	        	 	</tr>
	            	    	</thead>
    	     		       	<tbody>
    	     		       	<%    	     		       	
    	     		       		if (!Groups.isEmpty())
    	     		       		{    	     		       	    	     		       	
	    	     		       		dataIps = dataAccess.getThroughputAll(pageUrl.getParams(),false,Configuration.needsLongGraph(start, end));
    		     		       		int i = 1;
    		     		       		for (String currentGroup : Groups) 
    	    	 		       		{
    	     			       		%>
    	     			       			<tr>
    	     		    	   			<td class="center"> 
    	     		       				<p><a id="link.<%=currentGroup%>" href="javascript:expand('<%=currentGroup%>')">[+]</a>    	     		       	     		       			    	     		       			
    	     		       				<a href="<%= response.encodeURL(request.getContextPath() +"/include/groups.jsp?Group=" + currentGroup) %>"> <%=currentGroup%></a> </p>
    	     		       				<div id="<%=currentGroup%>" style="display:none">
    	     		      				<table id="TableIps<%=i%>" class="tableIps">               		          
    	     		      					<%
    	     		      						List<String> IpsGroup = Configuration.getIpsGroup(currentGroup);
    	     		      						if(IpsGroup.size() == 0)
    	     		      						{%>
	    	     		      							<tr>
	    	     		      								<td></td>
	    	     		      							</tr>
    	     		      						<%}
	    	     		      					for (String currentIp: IpsGroup)
    		     		      					{
    		     		      					%>
        		               					<tr> 
                		           					<td><%=currentIp%></td>
                    	   		        		</tr> 
                       			        		<%}%>    
	        								</table>
    	     		       				</div> 
    	     		       				<input type="hidden" id="Group<%=i%>"  name="Group<%=i%>" title="Group<%=i%>"  value="<%=currentGroup%>" size="8" />   	     		       		
    	     		       				</td>
										<td class="center"> <input type="checkbox" id="delGroup<%=i%>" name="delGroup<%=i%>" value="delGroup<%=i%>"/></td>
    	     		       				</tr>
    	     		       				<%
    	     		       				i++;	     			    	     		       			    	     		       			    	     		       			
    	     		       			}
    	     		       		}else
    	     		       		{
    	     		       		%>   
    	     		       			<tr><td class="empty"></td><td class="empty"></td></tr> 	          		        	     		       		     	                    		        	    	     		       	        	 		                            	
    	     		       		<%}%>   	          		        	     		       		     	                    		        	    	     		       	        	 		                            	
    	            		</tbody>                		
        	    		</table>
        	    		<input type="hidden" id="numGroups"  name="numGroups" title="numGroups"  value="<%=Groups.size()%>" size="8" />   
           			</div>
          			<div class="left_float_clear uoptions">    	       		
           				<p>
           					<label for="addGroup">Add new group</label>
         					<input type="text" id="addGroup"  name="addGroup" title="addGroup"  value="" size="11" />	             	             	             	         					
         				</p>
         			</div>        	
					</fieldset>					
					<div class="left_float_clear Back">    	     			           			
          				<p>
          				  	<a class="left" title="Click here to go to the previous view" href="javascript:history.back(1);">Back</a>                                                          
          				</p>
	   	       		</div>  				  
				</fieldset>        				
			</form>  		
    	</div>     	    	        	    	
    </div>
    </div>
    </body>   
</html>											