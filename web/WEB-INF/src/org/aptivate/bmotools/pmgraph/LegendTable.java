package org.aptivate.bmotools.pmgraph;

import java.util.ArrayList;

public class LegendTable {
	private ArrayList<ArrayList<LegendElement>> m_headers;

	private ArrayList<ArrayList<LegendElement>> m_rows;

	public LegendTable() {
		m_headers = new ArrayList<ArrayList<LegendElement>>();
		m_rows = new ArrayList<ArrayList<LegendElement>>();

	}

	public LegendTable(ArrayList<ArrayList<LegendElement>> headers,
			ArrayList<ArrayList<LegendElement>> rows) {
		m_headers = headers;
		m_rows = rows;
	}

	/**
	 * @return the headers
	 */
	public ArrayList<ArrayList<LegendElement>> getHeaders() {
		return m_headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(ArrayList<ArrayList<LegendElement>> headers) {
		this.m_headers = headers;
	}

	/**
	 * @return the rows
	 */
	public ArrayList<ArrayList<LegendElement>> getRows() {
		return m_rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(ArrayList<ArrayList<LegendElement>> rows) {
		this.m_rows = rows;
	}

}
