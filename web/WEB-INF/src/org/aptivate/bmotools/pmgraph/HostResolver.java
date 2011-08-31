package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import libAPI.ApiConn;

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

	private Connection m_OmapiConnection;
	private ApiConn m_MikrotikApi;
	private final int HOSTNAME_LENGTH_LIMIT = 40;
	private Map<String, String> m_MikrotikDhcpCache;
	
	public HostResolver() throws IOException
	{
		this.m_OmapiConnection = null;

		try
		{
			if (! Configuration.getDHCPAddress().isEmpty())
			{
				this.m_OmapiConnection = new Connection(Configuration
						.getDHCPAddress(), Configuration.getDHCPPort());
				this.m_OmapiConnection.setAuth(Configuration.getDHCPName(),
						Configuration.getDHCPPass());
			}
			else
			{
				m_logger.info("DHCP name resolution disabled");
			}
		}
		catch (OmapiInitException e)
		{
			m_logger.info("Unable to get a connection to DHCP server: "
					+ "possibly wrong IP address? DHCP name resolution disabled.",
					e);
		}
		catch (OmapiConnectionException e)
		{
			m_logger.info("Unable to connect to DHCP server: possibly "
					+ "wrong key or server disabled? DHCP name resolution "
					+ "disabled", e);
		}

		this.m_MikrotikApi = null;

		try
		{
			if (! Configuration.getMikrotikAddress().isEmpty())
			{
				m_MikrotikApi = new ApiConn(Configuration.getMikrotikAddress(),
							Configuration.getMikrotikApiPort());
				
			       if (!m_MikrotikApi.isConnected())
			       {
			    	   m_MikrotikApi.start();
			    	   m_MikrotikApi.join();
			       }
			       
			       if (!m_MikrotikApi.isConnected())
			       {
			    	   Exception e = m_MikrotikApi.getStoredException();
			    	   
			    	   if (e != null)
			    	   {
			    		   throw new Exception("Connection to Mikrotik failed", e);
			    	   }
			    	   else
			    	   {
			    		   throw new Exception("Connection to Mikrotik " +
			    		   		"failed (generic)");
			    	   }
			       }
			       
			       m_MikrotikApi.login(Configuration.getMikrotikUser(),
			    		   Configuration.getMikrotikPass().toCharArray());
			       
			       /*
			       m_MikrotikApi.sendCommand("/ip/address/print");
			       String result = m_MikrotikApi.getData();
			       m_logger.info("Connected to Mikrotik: " + result);
			       */
			       
			       m_MikrotikDhcpCache = new HashMap<String, String>();
			       m_MikrotikApi.sendCommand("/ip/dhcp-server/lease/print");
			       
			       while (true)
		    	   {
			    	   String result = m_MikrotikApi.getData();
			    	   m_logger.debug("Microtik API returned: " + result);
			    	   
			    	   /*
			    	    * Results look like this:
			    	    * 
			    	    * <pre>
!re
=.id=*1D
=address=192.168.88.237
=mac-address=00:D0:4B:91:13:A0
=server=default
=status=bound
=expires-after=1d22:36:21
=last-seen=1d1h23m39s
=active-address=192.168.88.237
=active-mac-address=00:D0:4B:91:13:A0
=active-server=default
=host-name=Lacie
=radius=false
=dynamic=true
=blocked=false
=disabled=false
=comment=</pre>
			    	    */
			    	   if (!result.startsWith("\n!"))
			    	   {
			    		   m_logger.warn("Mikrotik API returned unknown string: " + result);
			    		   break;
			    	   }

			    	   String [] lines = result.split("\n");
			    	   String resultVerb = lines[1];

		    		   if (resultVerb.equals("!re"))
		    		   {
		    			   String address = null, hostName = null;
		    			   
		    			   for (int i = 2; i < lines.length; i++)
		    			   {
		    				   String line = lines[i];
		    				   if (line.startsWith("="))
		    				   {
			    				   String [] parts = line.substring(1).split("=", 2);
			    				   
			    				   if (parts[0].equals("address"))
			    				   {
			    					   address = parts[1];
			    				   }
			    				   else if (parts[0].equals("host-name"))
			    				   {
			    					   hostName = parts[1];
			    				   }
		    				   }
		    				   else
		    				   {
		    					   m_logger.info("Ignoring unknown line " +
		    					   		"in result: " + line);
		    				   }
		    			   }
		    			   
		    			   if (address != null && hostName != null)
		    			   {
		    				   m_MikrotikDhcpCache.put(address, hostName);
		    			   }
		    		   }
		    		   else if (resultVerb.equals("!trap"))
			    	   {
			    		   m_logger.warn("Mikrotik API returned error: " + result);
			    		   break;
			    	   }
		    		   else if (resultVerb.equals("!done"))
			    	   {
			    		   break;
			    	   }
		    		   else
		    		   {
		    			   m_logger.warn("Mikrotik API returned unexpected " +
		    			   		"result: " + result);
		    		   }
			       }
			}
			else
			{
				m_logger.info("Mikrotik DHCP name resolution disabled");
			}
		}
		catch (Exception e)
		{
			m_logger.info("Unable to get a connection to Mikrotik router: "
					+ "possible wrong address. DHCP name resolution disabled",
					e);
		}
	}

	private String limitString (String text) {
		
		if (text.length() > HOSTNAME_LENGTH_LIMIT)
		{
			return text.substring(0, HOSTNAME_LENGTH_LIMIT) + "...";
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
		String hostName = null;

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
			
			if (e instanceof UnknownHostException)
			{
				m_logger.debug("Failed to resolve " + IpAddress + 
						" to hostname using DNS: unknown host");
			}
			else
			{
				m_logger.warn("Failed to resolve hostname using DNS", e);
			}
			
			// If the DHCP server is available
			if (this.m_OmapiConnection != null)
			{
				// Lets try using DHCP because we can't get any info with DNS
				try
				{
					Lease l = new Lease(m_OmapiConnection);
					l.setIPAddress(IpAddress);
					Lease remote = l.send(Message.OPEN);
					hostName = remote.getClientHostname();
				}
				catch (OmapiException e1)
				{
					m_logger.info("Failed to resolve hostname using DHCP server", e1);
				}
			}
			else
			{
				m_logger.debug("Failed to resolve " + IpAddress + " using " +
						"DHCP server: OMAPI is not configured");
			}
			
			if (hostName == null)
			{
				if (m_MikrotikApi != null)
				{
					hostName = m_MikrotikDhcpCache.get(IpAddress);
					
					if (hostName == null)
					{
						m_logger.debug("Failed to resolve hostname using Mikrotik API: " +
								"entry not found for " + IpAddress);
					}
				}
				else
				{
					m_logger.debug("Failed to resolve hostname using Mikrotik API: " +
							"not configured");
				}
			}
		}
		
		if (hostName == null)
		{
			m_logger.info("Failed to resolve hostname using any " +
					"available method: " + IpAddress);
			return "Unknown Host";
		}
		else
		{
			m_logger.info("Resolved hostname for " + IpAddress + " to " +
					hostName);
			return limitString(hostName);
		}
	}

	protected void finalize()
	{
		if (m_OmapiConnection != null)
		{
			m_OmapiConnection.close();
		}
		
		if (m_MikrotikApi != null)
		{
			try
			{
				m_MikrotikApi.disconnect();
			}
			catch (IOException e)
			{
				m_logger.info("Failed to disconnect from Mikrotik", e);
			}
		}
	}

}
