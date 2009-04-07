package org.aptivate.bmotools.pmgraph;

abstract class GraphUtilities
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
	static final String THROUGHPUT_PER_MINUTE = "SELECT stamp_inserted, "
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
	static final String THROUGHPUT_PER_IP = "SELECT (CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
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
	static final String THROUGHPUT_PER_IP_PER_MINUTE = "SELECT stamp_inserted, "
			+ "(CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY stamp_inserted, local_ip;";

	/*
	 * List all the throughput per an especific Ip group by minutes and ports
	 */
	static final String THROUGHPUT_ONE_IP_PER_PORT_PER_MINUTE = "SELECT stamp_inserted, "
			+ "(CASE WHEN ip_src = ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst = ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src = ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst = ?) OR (NOT (ip_dst LIKE ?) AND ip_src = ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY stamp_inserted, port;";

	/*
	 * List all the throughput per an especific Ip group by ports
	 */
	static final String THROUGHPUT_ONE_IP_PER_PORT = "SELECT "
			+ "(CASE WHEN ip_src = ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst = ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src = ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst = ?) OR (NOT (ip_dst LIKE ?) AND ip_src = ?)) "
			+ "AND stamp_inserted >= ? " + "AND stamp_inserted <= ? "
			+ "GROUP BY port;";

	/*
	 * Total throughput per each port Local trafic ommited
	 */
	static final String THROUGHPUT_PER_PORT = "SELECT "
			+ "(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? " + "AND stamp_inserted <= ? "
			+ "GROUP BY port;";

	/*
	 * throughput per each port per minute Local trafic ommited
	 */
	static final String THROUGHPUT_PER_PORT_PER_MINUTE = "SELECT stamp_inserted, "
			+ "(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY stamp_inserted, port;";

	/*
	 * List all the throughput per an especific Port group by ip
	 */
	static final String THROUGHPUT_ONE_PORT_PER_MINUTE_PER_IP = "SELECT stamp_inserted, "
			+ "(CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
			+ "(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? "
			+ "AND stamp_inserted <= ? "
			+ "GROUP BY  stamp_inserted, local_ip, port having port = ?;";

	static final String THROUGHPUT_ONE_PORT = "SELECT "
			+ "(CASE WHEN ip_src LIKE ? THEN ip_src ELSE ip_dst END) AS local_ip, "
			+ "(CASE WHEN ip_src LIKE ? THEN src_port ELSE dst_port END) AS port, "
			+ "SUM(CASE WHEN ip_dst LIKE ? THEN bytes ELSE 0 END) as downloaded, "
			+ "SUM(CASE WHEN ip_src LIKE ? THEN bytes ELSE 0 END) as uploaded,SUM(bytes) AS bytes_total "
			+ "FROM acct_v6 "
			+ "WHERE ((NOT (ip_src LIKE ?) AND ip_dst LIKE ?) OR (NOT (ip_dst LIKE ?) AND ip_src LIKE ?)) "
			+ "AND stamp_inserted >= ? " + "AND stamp_inserted <= ? "
			+ "GROUP BY local_ip, port having port = ?;";

}