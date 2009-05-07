package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessControlException;

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
 * A class containing the host resolver method. The DNS server is used to obtain 
 * the hostname, if this fails the DHCP server is used.
 * 
 */
public class HostResolver
{
	private static Logger m_logger = Logger.getLogger(HostResolver.class);

	private Connection m_connection;
	private final int HOSTNAME_LENGTH_LIMIT = 40;
	
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
			m_logger.info("Unable to get a connection to DHCP server "
					+ "possible wrong address (DHCP name resolution disabled)",
					e);
			this.m_connection = null;
		}
		catch (OmapiConnectionException e)
		{
			m_logger.info("Unable to connect to DHCP server possible "
					+ "key wrong or server disable (DHCP name resolution "
					+ "disabled)", e);
			this.m_connection = null;
		}
	}

	private String limitString (String text) {
		
		if (text.length() > HOSTNAME_LENGTH_LIMIT) {
			return (text.substring(0, HOSTNAME_LENGTH_LIMIT)+"...");
		}
		return text;
	}
	
	
	/**
	 * The DNS server is used to obtain the hostname of a IP, if this
	 * fails the DHCP server is used. The DHCP server has to be configured in
	 * the config file of the application
	 * 
	 * @param IpAddress
	 *            String representation of the IP
	 * 
	 * @return An String containing the hostName for the provided IP. If the
	 *         lookup for the Hostname is unsuccessful return the string
	 *         "Unknown Host"
	 */
	public String getHostname(String IpAddress)
	{
		String hostName = "Unknown Host";

		try
		{
			InetAddress inetadress = Address.getByAddress(IpAddress);
			hostName = Address.getHostName(inetadress);
		}
		catch (java.lang.Error e)
		{
			m_logger.error(ErrorMessages.DNS_ERROR_JAVA_SECURITY);
			m_logger.error(e);
		}
		catch (Exception e)
		{
			if (e instanceof AccessControlException)
				m_logger.error(ErrorMessages.DNS_ERROR_JAVA_SECURITY, e);

			m_logger.debug("Unknown host using DNS trying DHCP.");
			// Lets try using DHCP because we can't get any info with DNS

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
					m_logger.info("Hostname not found using "
							+ "DNS and DHCP unknown host returned.", e1);
				}
			}
		}
		return ( limitString (hostName));
	}

	protected void finalize()
	{
		this.m_connection.close();
	}

}
