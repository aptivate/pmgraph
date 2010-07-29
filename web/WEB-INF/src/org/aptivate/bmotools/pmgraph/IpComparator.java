package org.aptivate.bmotools.pmgraph;

import java.util.Comparator;

public class IpComparator implements Comparator<String>
{
	public int compare (String arg1, String arg2)
	{
		String[] ip1 = arg1.split("\\.");
		String[] ip2 = arg2.split("\\.");
		for(int i = 0; i < ip1.length; i++)
		{
			Integer ip1Part = new Integer(ip1[i]);
			Integer ip2Part = new Integer(ip2[i]);
			int comp = ip1Part.compareTo(ip2Part);
			if(comp!=0)
			{
				return comp;
			}
		}
		return 0;
	}
	

}
