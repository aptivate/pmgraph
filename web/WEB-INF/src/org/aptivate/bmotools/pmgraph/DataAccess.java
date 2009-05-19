package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
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


	
	
	private List getColumnInResultSet(ResultSet rs) throws SQLException {
		ArrayList<String> columns = new ArrayList();
		
		if (rs != null) {
		  ResultSetMetaData rsMetaData = rs.getMetaData();
		  int numberOfColumns = rsMetaData.getColumnCount();
		  // get the column names; column indexes start from 1
		  for (int i = 1; i < numberOfColumns + 1; i++) {
		    columns.add(rsMetaData.getColumnName(i));
		  }
		}
		return columns;
	}

	
	List<GraphData> getThroughput(RequestParams requestParams, boolean perMinute)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException,
			ConfigurationException
	{
		QueryBuilder queryBuilder;
		queryBuilder = new QueryBuilder();

		long initTime = System.currentTimeMillis();
		ArrayList<GraphData> resultData = new ArrayList<GraphData>();

		PreparedStatement statement = queryBuilder.buildQuery(requestParams,
				perMinute);

		m_logger.debug(statement);
		ResultSet ipResults = statement.executeQuery();
		long endTime = System.currentTimeMillis() - initTime;
		m_logger
				.debug("Execution Time in mysql query: " + endTime + " miliseg");
		initTime = System.currentTimeMillis();
		
		List columns = getColumnInResultSet(ipResults);
		while (ipResults.next())
		{
			resultData.add(new GraphData(ipResults, columns));
		}
		ipResults.close();
		statement.close();
		queryBuilder.releaseConnection();
		endTime = System.currentTimeMillis() - initTime;
		m_logger
				.debug("Creating array of results for query: " + endTime + " miliseg");
		return resultData;
	}

}
