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

	private HashMap<String, Map<String, String>> queries;

	private HashMap<String, Map<String, String>> legendQueries;
	
	private HashMap<String, Map<String, String>> longQueries;
	
	private HashMap<String, Map<String, String>> longLegendQueries;	

	public QueryBuilderTest() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		super();
	}		
	
	public void testQueryBuilder() throws Exception
	{
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
		setQueries(true);
		setQueries(false);
	}
	
	public void setQueries(boolean isLong) throws IOException
	{
		String shortDate = "1970-01-01 01:05:00";
		String longDate = "1970-01-06 01:00:00";
		String date;
		String table;
		HashMap<String, Map<String, String>> theQueries;
		HashMap<String, Map<String, String>> theLegendQueries;
		
		if(isLong)
		{
			longQueries = new HashMap<String, Map<String, String>>();
			longLegendQueries = new HashMap<String, Map<String, String>>();
			table = Configuration.getResultDatabaseLongTable();
			date = longDate;
			theQueries = longQueries;
			theLegendQueries = longLegendQueries;
		}
		else
		{
			queries = new HashMap<String, Map<String, String>>();
			legendQueries = new HashMap<String, Map<String, String>>();
			table = Configuration.getResultDatabaseTable();
			date = shortDate;
			theQueries = queries;
			theLegendQueries = legendQueries;
		}

		HashMap<String, String> views = new HashMap<String, String>();
		HashMap<String, String> legendViews = new HashMap<String, String>();
		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put("LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  port, ip_proto");
		// {} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_ip");
		// {} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_port, ip_proto");
		theQueries.put("", views);
		theLegendQueries.put("", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {ip=10.0.156.120} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  port, ip_proto");
		// {ip=10.0.156.120} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_ip");
		// {ip=10.0.156.120} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_port, ip_proto");
		theQueries.put("ip", views);
		theLegendQueries.put("ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90, ip=10.0.156.120} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_ip");
		// {port=90, ip=10.0.156.120} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_port, ip_proto");
		theQueries.put("port ip", views);
		theLegendQueries.put("port ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_ip=4.2.2.2, ip=10.0.156.120} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  port, ip_proto");
		// {remote_ip=4.2.2.2, ip=10.0.156.120} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_port, ip_proto");
		theQueries.put("remote_ip ip", views);
		theLegendQueries.put("remote_ip ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_port=10000, ip=10.0.156.120} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  port, ip_proto");
		// {remote_port=10000, ip=10.0.156.120} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_ip");
		theQueries.put("remote_port ip", views);
		theLegendQueries.put("remote_port ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90, remote_ip=4.2.2.2, ip=10.0.156.120} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  remote_port, ip_proto");
		theQueries.put("port remote_ip ip", views);
		theLegendQueries.put("port remote_ip ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_ip=4.2.2.2, remote_port=10000, ip=10.0.156.120} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) = '10.0.156.120' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  =  '10.0.156.120' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  =  '10.0.156.120') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  =  '10.0.156.120'))  group by  port, ip_proto");
		theQueries.put("remote_ip remote_port ip", views);
		theLegendQueries.put("remote_ip remote_port ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		theQueries.put("port remote_ip remote_port ip", views);
		theLegendQueries.put("port remote_ip remote_port ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {port=90} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_ip");
		// {port=90} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_port, ip_proto");
		theQueries.put("port", views);
		theLegendQueries.put("port", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90, remote_ip=4.2.2.2} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {port=90, remote_ip=4.2.2.2} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_port, ip_proto");
		theQueries.put("port remote_ip", views);
		theLegendQueries.put("port remote_ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90, remote_port=10000} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {port=90, remote_port=10000} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_ip");
		theQueries.put("port remote_port", views);
		theLegendQueries.put("port remote_port", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {port=90, remote_ip=4.2.2.2, remote_port=10000} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN src_port ELSE dst_port END) = 90 AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		theQueries.put("port remote_ip remote_port", views);
		theLegendQueries.put("port remote_ip remote_port", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_ip=4.2.2.2} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {remote_ip=4.2.2.2} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  port, ip_proto");
		// {remote_ip=4.2.2.2} REMOTE_PORT
		views.put( "REMOTE_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_port, ip_proto");
		legendViews.put( "REMOTE_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN dst_port ELSE src_port END) AS remote_port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_port, ip_proto");
		theQueries.put("remote_ip", views);
		theLegendQueries.put("remote_ip", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_ip=4.2.2.2, remote_port=10000} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {remote_ip=4.2.2.2, remote_port=10000} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) = '4.2.2.2' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  port, ip_proto");
		theQueries.put("remote_ip remote_port", views);
		theLegendQueries.put("remote_ip remote_port", legendViews);

		views = new HashMap<String, String>();
		legendViews = new HashMap<String, String>();
		// {remote_port=10000} LOCAL_IP
		views.put( "LOCAL_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  local_ip");
		legendViews.put( "LOCAL_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_src ELSE ip_dst END) AS local_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  local_ip");
		// {remote_port=10000} LOCAL_PORT
		views.put( "LOCAL_PORT",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  port, ip_proto");
		legendViews.put( "LOCAL_PORT",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, ip_proto, (CASE WHEN ip_src LIKE '10.0.156.%' THEN src_port ELSE dst_port END) AS port FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  port, ip_proto");
		// {remote_port=10000} REMOTE_IP
		views.put( "REMOTE_IP",
						"SELECT  stamp_inserted, SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  stamp_inserted,  remote_ip");
		legendViews.put( "REMOTE_IP",
						"SELECT SUM(CASE WHEN ip_dst LIKE '10.0.156.%' THEN bytes ELSE 0 END) as downloaded, SUM(CASE WHEN ip_src LIKE '10.0.156.%' THEN bytes ELSE 0 END) as uploaded, (CASE WHEN ip_src LIKE '10.0.156.%' THEN ip_dst ELSE ip_src END) AS remote_ip FROM "
								+ table
								+ " WHERE stamp_inserted >= '1970-01-01 01:00:00' AND stamp_inserted <= '" + date + "' AND (CASE WHEN ip_src  LIKE  '10.0.156.%' THEN dst_port ELSE src_port END) = 10000 AND ((NOT (ip_src LIKE '10.0.156.%') AND ip_dst  LIKE  '10.0.156.%') OR (NOT (ip_dst LIKE '10.0.156.%') AND ip_src  LIKE  '10.0.156.%'))  group by  remote_ip");
		theQueries.put("remote_port", views);
		theLegendQueries.put("remote_port", legendViews);

	}

	private String alterQuery(String query)
	{
		query = query.replace("stamp_inserted >= ", "datetime(stamp_inserted) >= ");
		query = query.replace("stamp_inserted <= ", "datetime(stamp_inserted) <= ");
		return query;
	}
	
	public void checkQuery(RequestParams requestParams, boolean isLong) throws SQLException, IOException, ParseException
	{
		List<View> views = View.getAvailableViews(requestParams);
		HashMap<String, Map<String, String>> theQueries;
		HashMap<String, Map<String, String>> theLegendQueries;
		if(isLong)
		{
			theQueries = longQueries;
			theLegendQueries = longLegendQueries;
		}
		else
		{
			theQueries = queries;
			theLegendQueries = legendQueries;
		}

		String stringParams = requestParams.getParams().keySet().toString()
				.replaceAll( "[\\],\\[]", "");
		for (View view : views)
		{
			String sql;
			requestParams.setView(view);
			m_queryBuilder.buildQuery(requestParams, true, isLong);//.toString().replaceAll( ".*: ", "");
			sql = m_queryBuilder.getQuery().replaceAll( ".*: ", "");
			//assertEquals(theQueries.get(stringParams).get(view.toString()), sql);
			if(TestConfiguration.getJdbcDriver().equals("org.sqlite.JDBC"))
			{
				String modifiedQuery = alterQuery(theQueries.get(stringParams).get(view.toString()));
				assertEquals(modifiedQuery, sql);
			}
			else
			{
				assertEquals(theQueries.get(stringParams).get(view.toString()), sql);
			}
			m_queryBuilder.buildQuery(requestParams, false, isLong);//.toString().replaceAll( ".*: ", "");
			sql = m_queryBuilder.getQuery();
			if(TestConfiguration.getJdbcDriver().equals("org.sqlite.JDBC"))
			{
				String modifiedQuery = alterQuery(theLegendQueries.get(stringParams).get(view.toString()));
				assertEquals(modifiedQuery, sql);
			}
			else
			{
				assertEquals(theLegendQueries.get(stringParams).get(view.toString()), sql);
			}
		}
	}
	
	public static Test suite()
	{
		return new TestSuite(QueryBuilderTest.class);
	}

}
