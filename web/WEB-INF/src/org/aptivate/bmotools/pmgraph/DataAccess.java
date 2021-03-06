package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DataAccess
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
		
		String group = requestParams.getSelectGroupIndex();		
		String subnet = requestParams.getSelectSubnetIndex();
		if ((subnet != null) && (subnet.equals("all")))	
		{
			requestParams.setSelectSubnetIndex(null);
		}
		if (group!=null && group.equals("all"))
		{
			group = null;
			requestParams.setSelectGroupIndex(null);
		}		
		if (group != null)
		{
			Pattern p = Pattern.compile("^(([1-9]?[0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}$");
			Matcher m = p.matcher(group);		    	
			// 	Subnet
			if (m.find())
			{
				requestParams.setSelectSubnetIndex(group);
				requestParams.setSelectGroupIndex(null);					
			}
		}
		PreparedStatement statement = queryBuilder.buildQuery(requestParams, isChart, isLong);									
		m_logger.debug(queryBuilder.getQuery());
		
		//access the database		
		ResultSet dataResults = statement.executeQuery();						
		
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " ms");
		initTime = System.currentTimeMillis();

		// for each element in result set an entry is created in the list of dataPoints
		Hashtable<Integer,List<DataPoint>> hashDataPoints = new Hashtable<Integer, List<DataPoint>>();
		int i=0;
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();			
		while (dataResults.next())				
			dataPoints.add(dataPointCreate(requestParams, dataResults, isChart));
		if (!dataPoints.isEmpty()) {
			hashDataPoints.put(i, dataPoints);
			i++;
		}
		dataResults.close();
		
		queryBuilder.releaseConnection();
		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Creating array of results for query: " + endTime
				+ " ms");
		
		return hashDataPoints;
	}
		
	
	/**
	 * Form and execute the database query to get all DataBase's content
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
	public List<String> getThroughputAll(RequestParams requestParams, boolean isChart, boolean isLong)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException,
			ConfigurationException
	{
		QueryBuilder queryBuilder;
		queryBuilder = new QueryBuilder();
		
		long initTime = System.currentTimeMillis();
		
		PreparedStatement statement = queryBuilder.buildQueryAll(requestParams, isChart, isLong);
		m_logger.debug(queryBuilder.getQuery());
		
		//access the database		
		ResultSet dataResults = statement.executeQuery();
		List<String> dataIps = new ArrayList<String>();			
		while (dataResults.next())
		{
			dataIps.add(dataResults.getString("ip_local"));
		}
		
		Collections.sort(dataIps, new IpComparator());
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " ms");
		initTime = System.currentTimeMillis();

		queryBuilder.releaseConnection();
		endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Creating array of results for query: " + endTime + " ms");		
		return dataIps;
	}

	private DataPoint dataPointCreate(RequestParams requestParams,
			ResultSet rs, boolean isChart) throws SQLException, IOException
	{
		switch (requestParams.getView())
		{
		case LOCAL_PORT:
			return new PortDataPoint(rs, rs.getInt("local_port"), rs
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
