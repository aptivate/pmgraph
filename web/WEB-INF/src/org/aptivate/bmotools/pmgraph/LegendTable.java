package org.aptivate.bmotools.pmgraph;

import java.util.ArrayList;

public class LegendTable {
	private ArrayList<ArrayList<LegendTableEntry>> m_headers;

	private ArrayList<ArrayList<LegendTableEntry>> m_rows;

	public LegendTable() {
		m_headers = new ArrayList<ArrayList<LegendTableEntry>>();
		m_rows = new ArrayList<ArrayList<LegendTableEntry>>();

	}

	public LegendTable(ArrayList<ArrayList<LegendTableEntry>> headers,
			ArrayList<ArrayList<LegendTableEntry>> rows) {
		m_headers = headers;
		m_rows = rows;
	}

	/**
	 * @return the headers
	 */
	public ArrayList<ArrayList<LegendTableEntry>> getHeaders() {
		return m_headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(ArrayList<ArrayList<LegendTableEntry>> headers) {
		this.m_headers = headers;
	}

	/**
	 * @return the rows
	 */
	public ArrayList<ArrayList<LegendTableEntry>> getRows() {
		return m_rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(ArrayList<ArrayList<LegendTableEntry>> rows) {
		this.m_rows = rows;
	}

}
