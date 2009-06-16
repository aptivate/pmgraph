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
 * Bean which contains a line of information collected for the pmacct in the
 * data base.
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

}
