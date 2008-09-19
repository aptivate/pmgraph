package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * The GraphBuilder class provides methods which return JFreeChart objects
 * (which can then be served as web content or saved as images) representing 
 * network traffic logged by pmacct on the BMO Box.
 * 
 * THIS CLASS IS THE RESULT OF EARLY PROTOTYPING AND IS INCLUDED TO PROVIDE
 * CODE EXAMPLES TO ASSIST WITH THE DEVELOPMENT OF PMGRAPH. METHODS OF THIS
 * CLASS SHOULD NOT BE CALLED OR COPIED VERBATIM
 * 
 * @author Thomas Sharp
 * @version 0.1
 */
public class GraphBuilder {

	// The connection to the MySQL database
	private Connection conn;
	
	// SQL query strings
	// Lists throughput put for IPs during last ? minutes
	private static final String THROUGHPUT_PER_IP =
		"SELECT ip_dst, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY ip_dst " +
		"ORDER BY total_bytes DESC;";
	
	// Lists throughput for top 10 IPs per minute for the last ? minutes
	private static final String THROUGHPUT_PER_IP_PER_MINUTE = 
		"SELECT stamp_inserted, CASE ip_dst WHEN ? THEN ip_dst " + // Case 1
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " +
										   "WHEN ? THEN ip_dst " + // Case 10
										   "ELSE \"Other\" END AS ip, " +
										   			"SUM(bytes) as total_bytes " +
		"FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY stamp_inserted, ip;";

	// Lists total throughput for the last ? minutes
	private static final String THROUGHPUT_PER_MINUTE = 
		"SELECT stamp_inserted, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY stamp_inserted;";
	
	// Lists total upstream throughput for the last ? minutes
	private static final String THROUGHPUT_PER_MINUTE_UP =
		"SELECT stamp_inserted, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_src REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY stamp_inserted;";
	
	// Lists throughput per port in in the last ? minutes
	private static final String THROUGHPUT_PER_PORT =
		"SELECT src_port, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY src_port " +
		"ORDER BY total_bytes DESC;";
	
	// Lists throughput per port per minute in the last ? minutes
	private static final String THROUGHPUT_PER_PORT_PER_MINUTE =
		"SELECT stamp_inserted, src_port, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY stamp_inserted, src_port;";
	
	// Lists throughput per protocol in the last ? minutes
	private static final String THROUGHPUT_PER_PROTOCOL = 
		"SELECT ip_proto, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY ip_proto " +
		"ORDER BY total_bytes DESC;";
	
	// Lists throughput per protocol per minute in the last ? minutes
	private static final String THROUGHPUT_PER_PROTOCOL_PER_MINUTE =
		"SELECT stamp_inserted, ip_proto, SUM(bytes) as total_bytes FROM acct_v6 " +
		"WHERE ip_dst REGEXP '^192.168.1.' AND " +
			"DATE_SUB(?, INTERVAL ? MINUTE) < stamp_inserted  AND " +
			"stamp_inserted < ? " +
		"GROUP BY stamp_inserted, ip_proto;";
	
	// Key SQL table fields
	private static final String BYTES = "total_bytes";
	private static final String IP = "ip";
	private static final String IP_DST = "ip_dst";
	private static final String PORT = "src_port";
	private static final String PROTO = "ip_proto";
	private static final String TIME = "stamp_inserted";
	
	// No. of IPs to see detailed stats. for
	private static final int LIMIT = 10;
	
	//TODO: Automatic colour wheel
	Color[] colours = {
			new Color(255,0,0), new Color(255,127,0), new Color(255,255,0), 
			new Color(127,255,0), new Color(0,255,0), new Color(0,255,127), 
			new Color(0,255,255), new Color(0,127,255), new Color(0,0,255),
			new Color(127,0,255), new Color(255,0,255), new Color(255,0,127)};
	
	/**
	 * Establishes a connection to the MySQL database at the given URL.
	 * 
	 * @param url						address of database (likely localhost:port)
	 * @param userName					MySQL DB user name
	 * @param password					MySQL DB password
	 * @throws ClassNotFoundException	if driver error
	 * @throws IllegalAccessException	if driver error
	 * @throws InstantiationException	if driver error
	 * @throws SQLException				if connection fails (maybe bad password)
	 */
    private GraphBuilder(String url, String userName, String password)
    					throws ClassNotFoundException, IllegalAccessException, 
    									  InstantiationException, SQLException {
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(url, userName, password);
    }
    
    /**
     * Returns a JFreeChart XYAreaChart showing the total throughput between 
     * <code>now</code> and <code>now - period minutes</code>.
     * 
     * @param now			end of query period
     * @param period		<code>now - period</code> = beginning of query period
     * @param max			maximum expected throughput
     * @return				chart showing traffic over <code>period</code> mins
     * @throws SQLException	in event of database error
     */
    private JFreeChart totalThroughput(Date now, int period, int max) throws SQLException {
    	/* First initialises a XYSeries to show 0 throughput for each minute to 
    	 * be graphed, then queries the MySQL database for total network 
    	 * throughput per minute and steps through the query results 
    	 * updating the dataset for the the minutes where throughput was greater 
    	 * than 0 (initialisation is necessary for neat graphing). Finally, the 
    	 * series is used to create the JFreeChart which is returned. */
    	
    	PreparedStatement statement;
    	ResultSet results;

    	// Initialise an XYSeries with 0 values for each minute of the query
		XYSeries series = new XYSeries("Throughput", true, false);
		long firstMinute = now.getTime() - period * 60000;
		for(int i = 0; i < period; i++) {
			series.add(firstMinute + i * 60000, 0);
		}
    	
		// Prepare and execute the SQL query
    	statement = conn.prepareStatement(THROUGHPUT_PER_MINUTE);
    	statement.setTimestamp(1, new Timestamp(now.getTime()));
    	statement.setInt(2, period + 1);
    	statement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(statement);
		results = statement.executeQuery();
		
		// Step though query results, updating as appropriate
		results.beforeFirst();
		while(results.next()) {
			Date inserted = results.getTimestamp(TIME);
			// Get dif. in mins between time record inserted and time now...
			long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			// ... and use it as a negative index to the series (timeDif is negative)
			long index = period + timeDif;
			series.updateByIndex((int) index, (results.getInt(BYTES) / 1024) /60);
		}
		statement.close();
		
		// Put our data into a "chartable" container
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		dataset.addSeries(series);
		
		// Configure the chart elements and create and return the chart
		DateAxis xAxis = new DateAxis("Time (minutes)");
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Throughput (kB/s)");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.addRangeMarker(new ValueMarker(max));
        plot.setForegroundAlpha(0.8f);
        plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA));
        
        JFreeChart chart = new JFreeChart("Total Network Throughput", 
        								  JFreeChart.DEFAULT_TITLE_FONT, 
        								  plot, 
        								  false);
        chart.addSubtitle(new TextTitle(now.toString()));
        return chart;
    }

    /**
     * Returns a JFreeChart StackedXYAreaChart showing the total throughput per
     *  IP between <code>now</code> and <code>now - period minutes</code>.
     * 
     * @param now			end of query period
     * @param period		<code>now - period</code> = beginning of query period
     * @param max			maximum expected throughput
     * @return				chart showing traffic over <code>period</code> mins
     * @throws SQLException	in event of database error
     */
    private JFreeChart stackedThroughput(Date now, int period, int max, 
    										boolean hybrid) throws SQLException {
    	/* Firstly, we find the top 10 active IPs on the network and create and
    	 * initialise (as in totalThrougput(...)) an XYSeries for each, and one
    	 * XYSeries for all of the rest. Then we query the database for 
    	 * network traffic data for each minute. For each result returned, we 
    	 * update the appropriate index in the appropriate XYSeries. We then add 
    	 * all of the series into a table dataset (adding the biggest downloader 
    	 * to the dataset first so it shows on the bottom of the graph stack) 
    	 * and use this to create an area graph. */
    	
    	// SQL query facilitators 
    	PreparedStatement ipStatement;
    	PreparedStatement thrptStatement;
    	ResultSet ipResults;
    	ResultSet thrptResults;
    	
    	// Prepare and execute the query to find all active IPs on the network
    	ipStatement = conn.prepareStatement(THROUGHPUT_PER_IP);
    	ipStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	ipStatement.setInt(2, period + 1);
    	ipStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(ipStatement);
    	ipResults = ipStatement.executeQuery();
    	ipResults.beforeFirst();
    	    	
    	thrptStatement = conn.prepareStatement(THROUGHPUT_PER_IP_PER_MINUTE);
    	int i = 1;
    	// For each of top 10 IPs initialise an XYSeries and store in a HashMap
    	HashMap IPs = new HashMap();
    	long firstMinute = now.getTime() - period * 60000;
    	while(ipResults.next() && i <= LIMIT) {
    		String ip = ipResults.getString(IP_DST);
    		XYSeries series = new XYSeries(ip, true, false);
    		for(int j = 0; j < period; j++) {
    			series.add(firstMinute + j * 60000, 0);
    		}
    		IPs.put(ip, series);
    		thrptStatement.setString(i, ip);
    		i++;
    	}
    	// Make XYSeries for the other IPs and store it
    	XYSeries others = new XYSeries("Other", true, false);
    	for(int j = 0; j < period; j++) {
			others.add(firstMinute + j * 60000, 0);
		}
    	IPs.put("Other", others);
    	// We don't close the 'active IPs' query just yet...
    	
    	// Prepare and execute network throughput query
    	thrptStatement.setTimestamp(i, new Timestamp(now.getTime()));
    	thrptStatement.setInt(i + 1, period + 1);
    	thrptStatement.setTimestamp(i + 2, new Timestamp(now.getTime()));
    	System.out.println(thrptStatement);
    	thrptResults = thrptStatement.executeQuery();
    	thrptResults.beforeFirst();
    	
    	// For each query result...
    	while(thrptResults.next()) {
    		// ... get the details ...
    		Date inserted = thrptResults.getTimestamp(TIME);
    		String ip = thrptResults.getString(IP);
    		// ... get the appropriate XYSeries ...
    		XYSeries series = (XYSeries) IPs.get(ip);

    		long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			long index = period + timeDif;
			// ... update the appropriate series entry
			series.updateByIndex((int) index, (thrptResults.getInt(BYTES) / 1024) /60);
    	}
    	thrptStatement.close();
    	
    	// Create a "chartable" data container..
    	DefaultTableXYDataset dataset = new DefaultTableXYDataset();
    	StackedXYAreaRenderer2 rend = new StackedXYAreaRenderer2();
    	ipResults.beforeFirst();
    	i = 0;
    	/* ... and go back over our first query, using the results to pull our
    	   XYSeries objects out of the HashMap and add them to the dataset */
    	while(ipResults.next() && i < LIMIT) {
    		String ip = ipResults.getString(IP_DST);
    		XYSeries series = (XYSeries) IPs.get(ip);
    		dataset.addSeries(series);
    		rend.setSeriesPaint(i, colours[i]);
    		i++;
    	}
    	// Add the "Other" series
    	XYSeries series = (XYSeries) IPs.get("Other");
		dataset.addSeries(series);
		rend.setSeriesPaint(i, colours[i]);
    	ipStatement.close();
    	
    	// Configure the chart elements and create and return the chart
    	DateAxis xAxis = new DateAxis("Time (minutes)");
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Throughput (kB/s)");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.addRangeMarker(new ValueMarker(max));
        plot.setForegroundAlpha(0.8f);
        // If chart is to be background to another, make area paint translucent
        if(hybrid) {
        	plot.setForegroundAlpha(0.4f);
        }
        plot.setRenderer(rend);
        
        JFreeChart chart = new JFreeChart("Network Throughput Per IP",
        								  JFreeChart.DEFAULT_TITLE_FONT,
        								  plot,
        								  true);
        chart.addSubtitle(new TextTitle(now.toString()));
        return chart;
    }
    
    /**
     * Returns a JFreeChart PieChart showing the total throughput per
     * IP between <code>now</code> and <code>now - period minutes</code>.
     * 
     * @param now			end of query period
     * @param period		<code>now - period</code> = beginning of query period
     * @param subChart		will this chart be superimposed upon another?
     * @return				chart showing traffic during <code>period</code> mins
     * @throws SQLException in event of database error
     */
    private JFreeChart pieThroughput(Date now, int period, 
    										boolean subChart) throws SQLException {
    	PreparedStatement statement;
    	ResultSet results;
    	
    	// Get the total throughput per IP
    	statement = conn.prepareStatement(THROUGHPUT_PER_IP);
    	statement.setTimestamp(1, new Timestamp(now.getTime()));
    	statement.setInt(2, period + 1);
    	statement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(statement);
    	results = statement.executeQuery();
    	results.beforeFirst();
    	// Create "chartable" data container and add query results
    	DefaultPieDataset dataset = new DefaultPieDataset();
    	PiePlot plot = new PiePlot(dataset);
    	int i = 0;
    	int others = 0;
    	while(results.next() ) {
    		String ip = results.getString(IP_DST);
    		int throughput = results.getInt(BYTES) / 1048576; // Give in mB
    		if(i < LIMIT) {
    			// Add 'biggest' users to pie chart
    			dataset.setValue(ip, throughput);
    			plot.setSectionPaint(ip, colours[i]);
    			i++;
    		}
    		else {
    			// Tally the total throughput of the rest
    			others += throughput;
    		}
    	}
    	dataset.setValue("Other", others);
    	plot.setSectionPaint("Other", colours[i]);
    	
    	// Configure chart elements and create chart
    	plot.setNoDataMessage("No traffic data to show");
    	plot.setSectionOutlinesVisible(false);
    	plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{1}mB")); 	
    	plot.setSimpleLabels(true);
    	plot.setShadowPaint(null);
               
        JFreeChart chart = new JFreeChart("",
        					  			  JFreeChart.DEFAULT_TITLE_FONT, 
        					  			  plot, 
        					  			  false);
        
        // If chart is to be superimposed upon another, strip borders & background
        if(subChart) {
            plot.setBackgroundPaint(null);
            plot.setOutlinePaint(null);
            plot.setForegroundAlpha(0.9f);
            chart.setBackgroundPaint(null);
            //chart.setTitle("Totals");
        }
        else {
        	chart.setTitle("Network Throughput Per IP");
        	chart.addSubtitle(new TextTitle(now.toString()));
        }
        return chart;
    }
    
    /**
     * This method is a derivative of stackedThroughput(...)
     *  
     * @param now
     * @param period
     * @param max
     * @return
     * @throws SQLException
     */
    private JFreeChart protocolThroughput(Date now, int period, int max) 
    													throws SQLException {
    	
    	// SQL query facilitators 
    	PreparedStatement protoStatement;
    	PreparedStatement thrptStatement;
    	ResultSet protoResults;
    	ResultSet thrptResults;
    	
    	//TODO We could save on a query here if we known what protos we want
    	// Prepare and execute the query to find protocols in use
    	protoStatement = conn.prepareStatement(THROUGHPUT_PER_PROTOCOL);
    	protoStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	protoStatement.setInt(2, period + 1);
    	protoStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(protoStatement);
    	protoResults = protoStatement.executeQuery();
    	protoResults.beforeFirst();
    	
    	// For each protocol initialise an XYSeries and store in a HashMap
    	HashMap protocols = new HashMap();
    	long firstMinute = now.getTime() - period * 60000;
    	while(protoResults.next()) {
    		String proto = protoResults.getString(PROTO);
    		XYSeries series = new XYSeries(proto, true, false);
    		for(int j = 0; j < period; j++) {
    			series.add(firstMinute + j * 60000, 0);
    		}
    		protocols.put(proto, series);
    	}
    	// We don't close the query just yet...

    	// Prepare and execute network throughput query
    	thrptStatement = conn.prepareStatement(THROUGHPUT_PER_PROTOCOL_PER_MINUTE);
    	thrptStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	thrptStatement.setInt(2, period + 1);
    	thrptStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(thrptStatement);
    	thrptResults = thrptStatement.executeQuery();
    	thrptResults.beforeFirst();

    	// For each query result...
    	while(thrptResults.next()) {
    		// ... get the details ...
    		Date inserted = thrptResults.getTimestamp(TIME);
    		String proto = thrptResults.getString(PROTO);
    		// ... get the appropriate XYSeries ...
    		XYSeries series = (XYSeries) protocols.get(proto);

    		long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			long index = period + timeDif;
			// ... update the appropriate series entry
			series.updateByIndex((int) index, (thrptResults.getInt(BYTES) / 1024) /60);
    	}
    	thrptStatement.close();
    	
    	// Create a "chartable" data container..
    	DefaultTableXYDataset dataset = new DefaultTableXYDataset();
    	StackedXYAreaRenderer2 rend = new StackedXYAreaRenderer2();
    	protoResults.beforeFirst();
    	int i = 0;
    	/* ... and go back over our first query, using the results to pull our
    	   XYSeries objects out of the HashMap and add them to the dataset */
    	while(protoResults.next()) {
    		String proto = protoResults.getString(PROTO);
    		XYSeries series = (XYSeries) protocols.get(proto);
    		dataset.addSeries(series);
    		rend.setSeriesPaint(i, colours[i]);
    		i++;
    	}
    	protoStatement.close();
    	
    	// Configure the chart elements and create and return the chart
    	DateAxis xAxis = new DateAxis("Time (minutes)");
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Throughput (kB/s)");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.addRangeMarker(new ValueMarker(max));
        plot.setForegroundAlpha(0.8f);
        plot.setRenderer(rend);
        
        JFreeChart chart = new JFreeChart("Network Throughput Per Protocol",
        								  JFreeChart.DEFAULT_TITLE_FONT,
        								  plot,
        								  true);
        chart.addSubtitle(new TextTitle(now.toString()));
        return chart;
    }
    
    /**
     * This method is a derivative of stackedThroughput(...)
     * 
     * @param now
     * @param period
     * @param max
     * @return
     * @throws SQLException
     */
    private JFreeChart portThroughput(Date now, int period, int max) 
    													throws SQLException {
    	// SQL query facilitators 
    	PreparedStatement portStatement;
    	PreparedStatement thrptStatement;
    	ResultSet portResults;
    	ResultSet thrptResults;
    	
    	//TODO We could save on a query here if we known what ports we want
    	portStatement = conn.prepareStatement(THROUGHPUT_PER_PORT);
    	portStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	portStatement.setInt(2, period + 1);
    	portStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(portStatement);
    	portResults = portStatement.executeQuery();
    	portResults.beforeFirst();
    	
    	HashMap ports = new HashMap();
    	long firstMinute = now.getTime() - period * 60000;
    	while(portResults.next()) {
    		String port = portResults.getString(PORT);
    		XYSeries series = new XYSeries(port, true, false);
    		for(int j = 0; j < period; j++) {
    			series.add(firstMinute + j * 60000, 0);
    		}
    		ports.put(port, series);
    	}
    	// We don't close the query just yet...

    	// Prepare and execute network throughput query
    	thrptStatement = conn.prepareStatement(THROUGHPUT_PER_PORT_PER_MINUTE);
    	thrptStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	thrptStatement.setInt(2, period + 1);
    	thrptStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(thrptStatement);
    	thrptResults = thrptStatement.executeQuery();
    	thrptResults.beforeFirst();

    	// For each query result...
    	while(thrptResults.next()) {
    		// ... get the details ...
    		Date inserted = thrptResults.getTimestamp(TIME);
    		String port = thrptResults.getString(PORT);
    		// ... get the appropriate XYSeries ...
    		XYSeries series = (XYSeries) ports.get(port);

    		long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			long index = period + timeDif;
			// ... update the appropriate series entry
			series.updateByIndex((int) index, (thrptResults.getInt(BYTES) / 1024) /60);
    	}
    	thrptStatement.close();
    	
    	// Create a "chartable" data container..
    	DefaultTableXYDataset dataset = new DefaultTableXYDataset();
    	StackedXYAreaRenderer2 rend = new StackedXYAreaRenderer2();
    	portResults.beforeFirst();
    	int i = 0;
    	/* ... and go back over our first query, using the results to pull our
    	   XYSeries objects out of the HashMap and add them to the dataset */
    	while(portResults.next()) {
    		String port = portResults.getString(PORT);
    		XYSeries series = (XYSeries) ports.get(port);
    		dataset.addSeries(series);
    		rend.setSeriesPaint(i, colours[i]);
    		i++;
    	}
    	portStatement.close();
    	
    	// Configure the chart elements and create and return the chart
    	DateAxis xAxis = new DateAxis("Time (minutes)");
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Throughput (kB/s)");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.addRangeMarker(new ValueMarker(max));
        plot.setForegroundAlpha(0.8f);
        plot.setRenderer(rend);
        
        JFreeChart chart = new JFreeChart("Network Throughput Per portcol",
        								  JFreeChart.DEFAULT_TITLE_FONT,
        								  plot,
        								  true);
        chart.addSubtitle(new TextTitle(now.toString()));
        return chart;    	
    }
    
    /**
     * This method is a derivative of totalThroughput(...)
     * @param now
     * @param period
     * @param maxDown
     * @param maxUp
     * @return
     * @throws SQLException
     */
    /*TODO: This method unnecessarily duplicates code but since the class is about
            to be refactored, we aren't too worried. */
    private JFreeChart totalDownUp(Date now, int period, int maxDown, int maxUp) throws SQLException {
    	PreparedStatement downStatement;
    	ResultSet downResults;

    	// Initialise an XYSeries with 0 values for each minute of the query
		XYSeries downSeries = new XYSeries("Throughput", true, false);
		long firstMinute = now.getTime() - period * 60000;
		for(int i = 0; i < period; i++) {
			downSeries.add(firstMinute + i * 60000, 0);
		}
    	
		// Prepare and execute the SQL query
    	downStatement = conn.prepareStatement(THROUGHPUT_PER_MINUTE);
    	downStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	downStatement.setInt(2, period + 1);
    	downStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(downStatement);
		downResults = downStatement.executeQuery();
		
		// Step though query results, updating as appropriate
		downResults.beforeFirst();
		while(downResults.next()) {
			Date inserted = downResults.getTimestamp(TIME);
			// Get dif. in mins between time record inserted and time now...
			long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			// ... and use it as a negative index to the series (timeDif is negative)
			long index = period + timeDif;
			downSeries.updateByIndex((int) index, (downResults.getInt(BYTES) / 1024) /60);
		}
		downStatement.close();
		
    	PreparedStatement upStatement;
    	ResultSet upResults;

    	// Initialise an XYSeries with 0 values for each minute of the query
		XYSeries upSeries = new XYSeries("Throughput", true, false);
		firstMinute = now.getTime() - period * 60000;
		for(int i = 0; i < period; i++) {
			upSeries.add(firstMinute + i * 60000, 0);
		}
    	
		// Prepare and execute the SQL query
    	upStatement = conn.prepareStatement(THROUGHPUT_PER_MINUTE_UP);
    	upStatement.setTimestamp(1, new Timestamp(now.getTime()));
    	upStatement.setInt(2, period + 1);
    	upStatement.setTimestamp(3, new Timestamp(now.getTime()));
    	System.out.println(upStatement);
		upResults = upStatement.executeQuery();
		
		// Step though query results, updating as appropriate
		upResults.beforeFirst();
		while(upResults.next()) {
			Date inserted = upResults.getTimestamp(TIME);
			// Get dif. in mins between time record inserted and time now...
			long timeDif = ((inserted.getTime() - now.getTime()) / 1000) / 60;
			// ... and use it as a negative index to the series (timeDif is negative)
			long index = period + timeDif;
			upSeries.updateByIndex((int) index, 0 - ((upResults.getInt(BYTES) / 1024) /60));
		}
		upStatement.close();
		
		// Put our data into a "chartable" container
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		dataset.addSeries(downSeries);
		dataset.addSeries(upSeries);
		
		// Configure the chart elements and create and return the chart
		DateAxis xAxis = new DateAxis("Time (minutes)");
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Throughput (kB/s)");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.addRangeMarker(new ValueMarker(maxDown));
        plot.addRangeMarker(new ValueMarker(0 - maxUp));
        plot.addRangeMarker(new ValueMarker(0));
        plot.setForegroundAlpha(0.8f);
        plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA));
        plot.getRenderer().setSeriesPaint(0, Color.blue);
        plot.getRenderer().setSeriesPaint(1, Color.blue);
        
        JFreeChart chart = new JFreeChart("Total Network Throughput", 
        								  JFreeChart.DEFAULT_TITLE_FONT, 
        								  plot, 
        								  false);
        chart.addSubtitle(new TextTitle(now.toString()));
        return chart;
    }
}