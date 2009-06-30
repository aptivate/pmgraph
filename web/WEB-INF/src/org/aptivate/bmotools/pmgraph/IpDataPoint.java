package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class IpDataPoint extends DataPoint
{

	private String m_Ip;

	public static final String OTHER_IP = "255.255.255.255";

	private Logger m_logger = Logger.getLogger(IpDataPoint.class.getName());

	public IpDataPoint(ResultSet rs, String ip, boolean isChart)
			throws SQLException {
		super(rs, isChart);
		m_Ip = ip;
	}

	public IpDataPoint(IpDataPoint ipDataPoint) {
		// call the parent constructor
		super(ipDataPoint);
		m_Ip = ipDataPoint.m_Ip;
	}

	public IpDataPoint(String ip) {
		super();
		m_Ip = ip;
	}

	public String getIp()
	{
		return m_Ip;
	}

	public void setIp(String ip)
	{
		m_Ip = ip;
	}

	@Override
	public String getId()
	{

		if (m_Ip != OTHER_IP)
			return m_Ip;
		return "Others";

	}

	/**
	 * Return a color obtained from creating a hash with the bytes of the Ip.
	 * 
	 * @param ip
	 * @return Color for the selected IP.
	 */
	public Color getSeriesColor()
	{
		if (m_Ip != null)
		{
			byte[] ipBytes = m_Ip.getBytes();

			return getColorFromByteArray(ipBytes);
		}
		m_logger
				.warn("Unable to assign a color to a null IP. (Black color assigned)");
		return (Color.BLACK);
	}

	@Override
	public String getSeriesId()
	{
		return (getId());
	}

	@Override
	public DataPoint createCopy()
	{

		return (new IpDataPoint(this));
	}
	/*
	 * @Override public boolean equals(Object obj) {
	 * 
	 * System.out.println("as;lkdja;lskjdlkajsd;ljk");
	 * 
	 * return super.equals(obj); }
	 */

}
