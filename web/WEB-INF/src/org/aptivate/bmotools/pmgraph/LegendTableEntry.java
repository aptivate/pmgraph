package org.aptivate.bmotools.pmgraph;

public class LegendTableEntry
{
	//m_value is the string shown in the lelgend, m_link is the link in case is used and m_name is the name associated to the link which is used to sort in some cases
	private String m_value;

	private String m_link;
	
	private String m_name;
	
	private boolean m_doubleRowSpan;
	
	private boolean m_doubleColSpan;

	public LegendTableEntry()
	{
	}

	public LegendTableEntry(String value)
	{
		this.m_value = value;
		this.m_link = null;
		this.m_doubleColSpan = false;
		this.m_doubleRowSpan = false;
	}

	public LegendTableEntry(String value, String link, String name)
	{
		this.m_value = value;
		this.m_link = link;
		this.m_name = name;
		this.m_doubleColSpan = false;
		this.m_doubleRowSpan = false;
	}
	
	public LegendTableEntry(String value, String link, String name, boolean doubleColSpan, boolean doubleRowSpan)
	{
		this.m_value = value;
		this.m_link = link;
		this.m_name = name;
		this.m_doubleColSpan = doubleColSpan;
		this.m_doubleRowSpan = doubleRowSpan;
	}

	/**
	 * @return the link
	 */
	public String getLink()
	{
		return m_link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link)
	{
		this.m_link = link;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return m_value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.m_value = value;
	}
	
	public boolean isDoubleRowSpan()
	{
		return m_doubleRowSpan;
	}
	
	public void setDoubleRowSpan(boolean doubleRowSpan)
	{
		this.m_doubleRowSpan = doubleRowSpan;
	}

	/**
	 * @return the m_doubleColSpan
	 */
	public boolean isDoubleColSpan()
	{
		return m_doubleColSpan;
	}

	/**
	 * @param colSpan the m_doubleColSpan to set
	 */
	public void setDoubleColSpan(boolean colSpan)
	{
		m_doubleColSpan = colSpan;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

}
