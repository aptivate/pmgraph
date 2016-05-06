package org.aptivate.bmotools.pmgraph;

public interface Resolver {
	public String getHostname(String IpAddress);
	
	public static class FakeResolver implements Resolver
	{
		public String getHostname(String IpAddress)
		{
			return "Unknown Host";
		}
	}
}
