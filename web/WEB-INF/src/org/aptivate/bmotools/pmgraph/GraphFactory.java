package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * Initialize a series for a port or IP graph with all the values set to zero and
	 * add it to the database and to the hashmap containing all the series.
	 * 
	 * @param dataset
	 * @param series
	 * @param renderer
	 * @param roundedStart
	 * @param name
	 * @param requestParams
	 */
	private void series2DataSet(DefaultTableXYDataset dataset,
			Map<DataPoint, float[]> series, XYItemRenderer renderer,
			long roundedStart, String name, RequestParams requestParams, int resolution)
	{
		// Put the series into the graph
		for (DataPoint seriesId : series.keySet())
		{
			XYSeries xySeries = new XYSeries(seriesId.getSeriesId() + name,
					true, false);
			float[] values = series.get(seriesId);
			long j = 0;

			for (float val : values)
			{
				xySeries.add(Long.valueOf(roundedStart + j * resolution), Float
						.valueOf(val));
				j++;
			}
			dataset.addSeries(xySeries);
			renderer.setSeriesPaint(dataset.getSeriesCount() - 1, 
					seriesId.getSeriesColor());
		}
	}

	/**
	 * Create the chart title based on the request type.
	 * 
	 * @param requestParams
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
	 * Return a list of the ports or IPs with most traffic. We need to sum
	 * totals by port or Ip to order by traffic volume, then sort them and
	 * select only the top (matching the result limit)
	 * 
	 * @param thrptResults  results from the database
	 * @param requestParams parameters from the request
	 * @return List of DataPoint with the top results set by the user or by default
	 */
	private List<DataPoint> getTopResults(List<DataPoint> thrptResults,
			RequestParams requestParams)
	{

		List<DataPoint> dataSeriesList = new ArrayList<DataPoint>();
		// seriesTotals is a hashmap used to accumulate totals
		Map<DataPoint, DataPoint> seriesTotals = new HashMap<DataPoint, DataPoint>();

		// create a list accumulating upload and download for each IP or port
		// Read through the results from the database (all the datapoints). 
		// For each Ip or port, create an entry in a data series list in which 
		//the total points are summed
		for (DataPoint thrptResult : thrptResults)
		{
			DataPoint seriesId = thrptResult.createCopy();
			seriesId.setTime(null);
			DataPoint data = seriesTotals.get(seriesId);

			// if the point already exists
			if (data != null)
			{
				data.addToUploaded(thrptResult.getUploaded());
				data.addToDownloaded(thrptResult.getDownloaded());
			} else
			// create a new item in the list, for Ip or Port
			{
				if (thrptResult instanceof PortDataPoint)
				{
					data = new PortDataPoint((PortDataPoint) thrptResult);
				} else
				{
					data = new IpDataPoint((IpDataPoint) thrptResult);
				}
				
				data.setTime(null);
				seriesTotals.put(seriesId, data);
			}
		}
		
		for (DataPoint series : seriesTotals.values())
		{
			dataSeriesList.add(series);
		}
		// sort the list using byte total
		Collections.sort(dataSeriesList, new BytesTotalComparator(true));

		// truncate the list to the result limit
		if (dataSeriesList.size() > requestParams.getResultLimit())
		{
			dataSeriesList = dataSeriesList.subList(0, (int) requestParams
					.getResultLimit());
		}
		return dataSeriesList;
	}

	/**
	 * Create a JFreeChart with the data in the List thrptResults creating a new
	 * series per each port, or Ip
	 * 
	 * @param thrptResults   List of data to chart
	 * @param requestParams  Parameters from the request
	 
	 * @return A JFreeChart with the data in the List thrptResults creating a
	 *         new series for each port or Ip
	 */
	private JFreeChart fillGraph(List<DataPoint> thrptResults,
			RequestParams requestParams, boolean isLong) {
		Map<DataPoint, float[]> downSeries = new LinkedHashMap<DataPoint, float[]>();
		Map<DataPoint, float[]> upSeries = new LinkedHashMap<DataPoint, float[]>();
		long roundedStart;    //Rounded to minutes
		long roundedEnd;
		long theStart = requestParams.getStartTime();			   //in milliseconds
		long theEnd = requestParams.getEndTime();

		String title = chartTitle(requestParams);

		int resolution = Configuration.getResolution(isLong, theEnd - theStart);
		roundedStart = requestParams.getRoundedStartTime(resolution);
		roundedEnd = requestParams.getRoundedEndTime(resolution);
		
		int timeUnits = (int) ((roundedEnd - roundedStart) / resolution);
		float[] otherUp = new float[timeUnits + 1];
		float[] otherDown = new float[timeUnits + 1];

		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		JFreeChart chart = createStackedXYGraph(title, dataset, roundedStart, roundedEnd,
				theStart, theEnd);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();

		// For each query result, get data and write to the appropriate series

		m_logger.debug("Start sorting result list.");
		long initTime = System.currentTimeMillis();
		List<DataPoint> topIds = getTopResults(thrptResults, requestParams);

		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Time spent in sorting: " + endTime + " millisecond");

		m_logger.debug("Start Filling the chart with data.");
		initTime = System.currentTimeMillis();
		m_logger.debug("Number of rows in result set = " + thrptResults.size());

		for (DataPoint topId : topIds) // Is in the Top X results.
		{
			float upSeriesElement[] = new float[timeUnits + 1];
			float downSeriesElement[] = new float[timeUnits + 1];
			upSeries.put(topId, upSeriesElement);
			downSeries.put(topId, downSeriesElement);

		}

		for (DataPoint thrptResult : thrptResults) {
			DataPoint seriesId = thrptResult.createCopy();
			seriesId.setTime(null); // this data point represent a whole series

			Timestamp inserted = thrptResult.getTime();
			// values in the database are in bytes per interval (normally 1
			// minute)
			// bytes * 8 = bits bits / 1024 = kilobits kilobits / 60 = kb/s
			// When isLong is true, the data is for every hour so divide by (60 * 60) = 3600 seconds = 1 hour
			int offset = 1;
			if(isLong)
			{
				offset = 60;
			}
			float downloaded = (float) ((thrptResult.getDownloaded() * 8) / 1024) / (60 * offset);
			float uploaded = (float) ((thrptResult.getUploaded() * 8) / 1024) / (60 * offset);

			// check if the ip already has its own series if not we create one
			// for it

			

			// We created a series for this port so it should be within the top
			// results
			// update the values of the series
			if (upSeries.containsKey(seriesId)) {
				float[] dSeries = downSeries.get(seriesId);
				float[] uSeries = upSeries.get(seriesId);
				dSeries[((int) ((inserted.getTime() - roundedStart) / resolution))] = downloaded;
				uSeries[((int) ((inserted.getTime() - roundedStart) / resolution))] = 0 - uploaded;
			} else { // the port belongs to the group other - just stack
						// values

				otherDown[((int) ((inserted.getTime() - roundedStart) / resolution))] += downloaded;
				otherUp[((int) ((inserted.getTime() - roundedStart) / resolution))] += 0 - uploaded;

				if (!(upSeries.size() == requestParams.getResultLimit() + 1)) {
					if (thrptResult instanceof IpDataPoint) {
						upSeries.put(new IpDataPoint(IpDataPoint.OTHER_IP),
								otherUp);
						downSeries.put(new IpDataPoint(IpDataPoint.OTHER_IP),
								otherDown);
					} else {
						upSeries.put(
								new PortDataPoint(PortDataPoint.OTHER_PORT),
								otherUp);
						downSeries.put(new PortDataPoint(
								PortDataPoint.OTHER_PORT), otherDown);
					}
				}
			}
		}
		// Once the data that should be in the graph is created lets go to
		// introduce it in the dataset of the chart.

		series2DataSet(dataset, downSeries, renderer, roundedStart, "<down>",
				requestParams, resolution);
		series2DataSet(dataset, upSeries, renderer, roundedStart, "<up>",
				requestParams, resolution);

		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Data inserted in chart Time : " + endTime + " millisec");

		return chart;
	}
	
	/**
	 * A method which creates the chart with the default options for the stacked
	 * charts
	 * 
	 * @param title   Title
	 * @param dataset
	 * @param roundedStart	  Start time rounded to minutes
	 * @param roundedEnd     
	 * @param theStart		  Start time in milliseconds
	 * @param theEnd
	 * @return A chart with the default options, and without data.
	 */
	private JFreeChart createStackedXYGraph(String title,
			DefaultTableXYDataset dataset, long roundedStart, long roundedEnd, long theStart,
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

		//Change the scale of the time axis to match the period
		DateAxis xAxis;
		long timePeriod = (theEnd - theStart) / 60000;
		if (timePeriod < 7)
		{
			xAxis = new DateAxis("Time (hours:minutes:seconds)");
		} else
			if ((timePeriod >= 7) && (timePeriod < 3650))
			{
				xAxis = new DateAxis("Time (hours:minutes)");
			} else
				if ((timePeriod >= 3650) && (timePeriod < 7299))
				{
					xAxis = new DateAxis("Time (day-month,hours:minutes)");
				} else
				// timePeriod >= 7299
				{
					xAxis = new DateAxis("Time (day-month)");
				}
		xAxis.setMinimumDate(new Date(roundedStart - 1));
		xAxis.setMaximumDate(new Date(roundedEnd));
		NumberAxis yAxis = new NumberAxis("Throughput (kb/s)");
		plot.setRangeAxis(yAxis);
		plot.setDomainAxis(xAxis);
		plot.addRangeMarker(new ValueMarker(0));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.GRAY);
		chart.addSubtitle(new TextTitle(new Date(roundedEnd).toString()));
		chart.setBackgroundPaint(null);
		chart.removeLegend();
		
		return chart;
	}

	/**
	 * Produces a JFreeChart showing total upload and download throughput for
	 * each IP as a cumulative stacked graph for the time period between roundedStart
	 * and roundedEnd.
	 * 
	 * @param requestParams The parameters entered in the request
	 *            Time in seconds since epoch, in which the chart will start
	 * @return JFreeChart Object containing the info of the stackedThroughput
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
		boolean isLong = Configuration.longGraphIsAllowed() &&  Configuration.needsLongGraph(requestParams.getFromDateAndTime().getTime(), requestParams.getToDateAndTime().getTime());

		DataAccess dataAccess = new DataAccess();
		List<DataPoint> thrptResults;
	
		thrptResults = dataAccess.getThroughput(requestParams, true, isLong);
		m_logger.debug("Start creating chart.");
		long initTime = System.currentTimeMillis();
		Collections.sort(thrptResults, new BytesTotalComparator(true));

		JFreeChart chart = fillGraph(thrptResults, requestParams, isLong);

		if (m_logger.isDebugEnabled())
		{
			long endTime = System.currentTimeMillis() - initTime;
			m_logger.debug("Execution Time creating chart : " + endTime
					+ " millisec");
		}
		thrptResults = null;
		return chart;
	}
}