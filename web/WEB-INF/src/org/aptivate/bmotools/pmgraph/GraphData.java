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
public class GraphData {
	
	private Timestamp m_time;
	private String m_localIp;
	private Long m_downloaded;
	private Long m_uploaded;
	private Long m_bytesTotal;
	
	
	/**
	 * Create a DbData from a result set taking into acount the columns returned 
	 * by the throught per Ip query.
	 * @param rs
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public GraphData(ResultSet rs) throws SQLException{
		
		this(rs.getTimestamp("stamp_inserted"),	rs.getString("local_ip"),
			rs.getLong("downloaded"), rs.getLong("uploaded")
			, rs.getLong("bytes_total"));
	}

	public GraphData(Timestamp time, String localIp,Long downloaded,
			Long uploaded,Long byteTotal) throws SQLException {
		
		this.m_time = time;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;
		this.m_bytesTotal = byteTotal;
	}
	
	public GraphData(String localIp,Long downloaded,
			Long uploaded,Long bytesTotal) throws SQLException {
		
		this.m_time = null;
		this.m_localIp = localIp;
		this.m_downloaded = downloaded;
		this.m_uploaded = uploaded;
		this.m_bytesTotal = bytesTotal;
	}
	
	

	public Long getDownloaded() {
		return m_downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.m_downloaded = downloaded;
	}
	
	public Timestamp getTime() {
		return m_time;
	}

	public void setTime(Timestamp time) {
		this.m_time = time;
	}

	public Long getUploaded() {
		return m_uploaded;
	}

	public void setUploaded(Long uploaded) {
		this.m_uploaded = uploaded;
	}

	public String getLocalIp() {
		return m_localIp;
	}

	public void setLocalIp(String localIp) {
		this.m_localIp = localIp;
	}

	public Long getBytesTotal()
	{
		return m_bytesTotal;
	}

	public void setBytesTotal(Long total)
	{
		m_bytesTotal = total;
	}
}
