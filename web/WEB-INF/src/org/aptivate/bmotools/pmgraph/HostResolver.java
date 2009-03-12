/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.talamonso.OMAPI.Connection;
import org.talamonso.OMAPI.Message;
import org.talamonso.OMAPI.Exceptions.OmapiConnectionException;
import org.talamonso.OMAPI.Exceptions.OmapiException;
import org.talamonso.OMAPI.Exceptions.OmapiInitException;
import org.talamonso.OMAPI.Objects.Lease;
import org.xbill.DNS.Address;

/**
 * @author noeg
 * 
 */
public class HostResolver {

	private Connection m_connection;

	public HostResolver() throws OmapiConnectionException, OmapiInitException, IOException {

		this.m_connection = new Connection(Configuration.getDHCPAddress(),Configuration.getDHCPPort());
		this.m_connection.setAuth(Configuration.getDHCPName(), Configuration.getDHCPPass());
	}

	public String getHostname(String IpAddress) {

		try {
			InetAddress inetadress = Address.getByAddress(IpAddress);
			return Address.getHostName(inetadress);
		} catch (UnknownHostException e) {
			// Lets try using DHCP cause we can't get any info with DNS
			try {
				Lease l = new Lease(m_connection);
				l.setIPAddress(IpAddress);
				Lease remote = l.send(Message.OPEN);
				return remote.getClientHostname();
			} catch (OmapiException e1) {
				System.out.println("that o just not hostname found on the dhcp then unknown host returned");
				//e1.printStackTrace();
			}
		}
		return ("Unknown Host");
	}

	protected void finalize() {
		this.m_connection.close();
	}

}
