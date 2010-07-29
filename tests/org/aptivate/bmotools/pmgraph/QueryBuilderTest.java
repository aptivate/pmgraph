package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test creates different queries and checks if the querybuilder is working
 * properly
 * 
 * @author Noeg
 * 
 */
public class QueryBuilderTest extends TestCase
{	
	
	private List<String> m_params;

	private Map<String, Object> m_paramsValues;

	private QueryBuilder m_queryBuilder;

	public QueryBuilderTest() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException, ConfigurationException {
		m_params = new ArrayList<String>();
		m_paramsValues = new HashMap<String, Object>();
		m_params.add("ip");
		m_paramsValues.put("ip", "10.0.156.120");
		m_params.add("port");
		m_paramsValues.put("port", 90);
		m_params.add("remote_ip");
		m_paramsValues.put("remote_ip", "4.2.2.2");
		m_params.add("remote_port");
		m_paramsValues.put("remote_port", 10000);
		
		m_queryBuilder = new QueryBuilder();
	}			
	
	private String GenerateQuery(RequestParams params, boolean isChart) throws IOException
	{
		String shortDate = "1970-01-01 01:05:00";
		String longDate = "1970-01-06 01:00:00";
		String date;
		String table;
		StringBuffer query = new StringBuffer("SELECT ");
		String viewName = params.getView().toString().toLowerCase();
		
		if(Configuration.needsLongGraph(params.getStartTime(), 
				params.getEndTime()) && Configuration.longGraphIsAllowed())
		{
			date = longDate;
			table = Configuration.getResultDatabaseLongTable();
		}
		else
		{
			date = shortDate;
			table = Configuration.getResultDatabaseTable();
		}
		
		if(isChart)
		{
			query.append("stamp_inserted, ");
		}
		query.append("SUM(CASE WHEN down THEN bytes ELSE 0 END) AS " + 
				"downloaded, SUM(CASE WHEN up THEN bytes ELSE 0 END) AS " + 
				"uploaded, ");
		query.append(viewName);
		if(viewName.endsWith("port"))
		{
			query.append(", ip_proto");
		}
		query.append(" FROM (SELECT stamp_inserted, CASE WHEN up THEN ip_dst " +
			"ELSE ip_src END AS remote_ip, CASE WHEN down THEN ip_dst ELSE " +
			"ip_src END AS local_ip, CASE WHEN up THEN dst_port ELSE " + 
			"src_port END AS remote_port, CASE WHEN down THEN dst_port ELSE " + 
			"src_port END AS local_port, bytes, up, down");
		if(viewName.contains("port"))
		{
			query.append(", ip_proto");
		}	
		query.append(" FROM (SELECT " +
			"stamp_inserted, ip_src, ip_dst, src_port, dst_port, bytes, " + 
			"(ip_src LIKE '");
		String[] subnets = Configuration.getLocalSubnet();
		boolean firstSubnet = true;
		StringBuffer dstSubnets =  new StringBuffer(); 
		for(String subnet : subnets)
		{
			if(firstSubnet)
			{
				query.append(subnet + "%'");
				firstSubnet = false;
				dstSubnets.append(subnet + "%'");
			}
			else
			{
				query.append(" OR ip_src LIKE '" + subnet + "%'");
				dstSubnets.append(" OR ip_dst LIKE '" + subnet + "%'");
			}
		}
		query.append(") AS up, (ip_dst LIKE '" + dstSubnets.toString() + ") " +  
				"AS down");
		if(viewName.contains("port"))
		{
			query.append(", ip_proto");
		}	
		query.append(" FROM " + table + " WHERE stamp_inserted >= '1970-01-01" + 
				" 01:00:00' AND stamp_inserted <= '" + date +  "' ) AS t1" +
				" WHERE up != down) AS t2");
		
		boolean hasLocalIp = params.getIp() != null;
		boolean hasRemoteIp = params.getRemoteIp() != null;
		boolean hasLocalPort = params.getPort() != null;
		boolean hasRemotePort = params.getRemotePort() != null;
		boolean hasGroupIndex = params.getSelectGroupIndex() != null;
		boolean hasSubnetIndex = params.getSelectSubnetIndex() != null;
		// A separate where clause is needed to select data on the basis of a 
		// request for a specific IP or port. If no IP or port has been 
		// requested we don't want to include the where clause at all.
		boolean needsWhere = hasLocalIp || hasRemoteIp || hasLocalPort ||
			hasRemotePort || hasGroupIndex || hasSubnetIndex;
		
		if(needsWhere)
		{
			query.append(" WHERE");
			boolean previousCondition = false;
			if(hasLocalIp)
			{
				query.append(" local_ip = '" + params.getIp()+ "'");
				previousCondition = true;
			}
			if(hasLocalPort)
			{
				if(previousCondition)
				{
					query.append(" AND");
				}
				query.append(" local_port = " + params.getPort());
				previousCondition = true;
			}
			if(hasRemoteIp)
			{
				if(previousCondition)
				{
					query.append(" AND");
				}
				query.append(" remote_ip = '" + params.getRemoteIp() + "'");
				previousCondition = true;
			}
			if(hasRemotePort)
			{
				if(previousCondition)
				{
					query.append(" AND");
				}
				query.append(" remote_port = " + params.getRemotePort());
			}
			if(hasGroupIndex)
			{
				if(previousCondition)
				{
					query.append(" AND");
				}
				String selectedGroup = params.getSelectGroupIndex();
				// If its a group then make it look for all the ips
				query.append(" (");
				boolean firstIp = true;
				List<String> ips = Configuration.getIpsGroup(selectedGroup);
				for(String ip : ips)
				{
					if(!firstIp)
					{
						query.append(" OR ");
					}
					else
					{
						firstIp = false;
					}
					query.append("local_ip = " + ip);
				}
				query.append(")");
			}
			if(hasSubnetIndex)
			{
				if(previousCondition)
				{
					query.append(" AND");
				}
				query.append(" local_ip LIKE '" + 
						params.getSelectSubnetIndex() + "%'");
			}
		}
			
		query.append(" GROUP BY ");
		
		if(isChart)
		{
			query.append("stamp_inserted, ");
		}
		
		query.append(viewName);
		
		if(viewName.contains("port"))
		{
			query.append(", ip_proto");
		}
		
		String sql = query.toString();
		if(TestConfiguration.getJdbcDriver().equals("org.sqlite.JDBC"))
		{
			sql = alterQuery(sql);
		} 
		return sql;
	}

	private String alterQuery(String query)
	{
		query = query.replace("stamp_inserted >= ", "datetime(stamp_inserted) >= ");
		query = query.replace("stamp_inserted <= ", "datetime(stamp_inserted) <= ");
		return query;
	}
		
	
	private void checkQuery(RequestParams requestParams, boolean isLong) throws SQLException, IOException, ParseException
	{
		List<View> views = View.getAvailableViews(requestParams);
		
		for (View view : views)
		{
			String sql;
			requestParams.setView(view);
			
			String expected = GenerateQuery(requestParams, true);
			m_queryBuilder.buildQuery(requestParams, true, isLong);
			sql = m_queryBuilder.getQuery().replaceAll( ".*: ", "");
			assertEquals(expected, sql);
			
			expected = GenerateQuery(requestParams, false);
			m_queryBuilder.buildQuery(requestParams, false, isLong);
			sql = m_queryBuilder.getQuery();
			assertEquals(expected, sql);
		}
	}
	
	/*
	 * method that tests the QueryBuilder in different cases
	 * 
	 * @throws SQLException
	 * @throws IOException
	 **/
	public void testQueryBuilder() throws SQLException, IOException, ParseException, Exception
	{
		m_queryBuilder = new QueryBuilder();			
		Map<String, Object> params = new HashMap<String, Object>();

		int i = 0;
		RequestParams requestParams = new RequestParams(params);
		requestParams.setStart(0);
		requestParams.setEnd(300000);
		
		RequestParams longRequestParams = new RequestParams(params);
		longRequestParams.setStart(0);
		longRequestParams.setEnd(5 * 24 * 60 * 60 * 1000);
		
		// check without parameters.
		checkQuery(requestParams, false);
		checkQuery(longRequestParams, true);
		requestParams.setSelectSubnetIndex("10.0.156.");
		checkQuery(requestParams, false);
		checkQuery(longRequestParams, true);
		requestParams.setSelectSubnetIndex(null);
		for (String param : m_params)
		{
			params.clear();
			params.put(param, m_paramsValues.get(param));
			// just set the parameter to a value that is not null
			checkQuery(requestParams,false);
			checkQuery(longRequestParams, true);
			i = 0;
			int limit = m_params.size();
			// check all possible groups.															
			for (int j = 1; j < limit; j++)
			{ // number of elements in the group

				for (int k = i + 1; k < m_params.size() - (j - 1); k++)
				{ // possible starts to make group of size j

					for (int l = 0; l < j; l++)
					{ // create the group, adding the parameter value to it
						params.put(m_params.get(k + l), m_paramsValues.get(m_params.get(k + l)));
					}
					checkQuery(requestParams, false);
					checkQuery(longRequestParams, true);

					// restart the parameters
					params.clear();
					params.put(param, m_paramsValues.get(param));
				}
			}
			i++;
		}
		requestParams.setSelectSubnetIndex("all");
	}
	
	public static Test suite()
	{
		return new TestSuite(QueryBuilderTest.class);
	}

}
