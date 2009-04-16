package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
class DataAccess
{
	private QueryBuilder m_queryBuilder;
	
	private Logger m_logger = Logger.getLogger(DataAccess.class
			.getName());

	/**
	 * Create the connection object and set the m_localSubnet to the subnet
	 * contained in the config file
	 * 
	 * @throws InstantiationExceptionpageUrl
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	DataAccess() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{
		m_queryBuilder = new QueryBuilder ();

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
	List<GraphData> getTotalThroughput(long start, long end)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, IOException, SQLException
	{
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
/*
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
*/
		return resultData;
	}
	
	List<GraphData> getThroughput(RequestParams requestParams, boolean perMinute) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException
	{

		long initTime = System.currentTimeMillis();
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();
				
		PreparedStatement statement = m_queryBuilder.buildQuery (requestParams, perMinute);
		
		m_logger.debug(statement);
		ResultSet ipResults = statement.executeQuery();
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults));

		}
		ipResults.close();
		statement.close();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger.debug("Execution Time in mysql query: " + endTime + " miliseg");	
		return resultData;
	}	

}
