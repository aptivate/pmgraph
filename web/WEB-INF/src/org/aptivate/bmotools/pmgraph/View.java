package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum View {
	// Show Ips or show ports in graph
	LOCAL_IP, LOCAL_PORT, REMOTE_IP, REMOTE_PORT;

	public static List<View> getAvailableViews(RequestParams requestParams) {
		List<View> views = new ArrayList<View>();

		for (View view : View.values()) { // for each view
			// if the ip is selected, local ip view doesn't make sense
			if ((view == LOCAL_IP) && (requestParams.getIp() != null))
				continue;
			if ((view == LOCAL_PORT) && (requestParams.getPort() != null))
				continue;

			// if the remote ip is selected, remote ip view doesn't make sense
			if ((view == REMOTE_IP) && (requestParams.getRemoteIp() != null))
				continue;
			if ((view == REMOTE_PORT)
					&& (requestParams.getRemotePort() != null))
				continue;
			views.add(view); // add the view to the list
		}
		return views;
	}

	public static View getNextView(RequestParams requestParams, String paramName) {

		for (View view : View.values()) { // for each view

			// if the ip is selected, local ip view doesn't make sense
			if (((view == LOCAL_IP) && (requestParams.getIp() != null))
					|| ((view == LOCAL_IP) && ("ip".equals(paramName))))
				continue;
			if (((view == LOCAL_PORT) && (requestParams.getPort() != null))
					|| ((view == LOCAL_PORT) && ("port".equals(paramName))))
				continue;
			// if the remote ip is selected remote ip view doesn't make sense
			if (((view == REMOTE_IP) && (requestParams.getRemoteIp() != null))
					|| ((view == REMOTE_IP) && ("remote_ip".equals(paramName))))
				continue;
			if (((view == REMOTE_PORT) && (requestParams.getRemotePort() != null))
					|| ((view == REMOTE_PORT) && ("remote_port"
							.equals(paramName))))
				continue;
			return (view); // add the view to the list
		}
		return null;
	}

	public static LegendTable getLegendTable(UrlBuilder pageUrl,
			List<GraphData> legendData) throws IOException {

		RequestParams requestParams = pageUrl.getParams();

		LegendTable legend = new LegendTable();

		String arrow = "ASC".equals(pageUrl.getParams().getOrder()) ? " &#8679;"
				: " &#8681;";
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
		ArrayList<ArrayList<LegendTableEntry>> headers = legend.getHeaders();
		ArrayList<ArrayList<LegendTableEntry>> rows = legend.getRows();

		ArrayList<LegendTableEntry> firstRowHeader = new ArrayList<LegendTableEntry>();
		ArrayList<LegendTableEntry> secondRowHeader = new ArrayList<LegendTableEntry>();

		// an empty field in the header for the colour column
		firstRowHeader
				.add(0, new LegendTableEntry("", null, null, false, true));

		switch (requestParams.getView()) {
		case LOCAL_PORT:
			firstRowHeader.add(new LegendTableEntry("Local Port", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Protocol", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Service", null, null,
					false, true));

			break;
		case REMOTE_PORT:
			firstRowHeader.add(new LegendTableEntry("Remote Port", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Protocol", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Service", null, null,
					false, true));

			break;
		case REMOTE_IP:
			firstRowHeader.add(new LegendTableEntry("Remote Port", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Host Name", null, null,
					false, true));

			break;
		default:
		case LOCAL_IP:
			firstRowHeader.add(new LegendTableEntry("Local IP", null, null,
					false, true));
			firstRowHeader.add(new LegendTableEntry("Host Name", null, null,
					false, true));

			break;
		}

		firstRowHeader.add(new LegendTableEntry(col3, pageUrl
				.getIndexURL("bytes_total"), "bytes_total", true, false));
		firstRowHeader.add(new LegendTableEntry("Average (kb/s)", null, null,
				true, false));

		secondRowHeader.add(new LegendTableEntry(col1, pageUrl
				.getIndexURL("downloaded"), "downloaded"));
		secondRowHeader.add(new LegendTableEntry(col2, pageUrl
				.getIndexURL("uploaded"), "uploaded"));
		secondRowHeader.add(new LegendTableEntry("Down"));
		secondRowHeader.add(new LegendTableEntry("Up"));

		headers.add(firstRowHeader);
		headers.add(secondRowHeader);

		int i = 0;
		GraphFactory graphFactory = new GraphFactory();

		boolean AvailableViewRemote = false;
		boolean AvailableViewLocal = false;
		boolean others = false;
		boolean remote = false;
		boolean ports = false;

		for (GraphData result : legendData) {

			ArrayList<LegendTableEntry> entry = new ArrayList<LegendTableEntry>();
			Color c;
			String name;
			String value;
			String link;
			String textLink;
			switch (requestParams.getView()) {

			case LOCAL_PORT:
			case REMOTE_PORT:
				Integer port;
				if (requestParams.getView() == View.REMOTE_PORT) {
					remote = true;
					textLink = "remote_port";
					port = result.getRemotePort();
					AvailableViewRemote = ((requestParams.getRemoteIp() == null)
							|| (requestParams.getPort() == null) || (requestParams
							.getIp() == null));
				} else {
					textLink = "port";
					port = result.getPort();
					AvailableViewLocal = ((requestParams.getRemoteIp() == null)
							|| (requestParams.getRemotePort() == null) || (requestParams
							.getIp() == null));
				}
				ports = true;
				name = Port2Services.getInstance().getService(port,
						result.getProtocol());
				c = graphFactory.getSeriesColor(port, result.getProtocol());
				value = port.toString();
				if (result.getProtocol() == Protocol.icmp)
					value = "n/a";
				others = GraphFactory.OTHER_PORT == port;
				link = pageUrl.getUrlGraph(port, textLink);
				break;
			default:
			case LOCAL_IP:
			case REMOTE_IP:
				String ip;
				if (requestParams.getView() == View.REMOTE_IP) {
					textLink = "remote_IP";
					remote = true;
					ip = result.getRemoteIp();
					AvailableViewRemote = ((requestParams.getRemotePort() == null)
							|| (requestParams.getPort() == null) || (requestParams
							.getIp() == null));
				} else {
					textLink = "IP";
					ip = result.getLocalIp();
					AvailableViewLocal = ((requestParams.getRemotePort() == null)
							|| (requestParams.getPort() == null) || (requestParams
							.getRemoteIp() == null));
				}
				HostResolver hostResolver = new HostResolver();
				name = hostResolver.getHostname(ip);
				c = graphFactory.getSeriesColor(ip);
				others = GraphFactory.OTHER_IP.equalsIgnoreCase(ip);
				value = ip;
				link = pageUrl.getUrlGraph(ip, textLink);
				break;
			}
			String fillColour = Integer.toHexString(c.getRGB() & 0x00ffffff);
			fillColour = "#" + "000000".substring(0, 6 - fillColour.length())
					+ fillColour;
			// Here the colour of each row of the legend is added
			entry.add(new LegendTableEntry(fillColour));
			// here the field is filled with the port number
			if (others) {
				entry.add(new LegendTableEntry("Others"));
				// Add a empty space to match with the number of field in the
				// other cases
				if (ports)
					entry.add(new LegendTableEntry(""));
			} else {
				if (remote) {
					if (AvailableViewRemote) {
						entry.add(new LegendTableEntry(value, link, null));
					} else {
						entry.add(new LegendTableEntry(value));
					}
				} else {
					if (AvailableViewLocal) {
						entry.add(new LegendTableEntry(value, link, null));
					} else {
						entry.add(new LegendTableEntry(value));
					}
				}
				// here is filled the field about the protocol
				if (ports)
					entry.add(new LegendTableEntry(result.getProtocol()
							.toString()));
			}
			entry.add(new LegendTableEntry(name));
			entry.add(new LegendTableEntry(getTotalThroughput(result
					.getDownloaded())));
			entry.add(new LegendTableEntry(getTotalThroughput(result
					.getUploaded())));
			entry.add(new LegendTableEntry(getAverage(result.getDownloaded(),
					time)));
			entry.add(new LegendTableEntry(getAverage(result.getUploaded(),
					time)));
			i++;
			rows.add(entry);
		}
		return legend;
	}

	private static String getAverage(long traffic, long time) {
		long bitsConversion = 8;
		long kbitsConversion = 1024;
		return ((traffic * bitsConversion / kbitsConversion / time) != 0) ? (String
				.valueOf(traffic * bitsConversion / kbitsConversion / time))
				: "&lt;1";
	}

	private static String getTotalThroughput(long traffic) {
		return ((traffic / 1048576) != 0) ? (String.valueOf(traffic / 1048576))
				: "&lt;1";
	}
}
