package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Tom Sharp
 * 
 * History:
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
public class DataAccess
{
	private String m_localSubnet; // used in the list of the DB data

	private Connection m_conn;

	// MySQL table fields
	final String DOWNLOADED = "downloaded";

	final String IP = "local_ip";

	final String UPLOADED = "uploaded";

	final String BYTES = "bytes_total";

	final String TIME_STAMP = "stamp_inserted";

	final String PORT = "port";

	private static Logger m_logger = Logger.getLogger(DataAccess.class
			.getName());

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
	public DataAccess() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{

		this.m_localSubnet = Configuration.getLocalSubnet();
		m_conn = getConnection();
	}

	/**
	 * Just get a conection to the database using the Configuration Class to
	 * obtain the values of the conection string.
	 * 
	 * @return java.sql.Connection to database
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{

		Class.forName(Configuration.getJdbcDriver()).newInstance();
		Connection con = DriverManager.getConnection(Configuration
				.getDatabaseURL(), Configuration.getDatabaseUser(),
				Configuration.getDatabasePass());
		return con;
	}

	/**
	 * Get a list of GraphData containing the total THROUGHPUT for the IP's
	 * which match with the m_localSubnet atribute and are between stard and end
	 * times.
	 * 
	 * @param start
	 *            Time in seconds since epoch
	 * @param end
	 *            Time in seconds since epoch
	 * @return A List od GrapData
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
			InstantiationException, IOException, SQLException
	{
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();

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
		while (results.next())
		{
			resultData.add(new GraphData(results));

		}
		statement.close();

		return resultData;
	}

	/**
	 * Get a list of GraphData containing the THROUGHPUT in each minute for the
	 * IP's which match with the m_localSubnet atribute and are between stard
	 * and end times.
	 * 
	 * @param start
	 *            Time in seconds since epoch
	 * @param end
	 *            Time in seconds since epoch
	 * @return A List of GraphData
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputPIPPMinute(long start, long end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException
	{
		long initTime = System.currentTimeMillis();
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
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
		while (thrptResults.next())
		{
			resultData.add(new GraphData(thrptResults));
		}
		thrptStatement.close();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " miliseg");
		return resultData;
	}

	public List<GraphData> getThroughputPerIP(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException
	{

		return getResultPerIP(start, end, GraphUtilities.THROUGHPUT_PER_IP);

	}

	/**
	 * Get a list of GraphData containing the THROUGHPUT for the IP's which
	 * match with the m_localSubnet atribute and are between stard and end
	 * times.
	 * 
	 * @param start
	 *            Time in seconds since epoch
	 * @param end
	 *            Time in seconds since epoch
	 * @param throughputPerIp
	 *            SQL query
	 * @return A List of GraphData
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private List<GraphData> getResultPerIP(long start, long end,
			String throughputPerIp) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException
	{
		
		long initTime = System.currentTimeMillis();
		
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				throughputPerIp, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
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
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults.getString(IP), ipResults
					.getLong(DOWNLOADED), ipResults.getLong(UPLOADED),
					ipResults.getLong(BYTES)));

		}
		ipResults.close();
		ipStatement.close();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " miliseg");		
		return resultData;
	}

	/**
	 * Return the Throughput of one specific IP estratified by ports
	 * 
	 * @param start
	 * @param end
	 * @param ip
	 * @return The Throughput of one specific IP estratified by ports
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputPIPPMinuteOneIpPerPort(long start,
			long end, String ip) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException
	{

		long initTime = System.currentTimeMillis();
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_ONE_IP_PER_PORT_PER_MINUTE,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		ipStatement.setString(1, ip);
		ipStatement.setString(2, ip);
		ipStatement.setString(3, ip);
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, ip);
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, ip);
		ipStatement.setTimestamp(8, new Timestamp(start));
		ipStatement.setTimestamp(9, new Timestamp(end));
		m_logger.debug(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults.getTimestamp(TIME_STAMP),
					ip, ipResults.getLong(DOWNLOADED), ipResults
							.getLong(UPLOADED), ipResults.getLong(BYTES),
					ipResults.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " miliseg");	
		return resultData;
	}

	/**
	 * Throughput per one Ip desglosed by ports. To be used in the legend generation.
	 * @param start 
	 * @param end
	 * @param ip
	 * @return Throughput per one Ip desglosed by ports.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputOneIpPerPort(long start, long end,
			String ip) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException
	{

		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_ONE_IP_PER_PORT,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		ipStatement.setString(1, ip);
		ipStatement.setString(2, ip);
		ipStatement.setString(3, ip);
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, ip);
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, ip);
		ipStatement.setTimestamp(8, new Timestamp(start));
		ipStatement.setTimestamp(9, new Timestamp(end));
		m_logger.debug(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while (ipResults.next())
		{
			resultData.add(new GraphData(ip, ipResults.getLong(DOWNLOADED),
					ipResults.getLong(UPLOADED), ipResults.getLong(BYTES),
					ipResults.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		return resultData;
	}

	/**
	 * Throughput per each port in each minute.
	 * 
	 * @param start
	 * @param end
	 * @return hroughput per each port in each minute.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputPerPortPerMinute(long start, long end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException
	{

		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		long initTime = System.currentTimeMillis();
		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_PER_PORT_PER_MINUTE,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

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
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults.getTimestamp(TIME_STAMP),
					ipResults.getLong(DOWNLOADED), ipResults.getLong(UPLOADED),
					ipResults.getLong(BYTES), ipResults.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " miliseg");	
		return resultData;
	}

	/**
	 * Throughput per each port, used in the Leged generation
	 * 
	 * @param start
	 * @param end
	 * @return Throughput per each port
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputPerPort(long start, long end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException
	{

		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_PER_PORT,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

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
		while (ipResults.next())
		{
			resultData.add(new GraphData((Timestamp) null, ipResults
					.getLong(DOWNLOADED), ipResults.getLong(UPLOADED),
					ipResults.getLong(BYTES), ipResults.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		return resultData;
	}

	/**
	 * Throughput for an specific port by each IP per minute
	 * 
	 * @param start
	 * @param end
	 * @param port
	 * @return Throughput for an specific port by each IP per minute
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputPIPPMinuteOnePortPerIp(long start,
			long end, Integer port) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException
	{

		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_ONE_PORT_PER_MINUTE_PER_IP,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		ipStatement.setString(1, m_localSubnet + "%");
		ipStatement.setString(2, m_localSubnet + "%");
		ipStatement.setString(3, m_localSubnet + "%");
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, m_localSubnet + "%");
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, m_localSubnet + "%");
		ipStatement.setString(8, m_localSubnet + "%");
		ipStatement.setTimestamp(9, new Timestamp(start));
		ipStatement.setTimestamp(10, new Timestamp(end));
		ipStatement.setInt(11, port);
		m_logger.debug(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults.getTimestamp(TIME_STAMP),
					ipResults.getString(IP), ipResults.getLong(DOWNLOADED),
					ipResults.getLong(UPLOADED), ipResults.getLong(BYTES),
					ipResults.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		return resultData;
	}

	/**
	 * Throughput per one port desglosed by Ips To be used in Legend generation.
	 * @param start
	 * @param end
	 * @param port
	 * @return  Throughput per one port desglosed by Ips 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<GraphData> getThroughputOnePort(long start, long end,
			Integer port) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException
	{

		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = m_conn.prepareStatement(
				GraphUtilities.THROUGHPUT_ONE_PORT,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		ipStatement.setString(1, m_localSubnet + "%");
		ipStatement.setString(2, m_localSubnet + "%");
		ipStatement.setString(3, m_localSubnet + "%");
		ipStatement.setString(4, m_localSubnet + "%");
		ipStatement.setString(5, m_localSubnet + "%");
		ipStatement.setString(6, m_localSubnet + "%");
		ipStatement.setString(7, m_localSubnet + "%");
		ipStatement.setString(8, m_localSubnet + "%");
		ipStatement.setTimestamp(9, new Timestamp(start));
		ipStatement.setTimestamp(10, new Timestamp(end));
		ipStatement.setInt(11, port);
		m_logger.debug(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		while (ipResults.next())
		{
			resultData.add(new GraphData((Timestamp) null, ipResults
					.getString(IP), ipResults.getLong(DOWNLOADED), ipResults
					.getLong(UPLOADED), ipResults.getLong(BYTES), ipResults
					.getInt("PORT")));

		}
		ipResults.close();
		ipStatement.close();
		return resultData;
	}

	protected void finalize()
	{
		try
		{
			m_conn.close();
		}
		catch (SQLException e)
		{
			m_logger.error("Error freing connection in finalize method", e);
		}
	}

}
