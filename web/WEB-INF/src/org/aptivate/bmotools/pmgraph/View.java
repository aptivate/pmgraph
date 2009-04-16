package org.aptivate.bmotools.pmgraph;

import java.util.ArrayList;
import java.util.List;

public enum View
{
	//	 Show Ips or show ports in graph
	LOCAL_IP, LOCAL_PORT, REMOTE_IP, REMOTE_PORT;
	
	
	public static List<View> getAvailableViews(RequestParams requestParams) {
		List<View> views = new ArrayList<View>();
		
		for (View view : View.values()) {  // for each view 
			// if the ip is selected local ip view doesn't have sense
			if ((view == LOCAL_IP) && (requestParams.getIp() != null)) 
				continue;
			if ((view == LOCAL_PORT) && (requestParams.getPort() != null)) 
				continue;
			
			// if the remote ip is selected remote ip view doesn't have sense
			if ((view == REMOTE_IP) && (requestParams.getRemoteIp() != null)) 
				continue;			
			if ((view == REMOTE_PORT) && (requestParams.getRemotePort() != null)) 
				continue;
			views.add(view);		// add the view to the list
		}
		return views;
    }

	public static View getNextView(RequestParams requestParams, String paramName) {
		
		for (View view : View.values()) {  // for each view 
		
			
			// if the ip is selected local ip view doesn't have sense
			if (((view == LOCAL_IP) && (requestParams.getIp() != null)) 
				|| ((view == LOCAL_IP) && ("ip".equals(paramName)))) 
				continue;
			if (((view == LOCAL_PORT) && (requestParams.getPort() != null))
				|| ((view == LOCAL_PORT) && ("port".equals(paramName))))
				continue;
			// if the remote ip is selected remote ip view doesn't have sense
			if (((view == REMOTE_IP) && (requestParams.getRemoteIp() != null))
				|| ((view == REMOTE_IP) && ("remote_ip".equals(paramName))))
				continue;			
			if (((view == REMOTE_PORT) && (requestParams.getRemotePort() != null))
				|| ((view == REMOTE_PORT) && ("remote_port".equals(paramName))))
				continue;
			return(view);		// add the view to the list
		}
		return null;
    }

}; 
