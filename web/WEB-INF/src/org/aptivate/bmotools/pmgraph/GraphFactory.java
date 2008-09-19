package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
public class GraphFactory {
	
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
							InstantiationException, IOException, SQLException {
    	// Round our times to the nearest minute
    	start = start - (start % 60000);
    	end = end - (end % 60000);
    	
    	// Get database connection and network properties
    	Connection conn = GraphUtilities.getConnection();
    	String localSubnet = GraphUtilities.getProperties();
    	
    	// Initialise the XYSeries with 0 values for each minute
    	XYSeries downSeries = new XYSeries("Downloaded", true, false);
    	XYSeries upSeries = new XYSeries("Uploaded", true, false);
    	int minutes = (int) (end - start) / 60000;
    	for(int i = 0; i < minutes; i++) {
    		downSeries.add(start + i * 60000, 0);
    		upSeries.add(start + i * 60000, 0);
    	}

    	// Prepare and execute the SQL query
    	PreparedStatement statement = 
    			conn.prepareStatement(GraphUtilities.THROUGHPUT_PER_MINUTE);
    	statement.setString(1, localSubnet + "%");
    	statement.setString(2, localSubnet + "%");
    	statement.setString(3, localSubnet + "%");
    	statement.setString(4, localSubnet + "%");
    	statement.setTimestamp(5, new Timestamp(start));
    	statement.setTimestamp(6, new Timestamp(end));
    	System.out.println(statement);
    	ResultSet results = statement.executeQuery();
    	
    	// Step though query results, updating as appropriate
    	results.beforeFirst();
    	while(results.next()) {
    		Date inserted = results.getTimestamp(TIME);
    		// bytes * 8 = bits    bits * 1024 = kilobits    kilobits / 60 = kB/s
    		int downloaded = ((results.getInt(DOWNLOADED) * 8) / 1024) / 60;
    		int uploaded = ((results.getInt(UPLOADED) * 8) / 1024) / 60;
    		downSeries.update(inserted.getTime(), downloaded);
    		upSeries.update(inserted.getTime(), 0 - uploaded);
    	}
    	statement.close();
    	
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
    				throws ClassNotFoundException, IllegalAccessException, 
    						InstantiationException, IOException, SQLException {
    	// Round our times to the nearest minute
    	start = start - (start % 60000);
    	end = end - (end % 60000);
    	
    	// Get database connection and network properties
    	Connection conn = GraphUtilities.getConnection();
    	String localSubnet = GraphUtilities.getProperties();
    	
    	// Prepare and execute the query to find all active IPs on the network
    	PreparedStatement ipStatement = 
    			conn.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP);
    	ipStatement.setString(1, localSubnet + "%");
    	ipStatement.setString(2, localSubnet + "%");
    	ipStatement.setString(3, localSubnet + "%");
    	ipStatement.setString(4, localSubnet + "%");
    	ipStatement.setString(5, localSubnet + "%");
    	ipStatement.setTimestamp(6, new Timestamp(start));
    	ipStatement.setTimestamp(7, new Timestamp(end));
    	System.out.println(ipStatement);
    	ResultSet ipResults = ipStatement.executeQuery();
    	ipResults.beforeFirst();
    	
    	// For each result, initialise XYSeries and store in HashMap
    	HashMap IPs = new HashMap();
    	int minutes = (int) (end - start) / 60000;
    	while(ipResults.next()) {
    		String ip = ipResults.getString(IP);
    		XYSeries downSeries = new XYSeries(ip + "<down>", true, false);
    		XYSeries upSeries = new XYSeries(ip + "<up>", true, false);
        	for(int i = 0; i < minutes; i++) {
        		downSeries.add(start + i * 60000, 0);
        		upSeries.add(start + i * 60000, 0);
        	}   
    		IPs.put(ip + "<down>", downSeries);
    		IPs.put(ip + "<up>", upSeries);
    	}
    	// We don't close the 'active IPs' query just yet...
    	
    	// Prepare and execute network throughput query
    	PreparedStatement thrptStatement = 
    			conn.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP_PER_MINUTE);
    	thrptStatement.setString(1, localSubnet + "%");
    	thrptStatement.setString(2, localSubnet + "%");
    	thrptStatement.setString(3, localSubnet + "%");
    	thrptStatement.setString(4, localSubnet + "%");
    	thrptStatement.setString(5, localSubnet + "%");
    	thrptStatement.setTimestamp(6, new Timestamp(start));
    	thrptStatement.setTimestamp(7, new Timestamp(end));
    	System.out.println(thrptStatement);
    	ResultSet thrptResults = thrptStatement.executeQuery();
    	thrptResults.beforeFirst();
      	
      	// For each query result, get data and write to the appropriate series
      	while(thrptResults.next()) {
      		Date inserted = thrptResults.getTimestamp(TIME);
      		String ip = thrptResults.getString(IP);
    		// bytes * 8 = bits    bits * 1024 = kilobits    kilobits / 60 = kB/s
    		int downloaded = ((thrptResults.getInt(DOWNLOADED) * 8) / 1024) / 60;
    		int uploaded = ((thrptResults.getInt(UPLOADED) * 8) / 1024) / 60;
    		
      		XYSeries downSeries = (XYSeries) IPs.get(ip + "<down>");
      		XYSeries upSeries = (XYSeries) IPs.get(ip + "<up>");
      		
      		downSeries.update(inserted.getTime(), downloaded);
    		upSeries.update(inserted.getTime(), 0 - uploaded);
      	}
      	thrptStatement.close();
      	
      	// Create a "chartable" data container
      	DefaultTableXYDataset dataset = new DefaultTableXYDataset();
      	StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
      	//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
      	
      	ipResults.beforeFirst();
      	int i = 0;
      	// Add each series in order to the container, using first query as iterator
      	while(ipResults.next()) {
      		String ip = ipResults.getString(IP);
      		XYSeries downSeries = (XYSeries) IPs.get(ip + "<down>");
      		XYSeries upSeries = (XYSeries) IPs.get(ip + "<up>");
      		dataset.addSeries(downSeries);
      		dataset.addSeries(upSeries);
      		
      		// Use SHA1 hash of IP address to give each series a unique colour
      		try {
      			byte[] ipBytes = ip.getBytes();
      			MessageDigest algorithm = MessageDigest.getInstance("SHA1");
      			algorithm.reset();
      			algorithm.update(ipBytes);
      			byte sha1[] = algorithm.digest();		
      			renderer.setSeriesPaint(i, new Color(sha1[0] & 0xFF, 
      												 sha1[1] & 0xFF, 
      												 sha1[2] & 0xFF));
      			renderer.setSeriesPaint(i+1, new Color(sha1[0] & 0xFF, 
      												   sha1[1] & 0xFF, 
      												   sha1[2] & 0xFF));
      		}
      		catch(NoSuchAlgorithmException excep) {
      			excep.printStackTrace();
      		}
      		i++;i++;
      	}
      	ipStatement.close();
      	
      	// Configure the chart elements and create and return the chart
      	DateAxis xAxis = new DateAxis("Time (minutes)");
  		xAxis.setLowerMargin(-0.01); // Shave a little whitespace off
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