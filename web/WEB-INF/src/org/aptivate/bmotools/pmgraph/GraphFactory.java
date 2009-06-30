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
			Map<DataPoint, float[]> series, XYItemRenderer renderer,
			long start, String name, RequestParams requestParams)
	{
		// Put the series into the graph
		for (DataPoint seriesId : series.keySet())
		{
			XYSeries xySeries = new XYSeries(seriesId.getSeriesId() + name,
					true, false);
			float[] values = series.get(seriesId);
			int j = 0;

			for (float val : values)
			{
				xySeries.add(Long.valueOf(start + j * 60000), Float
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
	 * Return a list of the ports or IPs with most traffic. We need to sum
	 * totals by port or Ip to order by traffic volume, then sort them and
	 * select only the top (matching the result limit)
	 * 
	 * @param thrptResults
	 * @param requestParams
	 * @return
	 */
	private List<DataPoint> getTopResults(List<DataPoint> thrptResults,
			RequestParams requestParams)
	{

		List<DataPoint> dataSeriesList = new ArrayList<DataPoint>();
		// seriesTotals is a hashmap used to accumulate totals
		Map<DataPoint, DataPoint> seriesTotals = new HashMap<DataPoint, DataPoint>();

		// create a list accumulating upload and download for each IP or port
		// Read through the results from the database (all the datapoints). For
		// each Ip or port, create an
		// entry in a data series list in which the total points are summed
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
				// we want generict instances to sum up all the data per a
				// series then we do not need Ip
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
	private JFreeChart fillGraph(List<DataPoint> thrptResults,
			RequestParams requestParams)
	{
		LinkedHashMap<DataPoint, float[]> downSeries = new LinkedHashMap<DataPoint, float[]>();
		LinkedHashMap<DataPoint, float[]> upSeries = new LinkedHashMap<DataPoint, float[]>();
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
		List<DataPoint> topIds = getTopResults(thrptResults, requestParams);

		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Time spent in sorting: " + endTime + " millisecond");

		m_logger.debug("Start Filling the chart with data.");
		initTime = System.currentTimeMillis();
		m_logger.debug("Number of rows in result set = " + thrptResults.size());

		for (DataPoint topId : topIds) // Is in the Top X results.
		{
			float upSeriesElement[] = new float[minutes + 1];
			float downSeriesElement[] = new float[minutes + 1];
			upSeries.put(topId, upSeriesElement);
			downSeries.put(topId, downSeriesElement);

		}

		for (DataPoint thrptResult : thrptResults)
		{
			DataPoint seriesId = thrptResult.createCopy();
			seriesId.setTime(null); // this data point represent a whole series

			Timestamp inserted = thrptResult.getTime();
			// values in the database are in bytes per interval (normally 1
			// minute)
			// bytes * 8 = bits bits / 1024 = kilobits kilobits / 60 = kb/s
			float downloaded = (float) ((thrptResult.getDownloaded() * 8) / 1024) / 60;
			float uploaded = (float) ((thrptResult.getUploaded() * 8) / 1024) / 60;

			// check if the ip already has its own series if not we create one
			// for it

			float[] dSeries = downSeries.get(seriesId);
			float[] uSeries = upSeries.get(seriesId);
			if (upSeries.containsKey(seriesId))
			{ // We created a series for
				// this port so it should be
				// in the
				// limit top
				// update the values of the series
				dSeries[((int) (inserted.getTime() - start)) / 60000] = downloaded;
				uSeries[((int) (inserted.getTime() - start)) / 60000] = 0 - uploaded;
			} else
			{ // the port belongs to the group other - just stack
				// values

				otherDown[((int) (inserted.getTime() - start)) / 60000] += downloaded;
				otherUp[((int) (inserted.getTime() - start)) / 60000] += 0 - uploaded;

				if (!(upSeries.size() == requestParams.getResultLimit() + 1))
				{
					upSeries.put(new IpDataPoint(IpDataPoint.OTHER_IP), otherUp);
					downSeries.put(new IpDataPoint(IpDataPoint.OTHER_IP),
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
		List<DataPoint> thrptResults = dataAccess.getThroughput(requestParams,
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