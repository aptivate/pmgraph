package org.aptivate.bmotools.pmgraph;

import java.util.*;
/**
 * Used for determining the time period displayed on the graph
 * @author daniell
 *
 */
public enum TimePeriod
{
	// Time periods should be declared in ascending order of length
	HOUR (3600000),
	MINUTE (60000);

	private final int m_timePeriod;
	
	TimePeriod(int timePeriod)
	{
		this.m_timePeriod = timePeriod;
	}
	
	/**
	 * Returns the length of the time period to be used
	 */
	int getTimePeriod()
	{
		return m_timePeriod;
	}
	
	/**
	 * This returns the lengths of the timeperiods in ascending order. Useful for finding an appropriet value in the array.
	 * @return An array of time periods sorted in ascending order.
	 */
	static int[] getTimePeriodLengths()
	{
		TimePeriod[] timePeriods = TimePeriod.values();
		int[] timeValues = new int[TimePeriod.values().length];
		for(int i = 0; i < timePeriods.length; i++)
		{
			timeValues[i] = timePeriods[i].getTimePeriod();
		}
		Arrays.sort(timeValues);
		return timeValues;
	}
}
