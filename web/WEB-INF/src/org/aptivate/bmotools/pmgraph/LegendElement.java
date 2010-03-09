package org.aptivate.bmotools.pmgraph;

/**
 * LegendElement stores the value and associates information for each entry in
 * the legend table
 * 
 * @author blancab
 * 
 */
public class LegendElement
{
	// m_value is the string shown in the legend, m_link is the link in case is
	// used and m_name is the name associated to the link which is used to sort
	// in some cases
	private String m_value;

	private String m_link;

	private String m_name;

	private boolean m_doubleRowSpan;

	private boolean m_doubleColSpan;

	public LegendElement() {
	}

	public LegendElement(String value) {
		this.m_value = value;
		this.m_link = null;
		this.m_doubleColSpan = false;
		this.m_doubleRowSpan = false;
	}

	public LegendElement(String value, String link, String name) {
		this.m_value = value;
		this.m_link = link;
		this.m_name = name;
		this.m_doubleColSpan = false;
		this.m_doubleRowSpan = false;
	}

	public LegendElement(String value, String link, String name, boolean doubleColSpan,
			boolean doubleRowSpan) {
		this.m_value = value;
		this.m_link = link;
		this.m_name = name;
		this.m_doubleColSpan = doubleColSpan;
		this.m_doubleRowSpan = doubleRowSpan;
	}

	public String getLink()
	{
		return m_link;
	}

	public void setLink(String link)
	{
		this.m_link = link;
	}

	public String getValue()
	{
		return m_value;
	}

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

	public boolean isDoubleColSpan()
	{
		return m_doubleColSpan;
	}

	public void setDoubleColSpan(boolean colSpan)
	{
		m_doubleColSpan = colSpan;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		this.m_name = name;
	}

}
