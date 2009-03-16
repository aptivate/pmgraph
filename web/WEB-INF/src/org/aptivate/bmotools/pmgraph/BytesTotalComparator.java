package org.aptivate.bmotools.pmgraph;

import java.util.Comparator;

/**
 * 
 * @author Noe A. Rodriguez Glez.
 *
 *  Just a simple comparator to be used when the method of java Collection
 *  are used. This comparator sort the result in a List of GraphData by the
 *  byteTotal field.
 */

public class BytesTotalComparator implements Comparator
{

	private boolean m_descending;

	public BytesTotalComparator(boolean descending)
	{

		m_descending = descending;
	}

	public int compare(Object o1, Object o2)
	{
		GraphData d1 = (GraphData) o1;
		GraphData d2 = (GraphData) o2;
		if (m_descending)
			return (0 - d1.getBytesTotal().compareTo(d2.getBytesTotal()));
		else
			return (d1.getBytesTotal().compareTo(d2.getBytesTotal()));
	}

	public boolean equals(Object o)
	{
		return this == o;
	}
}
