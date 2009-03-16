package org.aptivate.bmotools.pmgraph;

public abstract class GraphUtilities
{

	// SQL query strings
	// Lists total throughput

	/*
	 * "SELECT stamp_inserted, " + "SUM(IF(ip_dst LIKE ?, bytes, 0)) AS
	 * downloaded, " + "SUM(IF(ip_src LIKE ?, bytes, 0)) AS uploaded " + "FROM
	 * acct_v6 " + "WHERE (ip_src LIKE ? XOR ip_dst LIKE ?) AND " +
	 * "stamp_inserted >= ? AND " + "stamp_inserted <= ? " + "GROUP BY
	 * stamp_inserted;";
	 */
	public static final String THROUGHPUT_PER_MINUTE = "SELECT stamp_inserted, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY local_ip;";

	// changed sort
	// Lists throughput per IP
	/*
	 * "SELECT IF(ip_src LIKE ?, ip_src, ip_dst) AS local_ip, " + "SUM(IF(ip_dst
	 * LIKE ?, bytes, 0)) AS downloaded, " + "SUM(if(ip_src LIKE ?, bytes, 0))
	 * AS uploaded, " + "SUM(bytes) AS bytes_total " + "FROM acct_v6 " + "WHERE
	 * (ip_src LIKE ? XOR ip_dst LIKE ?) AND " + "stamp_inserted >= ? AND " +
	 * "stamp_inserted <= ? " + "GROUP BY local_ip " + "ORDER BY ?; ";
	 */
	public static final String THROUGHPUT_PER_IP = "SELECT (CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded, "
			+ "SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ? ) "
			+ "AND ip_dst LIKE ?) "
			+ "OR (NOT (ip_dst LIKE ?) "
			+ "AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY local_ip;";

	// Lists throughput per IP per minute

	/*
	 * "SELECT stamp_inserted, " + "IF(ip_src LIKE ?, ip_src, ip_dst) AS
	 * local_ip, " + "SUM(IF(ip_dst LIKE ?, bytes, 0)) as downloaded, " +
	 * "SUM(IF(ip_src LIKE ?, bytes, 0)) as uploaded " + "FROM acct_v6 " +
	 * "WHERE (ip_src LIKE ? XOR ip_dst LIKE ?) AND " + "stamp_inserted >= ? AND " +
	 * "stamp_inserted <= ? " + "GROUP BY stamp_inserted, local_ip;";
	 */
	public static final String THROUGHPUT_PER_IP_PER_MINUTE = "SELECT stamp_inserted, "
			+ "(CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY stamp_inserted, local_ip;";

}