package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DataAccess {
	private static String localSubnet;

	private Connection conn;

	private DataAccess(Connection con) {
		conn = con;
	}

	public static Properties getProperties() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException {
		Properties properties = new Properties();
		InputStream stream = DataAccess.class
				.getResourceAsStream("/database.properties");
		properties.load(stream);
		stream.close();
		localSubnet = properties.getProperty("LocalSubnet");
		return properties;

	}

	public static Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException {
		Properties properties = getProperties();
		Class.forName(properties.getProperty("JdbcDriver")).newInstance();
		Connection con = DriverManager.getConnection(properties
				.getProperty("DatabaseURL"), properties
				.getProperty("DatabaseUser"), properties
				.getProperty("DatabasePass"));
		return con;
	}

	public static DataAccess getDatabase() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException {
		return new DataAccess(getConnection());
	}

	public ResultSet getTotalThroughput(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException {
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		// Prepare and execute the SQL query
		PreparedStatement statement = conn
				.prepareStatement(GraphUtilities.THROUGHPUT_PER_MINUTE);
		statement.setString(1, localSubnet + "%");
		statement.setString(2, localSubnet + "%");
		statement.setString(3, localSubnet + "%");
		statement.setString(4, localSubnet + "%");
		statement.setString(5, localSubnet + "%");
		statement.setString(6, localSubnet + "%");
		statement.setTimestamp(7, new Timestamp(start));
		statement.setTimestamp(8, new Timestamp(end));
		System.out.println(statement);
		ResultSet results = statement.executeQuery();
		// statement.close();

		return results;
	}

	public ResultSet getThroughputPIPPMinute(long start, long end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);
		// Prepare and execute network throughput query

		PreparedStatement thrptStatement = conn
				.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP_PER_MINUTE);
		thrptStatement.setString(1, localSubnet + "%");
		thrptStatement.setString(2, localSubnet + "%");
		thrptStatement.setString(3, localSubnet + "%");
		thrptStatement.setString(4, localSubnet + "%");
		thrptStatement.setString(5, localSubnet + "%");
		thrptStatement.setString(6, localSubnet + "%");
		thrptStatement.setString(7, localSubnet + "%");
		thrptStatement.setTimestamp(8, new Timestamp(start));
		thrptStatement.setTimestamp(9, new Timestamp(end));
		System.out.println(thrptStatement);
		ResultSet thrptResults = thrptStatement.executeQuery();
		// thrptStatement.close();

		return thrptResults;

	}

	public ResultSet getThroughputPerIP(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException {

		String THROUGHPUT_PER_IP = getSQLThroughputPerIP("", "");
		return getResultPerIP(start, end, THROUGHPUT_PER_IP);

	}

	public ResultSet getThroughputPerIP(long start, long end, String sortby,
			String order) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException {

		String THROUGHPUT_PER_IP = getSQLThroughputPerIP(sortby, order);
		return getResultPerIP(start, end, THROUGHPUT_PER_IP);
	}

	private String getSQLThroughputPerIP(String sortBy, String order) {
		String sortByTmp = " ORDER BY bytes_total";
		String orderTmp = " DESC;";
		if (!sortBy.isEmpty())
			sortByTmp = " ORDER BY " + sortBy;
		if (!order.isEmpty())
			orderTmp = " " + order + ";";

		String THROUGHPUT_PER_IP = GraphUtilities.THROUGHPUT_PER_IP;
		int lastC = THROUGHPUT_PER_IP.indexOf(";");
		THROUGHPUT_PER_IP = THROUGHPUT_PER_IP.substring(0, lastC);
		THROUGHPUT_PER_IP = THROUGHPUT_PER_IP + sortByTmp + orderTmp;
		return THROUGHPUT_PER_IP;
	}

	private ResultSet getResultPerIP(long start, long end,
			String THROUGHPUT_PER_IP) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		// Round our times to the nearest minute
		start = start - (start % 60000);
		end = end - (end % 60000);

		// Get database connection and network properties
		PreparedStatement ipStatement = conn.prepareStatement(
				THROUGHPUT_PER_IP, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ipStatement.setString(1, localSubnet + "%");
		ipStatement.setString(2, localSubnet + "%");
		ipStatement.setString(3, localSubnet + "%");
		ipStatement.setString(4, localSubnet + "%");
		ipStatement.setString(5, localSubnet + "%");
		ipStatement.setString(6, localSubnet + "%");
		ipStatement.setString(7, localSubnet + "%");
		ipStatement.setTimestamp(8, new Timestamp(start));
		ipStatement.setTimestamp(9, new Timestamp(end));
		System.out.println(ipStatement);
		ResultSet ipResults = ipStatement.executeQuery();
		return ipResults;
	}

}
