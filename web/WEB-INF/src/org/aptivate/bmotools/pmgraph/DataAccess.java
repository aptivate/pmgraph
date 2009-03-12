package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
/**
 * 

 *
 */
public class DataAccess {
	private String m_localSubnet;							// used in the list of the DB data
	private Connection m_conn;
	// MySQL table fields
	private final String DOWNLOADED = "downloaded";
	private final String IP = "local_ip";
	private final String UPLOADED = "uploaded";

	public DataAccess() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		
		this.m_localSubnet = Configuration.getLocalSubnet();
		m_conn = getConnection();
	}

	private  Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException {
		
		Class.forName(Configuration.getJdbcDriver()).newInstance();
		Connection con = DriverManager.getConnection(Configuration.getDatabaseURL(), 
				Configuration.getDatabaseUser(),Configuration.getDatabasePass());
		return con;
	}
	

	public ArrayList getTotalThroughput(long start, long end)
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
		System.out.println(statement);
		ResultSet results = statement.executeQuery();		
		while(results.next()) 
    	{
			resultData.add (new GraphData(results));
    		
    	}
		statement.close();

		return resultData;
	}

	public  ArrayList getThroughputPIPPMinute(long start, long end)
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
		System.out.println(thrptStatement);
		ResultSet thrptResults = thrptStatement.executeQuery();
		while(thrptResults.next()) 
    	{
			resultData.add (new GraphData(thrptResults));
    		
    	}
		thrptStatement.close();

		return resultData;

	}

	public  ArrayList getThroughputPerIP(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException {

		String throughput_per_ip = getSQLThroughputPerIP("", "");
		return getResultPerIP(start, end, throughput_per_ip);

	}

	public ArrayList getThroughputPerIP(long start, long end, String sortby,
			String order) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException {

		String throughput_per_ip = getSQLThroughputPerIP(sortby, order);
		return getResultPerIP(start, end, throughput_per_ip);
	}

	private String getSQLThroughputPerIP(String sortBy, String order) {
		String sortByTmp = " ORDER BY bytes_total";
		String orderTmp = " DESC;";
		if (!sortBy.isEmpty())
			sortByTmp = " ORDER BY " + sortBy;
		if (!order.isEmpty())
			orderTmp = " " + order + ";";

		String throughput_per_ip = GraphUtilities.THROUGHPUT_PER_IP;
		int lastC = throughput_per_ip.indexOf(";");
		throughput_per_ip = throughput_per_ip.substring(0, lastC);
		throughput_per_ip = throughput_per_ip + sortByTmp + orderTmp;
		return throughput_per_ip;
	}
	

	private  ArrayList getResultPerIP(long start, long end,
			String THROUGHPUT_PER_IP) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		ArrayList<GraphData> resultData= new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				THROUGHPUT_PER_IP, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ipStatement.setString(1, m_localSubnet + "%");
		ipStatement.setString(2, m_localSubnet + "%");
		ipStatement.setString(3, m_localSubnet + "%");
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, m_localSubnet + "%");
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, m_localSubnet + "%");
		ipStatement.setTimestamp(8, new Timestamp(start));
		ipStatement.setTimestamp(9, new Timestamp(end));
		System.out.println(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while(ipResults.next()) 
    	{
			resultData.add (new GraphData(ipResults.getString(IP),
					ipResults.getLong(DOWNLOADED),
					ipResults.getLong(UPLOADED)));
    		
    	}
		ipStatement.close();
		return resultData;
	}
	
	

}
