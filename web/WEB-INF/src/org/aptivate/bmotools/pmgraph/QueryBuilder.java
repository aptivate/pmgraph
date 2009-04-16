package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

public class QueryBuilder
{
	private Logger m_logger = Logger.getLogger(QueryBuilder.class
			.getName());

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

	
	public QueryBuilder () throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		this.m_localSubnet = Configuration.getLocalSubnet();
		m_conn = getConnection();
	}
	
	
	private String buildSelect(RequestParams requestParams, List<Object> data, boolean perMinute) {
		StringBuffer sql = new StringBuffer();		 
		
		sql.append(" "+TIME_STAMP+", ");
		sql.append("SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, ");
		data.add (m_localSubnet + "%");
		sql.append("SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded, ");
		data.add (m_localSubnet + "%");
		
		switch (requestParams.getView()) {

			case LOCAL_PORT:
			case LOCAL_IP:
				sql.append(" (CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, ");		
				data.add (m_localSubnet + "%");
				sql.append("(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port ");
				data.add (m_localSubnet + "%");
				break;
			case REMOTE_PORT:	
				sql.append("(CASE WHEN ip_src LIKE ? THEN dst_port ELSE src_port END) AS remote_port ");
				data.add (m_localSubnet + "%");
				break;			
			case REMOTE_IP:		// Show Remote IP
				sql.append("(CASE WHEN ip_src LIKE ? THEN ip_dst ELSE ip_src END) AS remote_ip ");
				data.add (m_localSubnet + "%");
				break;

		}
		return (sql.toString());
	}
	
	private String buildWhere(RequestParams requestParams, List <Object> data) {
		StringBuffer where = new StringBuffer();
		String comparator = " LIKE ";
		String ip = m_localSubnet + "%";
		

		where.append("WHERE stamp_inserted >= ? " + "AND stamp_inserted <= ? ");	
		data.add (new Timestamp (requestParams.getRoundedStartTime()));
		data.add (new Timestamp (requestParams.getRoundedEndTime()));
		if (requestParams.getIp() != null) {	// for an specific local IP
			comparator = " = ";
			ip = requestParams.getIp();
			where.append(" AND (CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) = ? ");
			data.add (m_localSubnet + "%");
			data.add (requestParams.getIp());
		}
		if (requestParams.getPort() != null) {	// for an specific local Port
			where.append(" AND (CASE WHEN ip_src "+comparator+" ? THEN src_port ELSE dst_port END) = ? ");
			data.add (ip);
			data.add (requestParams.getPort());
		}
		if (requestParams.getRemoteIp() != null) {	// for an specific local IP
			where.append(" AND (CASE WHEN ip_src LIKE ? THEN ip_dst ELSE ip_src END) = ? ");
			data.add (m_localSubnet + "%");
			data.add (requestParams.getRemoteIp());
		}
		if (requestParams.getRemotePort() != null) {	// for an specific local Port
			where.append(" AND (CASE WHEN ip_src "+comparator+" ? THEN dst_port ELSE src_port END) = ? ");
			data.add (ip);
			data.add (requestParams.getRemotePort());
		}
		where.append(" AND ((NOT (ip_src LIKE ?) AND ip_dst "+comparator+" ?) OR (NOT (ip_dst LIKE ?) AND ip_src "+comparator+" ?)) ");
		data.add (m_localSubnet + "%");
		data.add (ip);
		data.add (m_localSubnet + "%");
		data.add (ip);
		return (where.toString());
	}
	
	private String buildGroupBy (RequestParams requestParams, List <Object> data, boolean perMinute) {
		StringBuffer groupBy = new StringBuffer();
		
		if (perMinute) {
			groupBy.append(" stamp_inserted, ");		
		}
		
		switch (requestParams.getView()) {
			case LOCAL_PORT:				
				groupBy.append(" port");
				break;			
			case LOCAL_IP:
				groupBy.append(" local_ip");
				break;
			case REMOTE_PORT:	
				groupBy.append(" remote_port");
				break;			
			case REMOTE_IP:	
				groupBy.append(" remote_ip");
				break;
				
			default:
		}
		
		if (groupBy.length() > 0) {
			return  " group by "+groupBy.toString();
		}
		return "";
	}
	
	PreparedStatement buildQuery (RequestParams requestParams, List<Object> data,  boolean perMinute) throws SQLException {
		
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append (buildSelect(requestParams, data,  perMinute));		
		sql.append ("FROM acct_v6 ");
		sql.append (buildWhere(requestParams,data));
		sql.append (buildGroupBy(requestParams, data, perMinute));
		
		PreparedStatement ipStatement = m_conn.prepareStatement(sql.toString(),
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		// set the query parameters depending of the query type
		setQueryParams (ipStatement, data);		
		return (ipStatement);
	}

	void setQueryParams (PreparedStatement statement, List<Object> data) throws SQLException {

		int i= 1;
		for (Object d1 : data) {
			statement.setObject (i,d1);
			i++;
		}
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
