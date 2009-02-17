package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class GraphUtilities {
	
	// SQL query strings
	// Lists total throughput
	public static final String THROUGHPUT_PER_MINUTE =
	    "SELECT stamp_inserted, " +
	    	   "SUM(IF(ip_dst LIKE ?, bytes, 0)) AS downloaded, " +
	    	   "SUM(IF(ip_src LIKE ?, bytes, 0)) AS uploaded " +
	     "FROM acct_v6 " + 
	    "WHERE (ip_src LIKE ? XOR ip_dst LIKE ?) AND " +
	          "stamp_inserted >= ? AND " +
	          "stamp_inserted <= ? " +
	    "GROUP BY stamp_inserted;";
	
	//changed sort
	// Lists throughput per IP
	public static final String THROUGHPUT_PER_IP =
		"SELECT IF(ip_src LIKE ?, ip_src, ip_dst) AS local_ip, " +
		       "SUM(IF(ip_dst LIKE ?, bytes, 0)) AS downloaded, " +
		       "SUM(if(ip_src LIKE ?, bytes, 0)) AS uploaded, " +
		       "SUM(bytes) AS bytes_total " +
		       "FROM acct_v6 " + 
		"WHERE (ip_src LIKE ? XOR ip_dst LIKE ?) AND " +
	          "stamp_inserted >= ? AND " + 
	          "stamp_inserted <= ? " +
		"GROUP BY local_ip " +
		"ORDER BY ?; ";
	
	// Lists throughput per IP per minute
	public static final String THROUGHPUT_PER_IP_PER_MINUTE =
		"SELECT stamp_inserted, " +
		       "IF(ip_src LIKE ?, ip_src, ip_dst) AS local_ip, " +
		       "SUM(IF(ip_dst LIKE ?, bytes, 0)) as downloaded, " +
		       "SUM(IF(ip_src LIKE ?, bytes, 0)) as uploaded " +
		       "FROM acct_v6 " + 
		       "WHERE (ip_src LIKE ? XOR ip_dst LIKE ?) AND " +
		      "stamp_inserted >= ? AND " + 
		      "stamp_inserted <= ? " +
		"GROUP BY stamp_inserted, local_ip;";
	
	public static Connection getConnection()
	throws ClassNotFoundException, IllegalAccessException, InstantiationException, 
		   IOException, SQLException 
	{
		Properties properties = new Properties();
		InputStream stream = GraphFactory.class.
		getResourceAsStream("/database.properties");
		properties.load(stream);
		stream.close();

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		return DriverManager.getConnection(properties.getProperty("DatabaseURL"), 
		 	                               properties.getProperty("DatabaseUser"), 
		 	                               properties.getProperty("DatabasePass"));
	}

	public static String getProperties()
	throws ClassNotFoundException, IllegalAccessException, InstantiationException, 
		   IOException, SQLException
	{
		Properties properties = new Properties();
		InputStream stream = GraphFactory.class.
		getResourceAsStream("/database.properties");
		properties.load(stream);
		stream.close();

		return properties.getProperty("LocalSubnet");
	}
}