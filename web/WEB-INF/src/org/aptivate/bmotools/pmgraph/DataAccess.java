package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
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

	/**
	 * Form and execute the database query
	 * 
	 * @param requestParams  Parameters from the request
	 * @param isChart        Set if this request is for the chart (not the legend)
	 * @param isLong		 Set if this request needs data sampled over a long time period
	 * @return a list of the data points
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	Hashtable<Integer,List<DataPoint>> getThroughput(RequestParams requestParams, boolean isChart, boolean isLong)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException,
			ConfigurationException
	{
		QueryBuilder queryBuilder;
		queryBuilder = new QueryBuilder();
		
		long initTime = System.currentTimeMillis();
		
		List<PreparedStatement> listStatement = new ArrayList<PreparedStatement>();
		listStatement = queryBuilder.buildQuery(requestParams, isChart, isLong);
		m_logger.debug(queryBuilder.getQuery());
		
		//access the database		
		List<ResultSet> listDataResults = new ArrayList<ResultSet>();
		
		Iterator iter = listStatement.iterator();
		while (iter.hasNext())
		{
			PreparedStatement statement = (PreparedStatement) iter.next();
			ResultSet dataResults = statement.executeQuery();
			listDataResults.add(dataResults);
		}
		
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " ms");
		initTime = System.currentTimeMillis();

		// for each element in result set an entry is created in the list of dataPoints
		
		Hashtable<Integer,List<DataPoint>> hashDataPoints = new Hashtable<Integer, List<DataPoint>>();
		iter = listDataResults.iterator();
		
		int i = 0;
		while (iter.hasNext())
		{
			ResultSet dataResults = (ResultSet) iter.next();
			List<DataPoint> dataPoints = new ArrayList<DataPoint>();
			while (dataResults.next())
				dataPoints.add(dataPointCreate(requestParams, dataResults, isChart));
			if (!dataPoints.isEmpty())
				hashDataPoints.put(i, dataPoints);
			i++;
			dataResults.close();
		}
		
		queryBuilder.releaseConnection();
		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Creating array of results for query: " + endTime
				+ " ms");
		
		return hashDataPoints;
	}

	private DataPoint dataPointCreate(RequestParams requestParams,
			ResultSet rs, boolean isChart) throws SQLException, IOException
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
