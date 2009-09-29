package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.util.*;

/**
 * Generic methods, currently all for displaying graphs with a lower sample rate
 * @author daniell*/

public class TimeSpanUtils 
{
	private static final long DAY = 86400000; 
	
	/**
	 * Gets the time period for each step on th x-axis of the graph
	 * @param isLong Whether or not a long time period is covered
	 * @return The value of each step on the x-axis of the graph as a long value in milliseconds
	 */
	public static int getResolution(boolean isLong, long timeSpan)
	{
		int requiredTimePeriod = TimePeriod.MINUTE.getTimePeriod();
		if(isLong)
		{
			int[] timePeriods = TimePeriod.getTimePeriodLengths();
			boolean found = false;
			long[] times = getTimeSpanLengths();
			if(times.length != 0)
			{
				for(int period : timePeriods)
				{
					// Ensure that the time period divided by the timespan is less than the number of minutes in a day
					// If it isn't then there's no real point in using it.
					for(long time : times)
					{
						if(timeSpan >= time)
						{
							if(timeSpan/(long)period < (60 * 24))
							{
								requiredTimePeriod = period;
								found = true;
								break;
							}
						}
					}
					if(!found)
					{
						requiredTimePeriod = timePeriods[timePeriods.length - 1];
					}
					else
					{
						break;
					}
				}
			}
		}
		return requiredTimePeriod;
	}
	
	/**
	 * This gets the table in which the data is stored
	 * @param timeSpan The length of time covered by the graph
	 * @return The name of the table
	 * @throws IOException
	 */
	public static String findTable(long timeSpan) throws IOException
	{
		long[] timeSpans = getTimeSpanLengths();
		for(int i = timeSpans.length - 1; i >= 0; i--)
		{
			if(timeSpan >= timeSpans[i])
			{
				switch(i){
					case 0:
						return Configuration.getResultDatabaseLongTable();
					case 1:
						String table = Configuration.getResultDatabaseVeryLongTable();
						if(table == null)
						{
							table = Configuration.getResultDatabaseLongTable();
						}
						return table;					
					default:
						return Configuration.getResultDatabaseLongTable();
				}
			}
		}
		return Configuration.getResultDatabaseTable();
	}
	
	/**
	 * This gets the different timespans for determining the period of data covered in the graph in ascending order
	 * @return An array of long values representing the timespans
	 */
	public static long[] getTimeSpanLengths()
	{
		long[] values;
		List<Long> temp = new ArrayList<Long>();
		try
		{
			String[] timeSpans = Configuration.getTimespansForLongGraph().split(",");
			for(String timeSpan : timeSpans)
			{
				try
				{
					long tempVal = Long.parseLong(timeSpan.trim()) * 60 * 60 * 1000;
					if(tempVal >= DAY)
					{
						temp.add(new Long(tempVal));
					}
				}
				catch(NumberFormatException e)
				{
					
				}
			}
			values = new long[temp.size()];
			for(int i = 0; i < values.length; i++)
			{
				values[i] = temp.get(i).longValue();
			}
			Arrays.sort(values);
			return values;
		}
		catch(IOException e)
		{
			values = new long[0];
			return values;
		}
		catch(NullPointerException e)
		{
			values = new long[0];
			return values;
		}
	}
	
	/**
	 * Determines if the graph should display data over a long time period
	 * @param start The time the graph shows data from
	 * @param end The time the graph shows data to
	 * @return a value saying whether or not the graph data should cover a long time period
	 */
	public static boolean needsLongGraph(long start, long end)
	{
		long[] timeSpans = getTimeSpanLengths();
		for(long timeSpan : timeSpans)
		{
			if(end - start >= timeSpan)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check that the displaying of long graphs has been enabled
	 * @return A boolean saying whether or not long graphs are enabled
	 */
	public static boolean longGraphIsAllowed()
	{
		try
		{
			String[] timeSpans = Configuration.getTimespansForLongGraph().split(",");
			for(String timeSpan : timeSpans)
			{
				long aTimeSpan = 0;
				try
				{
					aTimeSpan = Long.parseLong(timeSpan.trim());
				}
				catch(NumberFormatException e)
				{
					
				}
				// You can't display a long graph with time periods of less than one day.
				if(aTimeSpan >= 24)
				{
					return true;
				}
			}
			return false;
		}
		catch(IOException e)
		{
			return false;
		}
		catch(NullPointerException e)
		{
			return false;
		}
	}
}
