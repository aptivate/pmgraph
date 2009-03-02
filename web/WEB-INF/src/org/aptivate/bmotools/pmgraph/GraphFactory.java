package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
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
 */
public class GraphFactory 
{
	
	// MySQL table fields
	private static final String DOWNLOADED = "downloaded";
	private static final String IP = "local_ip";
	private static final String TIME = "stamp_inserted";
	private static final String UPLOADED = "uploaded";

	/**
	 * Produces a JFreeChart showing total upload and download throughput
	 * for the time period between start and end. 
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 */
    public static JFreeChart totalThroughput(long start, long end)
	throws ClassNotFoundException, IllegalAccessException, 
		   InstantiationException, IOException, SQLException
    {
    	// Get database connection 

    	
    	ResultSet results = (DataAccess.getDatabase()).getTotalThroughput(start, end);
    	
    	// Round our times to the nearest minute
    	start = start - (start % 60000);
    	end = end - (end % 60000);    	
    	// Initialise the XYSeries with 0 values for each minute
    	XYSeries downSeries = new XYSeries("Downloaded", true, false);
    	XYSeries upSeries = new XYSeries("Uploaded", true, false);
    	int minutes = (int) (end - start) / 60000;   	
    	for(int i = 0; i <= minutes; i++) 
    	{
    		downSeries.add(start + i * 60000, 0);
    		upSeries.add(start + i * 60000, 0);
    	}

    	while(results.next()) 
    	{
    		Date inserted = results.getTimestamp(TIME);
    		// bytes * 8 = bits    bits * 1024 = kilobits    kilobits / 60 = kB/s
    		long downloaded = ((results.getLong(DOWNLOADED) * 8) / 1024) / 60;
    		long uploaded = ((results.getLong(UPLOADED) * 8) / 1024) / 60;
    		downSeries.update(inserted.getTime(), downloaded);
    		upSeries.update(inserted.getTime(), 0 - uploaded);
    	}
    	results.close();
    	
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
    	//plot.addRangeMarker(new ValueMarker(maxDown));
    	//plot.addRangeMarker(new ValueMarker(0 - maxUp));
    	plot.addRangeMarker(new ValueMarker(0));
    	plot.setForegroundAlpha(0.8f);
    	plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA));
    	plot.getRenderer().setSeriesPaint(0, Color.blue);
    	plot.getRenderer().setSeriesPaint(1, Color.blue);

    	JFreeChart chart = new JFreeChart("Total Network Throughput", 
    			JFreeChart.DEFAULT_TITLE_FONT, 
    			plot, 
    			false);
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
     * @param end
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws SQLException
     */
    public static JFreeChart stackedThroughput(long start, long end)
    throws ClassNotFoundException, IllegalAccessException, InstantiationException, 
    IOException, SQLException, NoSuchAlgorithmException 
    {
    	// Save the start and the end
    	long theStart = start;
    	long theEnd = end;
    	
    	// Round our times to the nearest minute
    	start = start - (start % 60000);
    	end = end - (end % 60000);
    	
    	// Get database connection 
    	DataAccess dataAccess = DataAccess.getDatabase();
    	ResultSet ipResults = dataAccess.getThroughputPerIP(theStart, theEnd);
    	ipResults.beforeFirst();
    	
    	// For each result, initialise XYSeries and store in HashMap
    	HashMap IPs = new HashMap();
    	int minutes = (int) (end - start) / 60000;
    	
    	while(ipResults.next())
    	{
    		String ip = ipResults.getString(IP);
    		ip = ip.trim();
    		XYSeries downSeries = new XYSeries(ip + "<down>", true, false);
    		XYSeries upSeries = new XYSeries(ip + "<up>", true, false);
        	
    		for(int i = 0; i <= minutes; i++)
        	{
        		downSeries.add(Long.valueOf(start + i * 60000), Long.valueOf(0));
        		upSeries.add  (Long.valueOf(start + i * 60000), Long.valueOf(0));
        	}
    		
    		IPs.put(ip + "<down>", downSeries);
    		IPs.put(ip + "<up>", upSeries);
    	}
    	// We don't close the 'active IPs' query just yet...
    	
    	ResultSet thrptResults = dataAccess.getThroughputPIPPMinute(theStart, theEnd);
      	// For each query result, get data and write to the appropriate series
      	while(thrptResults.next())
      	{
      		Date inserted = thrptResults.getTimestamp(TIME);
      		String ip = thrptResults.getString(IP);
      		ip = ip.trim();
      		// values in the database are in bytes per interval (normally 1 minute)
    		// bytes * 8 = bits    bits / 1024 = kilobits    kilobits / 60 = kb/s
    		long downloaded = ((thrptResults.getLong(DOWNLOADED) * 8) / 1024) / 60;
    		long uploaded = ((thrptResults.getLong(UPLOADED) * 8) / 1024) / 60;
    		
      		XYSeries downSeries = (XYSeries) IPs.get(ip + "<down>");
      		XYSeries upSeries = (XYSeries) IPs.get(ip + "<up>");
      		
      		downSeries.update(Long.valueOf(inserted.getTime()), Long.valueOf(downloaded));
    		upSeries.update(Long.valueOf(inserted.getTime()), Long.valueOf(0 - uploaded));
      	}
      	thrptResults.close();
      	
      	// Create a "chartable" data container
      	DefaultTableXYDataset dataset = new DefaultTableXYDataset();
      	StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
      	//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
      	
      	ipResults.beforeFirst();
      	int i = 0;
      	// Add each series in order to the container, using first query as iterator
      	while(ipResults.next())
      	{
      		String ip = ipResults.getString(IP);
      		ip = ip.trim();
      		XYSeries downSeries = (XYSeries) IPs.get(ip + "<down>");
      		XYSeries upSeries = (XYSeries) IPs.get(ip + "<up>");
      		dataset.addSeries(downSeries);
      		dataset.addSeries(upSeries);
      		
      		// Use SHA1 hash of IP address to give each series a unique colour
      		try
      		{
      			byte[] ipBytes = ip.getBytes();
      			MessageDigest algorithm = MessageDigest.getInstance("SHA1");
      			algorithm.reset();
      			algorithm.update(ipBytes);
      			byte sha1[] = algorithm.digest();		
      			renderer.setSeriesPaint(i, new Color(sha1[0] & 0xFF, 
      												 sha1[1] & 0xFF, 
      												 sha1[2] & 0xFF));
      			i++;
      			renderer.setSeriesPaint(i, new Color(sha1[0] & 0xFF, 
      												   sha1[1] & 0xFF, 
      												   sha1[2] & 0xFF));
      			i++;
      		}
      		catch(NoSuchAlgorithmException excep)
      		{
      			excep.printStackTrace();
      			throw(excep);
      		}
      	}
      	ipResults.close();
      	
      	// Configure the chart elements and create and return the chart
      	DateAxis xAxis;
      	
      	long timePeriod = (theEnd - theStart)/60000;
      	if(timePeriod < 7)
      	{
      		xAxis = new DateAxis("Time (hours:minutes:seconds)");
      	}
      	else if((timePeriod >= 7) && (timePeriod < 3650))
      	{
      		xAxis = new DateAxis("Time (hours:minutes)");
      	}
      	else if((timePeriod >= 3650) && (timePeriod < 7299))
      	{
      		xAxis = new DateAxis("Time (day-month,hours:minutes)");
      	}
      	else // timePeriod >= 7299
      	{
      		xAxis = new DateAxis("Time (day-month)");
      	}
      
      	xAxis.setMinimumDate(new Date(start));
      	xAxis.setMaximumDate(new Date(end));
      	
  		xAxis.setLowerMargin(-0.01); // Save a little whitespace off
  		xAxis.setUpperMargin(0);
  		NumberAxis yAxis = new NumberAxis("Throughput (kb/s)");
  		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
  		plot.setOrientation(PlotOrientation.VERTICAL);
  		plot.setForegroundAlpha(0.8f);
  		plot.addRangeMarker(new ValueMarker(0));
  		plot.setRenderer(renderer);

  		JFreeChart chart = new JFreeChart("Network Throughput Per IP",
  				JFreeChart.DEFAULT_TITLE_FONT,
  				plot,
  				false);
  		chart.addSubtitle(new TextTitle(new Date(end).toString()));
  		chart.setBackgroundPaint(null);
  		return chart;
    }
}