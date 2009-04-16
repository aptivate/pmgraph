package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * 
 * @author Noe A. Rodriguez Glez.
 * 
 * Create the List of data necesary for generate a Leyenda.
 * 
 */
public class LegendData
{
	private DataAccess dataAccess;

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
				return (0 - d1.getUploaded().compareTo(d2.getUploaded()));
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
				return (0 - d1.getDownloaded().compareTo(d2.getDownloaded()));
			else
				return (d1.getDownloaded().compareTo(d2.getDownloaded()));
		}

		public boolean equals(Object o)
		{
			return this == o;
		}
	}

	/**
	 * Retun an appropiate comparator for de requested sorting
	 * 
	 * @param sortby
	 * @param order
	 * @return A conparator to be used in the sorting of the results list
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
		}
		return null;
	}

	/**
	 * Limit the list and sort it acondinggly the user request
	 * 
	 * @param dataList
	 * @param sortBy
	 * @param order
	 * @param pageUrl
	 * @param isPort
	 * @return A List<GraphData> limited to the number of results requested by 
	 * the user and sorted propertly.
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
					switch (requestParams.getView()) {
						case LOCAL_PORT:
							others = new GraphData(null, GraphFactory.OTHER_IP,
									0L, 0L, GraphFactory.OTHER_PORT);
							break;
						case REMOTE_PORT:
							// For port views constructor is diferent
							// Is for a specific IP get the Ip from the request
							if (requestParams.getIp() != null)
								others = new GraphData(null, requestParams.getIp(), 0L,
										0L, GraphFactory.OTHER_PORT);
							break;
						case LOCAL_IP:
							// Ip view
							others = new GraphData(GraphFactory.OTHER_IP, 0L, 0L);
						break;
						case REMOTE_IP:
							// Ip view
							others = new GraphData(null,
									requestParams.getIp(),GraphFactory.OTHER_IP, 0L, 0L);

					}
						
				}
				others.setUploaded(others.getUploaded()
						+ portResult.getUploaded());
				others.setDownloaded(others.getDownloaded()
						+ portResult.getDownloaded());
			}
			i++;
		}
		if (others != null)
			legendData.add(others);
		// once the result that are going to be showed are selected sort then
		// acordingly what the user has requested.
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
	 * @return The generated List containing the data that should be 
	 * showed on the legend when the graph is a legend data graph.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws SQLException
	 */
	public List<GraphData> getLegendData(String sortBy,
			String order, RequestParams requestParams) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, IOException,
			SQLException
	{

		List<GraphData> ipResults = dataAccess.getThroughput(requestParams, false);
		// allways sort using Bytes total to have same order that in the graph
		Collections.sort(ipResults, new BytesTotalComparator(true));

		return limitList(ipResults, sortBy, order, requestParams);
	}
}
