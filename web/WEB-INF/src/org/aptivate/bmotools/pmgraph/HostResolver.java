package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.talamonso.OMAPI.Connection;
import org.talamonso.OMAPI.Message;
import org.talamonso.OMAPI.Exceptions.OmapiConnectionException;
import org.talamonso.OMAPI.Exceptions.OmapiException;
import org.talamonso.OMAPI.Exceptions.OmapiInitException;
import org.talamonso.OMAPI.Objects.Lease;
import org.xbill.DNS.Address;

/**
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 * A class containing the host resolver method. In order to obtain the hostname
 * of a IP the DNS server is used if this fails de DHCP server is asked.
 * 
 */
public class HostResolver
{
	private static Logger m_logger = Logger.getLogger(HostResolver.class);

	private Connection m_connection;

	public HostResolver() throws IOException
	{

		try
		{
			if (!"".equalsIgnoreCase(Configuration.getDHCPAddress()))
			{
				this.m_connection = new Connection(Configuration
						.getDHCPAddress(), Configuration.getDHCPPort());
				this.m_connection.setAuth(Configuration.getDHCPName(),
						Configuration.getDHCPPass());
			}
			else
			{
				m_logger.info("DHCP name resolution disabled");
			}
		}
		catch (OmapiInitException e)
		{
			m_logger.info("Unable to get a connection to DHCP server possible wrong address (DHCP name resolution disabled)",
							e);
			this.m_connection = null;
		}
		catch (OmapiConnectionException e)
		{
			m_logger.info("Unable to connect to DHCP server possible key wrong or server disable (DHCP name resolution disabled)",
							e);
			this.m_connection = null;
		}
	}

	/**
	 * In order to obtain the hostname of a IP the DNS server is asked if this
	 * fails the DHCP server is asked. The DHCP server have to be configured in
	 * the config file of the application
	 * 
	 * @param IpAddress
	 *            String representation of the IP
	 * 
	 * @return An String containig the hostName for the provided IP. If the
	 *         lookup for the Hostname is unsucessfull return the string
	 *         "Unknown Host"
	 */
	public String getHostname(String IpAddress)
	{

		try
		{
			InetAddress inetadress = Address.getByAddress(IpAddress);
			return Address.getHostName(inetadress);
		}
		catch (UnknownHostException e)
		{
			m_logger.debug("Unknown host using DNS trying DHCP.");
			// Lets try using DHCP cause we can't get any info with DNS

			// If the DHCP server is available
			if (this.m_connection != null)
			{
				try
				{
					Lease l = new Lease(m_connection);
					l.setIPAddress(IpAddress);
					Lease remote = l.send(Message.OPEN);
					return remote.getClientHostname();
				}
				catch (OmapiException e1)
				{
					m_logger.info("Hostname not found using DNS and DHCP unknown host returned.",
									e1);
				}
			}
		}
		return ("Unknown Host");
	}

	protected void finalize()
	{
		this.m_connection.close();
	}

}
