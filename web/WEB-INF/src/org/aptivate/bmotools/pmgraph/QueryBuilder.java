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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

/**
 * This class is used to connect to / disconnect from the database and build the
 * sql select query from the request parameters
 * 
 * @author noeg
 * 
 */
public class QueryBuilder
{
	private Logger m_logger = Logger.getLogger(QueryBuilder.class.getName());

	//private String m_localSubnet; // used in the list of the DB data
	
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
	
	private StringBuffer m_sql;
	
	private StringBuffer m_query;

	private String [] m_localSubnets;
	
	private boolean m_firstCondition; /* used by buildAnd() */
	
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
	 * 
	 */
	private Connection getConnection() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException, ConfigurationException
	{
		try
		{
			System.setProperty("sqlite.purejava", "true");
			Class.forName(Configuration.getJdbcDriver()).newInstance();
			Connection con = DriverManager.getConnection(Configuration.getDatabaseURL(),
					Configuration.getDatabaseUser(), Configuration.getDatabasePass());
			return con;
		} catch (CommunicationsException e)
		{
			Throwable cause = e.getCause();
			// find out what caused the error
			if ((cause instanceof AccessControlException) || (cause instanceof SecurityException)
					|| (cause instanceof SocketException))
			{
				m_logger
						.fatal("Unable to get a mysql connection due to a Java security Exception: ");
				m_logger.fatal("Java Security Exception: " + cause.getLocalizedMessage());
				m_logger
						.fatal("If you have java security enabled please add a exception in the policy "
								+ "file to allow this web application to connect to the mysql port. ");
				throw (new ConfigurationException(
						ErrorMessages.MYSQL_CONNECTION_ERROR_JAVA_SECURITY, cause));
			} else
			{
				m_logger.fatal("Unable to get a mysql connection, "
						+ "please check your database.properties file: " + e.getLocalizedMessage());

				throw (new ConfigurationException(
						"Unable to get a mysql connection, please check your database.properties file:",
						e));
			}
		} catch (SQLException e)
		{
			m_logger.fatal("Unable to get a mysql connection,"
					+ " please check your database.properties file: " + e.getLocalizedMessage());

			throw (new ConfigurationException(ErrorMessages.MYSQL_CONNECTION_ERROR, e));
		}

	}

	/**
	 * Constructor - sets the Subnet, connection and query parameter list for
	 * this object.
	 * 
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Configuration Exception
	 */
	public QueryBuilder() throws IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, ConfigurationException {
		m_conn = getConnection();
		m_listData = new ArrayList<Object>();
		m_localSubnets = Configuration.getLocalSubnet();
	}

	private void buildSelect(RequestParams requestParams, boolean isChart)
	{
		// time stamp is not needed in legend
		if (isChart)
		{
			m_sql.append(TIME_STAMP + ", ");
		}

		m_sql.append("SUM(CASE WHEN down THEN bytes ELSE 0 END) AS downloaded, ");
		m_sql.append("SUM(CASE WHEN up THEN bytes ELSE 0 END) AS uploaded, ");

		switch (requestParams.getView())
		{
		case LOCAL_PORT:
			m_sql.append("local_port, ip_proto"); // id of the used protocol udp/tcp/icmp
			break;
		case LOCAL_IP:
			m_sql.append("local_ip");
			break;
		case REMOTE_PORT:
			m_sql.append("remote_port, ip_proto"); // id of the used protocol udp/tcp/icmp
			break;
		case REMOTE_IP: // Show Remote IP
			m_sql.append("remote_ip");
			break;
		}
		m_sql.append(" FROM (");
		m_sql.append("SELECT stamp_inserted, "+
				"CASE WHEN up THEN ip_dst ELSE ip_src END AS remote_ip, "+
				"CASE WHEN down THEN ip_dst ELSE ip_src END AS local_ip, "+
				"CASE WHEN up THEN dst_port ELSE src_port END AS remote_port, "+
				"CASE WHEN down THEN dst_port ELSE src_port END AS local_port, "+
				"bytes, up, down");
		buildIpProto(requestParams);
		m_sql.append(" FROM (");
		m_sql.append("SELECT stamp_inserted, ip_src, ip_dst, src_port, dst_port, bytes, ");
		IsLocal("ip_src");
		m_sql.append(" AS up, ");
		IsLocal("ip_dst");
		m_sql.append(" AS down");
		buildIpProto(requestParams);
		m_sql.append(" ");
	}
	
	private void buildIpProto(RequestParams requestParams)
	{
		if(requestParams.getView().toString().toLowerCase().contains("port"))
		{
			m_sql.append(", ip_proto");
		}
	}

	private void IsLocal(String field)
	{
		m_sql.append("(");
		boolean firstTime=true;
		for(String localSubnet: m_localSubnets)
		{
			if(!firstTime)
				m_sql.append(" OR ");
			firstTime=false;
			m_sql.append(field);
			m_sql.append(" LIKE ?");
			m_listData.add(localSubnet + "%");
		}
		m_sql.append(")");
	}
	
	private String buildFrom(RequestParams requestParams, boolean isLong) throws IOException
	{
		StringBuilder sql = new StringBuilder();
		if(isLong)
		{
			sql.append("FROM " + Configuration.findTable(requestParams.getEndTime() - requestParams.getStartTime()) + " ");
		}
		else
		{
			sql.append("FROM " + Configuration.getResultDatabaseTable() + " ");
		}
		return sql.toString();
	}
	
	private String buildWhereTime(RequestParams requestParams, boolean isLong) throws IOException
	{
		String sql;
		if(Configuration.getJdbcDriver().equals("org.sqlite.JDBC"))
		{
			sql= "WHERE datetime(stamp_inserted) >= ? AND datetime(stamp_inserted) <= ? ";
		}
		else
		{
			sql= "WHERE stamp_inserted >= ? AND stamp_inserted <= ? ";
		}
		long resolution = Configuration.getResolution(isLong, requestParams.getEndTime() - requestParams.getStartTime());
		m_listData.add(new Timestamp(requestParams.getRoundedStartTime(resolution)));
		m_listData.add(new Timestamp(requestParams.getRoundedEndTime(resolution)));
		return sql;
	}
	
	private void buildAnd()
	{
		if(!m_firstCondition)
		{
			m_sql.append("AND ");
		}
		else
		{
			m_sql.append("WHERE ");
			m_firstCondition = false;
		}
	}
	
	private void buildWhere(RequestParams requestParams, boolean isLong) throws IOException
	{
		m_sql.append(buildWhereTime(requestParams, isLong));
		m_sql.append(") AS t1 WHERE up != down) AS t2 ");
		m_firstCondition=true;
		if (requestParams.getIp() != null)
		{ // for a specific local IP
			buildAnd();
			m_sql.append("local_ip = ? ");
			m_listData.add(requestParams.getIp());
		}
		if (requestParams.getPort() != null)
		{ // for a specific local Port
			buildAnd();
			m_sql.append("local_port = ? ");
			m_listData.add(requestParams.getPort());
		}
		if (requestParams.getRemoteIp() != null)
		{ // for an specific local IP
			buildAnd();
			m_sql.append("remote_ip = ? ");
			m_listData.add(requestParams.getRemoteIp());
		}
		if (requestParams.getRemotePort() != null)
		{ // for a specific local Port
			buildAnd();
			m_sql.append("remote_port = ? ");
			m_listData.add(requestParams.getRemotePort());
		}
		String subnet = requestParams.getSelectSubnetIndex(); 
		if(subnet != null)
		{
			buildAnd();
			m_sql.append("local_ip LIKE ? ");
			m_listData.add(subnet);
		}
		String group = requestParams.getSelectGroupIndex(); 
		if(group != null)
		{
			List<String> ips = Configuration.getIpsGroup(group);
			if(ips.size() > 0)
			{
				buildAnd();
				m_sql.append("(");
			}
			boolean firstTime = true;
			for(String ip : ips)
			{
				if(!firstTime)
					m_sql.append(" OR ");
				firstTime=false;
				m_sql.append("local_ip = ?");
				m_listData.add(ip);
			}
			if(ips.size() > 0)
			{
				m_sql.append(") ");
			}
		}
	}

	private String buildGroupBy(RequestParams requestParams, boolean perMinute)
	{
		StringBuffer groupBy = new StringBuffer();

		if (perMinute)
		{
			groupBy.append(" stamp_inserted,");
		}

		switch (requestParams.getView())
		{
		case LOCAL_PORT:
			groupBy.append(" local_port, ip_proto");
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
			return "GROUP BY" + groupBy.toString();
		}
		return "";
	}

	/**
	 * Build the sql query from its component parts
	 * 
	 * @param requestParams
	 * @param isChart
	 * @param isLong
	 * @return PreparedStatement: an object that represents a precompiled SQL
	 *         statement.
	 * @throws SQLException
	 * @throws IOException
	 */
	PreparedStatement buildQuery(RequestParams requestParams, boolean isChart, boolean isLong) throws SQLException,
			IOException
	{
		m_sql = new StringBuffer("SELECT ");
		buildSelect(requestParams, isChart);
		m_sql.append(buildFrom(requestParams, isLong));
		buildWhere(requestParams, isLong);
		m_sql.append(buildGroupBy(requestParams, isChart));
	
		m_query = new StringBuffer(m_sql);
		PreparedStatement ipStatement = m_conn.prepareStatement(m_sql.toString(),
				ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		// set the query parameters depending on the query type
		setQueryParams(ipStatement);

		return ipStatement;
	}
	
	/**
	 * Build the sql query, to get all DataBase's content,
	 * from its component parts
	 * 
	 * @param requestParams
	 * @param isChart
	 * @param isLong
	 * @return PreparedStatement: an object that represents a precompiled SQL
	 *         statement.
	 * @throws SQLException
	 * @throws IOException
	 */
	PreparedStatement buildQueryAll(RequestParams requestParams, boolean isChart, boolean isLong) throws SQLException,
			IOException
	{								
		StringBuffer sql = new StringBuffer();
		boolean firstUnion = true;
		for(String column : new String[]{"ip_src", "ip_dst"})
		{
			if(!firstUnion)
				sql.append("UNION ");
			firstUnion=false;
			sql.append("SELECT DISTINCT "+column+" AS ip_local ");
			sql.append(buildFrom(requestParams, isLong));
			sql.append(buildWhereTime(requestParams, isLong));
			sql.append("AND ");
			boolean firstTime=true;
			for (String localSubnet: m_localSubnets)
			{
				if(!firstTime)
					sql.append("OR ");
				firstTime = false;
				sql.append(column+" LIKE ? ");
				m_listData.add(localSubnet+"%");
			}
		}
		m_query = new StringBuffer(sql.toString());
		PreparedStatement ipStatement = m_conn.prepareStatement(sql.toString(),
				ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY,
				ResultSet.CLOSE_CURSORS_AT_COMMIT);
		setQueryParams(ipStatement);
		return (ipStatement);
	}
	
	/**
	 * Get the contents of a query (for testing and debugging purposes).
	 * 
	 * @return String: a string representing the query
	 */
	public String getQuery()
	{
		return m_query.toString();
	}
	
	// The list data is used to store parameters for the SQL statement. They are
	// substituted for the "?" within the pre-compiled SQL statement. Search for
	// "statement.setObject" for more info.
	private void setQueryParams(PreparedStatement statement) throws SQLException, IOException
	{
		String value;
		int parameter_index = 1;
		for (Object parameter_value : m_listData)
		{
			if(parameter_value.getClass() == Timestamp.class)
			{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(!Configuration.getJdbcDriver().equals("org.sqlite.JDBC"))
				{
					value = format.format((Timestamp)parameter_value).toString();
				}
				else
				{
					parameter_value = format.format((Timestamp)parameter_value).toString();
					value = parameter_value.toString();
				}
			}
			else
			{
				value = parameter_value.toString();
			}
			if(parameter_value.getClass() == Integer.class)
			{
				m_query.replace(m_query.indexOf("?"), m_query.indexOf("?") + 1, value);
			}
			else
			{
				m_query.replace(m_query.indexOf("?"), m_query.indexOf("?") + 1, "\'" + value + "\'");
			}
			statement.setObject(parameter_index, parameter_value);
			parameter_index++;
		}
		m_listData.clear();
	}

	/**
	 * Release the connection from a query
	 */
	void releaseConnection()
	{
		try
		{
			m_conn.close();
		} catch (SQLException e)
		{
			m_logger.error("Error freeing connection in finalize method", e);
		}
	}
}
