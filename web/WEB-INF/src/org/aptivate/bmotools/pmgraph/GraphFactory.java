package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * The GraphFactory class provides static methods which return JFreeChart
 * objects (which can then be served as web content or saved as images)
 * representing network traffic logged by pmacct on the BMO Box.
 * 
 * @author Thomas Sharp
 * @version 0.1
 * 
 * History: Noe A. Rodrigez Glez. 12-03-2009 Use a standard method to assign
 * colors to series.
 * 
 * Changed the way in which the stackedThroughput graph is created in order to
 * make the code more readable.
 * 
 * Removed the use of Alpha chanel on graphs because it caused problems with the
 * colors of the series.
 * 
 * Added some comments.
 */
public class GraphFactory
{

	private Logger m_logger = Logger.getLogger(GraphFactory.class.getName());

	public static final int OTHER_PORT = -1;

	public static final String OTHER_IP = "255.255.255.255";

	/**
	 * This class contains the info that represents a specific series.
	 */
	private class SeriesId
	{

		private String m_id;

		private Protocol m_protocol;

		public SeriesId(String id, Protocol proto)
		{
			m_id = id;
			m_protocol = proto;
		}

		public String getId()
		{
			return m_id;
		}

		public void setId(String m_id)
		{
			this.m_id = m_id;
		}

		public Protocol getProtocol()
		{
			return m_protocol;
		}

		public void setProtocol(Protocol m_protocol)
		{
			this.m_protocol = m_protocol;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((m_id == null) ? 0 : m_id.hashCode());
			result = PRIME * result
					+ ((m_protocol == null) ? 0 : m_protocol.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SeriesId other = (SeriesId) obj;
			if (m_id == null)
			{
				if (other.m_id != null)
					return false;
			} else if (!m_id.equals(other.m_id))
				return false;
			if (m_protocol == null)
			{
				if (other.m_protocol != null)
					return false;
			} else if (!m_protocol.equals(other.m_protocol))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			if (m_protocol != null)
				return (m_id + m_protocol.toString());
			return (m_id);
		}

	}

	private Color getColorFromByteArray(byte[] bytes)
	{
		MessageDigest algorithm;
		try
		{
			algorithm = MessageDigest.getInstance("SHA1");
			algorithm.reset();
			algorithm.update(bytes);
			byte sha1[] = algorithm.digest();
			return (new Color(sha1[0] & 0xFF, sha1[1] & 0xFF, sha1[2] & 0xFF));
		} catch (NoSuchAlgorithmException e)
		{
			m_logger.error(e.getMessage(), e);
		}
		return (Color.BLACK);
	}

	/**
	 * Initialize a series for a port graph with all the values set to zero and
	 * add it to the database and to the hashmap containing all the series.
	 * 
	 * @param dataset
	 * @param series
	 * @param renderer
	 * @param start
	 * @param name
	 * @param view
	 */
	private void series2DataSet(DefaultTableXYDataset dataset,
			Map<SeriesId, float[]> series, XYItemRenderer renderer, long start,
			String name, RequestParams requestParams)
	{
		//int i=0 ;
		// Put the series into the graph
		for (SeriesId seriesId : series.keySet())
		{
			Color color;
			String id = seriesId.getId();
			Protocol protocol = seriesId.getProtocol();
			switch (requestParams.getView())
			{
				case LOCAL_PORT:
				case REMOTE_PORT:
					if (id == OTHER_IP)
						color = getSeriesColor(OTHER_PORT, Protocol.tcp);
					else
						color = getSeriesColor(Integer.valueOf(id), protocol);
					break;
				default:
				case LOCAL_IP:
				case REMOTE_IP:
					color = getSeriesColor(id);
					break;
			}
			XYSeries xySeries = new XYSeries(seriesId.toString() + name, true,
					false);
			float[] values = series.get(seriesId);
			int j = 0;
			
			
			//System.out.println ("// Values for "+seriesId.toString() );
			for (float val : values)
			{
				/*if (Float.valueOf(val) != 0) {
					int pos = 2*i;
					if (name == "<up>") {
						System.out.println ("values["+pos+"]["+j+"] = "+Float.valueOf(val)+"f; // Upload values Time "+j+"");
					} else {
						pos = 2*i+1;
						System.out.println ("values["+pos+"]["+j+"] = "+Float.valueOf(val)+"f; // Download values Time "+j+" ");
					}
				}*/
				
				xySeries.add(Long.valueOf(start + j * 60000), Float.valueOf(val));
				j++;
			}
			//i++;
			dataset.addSeries(xySeries);
			renderer.setSeriesPaint(dataset.getSeriesCount() - 1, color);
		}
	}

	/**
	 * Create the chart title based on the request type.
	 * 
	 * @param requestParams
	 * @returnNewVolunteerProcess.CoderEmailText
	 */
	private String chartTitle(RequestParams requestParams)
	{
		String title = "";

		if (requestParams.getIp() != null)
		{
			title = " For Local Ip = " + requestParams.getIp();
		}
		if (requestParams.getPort() != null)
		{
			title += " For Local Port = " + requestParams.getPort();
		}
		if (requestParams.getRemoteIp() != null)
		{
			title += " For Remote Ip = " + requestParams.getRemoteIp();
		}
		if (requestParams.getRemotePort() != null)
		{
			title += " For Remote Port = " + requestParams.getRemotePort();
		}
		return "Network Throughput" + title;
	}

	/**
	 * 
	 * @param pageUrl
	 * @param graphData
	 * @return
	 */
	private String getSeriesId(RequestParams requestParams, GraphData graphData)
	{

		switch (requestParams.getView())
		{
			case LOCAL_PORT:
				return graphData.getPort().toString();
			case REMOTE_PORT:
				return graphData.getRemotePort().toString();

			default:
			case LOCAL_IP:
				return graphData.getLocalIp().trim();
			case REMOTE_IP:
				return graphData.getRemoteIp().trim();
		}

	}
	
	/**
	 * Retun a list of the X id with most traffic.
	 * 
	 * @param thrptResults
	 * @param requestParams
	 * @return
	 */
	private List getTopResults(List<GraphData> thrptResults,
			RequestParams requestParams)
	{

		List<GraphData> topList = new ArrayList<GraphData>();
		ArrayList<SeriesId> topId = new ArrayList<SeriesId>();
		Map<SeriesId, GraphData> aux = new HashMap<SeriesId, GraphData>();

		// create a list accumulating upload and download per minute
		for (GraphData thrptResult : thrptResults)
		{
			String id = getSeriesId(requestParams, thrptResult);
			SeriesId seriesId = new SeriesId(id, thrptResult.getProtocol());
			GraphData data = aux.get(seriesId);
			if (data != null)
			{
				data.incrementUploaded(thrptResult.getUploaded());
				data.incrementDownloaded(thrptResult.getDownloaded());
			} else
			{
				aux.put(seriesId, new GraphData(thrptResult));

			}
		}
		for (GraphData thrptResult : aux.values())
		{
			topList.add(thrptResult);
		}
		// sort the list using byte total
		Collections.sort(topList, new BytesTotalComparator(true));
		if (topList.size() > requestParams.getResultLimit())
		{
			topList = topList.subList(0, (int) requestParams.getResultLimit());
		}
		for (GraphData thrptResult : topList)
		{
			topId.add(new SeriesId(getSeriesId(requestParams, thrptResult),
					thrptResult.getProtocol()));
		}
		return (topId);
	}

	/**
	 * Create a JFreeChart with the data in the List thrptResults creating a new
	 * series per each port, or Ip
	 * 
	 * @param start
	 * @param end
	 * @param theStart
	 * @param theEnd
	 * @param thrptResults
	 * @param limitResult
	 * @param title
	 * @param portGraph
	 *            The series are Ip's or Ports
	 * @return A JFreeChart with the data in the List thrptResults creating a
	 *         new series for each port or Ip
	 */
	private JFreeChart fillGraph(List<GraphData> thrptResults,
			RequestParams requestParams)
	{
		LinkedHashMap<SeriesId, float[]> downSeries = new LinkedHashMap<SeriesId, float[]>();
		LinkedHashMap<SeriesId, float[]> upSeries = new LinkedHashMap<SeriesId, float[]>();
		long start = requestParams.getRoundedStartTime();
		long end = requestParams.getRoundedEndTime();
		long theStart = requestParams.getStartTime();
		long theEnd = requestParams.getEndTime();
		// boolean other = false;

		String title = chartTitle(requestParams);

		int minutes = (int) (end - start) / 60000;
		float[] otherUp = new float[minutes + 1];
		float[] otherDown = new float[minutes + 1];

		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		JFreeChart chart = createStackedXYGraph(title, dataset, start, end,
				theStart, theEnd);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();

		// For each query result, get data and write to the appropriate series

		m_logger.debug("Start sorting result list.");
		long initTime = System.currentTimeMillis();
		List<SeriesId> topIds = getTopResults(thrptResults, requestParams);

		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Time spent in sorting: " + endTime + " millisecond");

		m_logger.debug("Start Filling the chart with data.");
		initTime = System.currentTimeMillis();
		m_logger.debug("Number of rows in result set = " + thrptResults.size());
		
		//System.out.print ("rows = new String[] {");
		for (SeriesId topId : topIds) // Is in the Top X
		// results.
		{
			float upSerie[] = new float[minutes + 1];
			float downSerie[] = new float[minutes + 1];
			upSeries.put(topId, upSerie);
			downSeries.put(topId, downSerie);
			
			// init array to zero values			
			
			//System.out.print (topId.toString()+", ");			
		}

		//System.out.println ("};");
		//System.out.println ("values = new float[2 * "+topIds.size()+"][4];");
		
		
		for (GraphData thrptResult : thrptResults)
		{
			String id = getSeriesId(requestParams, thrptResult);
			SeriesId seriesId = new SeriesId(id, thrptResult.getProtocol());
			Timestamp inserted = thrptResult.getTime();
			// values in the database are in bytes per interval (normally 1
			// minute)
			// bytes * 8 = bits bits / 1024 = kilobits kilobits / 60 = kb/s
			float downloaded = (float)((thrptResult.getDownloaded() * 8) / 1024) / 60;
			float uploaded = (float)((thrptResult.getUploaded() * 8) / 1024) / 60;

			// check if the ip already has its own series if not we create one
			// for it

			float[] dSeries = downSeries.get(seriesId);
			float[] uSeries = upSeries.get(seriesId);
			if (upSeries.containsKey(seriesId))
			{ // We created a series for this port so it should be in the
				// limit top
				// update the values of the series
				dSeries[((int) (inserted.getTime() - start)) / 60000] = downloaded;
				uSeries[((int) (inserted.getTime() - start)) / 60000] = 0 - uploaded;
			} else
			{ // the port belongs to the group other - just stack values

				otherDown[((int) (inserted.getTime() - start)) / 60000] += downloaded;
				otherUp[((int) (inserted.getTime() - start)) / 60000] += 0 - uploaded;

				if (!(upSeries
						.containsKey(new SeriesId(OTHER_IP, Protocol.tcp))))
				{
					upSeries.put(new SeriesId(OTHER_IP, Protocol.tcp), otherUp);
					downSeries.put(new SeriesId(OTHER_IP, Protocol.tcp),
							otherDown);
				}
			}
		}
		// Once the data that should be in the graph is created lets go to
		// introduce it in the dataset of the chart.

		series2DataSet(dataset, downSeries, renderer, start, "<down>",
				requestParams);
		series2DataSet(dataset, upSeries, renderer, start, "<up>",
				requestParams);

		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Data iserted in chart Time : " + endTime + " miliseg");

		return chart;
	}

	/**
	 * A method which creates the chart with the default options for the stacked
	 * charts
	 * 
	 * @param dataset
	 * @param start
	 * @param end
	 * @param theStart
	 * @param theEnd
	 * @return A chart with the default options, and without data.
	 */
	private JFreeChart createStackedXYGraph(String title,
			DefaultTableXYDataset dataset, long start, long end, long theStart,
			long theEnd)
	{

		m_logger.debug("Create Jfreechart Graph instance");
		JFreeChart chart = ChartFactory.createStackedXYAreaChart(title, // chart
				// title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, false);

		m_logger.debug("Jfreechart Graph instance already created.");

		XYPlot plot = chart.getXYPlot();

		DateAxis xAxis;
		long timePeriod = (theEnd - theStart) / 60000;
		if (timePeriod < 7)
		{
			xAxis = new DateAxis("Time (hours:minutes:seconds)");
		} else if ((timePeriod >= 7) && (timePeriod < 3650))
		{
			xAxis = new DateAxis("Time (hours:minutes)");
		} else if ((timePeriod >= 3650) && (timePeriod < 7299))
		{
			xAxis = new DateAxis("Time (day-month,hours:minutes)");
		} else
		// timePeriod >= 7299
		{
			xAxis = new DateAxis("Time (day-month)");
		}
		xAxis.setMinimumDate(new Date(start - 1));
		xAxis.setMaximumDate(new Date(end));
		NumberAxis yAxis = new NumberAxis("Throughput (kb/s)");
		plot.setRangeAxis(yAxis);
		plot.setDomainAxis(xAxis);
		plot.addRangeMarker(new ValueMarker(0));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.GRAY);
		chart.addSubtitle(new TextTitle(new Date(end).toString()));
		chart.setBackgroundPaint(null);
		chart.removeLegend();
		return chart;
	}

	/**
	 * Return a color obtained from creating a hash with the bytes of the Ip.
	 * 
	 * @param ip
	 * @return Color for the selected IP.
	 */
	public Color getSeriesColor(String ip)
	{
		if (ip != null)
		{
			byte[] ipBytes = ip.getBytes();

			return getColorFromByteArray(ipBytes);
		}
		m_logger
				.warn("Unable to assign a color to a null IP. (Black color assigned)");
		return (Color.BLACK);
	}

	/**
	 * Return a color obtained from creating a hash with the bytes of the Port.
	 * 
	 * @param port
	 * @return Color for the selected port
	 */
	public Color getSeriesColor(int port, Protocol protocol)
	{

		byte[] portBytes = new byte[] { (byte) (port >>> 24),
				(byte) (port >>> 16), (byte) (port >>> 8), (byte) port };

		if (protocol != null)
		{
			byte protocolByte = (byte) protocol.ordinal();
			portBytes[0] = (byte) (protocolByte | portBytes[0]);
		}
		return getColorFromByteArray(portBytes);
	}

	/**
	 * Produces a JFreeChart showing total upload and download throughput for
	 * each IP as a cumulative stacked graph for the time period between start
	 * and end.
	 * 
	 * @param start
	 *            Time in seconds since epoch, in which the chart will start
	 * @param end
	 *            Time in seconds since epoch, in which the chart will end
	 * @return JFreeChart Object containing the info of the stackedThroughput
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws PageUrlException
	 */
	JFreeChart stackedThroughputGraph(RequestParams requestParams)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException,
			NoSuchAlgorithmException, ConfigurationException
	{

		DataAccess dataAccess = new DataAccess();
		List<GraphData> thrptResults = dataAccess.getThroughput(requestParams,
				true);

		m_logger.debug("Start creating chart.");
		long initTime = System.currentTimeMillis();
		Collections.sort(thrptResults, new BytesTotalComparator(true));

		JFreeChart chart = fillGraph(thrptResults, requestParams);

		if (m_logger.isDebugEnabled())
		{
			long endTime = System.currentTimeMillis() - initTime;
			m_logger.debug("Execution Time creating chart : " + endTime
					+ " miliseg");
		}
		thrptResults = null;
		return chart;
	}
}