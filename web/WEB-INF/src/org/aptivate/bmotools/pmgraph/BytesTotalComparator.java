package org.aptivate.bmotools.pmgraph;

import java.util.Comparator;

/**
 * 
 * @author Noe A. Rodriguez Glez.
 * 
 * Just a simple comparator to be used when the methods of java Collection are
 * used. This comparator sorts the results in a List of GraphData by the
 * byteTotal field.
 */

class BytesTotalComparator implements Comparator<Object>
{

	private boolean m_descending;

	public BytesTotalComparator(boolean descending) {

		m_descending = descending;
	}

	public int compare(Object o1, Object o2)
	{
		DataPoint d1 = (DataPoint) o1;
		DataPoint d2 = (DataPoint) o2;
		if (m_descending)
			return (-d1.getBytesTotal().compareTo(d2.getBytesTotal()));
		else
			return (d1.getBytesTotal().compareTo(d2.getBytesTotal()));
	}

	public boolean equals(Object o)
	{
		return this == o;
	}
}
