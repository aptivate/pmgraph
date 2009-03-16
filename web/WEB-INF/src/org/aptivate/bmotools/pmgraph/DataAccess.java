package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * @author Tom Sharp
 * 
 *	
 * 	
 * 	History:
 * 
 * 	11-03-2009	Modified by Noe A. Rodriguez Glez. 
 * 	
 * 		Removed static methods in order to make the class threadsave.
 * 		Use of a Configuration class which contains all configuration 
 * 		data.
 * 		Add coments.
 * 		Log4java logging
		Avoided Order By querys in order to reduce execution time, now
		all sortening is doing using Collection.sort.
 *
 */
public class DataAccess {
	private String m_localSubnet;							// used in the list of the DB data
	private Connection m_conn;
	// MySQL table fields
	private final String DOWNLOADED = "downloaded";
	private final String IP = "local_ip";
	private final String UPLOADED = "uploaded";
	private final String BYTES = "bytes_total";
	private static Logger m_logger = Logger.getLogger(DataAccess.class.getName());

	
	private class UploadComparator  implements Comparator {

		private boolean m_descending;
		
		public UploadComparator (boolean descending) {
		
				m_descending= descending;
		}
		
		
	  public int compare(Object o1, Object o2) {
		  
	    GraphData d1 = (GraphData) o1;
	    GraphData d2 = (GraphData) o2;
	    if (m_descending)
	    	return (0 - d1.getUploaded().compareTo(d2.getUploaded()));
	    else
	    	return (d1.getUploaded().compareTo(d2.getUploaded())); 	
	  }
	  

	  public boolean equals(Object o) {
	    return this == o;
	  }
	}

	private class DownloadComparator implements Comparator  {

		private boolean m_descending;
		
		public DownloadComparator (boolean descending) {
		
				m_descending= descending;
		}
		
		
	  public int compare(Object o1, Object o2) {
		  
	    GraphData d1 = (GraphData) o1;
	    GraphData d2 = (GraphData) o2;
	    if (m_descending)
	    	return (0 - d1.getDownloaded().compareTo(d2.getDownloaded()));
	    else
	    	return (d1.getDownloaded().compareTo(d2.getDownloaded()));    	
	  }
	  

	  public boolean equals(Object o) {
	    return this == o;
	  }
	}
	

	private class IpComparator implements Comparator {

		private boolean m_descending;
		
		public IpComparator (boolean descending) {
		
				m_descending= descending;
		}
		
		
	  public int compare(Object o1, Object o2) {
		  
	    GraphData d1 = (GraphData) o1;
	    GraphData d2 = (GraphData) o2;
	    if (m_descending)
	    	return (0 - d1.getLocalIp().compareTo(d2.getLocalIp()));
	    else
	    	return (d1.getLocalIp().compareTo(d2.getLocalIp()));    	
	  }
	  

	  public boolean equals(Object o) {
	    return this == o;
	  }
	}
	
	
	/**
	 * 	Retun an appropiate comparator for de requested sorting
	 * @param sortby
	 * @param order
	 * @return
	 */	
	private Comparator getComparator (String sortby, String order) {
		
		if (!"".equalsIgnoreCase(sortby)) {
			if (UPLOADED.equalsIgnoreCase(sortby)) {
				if ("DESC".equalsIgnoreCase(order))
					return(new UploadComparator(true));
				else
					return(new UploadComparator(false));
			} else {
				if (BYTES.equalsIgnoreCase(sortby)) {
					if ("DESC".equalsIgnoreCase(order))
						return(new BytesTotalComparator(true));
					else
						return(new BytesTotalComparator(false));
				} else {			
					if (DOWNLOADED.equalsIgnoreCase(sortby)) {
						if ("DESC".equalsIgnoreCase(order))
							return(new DownloadComparator(true));
						else
							return(new DownloadComparator(false));
					}
				}
			}
		}	
		return null;
	}
	
	/**
	 * Create the connection object and set the m_localSubnet to the subnet 
	 * contained in the config file
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public DataAccess() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		
		this.m_localSubnet = Configuration.getLocalSubnet();
		m_conn = getConnection();
	}

	/**
	 *  Just get a conection to the database using the Configuration
	 *  Class to obtain the values of the conection string.
	 *  
	 * @return java.sql.Connection to database
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private  Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException {
		
		Class.forName(Configuration.getJdbcDriver()).newInstance();
		Connection con = DriverManager.getConnection(Configuration.getDatabaseURL(), 
				Configuration.getDatabaseUser(),Configuration.getDatabasePass());
		return con;
	}
	

	/**
	 *  Get a list of GraphData containing the total THROUGHPUT  
	 *  for the IP's which match with the m_localSubnet 
	 *  atribute and are between stard and end times.
	 *  
	 * @param start Time in seconds since epoch 
	 * @param end  Time in seconds since epoch 
	 * @return  A List od GrapData
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * 
	 */
	public List<GraphData> getTotalThroughput(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException {
		ArrayList<GraphData> resultData= new ArrayList<GraphData>();
		
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		// Prepare and execute the SQL query
		PreparedStatement statement = m_conn
				.prepareStatement(GraphUtilities.THROUGHPUT_PER_MINUTE);
		statement.setString(1, m_localSubnet + "%");
		statement.setString(2, m_localSubnet + "%");
		statement.setString(3, m_localSubnet + "%");
		statement.setString(4, m_localSubnet + "%");
		statement.setString(5, m_localSubnet + "%");
		statement.setString(6, m_localSubnet + "%");
		statement.setTimestamp(7, new Timestamp(start));
		statement.setTimestamp(8, new Timestamp(end));
		m_logger.debug(statement);
		ResultSet results = statement.executeQuery();		
		while(results.next()) 
    	{
			resultData.add (new GraphData(results));
    		
    	}
		statement.close();

		return resultData;
	}

	/**
	 * 	Get a list of GraphData containing the THROUGHPUT in
	 * 	each minute for the IP's which match with the m_localSubnet 
	 *  atribute and are between stard and end times.
	 *  
	 * @param start Time in seconds since epoch 
	 * @param end  Time in seconds since epoch 
	 * @return A List of GraphData 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public  List<GraphData> getThroughputPIPPMinute(long start, long end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		ArrayList<GraphData> resultData= new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		// Prepare and execute network throughput query

		PreparedStatement thrptStatement = m_conn
				.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP_PER_MINUTE);
		thrptStatement.setString(1, m_localSubnet + "%");
		thrptStatement.setString(2, m_localSubnet + "%");
		thrptStatement.setString(3, m_localSubnet + "%");
		thrptStatement.setString(4, m_localSubnet + "%");
		thrptStatement.setString(5, m_localSubnet + "%");
		thrptStatement.setString(6, m_localSubnet + "%");
		thrptStatement.setString(7, m_localSubnet + "%");
		thrptStatement.setTimestamp(8, new Timestamp(start));
		thrptStatement.setTimestamp(9, new Timestamp(end));
		m_logger.debug(thrptStatement);
		ResultSet thrptResults = thrptStatement.executeQuery();
		while(thrptResults.next()) 
    	{
			resultData.add (new GraphData(thrptResults));
    		
    	}
		thrptStatement.close();

		return resultData;

	}

	public  List<GraphData> getThroughputPerIP(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException {

		return getResultPerIP(start, end, GraphUtilities.THROUGHPUT_PER_IP);

	}

	public List<GraphData> getThroughputPerIP(long start, long end, String sortby,
			String order) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException {
		List<GraphData> result;

		
		result = getResultPerIP(start, end, GraphUtilities.THROUGHPUT_PER_IP);
		Comparator comparator = getComparator(sortby, order);
		if (comparator != null)
			Collections.sort(result,comparator);
	
		return result;
	}

		
	/**
	 * 	Get a list of GraphData containing the THROUGHPUT 
	 *  for the IP's which match with the m_localSubnet 
	 *  atribute and are between stard and end times.
	 * 
	 * @param start Time in seconds since epoch 
	 * @param end  Time in seconds since epoch 
	 * @param throughputPerIp SQL query 
	 * @return A List of GraphData
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private  List<GraphData> getResultPerIP(long start, long end,
			String throughputPerIp) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		ArrayList<GraphData> resultData= new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				throughputPerIp, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ipStatement.setString(1, m_localSubnet + "%");
		ipStatement.setString(2, m_localSubnet + "%");
		ipStatement.setString(3, m_localSubnet + "%");
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, m_localSubnet + "%");
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, m_localSubnet + "%");
		ipStatement.setTimestamp(8, new Timestamp(start));
		ipStatement.setTimestamp(9, new Timestamp(end));
		m_logger.debug(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while(ipResults.next()) 
    	{
			resultData.add (new GraphData(ipResults.getString(IP),
					ipResults.getLong(DOWNLOADED),
					ipResults.getLong(UPLOADED),ipResults.getLong(BYTES)));
    		
    	}
		ipStatement.close();
		return resultData;
	}
	
	

}
