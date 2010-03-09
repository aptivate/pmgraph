package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.log4j.Logger;

/**
 * This class defines some general methods (insert new rows in the database,
 * establish the connection...) that are used in different tests
 * 
 * @author noeg
 * 
 */
class TestUtils
{

	// The connection to the MySQL database
	private Connection m_conn;

	private static Logger m_logger = Logger.getLogger(TestUtils.class.getName());

	final Timestamp t1 = new Timestamp(60000);

	final Timestamp t2 = new Timestamp(120000);

	final Timestamp t3 = new Timestamp(180000);

	final Timestamp t4 = new Timestamp(240000);
	
	// Long time stamps are one day apart
	final Timestamp lt1 = new Timestamp(1 * 24 * 60 * 60 * 1000);
	
	final Timestamp lt2 = new Timestamp(2 * 24 * 60 * 60 * 1000);
	
	final Timestamp lt3 = new Timestamp(3 * 24 * 60 * 60 * 1000);
	
	final Timestamp lt4 = new Timestamp(4 * 24 * 60 * 60 * 1000);
	
	// Very long time stamps are one week apart
	final Timestamp vlt1 = new Timestamp(1 * 7 * 24 * 60 * 60 * 1000);
	
	final Timestamp vlt2 = new Timestamp(2 * 7 * 24 * 60 * 60 * 1000);
	
	final Timestamp vlt3 = new Timestamp(3 * 7 * 24 * 60 * 60 * 1000);
	
	final Timestamp vlt4 = new Timestamp(4L * 7L * 24L * 60L * 60L * 1000L);

	private static final String TABLE_NAME = "acct_v6";
	
	private static final String LONG_TABLE_NAME = "acct_v6_long";
	
	private static final String VERY_LONG_TABLE_NAME = "acct_v6_very_long";

	// MySQL table fields
	private static final String IP_SRC = "ip_src";

	private static final String IP_DEST = "ip_dst";

	private static final String BYTES = "bytes";

	private static final String TIME = "stamp_inserted";

	public static final String LEGEND_TBL = "legend_tbl";

	private String m_urlPmgraph;

	// SQL query strings

	/* Create the database table */
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + BYTES
			+ " bigint NOT NULL, " + TIME + " timestamp NOT NULL, " + IP_SRC
			+ " char(15) NOT NULL, " + IP_DEST + " char(15) NOT NULL, " + "src_port int NOT NULL, "
			+ "dst_port int NOT NULL, " + " ip_proto char(6) NOT NULL" + " );";
	
	private static final String CREATE_LONG_TABLE = "CREATE TABLE " + LONG_TABLE_NAME + "(" + BYTES
	+ " bigint NOT NULL, " + TIME + " timestamp NOT NULL, " + IP_SRC
	+ " char(15) NOT NULL, " + IP_DEST + " char(15) NOT NULL, " + "src_port int NOT NULL, "
	+ "dst_port int NOT NULL, " + " ip_proto char(6) NOT NULL" + " );";
	
	private static final String CREATE_VERY_LONG_TABLE = "CREATE TABLE " + VERY_LONG_TABLE_NAME + "(" + BYTES
	+ " bigint NOT NULL, " + TIME + " timestamp NOT NULL, " + IP_SRC
	+ " char(15) NOT NULL, " + IP_DEST + " char(15) NOT NULL, " + "src_port int NOT NULL, "
	+ "dst_port int NOT NULL, " + " ip_proto char(6) NOT NULL" + " );";

	private static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";

	private static final String DELETE_LONG_TABLE = "DROP TABLE " + LONG_TABLE_NAME + ";";
	
	private static final String DELETE_VERY_LONG_TABLE = "DROP TABLE " + VERY_LONG_TABLE_NAME + ";";
	
	private static final String DELETE_TABLE_DATA = "DELETE FROM " + LONG_TABLE_NAME + ";";
	
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
	 */

	private Connection getConnection() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{
		Class.forName(TestConfiguration.getJdbcDriver()).newInstance();
		Connection connection = DriverManager.getConnection(TestConfiguration.getDatabaseURL(),
				TestConfiguration.getDatabaseUser(), TestConfiguration.getDatabasePass());
		return connection;
	}

	TestUtils() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			SQLException, IOException {

		m_conn = getConnection();
		m_urlPmgraph = "http://localhost:" + TestConfiguration.getPort() + "/pmgraph/";
	}

	void CreateTable() throws SQLException
	{
		// Allow the program to be run more than once,
		// attempt to remove the table from the database
		PreparedStatement sqlStatement;
		try
		{
			// Delete the table
			sqlStatement = m_conn.prepareStatement(DELETE_TABLE);
			m_logger.debug(DELETE_TABLE);
			sqlStatement.executeUpdate();
			sqlStatement.close();
			
			sqlStatement = m_conn.prepareStatement(DELETE_LONG_TABLE);
			m_logger.debug(DELETE_LONG_TABLE);
			sqlStatement.executeUpdate();
			sqlStatement.close();
			
			sqlStatement = m_conn.prepareStatement(DELETE_VERY_LONG_TABLE);
			m_logger.debug(DELETE_VERY_LONG_TABLE);
			sqlStatement.executeUpdate();
			sqlStatement.close();
		} catch (SQLException e)
		{
			/* don't care if it fails, table may not exist */
			m_logger.error(e.getMessage(), e);
		}

		sqlStatement = m_conn.prepareStatement(CREATE_TABLE);
		m_logger.debug(CREATE_TABLE);
		sqlStatement.executeUpdate();
		sqlStatement.close();
		
		sqlStatement = m_conn.prepareStatement(CREATE_LONG_TABLE);
		m_logger.debug(CREATE_LONG_TABLE);
		sqlStatement.executeUpdate();
		sqlStatement.close();
		
		sqlStatement = m_conn.prepareStatement(CREATE_VERY_LONG_TABLE);
		m_logger.debug(CREATE_VERY_LONG_TABLE);
		sqlStatement.executeUpdate();
		sqlStatement.close();
	}

	void insertNewRow(long bytes, Timestamp theTime, String ip_src, String ip_dst, boolean isLong)
			throws SQLException, IOException
	{
		insertRow(ip_src, ip_dst, 0, 0, bytes, new Timestamp(theTime.getTime()), isLong);
	}

	private void insertRow(String ip_src, String ip_dst, int src_port, int dst_port, long bytes,
			Timestamp t, boolean isLong) throws SQLException, IOException
	{
		insertRow(ip_src, ip_dst, src_port, dst_port, bytes, t, "tcp", isLong);
	}

	private void insertRow(String ip_src, String ip_dst, int src_port, int dst_port, long bytes,
			Timestamp t, String proto, boolean isLong) throws SQLException, IOException
	{
		String theTableName;
		StringBuffer sql; 
		if(isLong)
		{
			theTableName = LONG_TABLE_NAME;
		}
		else
		{
			theTableName = TABLE_NAME;
		}
		
		sql = new StringBuffer("INSERT INTO " + theTableName
				+ " (ip_src, ip_dst, src_port, dst_port, "
				+ "bytes, stamp_inserted, ip_proto) VALUES (?,?,?,?,?,?,?)");
		PreparedStatement sqlStatement = m_conn.prepareStatement(sql.toString());
		sqlStatement.setString(1, ip_src);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, ip_src);
		sqlStatement.setString(2, ip_dst);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, ip_dst);
		sqlStatement.setInt(3, src_port);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, new Integer(src_port).toString());
		sqlStatement.setInt(4, dst_port);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, new Integer(dst_port).toString());
		sqlStatement.setLong(5, bytes);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, new Long(bytes).toString());
		if(TestConfiguration.getJdbcDriver().equals("org.sqlite.JDBC"))
		{
			sqlStatement.setString(6, t.toString());
		}
		else
		{
			sqlStatement.setTimestamp(6, t);
		}
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, t.toString());
		sqlStatement.setString(7, proto);
		sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, proto);

		// Insert the row
		m_logger.debug(sql.toString());
		sqlStatement.executeUpdate();
		sqlStatement.close();
	}
	
	void ClearTable() throws SQLException
	{
		PreparedStatement sqlStatement = m_conn.prepareStatement(DELETE_TABLE_DATA);
		sqlStatement.executeUpdate();
		sqlStatement.close();
	}

	void InsertVeryLongSampleData() throws SQLException, IOException
	{
//		 convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, vlt1, true);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, vlt1, true);
		insertRow("10.0.156.110", "4.2.2.2", 12300, 80, 2000 * 128 * 60, vlt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 90 * 128 * 60, vlt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 80 * 128 * 60, vlt2, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 70 * 128 * 60, vlt4, true);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23400, 50 * 128 * 60, vlt2, true);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23500, 75 * 128 * 60, vlt4, true);
		insertRow("10.0.156.120", "4.2.2.2", 90, 10000, 500 * 128 * 60 + 1, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.3", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.4", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.5", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.6", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.7", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.8", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.9", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.10", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.11", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.120", "4.2.2.12", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("4.2.2.3", "10.0.156.120", 90, 10000, 1000 * 128 * 60, vlt2, true);
		insertRow("4.2.2.4", "10.0.156.120", 90, 10000, 900 * 128 * 60, vlt2, true);
		insertRow("4.2.2.5", "10.0.156.120", 90, 10000, 800 * 128 * 60, vlt2, true);
		insertRow("4.2.2.6", "10.0.156.120", 90, 10000, 700 * 128 * 60, vlt2, true);
		insertRow("4.2.2.7", "10.0.156.120", 90, 10000, 600 * 128 * 60, vlt2, true);
		insertRow("4.2.2.8", "10.0.156.120", 90, 10000, 500 * 128 * 60, vlt2, true);
		insertRow("4.2.2.9", "10.0.156.120", 90, 10000, 400 * 128 * 60, vlt2, true);
		insertRow("4.2.2.10", "10.0.156.120", 90, 10000, 300 * 128 * 60, vlt2, true);
		insertRow("4.2.2.11", "10.0.156.120", 90, 10000, 200 * 128 * 60, vlt2, true);
		insertRow("4.2.2.12", "10.0.156.120", 90, 10000, 100 * 128 * 60, vlt2, true);
		insertRow("10.0.156.131", "4.2.2.2", 90, 10000, 1050 * 128 * 60, vlt4, true);
		insertRow("10.0.156.132", "4.2.2.2", 90, 10000, 950 * 128 * 60, vlt4, true);
		insertRow("10.0.156.133", "4.2.2.2", 90, 10000, 850 * 128 * 60, vlt4, true);
		insertRow("10.0.156.134", "4.2.2.2", 90, 10000, 750 * 128 * 60, vlt4, true);
		insertRow("10.0.156.135", "4.2.2.2", 90, 10000, 650 * 128 * 60, vlt4, true);
		insertRow("10.0.156.136", "4.2.2.2", 90, 10000, 550 * 128 * 60, vlt4, true);
		insertRow("10.0.156.137", "4.2.2.2", 90, 10000, 450 * 128 * 60, vlt4, true);
		insertRow("10.0.156.138", "4.2.2.2", 90, 10000, 350 * 128 * 60, vlt4, true);
		insertRow("10.0.156.139", "4.2.2.2", 90, 10000, 250 * 128 * 60, vlt4, true);
		insertRow("10.0.156.140", "4.2.2.2", 90, 10000, 150 * 128 * 60, vlt4, true);
	}
	
	void InsertVeryLongPortsSampleData() throws SQLException, IOException
	{
//		 convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, vlt1, "udp", true);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, vlt1, "udp", true);
		insertRow("10.0.156.110", "4.2.2.2", 80, 1080, 500 * 128 * 60, vlt1, true);
		insertRow("10.0.156.110", "4.2.2.5", 80, 1180, 600 * 128 * 60, vlt1, true);
		insertRow("10.0.156.130", "192.168.1.5", 80, 1025, 150 * 128 * 60, vlt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 4560, 80, 100 * 128 * 60, vlt1, true);
		insertRow("4.2.2.2", "10.0.156.130", 8956, 80, 125 * 128 * 60, vlt1, true);
		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 100 * 128 * 60, vlt1, true);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 50 * 128 * 60, vlt1, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 100 * 128 * 60, vlt1, true);

		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 500 * 128 * 60, vlt2, true);
		insertRow("4.2.2.2", "10.0.156.110", 1567, 110, 700 * 128 * 60, vlt2, true);
		insertRow("10.0.156.110", "4.56.2.2", 110, 1780, 300 * 128 * 60, vlt2, true);
		insertRow("10.0.156.110", "4.2.2.3", 110, 10500, 500 * 128 * 60, vlt2, true);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 100 * 128 * 60, vlt2, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 50 * 128 * 60, vlt2, true);

		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 600 * 128 * 60, vlt3, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 300 * 128 * 60, vlt3, true);
		insertRow("4.2.2.4", "10.0.156.110", 190, 443, 100 * 128 * 60, vlt3, true);
		insertRow("10.0.156.130", "4.2.2.4", 443, 10000, 150 * 128 * 60, vlt3, true);
		insertRow("4.2.2.4", "10.0.156.130", 4000, 443, 75 * 128 * 60, vlt3, "udp", true);
	}
	
	void InsertLongSampleData() throws SQLException, IOException
	{
//		 convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, lt1, true);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, lt1, true);
		insertRow("10.0.156.110", "4.2.2.2", 12300, 80, 2000 * 128 * 60, lt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 90 * 128 * 60, lt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 80 * 128 * 60, lt2, true);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 70 * 128 * 60, lt4, true);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23400, 50 * 128 * 60, lt2, true);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23500, 75 * 128 * 60, lt4, true);
		insertRow("10.0.156.120", "4.2.2.2", 90, 10000, 500 * 128 * 60 + 1, lt2, true);
		insertRow("10.0.156.120", "4.2.2.3", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.4", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.5", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.6", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.7", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.8", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.9", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.10", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.11", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.120", "4.2.2.12", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("4.2.2.3", "10.0.156.120", 90, 10000, 1000 * 128 * 60, lt2, true);
		insertRow("4.2.2.4", "10.0.156.120", 90, 10000, 900 * 128 * 60, lt2, true);
		insertRow("4.2.2.5", "10.0.156.120", 90, 10000, 800 * 128 * 60, lt2, true);
		insertRow("4.2.2.6", "10.0.156.120", 90, 10000, 700 * 128 * 60, lt2, true);
		insertRow("4.2.2.7", "10.0.156.120", 90, 10000, 600 * 128 * 60, lt2, true);
		insertRow("4.2.2.8", "10.0.156.120", 90, 10000, 500 * 128 * 60, lt2, true);
		insertRow("4.2.2.9", "10.0.156.120", 90, 10000, 400 * 128 * 60, lt2, true);
		insertRow("4.2.2.10", "10.0.156.120", 90, 10000, 300 * 128 * 60, lt2, true);
		insertRow("4.2.2.11", "10.0.156.120", 90, 10000, 200 * 128 * 60, lt2, true);
		insertRow("4.2.2.12", "10.0.156.120", 90, 10000, 100 * 128 * 60, lt2, true);
		insertRow("10.0.156.131", "4.2.2.2", 90, 10000, 1050 * 128 * 60, lt4, true);
		insertRow("10.0.156.132", "4.2.2.2", 90, 10000, 950 * 128 * 60, lt4, true);
		insertRow("10.0.156.133", "4.2.2.2", 90, 10000, 850 * 128 * 60, lt4, true);
		insertRow("10.0.156.134", "4.2.2.2", 90, 10000, 750 * 128 * 60, lt4, true);
		insertRow("10.0.156.135", "4.2.2.2", 90, 10000, 650 * 128 * 60, lt4, true);
		insertRow("10.0.156.136", "4.2.2.2", 90, 10000, 550 * 128 * 60, lt4, true);
		insertRow("10.0.156.137", "4.2.2.2", 90, 10000, 450 * 128 * 60, lt4, true);
		insertRow("10.0.156.138", "4.2.2.2", 90, 10000, 350 * 128 * 60, lt4, true);
		insertRow("10.0.156.139", "4.2.2.2", 90, 10000, 250 * 128 * 60, lt4, true);
		insertRow("10.0.156.140", "4.2.2.2", 90, 10000, 150 * 128 * 60, lt4, true);
	}
	
	void InsertLongPortsSampleData() throws SQLException, IOException
	{
//		 convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, lt1, "udp", true);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, lt1, "udp", true);
		insertRow("10.0.156.110", "4.2.2.2", 80, 1080, 500 * 128 * 60, lt1, true);
		insertRow("10.0.156.110", "4.2.2.5", 80, 1180, 600 * 128 * 60, lt1, true);
		insertRow("10.0.156.130", "192.168.1.5", 80, 1025, 150 * 128 * 60, lt1, true);
		insertRow("4.2.2.2", "10.0.156.110", 4560, 80, 100 * 128 * 60, lt1, true);
		insertRow("4.2.2.2", "10.0.156.130", 8956, 80, 125 * 128 * 60, lt1, true);
		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 100 * 128 * 60, lt1, true);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 50 * 128 * 60, lt1, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 100 * 128 * 60, lt1, true);

		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 500 * 128 * 60, lt2, true);
		insertRow("4.2.2.2", "10.0.156.110", 1567, 110, 700 * 128 * 60, lt2, true);
		insertRow("10.0.156.110", "4.56.2.2", 110, 1780, 300 * 128 * 60, lt2, true);
		insertRow("10.0.156.110", "4.2.2.3", 110, 10500, 500 * 128 * 60, lt2, true);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 100 * 128 * 60, lt2, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 50 * 128 * 60, lt2, true);

		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 600 * 128 * 60, lt3, true);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 300 * 128 * 60, lt3, true);
		insertRow("4.2.2.4", "10.0.156.110", 190, 443, 100 * 128 * 60, lt3, true);
		insertRow("10.0.156.130", "4.2.2.4", 443, 10000, 150 * 128 * 60, lt3, true);
		insertRow("4.2.2.4", "10.0.156.130", 4000, 443, 75 * 128 * 60, lt3, "udp", true);
	}
	
	void InsertSampleData() throws SQLException, IOException
	{
		// convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, t1, false);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, t1, false);
		insertRow("10.0.156.110", "4.2.2.2", 12300, 80, 2000 * 128 * 60, t1, false);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 90 * 128 * 60, t1, false);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 80 * 128 * 60, t2, false);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 70 * 128 * 60, t4, false);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23400, 50 * 128 * 60, t2, false);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23500, 75 * 128 * 60, t4, false);
		insertRow("10.0.156.120", "4.2.2.2", 90, 10000, 500 * 128 * 60 + 1, t2, false);
		insertRow("10.0.156.120", "4.2.2.3", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.4", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.5", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.6", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.7", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.8", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.9", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.10", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.11", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.120", "4.2.2.12", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("4.2.2.3", "10.0.156.120", 90, 10000, 1000 * 128 * 60, t2, false);
		insertRow("4.2.2.4", "10.0.156.120", 90, 10000, 900 * 128 * 60, t2, false);
		insertRow("4.2.2.5", "10.0.156.120", 90, 10000, 800 * 128 * 60, t2, false);
		insertRow("4.2.2.6", "10.0.156.120", 90, 10000, 700 * 128 * 60, t2, false);
		insertRow("4.2.2.7", "10.0.156.120", 90, 10000, 600 * 128 * 60, t2, false);
		insertRow("4.2.2.8", "10.0.156.120", 90, 10000, 500 * 128 * 60, t2, false);
		insertRow("4.2.2.9", "10.0.156.120", 90, 10000, 400 * 128 * 60, t2, false);
		insertRow("4.2.2.10", "10.0.156.120", 90, 10000, 300 * 128 * 60, t2, false);
		insertRow("4.2.2.11", "10.0.156.120", 90, 10000, 200 * 128 * 60, t2, false);
		insertRow("4.2.2.12", "10.0.156.120", 90, 10000, 100 * 128 * 60, t2, false);
		insertRow("10.0.156.131", "4.2.2.2", 90, 10000, 1050 * 128 * 60, t4, false);
		insertRow("10.0.156.132", "4.2.2.2", 90, 10000, 950 * 128 * 60, t4, false);
		insertRow("10.0.156.133", "4.2.2.2", 90, 10000, 850 * 128 * 60, t4, false);
		insertRow("10.0.156.134", "4.2.2.2", 90, 10000, 750 * 128 * 60, t4, false);
		insertRow("10.0.156.135", "4.2.2.2", 90, 10000, 650 * 128 * 60, t4, false);
		insertRow("10.0.156.136", "4.2.2.2", 90, 10000, 550 * 128 * 60, t4, false);
		insertRow("10.0.156.137", "4.2.2.2", 90, 10000, 450 * 128 * 60, t4, false);
		insertRow("10.0.156.138", "4.2.2.2", 90, 10000, 350 * 128 * 60, t4, false);
		insertRow("10.0.156.139", "4.2.2.2", 90, 10000, 250 * 128 * 60, t4, false);
		insertRow("10.0.156.140", "4.2.2.2", 90, 10000, 150 * 128 * 60, t4, false);
	}

	void InsertPortsSampleData() throws SQLException, IOException
	{
		// convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, t1, "udp", false);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, t1, "udp", false);
		insertRow("10.0.156.110", "4.2.2.2", 80, 1080, 500 * 128 * 60, t1, false);
		insertRow("10.0.156.110", "4.2.2.5", 80, 1180, 600 * 128 * 60, t1, false);
		insertRow("10.0.156.130", "192.168.1.5", 80, 1025, 150 * 128 * 60, t1, false);
		insertRow("4.2.2.2", "10.0.156.110", 4560, 80, 100 * 128 * 60, t1, false);
		insertRow("4.2.2.2", "10.0.156.130", 8956, 80, 125 * 128 * 60, t1, false);
		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 100 * 128 * 60, t1, false);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 50 * 128 * 60, t1, false);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 100 * 128 * 60, t1, false);

		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 500 * 128 * 60, t2, false);
		insertRow("4.2.2.2", "10.0.156.110", 1567, 110, 700 * 128 * 60, t2, false);
		insertRow("10.0.156.110", "4.56.2.2", 110, 1780, 300 * 128 * 60, t2, false);
		insertRow("10.0.156.110", "4.2.2.3", 110, 10500, 500 * 128 * 60, t2, false);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 100 * 128 * 60, t2, false);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 50 * 128 * 60, t2, false);

		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 600 * 128 * 60, t3, false);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 300 * 128 * 60, t3, false);
		insertRow("4.2.2.4", "10.0.156.110", 190, 443, 100 * 128 * 60, t3, false);
		insertRow("10.0.156.130", "4.2.2.4", 443, 10000, 150 * 128 * 60, t3, false);
		insertRow("4.2.2.4", "10.0.156.130", 4000, 443, 75 * 128 * 60, t3, "udp", false);
	}

	String getUrlPmgraph()
	{
		return m_urlPmgraph;
	}

}
