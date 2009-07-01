package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Form the query, access the database and store the results in a list
 * 
 * @author Tom Sharp
 * 
 * History:
 * 
 * 11-03-2009 Modified by Noe A. Rodriguez Glez.
 * 
 * Removed static methods in order to make the class threadsafe. Use of a
 * Configuration class which contains all configuration data. Add comments.
 * Log4java logging. Avoided Order By querys in order to reduce execution time,
 * now all sorting is doing using Collection.sort.
 * 
 */
class DataAccess
{
	private Logger m_logger = Logger.getLogger(DataAccess.class.getName());

	List<DataPoint> getThroughput(RequestParams requestParams, boolean isChart)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException,
			ConfigurationException
	{
		QueryBuilder queryBuilder;
		queryBuilder = new QueryBuilder();

		long initTime = System.currentTimeMillis();
		
		ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

		PreparedStatement statement = queryBuilder.buildQuery(requestParams,
				isChart);

		m_logger.debug(statement);
		
		//access the database
		ResultSet dataResults = statement.executeQuery();
		
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " ms");
		initTime = System.currentTimeMillis();

		//for each element in result set an entry is created in the list of dataPoints
		while (dataResults.next())
		{
			dataPoints.add(dataPointCreate(requestParams, dataResults, isChart));
		}
		dataResults.close();
		statement.close();
		queryBuilder.releaseConnection();
		
		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Creating array of results for query: " + endTime
				+ " ms");
		
		return dataPoints;
	}

	private DataPoint dataPointCreate(RequestParams requestParams,
			ResultSet rs, boolean isChart) throws SQLException
	{
		switch (requestParams.getView())
		{
		case LOCAL_PORT:
			return new PortDataPoint(rs, rs.getInt("port"), rs
					.getString("ip_proto"), isChart);
		case REMOTE_PORT:
			return new PortDataPoint(rs, rs.getInt("remote_port"), 
					rs.getString("ip_proto"), isChart);

		default:
		case LOCAL_IP:
			return new IpDataPoint(rs, rs.getString("local_ip"), isChart);
		case REMOTE_IP:
			return new IpDataPoint(rs, rs.getString("remote_ip"), isChart);
		}
	}

}
