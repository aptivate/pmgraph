/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Noe A. Rodriguez Glez.
 * 
 * Bean which contains a row of information collected from the pmacct database.
 * 
 */
public class GraphData
{
	private Timestamp m_time;

	private String m_localIp;
	
	private String m_remoteIp;

	private Long m_downloaded;

	private Long m_uploaded;

	private Integer m_port;
	
	private Integer m_remotePort;
	
	private Protocol m_protocol;

	/**
	 * Create a GraphData object from a result set using the columns returned
	 * by the database query.
	 * 
	 * @param rs
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public GraphData(ResultSet rs, List columns) throws SQLException
	{

		if (columns.contains("stamp_inserted"))
			setTime(rs.getTimestamp("stamp_inserted"));
		setDownloaded(rs.getLong("downloaded"));
		setUploaded(rs.getLong("uploaded"));		
		if (columns.contains("local_ip"))
			setLocalIp(rs.getString("local_ip"));
		if (columns.contains("remote_ip"))
			setRemoteIp(rs.getString("remote_ip"));
		if (columns.contains("port"))
			setPort(rs.getInt("port"));
		if (columns.contains("remote_port"))	
			setRemotePort(rs.getInt("remote_port"));
		if (columns.contains("ip_proto"))	
			setProtocol(Protocol.valueOf (rs.getString("ip_proto")));
	}
	
	public GraphData(GraphData source) 
	{
		this.m_time = source.m_time;
		this.m_localIp = source.m_localIp;
		this.m_downloaded = source.m_downloaded;
		this.m_uploaded = source.m_uploaded;		
		this.m_remoteIp = source.m_remoteIp;
		this.m_remotePort = source.m_remotePort;
		this.m_port = source.m_port;
		this.m_protocol = source.m_protocol;
	}

	public GraphData(Timestamp time, String localIp, Long downloaded,
			Long uploaded) throws SQLException
	{
		this.m_time = time;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;
	}

	public GraphData(String localIp, Long downloaded, Long uploaded) throws SQLException
	{
		this.m_time = null;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;

	}

	public GraphData(Timestamp time, String localIp, Long downloaded,
			Long uploaded, Integer port) throws SQLException
	{
		this.m_time = time;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;		
		this.m_port = port;
	}

	public GraphData(String localIp, Long downloaded, Long uploaded,
			Integer port) throws SQLException
	{
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;		
		this.m_port = port;
	}

	public GraphData(Timestamp time, Long downloaded, Long uploaded,
			Integer port) throws SQLException
	{
		this.m_time = time;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;		
		this.m_port = port;
	}
	
	public GraphData(Timestamp time, String localIp, String remoteIp, 
			Long downloaded, Long uploaded) 
			throws SQLException
	{
		this.m_time = time;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;		
		this.m_remoteIp = remoteIp;
	}

	public Long getDownloaded()
	{
		return m_downloaded;
	}

	public void setDownloaded(Long downloaded)
	{
		this.m_downloaded = downloaded;
	}

	public Timestamp getTime()
	{
		return m_time;
	}

	public void setTime(Timestamp time)
	{
		this.m_time = time;
	}

	public Long getUploaded()
	{
		return m_uploaded;
	}

	public void setUploaded(Long uploaded)
	{
		this.m_uploaded = uploaded;
	}
	
	public void incrementUploaded(Long uploaded)
	{
		this.m_uploaded += uploaded;
	}
	
	public void incrementDownloaded(Long downloaded)
	{
		this.m_downloaded += downloaded;
	}

	public String getLocalIp()
	{
		return m_localIp;
	}

	public void setLocalIp(String localIp)
	{
		this.m_localIp = localIp;
	}

	public Long getBytesTotal()
	{
		return m_uploaded + m_downloaded;
	}
	
	public Integer getPort()
	{
		return m_port;
	}

	public void setPort(Integer port)
	{
		this.m_port = port;
	}

	public String getRemoteIp()
	{
		return m_remoteIp;
	}

	public void setRemoteIp(String remoteIp)
	{
		this.m_remoteIp = remoteIp;
	}

	public Integer getRemotePort()
	{
		return m_remotePort;
	}

	public void setRemotePort(Integer remotePort)
	{
		this.m_remotePort = remotePort;
	}

	public Protocol getProtocol()
	{
		return m_protocol;
	}

	public void setProtocol(Protocol protocol)
	{
		this.m_protocol = protocol;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((m_localIp == null) ? 0 : m_localIp.hashCode());
		result = PRIME * result + ((m_port == null) ? 0 : m_port.hashCode());
		result = PRIME * result + ((m_protocol == null) ? 0 : m_protocol.hashCode());
		result = PRIME * result + ((m_remoteIp == null) ? 0 : m_remoteIp.hashCode());
		result = PRIME * result + ((m_remotePort == null) ? 0 : m_remotePort.hashCode());
		result = PRIME * result + ((m_time == null) ? 0 : m_time.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GraphData other = (GraphData) obj;
		if (m_localIp == null)
		{
			if (other.m_localIp != null)
				return false;
		}
		else if (!m_localIp.equals(other.m_localIp))
			return false;
		if (m_port == null)
		{
			if (other.m_port != null)
				return false;
		}
		else if (!m_port.equals(other.m_port))
			return false;
		if (m_protocol == null)
		{
			if (other.m_protocol != null)
				return false;
		}
		else if (!m_protocol.equals(other.m_protocol))
			return false;
		if (m_remoteIp == null)
		{
			if (other.m_remoteIp != null)
				return false;
		}
		else if (!m_remoteIp.equals(other.m_remoteIp))
			return false;
		if (m_remotePort == null)
		{
			if (other.m_remotePort != null)
				return false;
		}
		else if (!m_remotePort.equals(other.m_remotePort))
			return false;
		if (m_time == null)
		{
			if (other.m_time != null)
				return false;
		}
		else if (!m_time.equals(other.m_time))
			return false;
		return true;
	}

	
	
}
