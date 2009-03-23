/**
 * 
 */
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
 * 	Create the List of data necesary for generate a Leyenda.   
 *
 */
public class LegendData
{
	private DataAccess dataAccess;
	
	private class UploadComparator  implements Comparator {

		private boolean m_descending;
		
		public UploadComparator (boolean descending) {
		
				m_descending= descending;
		}
		
		
	  public int compare(Object o1, Object o2) {
		  
	    GraphData d1 = (GraphData) o1;
	    GraphData d2 = (GraphData) o2;
	    if (m_descending)
	    	return (0 - d1.getUploaded().compareTo(d2.getUploaded()));
	    else
	    	return (d1.getUploaded().compareTo(d2.getUploaded())); 	
	  }
	  

	  public boolean equals(Object o) {
	    return this == o;
	  }
	}

	private class DownloadComparator implements Comparator  {

		private boolean m_descending;
		
		public DownloadComparator (boolean descending) {
		
				m_descending= descending;
		}
		
		
	  public int compare(Object o1, Object o2) {
		  
	    GraphData d1 = (GraphData) o1;
	    GraphData d2 = (GraphData) o2;
	    if (m_descending)
	    	return (0 - d1.getDownloaded().compareTo(d2.getDownloaded()));
	    else
	    	return (d1.getDownloaded().compareTo(d2.getDownloaded()));    	
	  }
	  

	  public boolean equals(Object o) {
	    return this == o;
	  }
	}
	
	/**
	 * 	Retun an appropiate comparator for de requested sorting
	 * @param sortby
	 * @param order
	 * @return A conparator to be used in the sorting of the
	 *  results list
	 */	
	private Comparator getComparator (String sortby, String order) {
		
		if (!"".equalsIgnoreCase(sortby)) {
			if (dataAccess.UPLOADED.equalsIgnoreCase(sortby)) {
				if ("DESC".equalsIgnoreCase(order))
					return(new UploadComparator(true));
				else
					return(new UploadComparator(false));
			} else {
				if (dataAccess.BYTES.equalsIgnoreCase(sortby)) {
					if ("DESC".equalsIgnoreCase(order))
						return(new BytesTotalComparator(true));
					else
						return(new BytesTotalComparator(false));
				} else {			
					if (dataAccess.DOWNLOADED.equalsIgnoreCase(sortby)) {
						if ("DESC".equalsIgnoreCase(order))
							return(new DownloadComparator(true));
						else
							return(new DownloadComparator(false));
					}
				}
			}
		}	
		return null;
	}

	
	public LegendData () throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		 
		dataAccess = new DataAccess();		
	}
	
	public List<GraphData> getLegendData (long start, long end, String sortBy, String order, Integer limitResult) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, SQLException {
		List<GraphData> legendData = new ArrayList<GraphData>();
		GraphData otherIps = null;
		
		List<GraphData> ipResults = dataAccess.getThroughputPerIP(start, end);
		// allways sort using Bytes total to have same order that in the graph
		Collections.sort(ipResults, new BytesTotalComparator(true));
		
		int i = 0;
	   for (GraphData ipResult : ipResults) 
	    {		    			        
			if (i < limitResult) {
				legendData.add (ipResult);
		    } 
		    else 
		    {
		    	if (i == limitResult)
			    	otherIps = new GraphData ("255.255.255.255", 0L, 0L, 0L);
		    	otherIps.setUploaded (otherIps.getUploaded()+ipResult.getUploaded());
		    	otherIps.setDownloaded (otherIps.getDownloaded()+ipResult.getDownloaded());
		    	otherIps.setBytesTotal (otherIps.getBytesTotal()+ipResult.getBytesTotal());
		    }
			i++;
	    }
	   	if (otherIps != null)
	   		legendData.add (otherIps);
	   	// once the result that are going to be showed are selected sort then 
	   	// acordingly what the user has requested.
   	Comparator c = getComparator(sortBy, order);
	   	if (c != null)
	   		Collections.sort(legendData, getComparator(sortBy, order));	   	
		
		return legendData;		
		
	}

}
