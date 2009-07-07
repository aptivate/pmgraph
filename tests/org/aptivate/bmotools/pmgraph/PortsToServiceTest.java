package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class is designed to test the class Port2Services.java which associates
 * the port number to a service. In order to do so, some common ports have been
 * checked.
 * 
 * @author blancab
 */
public class PortsToServiceTest extends TestCase
{
	/**
	 * Check if the association services to the port number for udp and tcp is
	 * correctly done
	 * 
	 * @throws Exception
	 */
	public void testPortsToService() throws Exception
	{
		int ports[] = { 21, 22, 25, 69, 80, 101, 123, 143, 443, 992 };
		String servicesUDP[] = { "ftp", "ssh", "smtp", "tftp", "http", "hostname", "ntp", "imap",
				"https", "telnets" };
		String servicesTCP[] = { "ftp", "ssh", "smtp", "tftp", "http", "hostname", "ntp", "imap",
				"https", "telnets" };

		Port2Services portService = Port2Services.getInstance();
		for (int i = 0; i < ports.length; i++)
		{
			assertEquals("Port to Services match alert.", servicesUDP[i], portService.getService(
					ports[i], Protocol.udp));
			assertEquals("Port to Services match alert.", servicesTCP[i], portService.getService(
					ports[i], Protocol.tcp));
		}

	}

	public static Test suite()
	{
		return new TestSuite(PortsToServiceTest.class);
	}
}
