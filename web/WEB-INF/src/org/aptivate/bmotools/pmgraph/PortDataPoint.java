package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PortDataPoint extends DataPoint
{

	private Integer m_port;

	private Protocol m_protocol;

	public static final int OTHER_PORT = -1;

	public PortDataPoint(ResultSet rs, Integer port, String protocol,
			boolean isChart) throws SQLException {
		super(rs, isChart);
		m_port = port;
		m_protocol = Protocol.valueOf(protocol);
	}

	public PortDataPoint(PortDataPoint portDataPoint) {
		// call the parent constructor
		super(portDataPoint);
		m_port = portDataPoint.m_port;
		m_protocol = portDataPoint.m_protocol;
	}

	public PortDataPoint(Integer port) {
		super();
		m_port = port;
		m_protocol = null;
	}

	public Integer getPort()
	{
		return m_port;
	}

	public void setPort(Integer port)
	{
		this.m_port = port;
	}

	public Protocol getProtocol()
	{
		return m_protocol;
	}

	public void setProtocol(Protocol protocol)
	{
		this.m_protocol = protocol;
	}

	/**
	 * @return Particular port if one of top ports, if not "Others"
	 */
	@Override
	public String getId()
	{

		if (m_port != OTHER_PORT)
			return String.valueOf(m_port);
		return "Others";
	}

	/**
	 * Return a color obtained from creating a hash with the bytes of the Port and the protocol.
	 * 
	 * @return Color for the selected port
	 */
	public Color getSeriesColor()
	{
		byte[] portBytes = new byte[] { (byte) (m_port >>> 24),
				(byte) (m_port >>> 16), (byte) (m_port >>> 8),
				(byte) m_port.intValue() };

		if (m_protocol != null)
		{
			byte protocolByte = (byte) m_protocol.ordinal();
			portBytes[0] = (byte) (protocolByte | portBytes[0]);
		}
		return getColorFromByteArray(portBytes);
	}

	/**
	 * Add protocol to the Id
	 * 
	 * @return Id + protocol
	 */
	@Override
	public String getSeriesId()
	{
		return (getId() + m_protocol);
	}

	/**
	 * This method allows us to work with a generic DataPoint when we don't yet know the specific 
	 * subclass (IP or port)
	 */
	@Override
	public DataPoint createCopy()
	{
		return (new PortDataPoint(this));
	}

}
