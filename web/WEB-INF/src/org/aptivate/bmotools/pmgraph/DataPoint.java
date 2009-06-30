package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public abstract class DataPoint
{

	private Timestamp m_time;

	private Long m_downloaded;

	private Long m_uploaded;

	abstract public String getId();

	abstract public String getSeriesId();

	abstract public Color getSeriesColor();

	abstract public DataPoint createCopy();

	private Logger m_logger = Logger.getLogger(DataPoint.class.getName());

	public DataPoint(ResultSet rs, boolean isChart) throws SQLException {
		if (isChart)
			setTime(rs.getTimestamp("stamp_inserted"));
		setDownloaded(rs.getLong("downloaded"));
		setUploaded(rs.getLong("uploaded"));
	}

	public DataPoint(DataPoint source) {
		m_time = source.m_time;
		m_downloaded = source.m_downloaded;
		m_uploaded = source.m_uploaded;

	}

	public DataPoint() {
		m_downloaded = 0L;
		m_uploaded = 0L;
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

	public void addToUploaded(Long uploaded)
	{
		this.m_uploaded += uploaded;
	}

	public void addToDownloaded(Long downloaded)
	{
		this.m_downloaded += downloaded;
	}

	public Long getBytesTotal()
	{
		return m_uploaded + m_downloaded;
	}

	protected Color getColorFromByteArray(byte[] bytes)
	{
		MessageDigest algorithm;
		try
		{
			algorithm = MessageDigest.getInstance("SHA1");
			algorithm.reset();
			algorithm.update(bytes);
			byte sha1[] = algorithm.digest();
			return (new Color(sha1[0] & 0xFF, sha1[1] & 0xFF, sha1[2] & 0xFF));
		} catch (NoSuchAlgorithmException e)
		{
			m_logger.error(e.getMessage(), e);
		}
		return (Color.BLACK);
	}

	public String getColorAsHexadecimal()
	{
		Color c = getSeriesColor();
		String fillColour = Integer.toHexString(c.getRGB() & 0x00ffffff);
		return fillColour = "#"
				+ "000000".substring(0, 6 - fillColour.length()) + fillColour;
	}

	/**
	 * Two object are equal if they have the same port and protocol
	 * 
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

		final DataPoint other = (DataPoint) obj;

		if (m_time == null)
		{
			if (m_time != null)
				return false;
		} else
			if (!m_time.equals(other.getTime()))
				return false;
		if (getSeriesId() == null)
		{
			if (other.getSeriesId() != null)
				return false;
		} else
			if (!other.getSeriesId().equals(other.getSeriesId()))
				return false;
		return true;
	}

	/**
	 * hashCode is the method used to assign a position in a hashMap. This
	 * object is going to be used to index hashMap so we need to implement this
	 * method. This method uses getSeriesId, which is an abstract method that is
	 * implemented in the son classes. Therefore, when you have an instance of
	 * one the especific son classes the getSeriesId executed will be the one
	 * implemented in the especific class.
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((getSeriesId() == null) ? 0 : getSeriesId().hashCode());
		return result;
	}

}
