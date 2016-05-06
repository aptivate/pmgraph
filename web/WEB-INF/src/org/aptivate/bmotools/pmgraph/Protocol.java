package org.aptivate.bmotools.pmgraph;

/**
 * The different values that the protocol takes in the database
 * 
 * @author noeg
 * 
 */
public enum Protocol {
	// Show IPs or show ports in graph
	tcp, icmp, udp, other;
	
	public static Protocol fromString(String protocol)
	{
		if(protocol.equals("tcp") || protocol.equals("udp") || 
				protocol.equals("icmp"))
		{
			return Protocol.valueOf(protocol);
		}
		else
		{
			return other;
		}
	}
}
