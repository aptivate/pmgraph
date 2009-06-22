package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.SocketException;
import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class QueryBuilder
{
	private Logger m_logger = Logger.getLogger(QueryBuilder.class.getName());

	private String m_localSubnet; // used in the list of the DB data

	private Connection m_conn;

	// MySQL table fields
	static final String DOWNLOADED = "downloaded";

	static final String IP = "local_ip";

	static final String UPLOADED = "uploaded";

	static final String BYTES = "bytes_total";

	static final String TIME_STAMP = "stamp_inserted";

	static final String PORT = "port";

	static final String REMOTE_IP = "remote_ip";

	private List<Object> m_listData;

	/**
	 * Just get a connection to the database using the Configuration Class to
	 * obtain the values of the connection string.
	 * 
	 * @return java.sql.Connection to database
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ConfigurationException
	 *             TODO
	 */
	private Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException, ConfigurationException
	{
		try
		{
			Class.forName(Configuration.getJdbcDriver()).newInstance();
			Connection con = DriverManager.getConnection(Configuration
					.getDatabaseURL(), Configuration.getDatabaseUser(),
					Configuration.getDatabasePass());
			return con;
			// CommunicationsException
		}
		catch (CommunicationsException e)
		{
			Throwable cause = e.getCause();
			// detect if the error is because of
			if ((cause instanceof AccessControlException)
					|| (cause instanceof SecurityException)
					|| (cause instanceof SocketException))
			{
				m_logger
						.fatal("Unable to get a mysql connection due to a Java security Exception: ");
				m_logger.fatal("Java Security Exception: "
						+ cause.getLocalizedMessage());
				m_logger
						.fatal("If you have java security enabled please add a exception in the policy "
								+ "file to allow this web application to connect to the mysql port. ");
				throw (new ConfigurationException(
						ErrorMessages.MYSQL_CONNECTION_ERROR_JAVA_SECURITY, cause));
			}
			else
			{
				m_logger.fatal("Unable to get a mysql connection, " +
						"please check your database.properties file: "
						+ e.getLocalizedMessage());
				
				throw (new ConfigurationException(
						"Unable to get a mysql connection, please check your database.properties file:", e));
			}
		}
		catch (SQLException e)
		{
			m_logger.fatal("Unable to get a mysql connection," +
				" please check your database.properties file: "
				+ e.getLocalizedMessage());
			
			throw (new ConfigurationException(
					ErrorMessages.MYSQL_CONNECTION_ERROR, e));
		}

	}

	public QueryBuilder() throws IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			ConfigurationException
	{

		this.m_localSubnet = Configuration.getLocalSubnet();
		m_conn = getConnection();
		m_listData = new ArrayList<Object>();
	}

	private String buildSelect(RequestParams requestParams, boolean perMinute)
	{
		StringBuffer sql = new StringBuffer();

		if (perMinute)
			sql.append(" " + TIME_STAMP + ", ");

		sql.append("SUM(CASE WHEN ip_dst LIKE ? " +
			"THEN bytes ELSE 0 END) as downloaded, ");
		
		m_listData.add(m_localSubnet + "%");
		sql.append("SUM(CASE WHEN ip_src LIKE ? " +
			"THEN bytes ELSE 0 END) as uploaded, ");
		m_listData.add(m_localSubnet + "%");
		
		switch (requestParams.getView())
		{
			case LOCAL_PORT:
				sql.append("ip_proto, ");				// id of the used protocol udp/tcp/icmp
				sql.append("(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port ");
				m_listData.add(m_localSubnet + "%");
				break;
			case LOCAL_IP:
				sql.append("(CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip ");
				m_listData.add(m_localSubnet + "%");
				break;
			case REMOTE_PORT:
				sql.append("ip_proto, ");				// id of the used protocol udp/tcp/icmp
				sql.append("(CASE WHEN ip_src LIKE ? THEN dst_port ELSE src_port END) AS remote_port ");
				m_listData.add(m_localSubnet + "%");
				break;
			case REMOTE_IP: // Show Remote IP
				sql.append("(CASE WHEN ip_src LIKE ? THEN ip_dst ELSE ip_src END) AS remote_ip ");
				m_listData.add(m_localSubnet + "%");
				break;

		}
		return (sql.toString());
	}

	private String buildWhere(RequestParams requestParams)
	{
		StringBuffer where = new StringBuffer();
		String comparator = " LIKE ";
		String ip = m_localSubnet + "%";

		where.append("WHERE stamp_inserted >= ? " + "AND stamp_inserted <= ? ");
		m_listData.add(new Timestamp(requestParams.getRoundedStartTime()));
		m_listData.add(new Timestamp(requestParams.getRoundedEndTime()));
		if (requestParams.getIp() != null)
		{ // for a specific local IP
			comparator = " = ";
			ip = requestParams.getIp();
			where
					.append("AND (CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) = ? ");
			m_listData.add(m_localSubnet + "%");
			m_listData.add(requestParams.getIp());
		}
		if (requestParams.getPort() != null)
		{ // for a specific local Port
			where.append("AND (CASE WHEN ip_src " + comparator
					+ " ? THEN src_port ELSE dst_port END) = ? ");
			m_listData.add(ip);
			m_listData.add(requestParams.getPort());
		}
		if (requestParams.getRemoteIp() != null)
		{ // for an specific local IP
			where.append("AND (CASE WHEN ip_src LIKE ? THEN ip_dst ELSE ip_src END) = ? ");
			m_listData.add(m_localSubnet + "%");
			m_listData.add(requestParams.getRemoteIp());
		}
		if (requestParams.getRemotePort() != null)
		{ // for a specific local Port
			where.append("AND (CASE WHEN ip_src " + comparator
					+ " ? THEN dst_port ELSE src_port END) = ? ");
			m_listData.add(ip);
			m_listData.add(requestParams.getRemotePort());
		}
		where.append("AND ((NOT (ip_src LIKE ?) AND ip_dst " + comparator
				+ " ?) OR (NOT (ip_dst LIKE ?) AND ip_src " + comparator
				+ " ?)) ");
		m_listData.add(m_localSubnet + "%");
		m_listData.add(ip);
		m_listData.add(m_localSubnet + "%");
		m_listData.add(ip);
		return (where.toString());
	}

	private String buildGroupBy(RequestParams requestParams, boolean perMinute)
	{
		StringBuffer groupBy = new StringBuffer();

		if (perMinute)
		{
			groupBy.append(" stamp_inserted, ");
		}

		switch (requestParams.getView())
		{
			case LOCAL_PORT:
				groupBy.append(" port, ip_proto");
				break;
			case LOCAL_IP:
				groupBy.append(" local_ip");
				break;
			case REMOTE_PORT:
				groupBy.append(" remote_port, ip_proto");
				break;
			case REMOTE_IP:
				groupBy.append(" remote_ip");
				break;

			default:
		}

		if (groupBy.length() > 0)
		{
			return " group by " + groupBy.toString();
		}
		return "";
	}
	/**
	 * TODO
	 * @param requestParams
	 * @param perMinute
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	PreparedStatement buildQuery(RequestParams requestParams, boolean perMinute)
			throws SQLException, IOException
	{

		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(buildSelect(requestParams, perMinute));
		sql.append("FROM "+Configuration.getResultDatabaseTable()+" ");
		sql.append(buildWhere(requestParams));
		sql.append(buildGroupBy(requestParams, perMinute));

		PreparedStatement ipStatement = m_conn.prepareStatement(sql.toString(),
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		// set the query parameters depending on the query type
		setQueryParams(ipStatement);
		return (ipStatement);
	}

	private void setQueryParams(PreparedStatement statement)
			throws SQLException
	{

		int i = 1;
		for (Object d1 : m_listData)
		{
			statement.setObject(i, d1);
			i++;
		}
		m_listData.clear();
	}

	void releaseConnection()
	{
		try
		{
			m_conn.close();
		}
		catch (SQLException e)
		{
			m_logger.error("Error freeing connection in finalize method", e);
		}
	}

}
