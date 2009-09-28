package org.aptivate.bmotools.pmgraph;

/**
 * Used for determining the time period displayed on the graph
 * @author daniell
 *
 */
public enum TimePeriod
{
	//DAY (86400000),
	HOUR (3600000),
	MINUTE (60000);

	private final int timePeriod;
	TimePeriod(int timePeriod)
	{
		this.timePeriod = timePeriod;
	}
	
	/**
	 * Returns the length of the time period to be used
	 */
	public int getTimePeriod()
	{
		return timePeriod;
	}
	
	/**
	 * This returns the timeperiods in acending order. Useful for finidng an appropriet value in the array.
	 * @return An array of time periods sorted in ascending order.
	 */
	public static TimePeriod[] getValues()
	{
		TimePeriod[] timePeriods = TimePeriod.values();
		boolean exchange;
		do
		{
			exchange = false;
			for(int i = 0; i < timePeriods.length - 1; i++)
			{
				if(compare(timePeriods[i], timePeriods[i + 1]) > 0)
				{
					TimePeriod temp = timePeriods[i + 1];
					timePeriods[i + 1] = timePeriods[i];
					timePeriods[i] = temp;
					exchange = true;
				}
			}
		}while(exchange);
		return timePeriods;
	}
	
	/**
	 * Compare time periods and return an integer value depending on the result
	 * @param time1 The first TimePeriod
	 * @param time2 The second TimePeriod
	 * @return The diffrence between the first time period and the second time period
	 */
	public static int compare(TimePeriod time1, TimePeriod time2)
	{
		return time1.getTimePeriod() - time2.getTimePeriod();
	}
}
