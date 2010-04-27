package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Noe A. Rodriguez Glez.
 * 
 * Create the List of data needed to generate a Legend. Sort the Legend data
 * according to the user selection. Limit the results in the legend creating the
 * Others group.
 * 
 */
public class LegendData
{
	private DataAccess dataAccess;

	private Logger m_logger = Logger.getLogger(LegendData.class);

	private class UploadComparator implements Comparator<Object>
	{

		private boolean m_descending;

		public UploadComparator(boolean descending) {

			m_descending = descending;
		}

		public int compare(Object o1, Object o2)
		{

			DataPoint d1 = (DataPoint) o1;
			DataPoint d2 = (DataPoint) o2;
			if (m_descending)
				return (-d1.getUploaded().compareTo(d2.getUploaded()));
			else
				return (d1.getUploaded().compareTo(d2.getUploaded()));
		}

		public boolean equals(Object o)
		{
			return this == o;
		}
	}

	private class DownloadComparator implements Comparator<Object>
	{

		private boolean m_descending;

		public DownloadComparator(boolean descending) {

			m_descending = descending;
		}

		public int compare(Object o1, Object o2)
		{

			DataPoint d1 = (DataPoint) o1;
			DataPoint d2 = (DataPoint) o2;
			if (m_descending)
				return (-d1.getDownloaded().compareTo(d2.getDownloaded()));
			else
				return (d1.getDownloaded().compareTo(d2.getDownloaded()));
		}

		public boolean equals(Object o)
		{
			return this == o;
		}
	}

	/**
	 * Return an appropriate comparator for the requested sort
	 * 
	 * @param sortby
	 * @param order
	 * @return A comparator to be used to sort the results list
	 */
	private Comparator<Object> getComparator(String sortby, String order)
	{

		if (!"".equalsIgnoreCase(sortby))
		{
			if (QueryBuilder.UPLOADED.equalsIgnoreCase(sortby))
			{
				if ("DESC".equalsIgnoreCase(order))
					return (new UploadComparator(true));
				else
					return (new UploadComparator(false));
			} 
			else
			{
				if (QueryBuilder.BYTES.equalsIgnoreCase(sortby))
				{
					if ("DESC".equalsIgnoreCase(order))
						return (new BytesTotalComparator(true));
					else
						return (new BytesTotalComparator(false));
				} 
				else
				{
					if (QueryBuilder.DOWNLOADED.equalsIgnoreCase(sortby))
					{
						if ("DESC".equalsIgnoreCase(order))
							return (new DownloadComparator(true));
						else
							return (new DownloadComparator(false));
					}
				}
			}
			m_logger.error("Invalid sorting selected, no sorting assumed." +
					" User has set the sort parameter in the URL incorrectly");
		}
		return null;
	}
	
	/**
	 * Limit the list and sort it according to the user request
	 * 
	 * @param dataList
	 * @param sortBy
	 * @param order
	 * @param requestParams
	 * @return A List<DataPoint> limited to the number of results requested by
	 *         the user and sorted properly.
	 * @throws SQLException
	 */
	private Hashtable<Integer,List<DataPoint>> limitList(Hashtable<Integer,List<DataPoint>> dataHash, String sortBy,
			String order, RequestParams requestParams) throws SQLException
	{
		DataPoint others = null;
		Hashtable<Integer,List<DataPoint>> legendDataHash = new Hashtable<Integer,List<DataPoint>>();
		int cont = 1;
		for (Enumeration e = dataHash.keys(); e.hasMoreElements();) 
		{
			int key = (Integer) e.nextElement();
			List<DataPoint> dataList = dataHash.get(key);
			
			List<DataPoint> legendData = new ArrayList<DataPoint>();
			int i = 0;
			for (DataPoint portResult : dataList)
			{
				if (i < requestParams.getResultLimit())
				{
					legendData.add(portResult);
				} 
				else
				{
					if (i == requestParams.getResultLimit())
					{
						switch (requestParams.getView())
						{
							case LOCAL_PORT:
							case REMOTE_PORT:
								others = new PortDataPoint(PortDataPoint.OTHER_PORT);
								break;
							default:
							case LOCAL_IP:
							case REMOTE_IP:
								// Ip view
								others = new IpDataPoint(IpDataPoint.OTHER_IP);
								break;
						}

					}
					m_logger.debug("Legend view: " + requestParams.getView());
					m_logger.debug("other: " + others);

					others.addToUploaded(portResult.getUploaded());
					others.addToDownloaded(portResult.getDownloaded());
				}	
				i++;
			}
			if (others != null)
				legendData.add(others);
			if (!legendData.isEmpty()) {
				legendDataHash.put(cont, legendData);
				cont++;
			}
		}
		// once the results that are going to be shown are selected, sort them
		// according to what the user has requested.
		Comparator<Object> c = getComparator(sortBy, order);
		if (c != null)
			for (Enumeration e = legendDataHash.keys(); e.hasMoreElements();) 
			{
				int key = (Integer) e.nextElement();
				List<DataPoint> legendData = legendDataHash.get(key);
				if (!legendData.isEmpty()) 
				{
					Collections.sort(legendData, c);
					legendDataHash.put(key, legendData);
				}
			}
		return legendDataHash;
	}

	public LegendData() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {

		dataAccess = new DataAccess();
	}

	/**
	 * Get a list of the Legend data sorted by total bytes order
	 * 
	 * @param sortBy
	 * @param order
	 * @param requestParams
	 * @return The generated List containing the data that should be shown on
	 *         the legend when the graph is a legend data graph.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ConfigurationException
	 */
	
	public Hashtable<Integer,List<DataPoint>> getLegendData(String sortBy, String order,
			RequestParams requestParams, boolean isLong) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException, ConfigurationException
	{

		Hashtable<Integer,List<DataPoint>> ipResults = dataAccess.getThroughput(requestParams,
				false, isLong);
		
		for (Enumeration enumListResult = ipResults.keys (); enumListResult.hasMoreElements ();) 
		{
			int key = (Integer) enumListResult.nextElement();
			List<DataPoint> listResults = ipResults.get(key);
			if (!listResults.isEmpty()) {
				Collections.sort(listResults, new BytesTotalComparator(true));
				ipResults.put(key, listResults);
			}
		}

		return (limitList(ipResults, sortBy, order, requestParams));
	}
}
