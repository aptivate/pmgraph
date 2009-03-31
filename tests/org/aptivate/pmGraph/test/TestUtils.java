package org.aptivate.pmGraph.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class TestUtils
{

	// The connection to the MySQL database
	private Connection m_conn;

	private static Logger m_logger = Logger
			.getLogger(TestUtils.class.getName());

	final Timestamp t1 = new Timestamp(60000);

	final Timestamp t2 = new Timestamp(120000);

	final Timestamp t3 = new Timestamp(180000);

	final Timestamp t4 = new Timestamp(240000);

	private static final String TABLE_NAME = "acct_v6";

	// MySQL table fields
	private static final String IP_SRC = "ip_src";

	private static final String IP_DEST = "ip_dst";

	private static final String BYTES = "bytes";

	private static final String TIME = "stamp_inserted";

	public static final String LEGEND_TBL = "legend_tbl";

	private String m_urlPmgraph;

	// SQL query strings

	/*
	 * private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
	 * "(" + BYTES + " bigint(20) unsigned NOT NULL, " + TIME + " datetime NOT
	 * NULL, " + IP_SRC + " char(15) NOT NULL, " + IP_DEST + " char(15) NOT
	 * NULL, " + "src_port int(2) unsigned NOT NULL, " + "dst_port int(2)
	 * unsigned NOT NULL" + ");";
	 */
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "(" + BYTES + " bigint NOT NULL, " + TIME
			+ " timestamp NOT NULL, " + IP_SRC + " char(15) NOT NULL, "
			+ IP_DEST + " char(15) NOT NULL, " + "src_port int NOT NULL, "
			+ "dst_port int NOT NULL" + ");";

	private static final String INSERT_DATA = "INSERT INTO " + TABLE_NAME + "("
			+ BYTES + "," + TIME + "," + IP_SRC + "," + IP_DEST
			+ ",src_port, dst_port" + ") VALUES (?,?,?,?,?,?);";

	private static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";

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

		Class.forName(TestConfiguration.getJdbcDriver()).newInstance();
		Connection con = DriverManager.getConnection(TestConfiguration
				.getDatabaseURL(), TestConfiguration.getDatabaseUser(),
				TestConfiguration.getDatabasePass());
		return con;
	}

	public TestUtils() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{

		m_conn = getConnection();
		m_urlPmgraph = "http://localhost:" + TestConfiguration.getPort()
				+ "/pmgraph/";
	}

	void CreateTable() throws SQLException
	{
		// Allow the program to be run more than once,
		// attempt to remove the table from the database
		try
		{
			// Delete the table
			PreparedStatement pstmt = m_conn.prepareStatement(DELETE_TABLE);
			m_logger.debug(pstmt);
			pstmt.executeUpdate();
		}
		catch (SQLException e)
		{
			/* don't care if it fails, table may not exist */
			m_logger.error(e.getMessage(), e);
		}

		PreparedStatement pstmt = m_conn.prepareStatement(CREATE_TABLE);
		m_logger.debug(pstmt);
		pstmt.executeUpdate();
	}

	void insertNewRow(long bytes, Timestamp theTime, String ip_src,
			String ip_dest) throws SQLException
	{
		PreparedStatement stmt = m_conn.prepareStatement(INSERT_DATA);
		stmt.setLong(1, bytes);
		stmt.setTimestamp(2, new Timestamp(theTime.getTime()));
		stmt.setString(3, ip_src);
		stmt.setString(4, ip_dest);
		stmt.setInt(5, 0);
		stmt.setInt(6, 0);

		// Insert the row
		stmt.executeUpdate();
	}

	private void insertRow(String ip_src, String ip_dst, int src_port,
			int dst_port, long bytes, Timestamp t) throws SQLException
	{
		PreparedStatement stmt = m_conn.prepareStatement("INSERT INTO "
				+ TABLE_NAME + " (ip_src, ip_dst, src_port, dst_port, "
				+ "bytes, stamp_inserted) VALUES (?,?,?,?,?,?)");
		stmt.setString(1, ip_src);
		stmt.setString(2, ip_dst);
		stmt.setInt(3, src_port);
		stmt.setInt(4, dst_port);
		stmt.setLong(5, bytes);
		stmt.setTimestamp(6, t);

		// Insert the row
		m_logger.debug(stmt);
		stmt.executeUpdate();
	}

	void InsertSampleData() throws SQLException
	{
		// convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.110", "4.2.2.2", 12300, 80, 2000 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 90 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 80 * 128 * 60, t2);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 70 * 128 * 60, t4);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23400, 50 * 128 * 60, t2);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23500, 75 * 128 * 60, t4);
		insertRow("10.0.156.120", "4.2.2.2", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.3", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.4", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.5", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.6", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.7", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.8", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.9", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.10", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.11", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.12", 90, 10000, 500 * 128 * 60, t2);
		insertRow("4.2.2.3", "10.0.156.120", 90, 10000, 1000 * 128 * 60, t2);
		insertRow("4.2.2.4", "10.0.156.120", 90, 10000, 900 * 128 * 60, t2);
		insertRow("4.2.2.5", "10.0.156.120", 90, 10000, 800 * 128 * 60, t2);
		insertRow("4.2.2.6", "10.0.156.120", 90, 10000, 700 * 128 * 60, t2);
		insertRow("4.2.2.7", "10.0.156.120", 90, 10000, 600 * 128 * 60, t2);
		insertRow("4.2.2.8", "10.0.156.120", 90, 10000, 500 * 128 * 60, t2);
		insertRow("4.2.2.9", "10.0.156.120", 90, 10000, 400 * 128 * 60, t2);
		insertRow("4.2.2.10", "10.0.156.120", 90, 10000, 300 * 128 * 60, t2);
		insertRow("4.2.2.11", "10.0.156.120", 90, 10000, 200 * 128 * 60, t2);
		insertRow("4.2.2.12", "10.0.156.120", 90, 10000, 100 * 128 * 60, t2);
		insertRow("10.0.156.131", "4.2.2.2", 90, 10000, 1050 * 128 * 60, t4);
		insertRow("10.0.156.132", "4.2.2.2", 90, 10000, 950 * 128 * 60, t4);
		insertRow("10.0.156.133", "4.2.2.2", 90, 10000, 850 * 128 * 60, t4);
		insertRow("10.0.156.134", "4.2.2.2", 90, 10000, 750 * 128 * 60, t4);
		insertRow("10.0.156.135", "4.2.2.2", 90, 10000, 650 * 128 * 60, t4);
		insertRow("10.0.156.136", "4.2.2.2", 90, 10000, 550 * 128 * 60, t4);
		insertRow("10.0.156.137", "4.2.2.2", 90, 10000, 450 * 128 * 60, t4);
		insertRow("10.0.156.138", "4.2.2.2", 90, 10000, 350 * 128 * 60, t4);
		insertRow("10.0.156.139", "4.2.2.2", 90, 10000, 250 * 128 * 60, t4);
		insertRow("10.0.156.140", "4.2.2.2", 90, 10000, 150 * 128 * 60, t4);
	}

	void InsertPortsSampleData() throws SQLException
	{
		// convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.110", "4.2.2.2", 80, 1080, 500 * 128 * 60, t1);
		insertRow("10.0.156.110", "4.2.2.5", 80, 1180, 600 * 128 * 60, t1);
		insertRow("10.0.156.130", "192.168.1.5", 80, 1025, 150 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.110", 4560, 80, 100 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.130", 8956, 80, 125 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 100 * 128 * 60, t1);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 50 * 128 * 60, t1);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 100 * 128 * 60, t1);

		insertRow("4.2.2.2", "10.0.156.130", 8963, 110, 500 * 128 * 60, t2);
		insertRow("4.2.2.2", "10.0.156.110", 1567, 110, 700 * 128 * 60, t2);
		insertRow("10.0.156.110", "4.56.2.2", 110, 1780, 300 * 128 * 60, t2);
		insertRow("10.0.156.110", "4.2.2.3", 110, 10500, 500 * 128 * 60, t2);
		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 100 * 128 * 60, t2);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 50 * 128 * 60, t2);

		insertRow("10.0.156.131", "4.2.2.4", 443, 10000, 600 * 128 * 60, t3);
		insertRow("4.2.2.3", "10.0.156.131", 190, 443, 300 * 128 * 60, t3);
		insertRow("4.2.2.4", "10.0.156.110", 190, 443, 100 * 128 * 60, t3);
		insertRow("10.0.156.130", "4.2.2.4", 443, 10000, 150 * 128 * 60, t3);
		insertRow("4.2.2.4", "10.0.156.130", 4000, 443, 75 * 128 * 60, t3);
	}

	String getUrlPmgraph()
	{
		return m_urlPmgraph;
	}

}
