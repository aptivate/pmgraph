/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Noe A. Rodriguez Glez.
 * 
 * Bean which contains a line of the information collected for the pmacct in the
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

	/**
	 * Create a DbData from a result set taking into acount the columns returned
	 * by the throught per Ip query.
	 * 
	 * @param rs
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public GraphData(ResultSet rs) throws SQLException
	{

		setTime(rs.getTimestamp("stamp_inserted"));
		setDownloaded(rs.getLong("downloaded"));
		setUploaded(rs.getLong("uploaded"));		
		try {
			setLocalIp(rs.getString("local_ip"));
		}catch (SQLException e) {
			;
		}
		try {
			setRemoteIp(rs.getString("remote_ip"));
		}catch (SQLException e) {
			;
		}
		
		try {
			setPort(rs.getInt("port"));
		}catch (SQLException e) {
			;
		}
		try {
			setRemotePort(rs.getInt("remote_port"));
		}catch (SQLException e) {
			//e.printStackTrace();
		}
		
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

}
