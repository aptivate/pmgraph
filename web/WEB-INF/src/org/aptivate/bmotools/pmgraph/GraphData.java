/**
 * 
 */
package org.aptivate.bmotools.pmgraph;

import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * @author noeg
 * 
 * Bean which contains a line of the information collected for the pmacct in the
 * data base.
 * 
 */
public class GraphData {
	
	private Timestamp time;
	private String localIp;
	private Long downloaded;
	private Long uploaded;
	
	
	/**
	 * Create a DbData from a result set taking into acount the columns returned 
	 * by the throught per Ip query.
	 * @param rs
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public GraphData(ResultSet rs) throws SQLException{
		
		this(rs.getTimestamp("stamp_inserted"),	rs.getString("local_ip"),
			rs.getLong("downloaded"), rs.getLong("uploaded"));
	}

	public GraphData(Timestamp time, String localIp,Long downloaded,
			Long uploaded) throws SQLException {
		
		this.time = time;
		this.localIp = localIp;
		this.downloaded = downloaded;
		this.uploaded = uploaded;
	}
	public GraphData(String localIp,Long downloaded,
			Long uploaded) throws SQLException {
		
		this.time = null;
		this.localIp = localIp;
		this.downloaded = downloaded;
		this.uploaded = uploaded;
	}
	
	

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}
	
	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Long getUploaded() {
		return uploaded;
	}

	public void setUploaded(Long uploaded) {
		this.uploaded = uploaded;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
}
