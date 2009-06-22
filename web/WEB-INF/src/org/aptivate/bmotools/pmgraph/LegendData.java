package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Noe A. Rodriguez Glez.
 * 
 * Create the List of data needed to generate a Legend.
 * Sort the Legend data according to the user selection
 * Limit the results in the legend creating the Others
 * group.
 * 
 */
public class LegendData
{
	private DataAccess dataAccess;

	private Logger m_logger = Logger.getLogger(LegendData.class);

	private class UploadComparator implements Comparator
	{

		private boolean m_descending;

		public UploadComparator(boolean descending)
		{

			m_descending = descending;
		}

		public int compare(Object o1, Object o2)
		{

			GraphData d1 = (GraphData) o1;
			GraphData d2 = (GraphData) o2;
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

	private class DownloadComparator implements Comparator
	{

		private boolean m_descending;

		public DownloadComparator(boolean descending)
		{

			m_descending = descending;
		}

		public int compare(Object o1, Object o2)
		{

			GraphData d1 = (GraphData) o1;
			GraphData d2 = (GraphData) o2;
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
	private Comparator getComparator(String sortby, String order)
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
			m_logger.error ("Invalid sorting selected, no sorting assumed. User has set the sort parameter in the URL incorrectly");
		}
		return null;
	}

	/**
	 * Limit the list and sort it according to the user request
	 * 
	 * @param dataList
	 * @param sortBy
	 * @param order
	 * @param pageUrl
	 * @param isPort
	 * @return A List<GraphData> limited to the number of results requested by
	 *         the user and sorted properly.
	 * @throws SQLException
	 */
	private List<GraphData> limitList(List<GraphData> dataList, String sortBy,
			String order, RequestParams requestParams) throws SQLException
	{
		List<GraphData> legendData = new ArrayList<GraphData>();
		GraphData others = null;
		int i = 0;
		for (GraphData portResult : dataList)
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
							others = new GraphData(null, GraphFactory.OTHER_IP,
									0L, 0L, GraphFactory.OTHER_PORT);
							break;
						case REMOTE_PORT:
							// For port views, constructor is different
							// If it is for a specific IP get the Ip from the request
							others = new GraphData(null, requestParams.getIp(),
									0L, 0L);
							others.setRemotePort(GraphFactory.OTHER_PORT);
							break;
						case REMOTE_IP:
							// Ip view
							others = new GraphData(null, requestParams.getIp(),
									GraphFactory.OTHER_IP, 0L, 0L);
							break;
						default:
						case LOCAL_IP:
							// Ip view
							others = new GraphData(GraphFactory.OTHER_IP, 0L,
									0L);
							break;
					}

				}
				m_logger.debug("Legend view: " + requestParams.getView());
				m_logger.debug("other: " + others);

				others.setUploaded(others.getUploaded()
						+ portResult.getUploaded());
				others.setDownloaded(others.getDownloaded()
						+ portResult.getDownloaded());
			}
			i++;
		}
		if (others != null)
			legendData.add(others);
		// once the results that are going to be shown are selected, sort them
		// according to what the user has requested.
		Comparator c = getComparator(sortBy, order);
		if (c != null)
			Collections.sort(legendData, c);

		return legendData;
	}

	public LegendData() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{

		dataAccess = new DataAccess();
	}

	/**
	 * 
	 * @param start
	 * @param end
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
	 * @throws PageUrlException
	 */
	public List<GraphData> getLegendData(String sortBy, String order,
			RequestParams requestParams) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException, ConfigurationException
	{

		List<GraphData> ipResults = dataAccess.getThroughput(requestParams,
				false);
		// always sort using Bytes total to keep the same order as in the graph
		Collections.sort(ipResults, new BytesTotalComparator(true));

		return limitList(ipResults, sortBy, order, requestParams);
	}
}
