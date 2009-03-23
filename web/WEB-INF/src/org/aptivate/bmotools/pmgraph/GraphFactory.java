package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
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
 * History:
 * 	Noe A. Rodrigez Glez.
 * 	12-03-2009	Use a standar method to asign colors to series.
 * 				
 * 				Changed the way in which the stackedThroughput graph
 * 				is created in order to make the code more readable.
 * 				
 * 				Removed the use of Alpha chanel on graphs because it
 * 				caused problems with the colors of the series.  
 * 				
 * 				Added some coments.
 */
public class GraphFactory
{

	private static Logger m_logger = Logger.getLogger(GraphFactory.class
			.getName());

	public Color getSeriesColor(String ip)
	{

		byte[] ipBytes = ip.getBytes();
		MessageDigest algorithm;
		try
		{
			algorithm = MessageDigest.getInstance("SHA1");
			algorithm.reset();
			algorithm.update(ipBytes);
			byte sha1[] = algorithm.digest();
			return (new Color(sha1[0] & 0xFF, sha1[1] & 0xFF, sha1[2] & 0xFF));
		}
		catch (NoSuchAlgorithmException e)
		{
			m_logger.error(e.getMessage(), e);
		}
		return (Color.BLACK);
	}

	/**
	 * Produces a JFreeChart showing total upload and download throughput for
	 * the time period between start and end.
	 * 
	 * @param start
	 *            Time in seconds since epoch, in which the chart will start
	 * @param end
	 *            Time in seconds since epoch, in which the chart will end
	 * @return a new JFreeChart
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * 
	 * 
	 */
	public JFreeChart totalThroughput(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException
	{
		// Get database connection
		DataAccess dataAccess = new DataAccess();

		List<GraphData> results = dataAccess.getTotalThroughput(start, end);

		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		// Initialise the XYSeries with 0 values for each minute
		XYSeries downSeries = new XYSeries("Downloaded", true, false);
		XYSeries upSeries = new XYSeries("Uploaded", true, false);
		int minutes = (int) (end - start) / 60000;
		for (int i = 0; i <= minutes; i++)
		{
			downSeries.add(start + i * 60000, 0);
			upSeries.add(start + i * 60000, 0);
		}

		for (GraphData dbData : results)
		{
			Date inserted = dbData.getTime();
			// bytes * 8 = bits bits * 1024 = kilobits kilobits / 60 = kB/s
			long downloaded = ((dbData.getDownloaded() * 8) / 1024) / 60;
			long uploaded = ((dbData.getUploaded() * 8) / 1024) / 60;
			downSeries.update(inserted.getTime(), downloaded);
			upSeries.update(inserted.getTime(), 0 - uploaded);
		}

		// Put our data into a "chartable" container
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		dataset.addSeries(downSeries);
		dataset.addSeries(upSeries);

		// Configure the chart elements and create and return the chart
		DateAxis xAxis = new DateAxis();
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
		NumberAxis yAxis = new NumberAxis("Throughput (kb/s)");
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.addRangeMarker(new ValueMarker(0));
		plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA));
		plot.getRenderer().setSeriesPaint(0, Color.blue);
		plot.getRenderer().setSeriesPaint(1, Color.blue);

		JFreeChart chart = new JFreeChart("Total Network Throughput",
				JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		chart.addSubtitle(new TextTitle(new Date(end).toString()));
		chart.setBackgroundPaint(null);
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
	 */
	public JFreeChart stackedThroughput(long start, long end, Integer limitResult)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException,
			NoSuchAlgorithmException
	{
		// Save the start and the end
		long theStart = start;
		long theEnd = end;

		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		HashMap<String, XYSeries> ip_XYSeries = new HashMap<String, XYSeries>();
		HashMap<Long, Long> otherUp = null;
		HashMap<Long, Long> otherDown = null;
		int minutes = (int) (end - start) / 60000;

		// Create a "chartable" data container
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		final JFreeChart chart = ChartFactory.createStackedXYAreaChart(
				"Network Throughput Per IP", // chart title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, false);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();

		DataAccess dataAccess = new DataAccess();
		List<GraphData> thrptResults = dataAccess.getThroughputPIPPMinute(
				theStart, theEnd);
		Collections.sort(thrptResults, new BytesTotalComparator(true));

		int j=0;
		// For each query result, get data and write to the appropriate series
		for (GraphData thrptResult : thrptResults)
		{
			Timestamp inserted = thrptResult.getTime();
			String ip = thrptResult.getLocalIp();
			ip = ip.trim();

			// values in the database are in bytes per interval (normally 1
			// minute)
			// bytes * 8 = bits bits / 1024 = kilobits kilobits / 60 = kb/s
			long downloaded = ((thrptResult.getDownloaded() * 8) / 1024) / 60;
			long uploaded = ((thrptResult.getUploaded() * 8) / 1024) / 60;
				
			// check if the ip already have its own serie if not we create one
			// for it
			if (!ip_XYSeries.containsKey(ip + "<down>"))
			{
				if (j < limitResult) {
					XYSeries downSeries = new XYSeries(ip + "<down>", true, false);
					XYSeries upSeries = new XYSeries(ip + "<up>", true, false);
					for (int i = 0; i <= minutes; i++)
					{
						downSeries.add(Long.valueOf(start + i * 60000), Long
								.valueOf(0));
						upSeries.add(Long.valueOf(start + i * 60000), Long
								.valueOf(0));
					}
					// keep the series in a hash in order to reuse it when we have
					// results for the same IP
					ip_XYSeries.put(ip + "<down>", downSeries);
					ip_XYSeries.put(ip + "<up>", upSeries);
					// Set the same color for the upload and download series of an
					// IP
					Color color = getSeriesColor(ip);
					// Put the series into the graph
					dataset.addSeries(downSeries);
					renderer.setSeriesPaint(dataset.getSeriesCount() - 1, color);
					dataset.addSeries(upSeries);
					renderer.setSeriesPaint(dataset.getSeriesCount() - 1, color);
				} 
				else 
				{
					// Create a hashMap to keep stacked values for group other
					if (otherUp == null) {

						otherUp = new HashMap<Long, Long>();
						otherDown = new HashMap<Long, Long>();
						for (int i = 0; i <= minutes; i++)
						{
							otherUp.put(Long.valueOf(start + i * 60000),0L);
							otherDown.put(Long.valueOf(start + i * 60000), 0L);
						}
					}
				}
				j++;
			}
			XYSeries downSeries = ip_XYSeries.get(ip + "<down>");
			XYSeries upSeries = ip_XYSeries.get(ip + "<up>");
			if ( downSeries != null) {			// We created series for this ip then it should be in the limit top
							// update the values of the series
				downSeries.update(inserted.getTime(), downloaded);
				upSeries.update(inserted.getTime(), (0 - uploaded));
			} else {		// the ip belong to the group other just stack values
				otherDown.put(inserted.getTime(), otherDown.get(inserted.getTime()) + downloaded);
				otherUp.put(inserted.getTime(), otherUp.get(inserted.getTime()) + (0 - uploaded));
			}
		}	
		
		if (otherUp != null) {
			XYSeries downSeries = new XYSeries("other <down>", true, false);
			XYSeries upSeries = new XYSeries("other <up>", true, false);
			for (int i = 0; i <= minutes; i++)
			{
				Long time= Long.valueOf(start + i * 60000);
				downSeries.add(time, otherDown.get(time));
				upSeries.add(time, otherUp.get(time));
				
			}
			Color otherGroupColor = getSeriesColor("255.255.255.255");
			dataset.addSeries(downSeries);
			renderer.setSeriesPaint(dataset.getSeriesCount() - 1, otherGroupColor);
			dataset.addSeries(upSeries);
			renderer.setSeriesPaint(dataset.getSeriesCount() - 1, otherGroupColor);
			
		}
		
		DateAxis xAxis;
		long timePeriod = (theEnd - theStart) / 60000;
		if (timePeriod < 7)
		{
			xAxis = new DateAxis("Time (hours:minutes:seconds)");
		}
		else if ((timePeriod >= 7) && (timePeriod < 3650))
		{
			xAxis = new DateAxis("Time (hours:minutes)");
		}
		else if ((timePeriod >= 3650) && (timePeriod < 7299))
		{
			xAxis = new DateAxis("Time (day-month,hours:minutes)");
		}
		else
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
		plot.setRenderer(renderer);
		chart.addSubtitle(new TextTitle(new Date(end).toString()));
		chart.setBackgroundPaint(null);
		chart.removeLegend();
	/*	A test to check Jfreechart
		XYSeries s1 = new XYSeries(1, true, false);
		XYSeries s2 = new XYSeries(2, true, false);
		XYSeries s3 = new XYSeries(3, true, false);

		s1.add (0,0);
		s1.add (1,-3);
		s1.add (2,0);
		s1.add (3,0);
		s2.add (1,0);
		s2.add (0,0);
		s2.add (2,-4);
		s2.add (3,0);
		s3.add (1,0);
		s3.add (0,0);
		s3.add (2,-4);
		s3.add (3,0);
		dataset.addSeries(s1);
		dataset.addSeries(s2);
//		dataset.addSeries(s3);
*/
		return (chart);
	}
}