package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to determine which view should be used for the display. The title and column details
 * are populated accordingly
 * 
 * @author noeg
 *
 */
public enum View {
	// Show Ips or show ports in graph
	LOCAL_IP, LOCAL_PORT, REMOTE_IP, REMOTE_PORT;

	/**
	 * Used to return a list of available views
	 * 
	 * @param requestParams Parameters from the request
	 * @return List of the available views
	 */
	public static List<View> getAvailableViews(RequestParams requestParams)
	{
		List<View> views = new ArrayList<View>();

		for (View view : View.values())
		{ // for each view
			// if the ip is selected, local ip view is not relevant
			if ((view == LOCAL_IP) && (requestParams.getIp() != null))
				continue;
			if ((view == LOCAL_PORT) && (requestParams.getPort() != null))
				continue;
			// if the remote ip is selected, remote ip view doesn't make sense
			if ((view == REMOTE_IP) && (requestParams.getRemoteIp() != null))
				continue;
			if ((view == REMOTE_PORT) && (requestParams.getRemotePort() != null))
				continue;
			views.add(view); // add the view to the list
		}
		return views;
	}
	
	/**
	 * Used to create the URL to follow if you click one of the ports or IPs shown in the legend
	 * 
	 * @param requestParams Parameters from the request
	 * @param paramName port or ip/ remote or local
	 * @return Next selected view
	 */
	public static View getNextView(RequestParams requestParams, String paramName)
	{

		for (View view : View.values())
		{ // for each view

			// if the ip is selected, local ip view doesn't make sense
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
			return (view); // add the view to the list
		}
		return null;
	}
	
	/**
	 * Used to fill the legend.
	 * 
	 * @param pageUrl The requested URL
	 * @param legendData The data set to be displayed within the legend
	 * @return legend table ready for display
	 */
	public static LegendTable getLegendTable(UrlBuilder pageUrl, List<DataPoint> legendData)
			throws IOException
	{

		RequestParams requestParams = pageUrl.getParams();

		LegendTable legend = new LegendTable();

		String arrow = "ASC".equals(pageUrl.getParams().getOrder()) ? " &#8679;" : " &#8681;";
		String col1 = "Down";
		String col2 = "Up";
		String col3 = "Totals (MB)";

		if ("downloaded".equals(pageUrl.getParams().getSortBy()))
			col1 = col1 + arrow;
		if ("uploaded".equals(pageUrl.getParams().getSortBy()))
			col2 = col2 + arrow;
		if ("bytes_total".equals(pageUrl.getParams().getSortBy()))
			col3 = col3 + arrow;

		// time in seconds
		RequestParams param = pageUrl.getParams();
		long time = (param.getRoundedEndTime() - param.getRoundedStartTime()) / 1000;
		ArrayList<ArrayList<LegendElement>> headers = legend.getHeaders();
		ArrayList<ArrayList<LegendElement>> rows = legend.getRows();

		//fill the header 
		ArrayList<LegendElement> firstRowHeader = new ArrayList<LegendElement>();
		ArrayList<LegendElement> secondRowHeader = new ArrayList<LegendElement>();

		// an empty field in the header for the colour column
		firstRowHeader.add(0, new LegendElement("", null, null, false, true));

		switch (requestParams.getView())
		{
		case LOCAL_PORT:
			firstRowHeader.add(new LegendElement("Local Port", null, null, false, true));
			firstRowHeader.add(new LegendElement("Protocol", null, null, false, true));
			firstRowHeader.add(new LegendElement("Service", null, null, false, true));

			break;
		case REMOTE_PORT:
			firstRowHeader.add(new LegendElement("Remote Port", null, null, false, true));
			firstRowHeader.add(new LegendElement("Protocol", null, null, false, true));
			firstRowHeader.add(new LegendElement("Service", null, null, false, true));

			break;
		case REMOTE_IP:
			firstRowHeader.add(new LegendElement("Remote Port", null, null, false, true));
			firstRowHeader.add(new LegendElement("Host Name", null, null, false, true));

			break;
		default:
		case LOCAL_IP:
			firstRowHeader.add(new LegendElement("Local IP", null, null, false, true));
			firstRowHeader.add(new LegendElement("Host Name", null, null, false, true));

			break;
		}

		firstRowHeader.add(new LegendElement(col3, pageUrl.getIndexURL("bytes_total"),
				"bytes_total", true, false));
		firstRowHeader.add(new LegendElement("Average (kb/s)", null, null, true, false));

		secondRowHeader.add(new LegendElement(col1, pageUrl.getIndexURL("downloaded"),
				"downloaded"));
		secondRowHeader
				.add(new LegendElement(col2, pageUrl.getIndexURL("uploaded"), "uploaded"));
		secondRowHeader.add(new LegendElement("Down"));
		secondRowHeader.add(new LegendElement("Up"));

		headers.add(firstRowHeader);
		headers.add(secondRowHeader);

		//fill the legend
		
		int i = 0;

		switch (requestParams.getView())
		{

		case LOCAL_PORT:
		case REMOTE_PORT:
			for (DataPoint result : legendData)
			{
				PortDataPoint legendPoint = (PortDataPoint) result;
				ArrayList<LegendElement> entry = new ArrayList<LegendElement>();
				String portName = Port2Services.getInstance().getService(legendPoint.getPort(),
						legendPoint.getProtocol());
				//add color to the row
				entry.add(new LegendElement(legendPoint.getColorAsHexadecimal()));
				if (legendPoint.getProtocol() == Protocol.icmp)
				{
					entry.add(new LegendElement("n/a"));
				} else
				{
					//number of port and link if it is possible 
					entry.add(new LegendElement(legendPoint.getId(), 
							pageUrl.getLinkUrl(legendPoint.getId()), null));
				}
				// add protocol
				if (legendPoint.getProtocol() != null)
					entry.add(new LegendElement(legendPoint.getProtocol().toString()));
				else
					entry.add(new LegendElement(""));

				entry.add(new LegendElement(portName));
				entry.add(new LegendElement(getTotalThroughput(result.getDownloaded())));
				entry.add(new LegendElement(getTotalThroughput(result.getUploaded())));
				entry.add(new LegendElement(getAverage(result.getDownloaded(), time)));
				entry.add(new LegendElement(getAverage(result.getUploaded(), time)));
				i++;
				rows.add(entry);
			}
			break;
		default:
		case LOCAL_IP:
		case REMOTE_IP:
			for (DataPoint result : legendData)
			{
				IpDataPoint legendPoint = (IpDataPoint) result;
				ArrayList<LegendElement> entry = new ArrayList<LegendElement>();

				entry.add(new LegendElement(legendPoint.getColorAsHexadecimal()));

				entry.add(new LegendElement(legendPoint.getId(),
						pageUrl.getLinkUrl(legendPoint.getId()), null));

				HostResolver hostResolver = new HostResolver();
				String hostName = hostResolver.getHostname(legendPoint.getIp());

				entry.add(new LegendElement(hostName));
				entry.add(new LegendElement(getTotalThroughput(result.getDownloaded())));
				entry.add(new LegendElement(getTotalThroughput(result.getUploaded())));
				entry.add(new LegendElement(getAverage(result.getDownloaded(), time)));
				entry.add(new LegendElement(getAverage(result.getUploaded(), time)));
				i++;
				rows.add(entry);
			}
			break;
		}
		return legend;
	}

	private static String getAverage(long traffic, long time)
	{
		long bitsConversion = 8;
		long kbitsConversion = 1024;
		return ((traffic * bitsConversion / kbitsConversion / time) != 0) ? (String.valueOf(traffic
				* bitsConversion / kbitsConversion / time)) : "&lt;1";
	}

	private static String getTotalThroughput(long traffic)
	{
		return ((traffic / 1048576) != 0) ? (String.valueOf(traffic / 1048576)) : "&lt;1";
	}
}
