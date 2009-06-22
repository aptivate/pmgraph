package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Noe Andres Rodriguez Gonzalez.
 * 
 */
public class SpecificGraphsTest extends GraphTestBase
{
	private static Logger m_logger = Logger.getLogger(SpecificGraphsTest.class
			.getName());

	public SpecificGraphsTest() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{
		super();
	}

	public void setUp() throws Exception
	{
		m_testUtils.CreateTable();
		m_testUtils.InsertPortsSampleData();
	}

	/**
	 * Check if the graph created for an espxific Ip or port match What is
	 * indicated in the parameters Just to be used with especific port or Ip
	 * graphs
	 * 
	 * @param ipOrPort
	 * @param values
	 * @param rows
	 * @param view
	 *            Is a port grap or a Ip graph default is Ip graph
	 * @throws Exception
	 */
	private void checkOneIpPort(String ipOrPort, long values[][],
			String[] rows, View view) throws Exception
	{
		JFreeChart chart;
		GraphFactory graphFactory = new GraphFactory();
		RequestParams request;

		switch (view)
		{
		default:
			m_logger.warn(" View Unknown assumed default view IP");
		case LOCAL_IP:
			request = new RequestParams(m_testUtils.t1
					.getTime(), m_testUtils.t4.getTime(),View.LOCAL_PORT, 15, ipOrPort);
			chart = graphFactory.stackedThroughputGraph(request);
			
			assertEquals("Network Throughput For Local Ip = " + ipOrPort, chart
					.getTitle().getText());
			break;
		case LOCAL_PORT:			
			request = new RequestParams(m_testUtils.t1
					.getTime(), m_testUtils.t4.getTime(),View.LOCAL_IP, 15, Integer.valueOf(ipOrPort));
			chart = graphFactory.stackedThroughputGraph(request);

			assertEquals("Network Throughput For Local Port = " + ipOrPort, chart
					.getTitle().getText());
			break;
		}
		checkChartData(values, rows, chart);
	}

	/**
	 * Check if the graph contains the values for each series that it should
	 * contain acordingly to the test data.
	 * 
	 * @throws Exception
	 */
	public void testOneIpGraph() throws Exception
	{
		// check values per each serie.
		String ports[] = new String[] { "110tcp", "80tcp", "443tcp" };
		long values[][] = { { 0, -800, 0, 0 }, // 110 up
				{ 0, 700, 0, 0 }, // 110 down
				{ -1100, 0, 0, 0 }, // 80 up
				{ 100, 0, 0, 0 }, // 80 down
				{ 0, 0, 0, 0 }, // 443 up
				{ 0, 0, 100, 0 }, // 443 down
		};
		
		checkOneIpPort("10.0.156.110", values, ports, View.LOCAL_IP);

		ports = new String[] { "110tcp", "80tcp", "443tcp", "443udp" };
		values = new long[][] { { 0, 0, 0, 0 }, // 110 up
				{ 100, 500, 0, 0 }, // 110 down
				{ -150, 0, 0, 0 }, // 80 up
				{ 125, 0, 0, 0 }, // 80 down
				{ 0, 0, -150, 0 }, // 443 up
				{ 0, 0, 0, 0 }, // 443 down
				{ 0, 0, 0, 0}, // 443 udp up
				{ 0, 0, 75, 0 }, // 443 udp down
		};
		checkOneIpPort("10.0.156.130", values, ports, View.LOCAL_IP);

		ports = new String[] { "443tcp"};
		values = new long[][] { { -50, -100, -600, 0 }, // 443 up
				{ 100, 50, 300, 0 }, // 443 down
		};
		checkOneIpPort("10.0.156.131", values, ports, View.LOCAL_IP);

	}

	public void testOnePortGraph() throws Exception
	{

		// check values per each serie.
		// Port 80
		String ips[] = new String[] { "10.0.156.110", "10.0.156.130" };
		long values[][] = { { -1100, 0, 0, 0 }, // 10.0.156.110 up
				{ 100, 0, 0, 0 }, // 10.0.156.110 down
				{ -150, 0, 0, 0 }, // 10.0.156.130 up
				{ 125, 0, 0, 0 }, // 10.0.156.130 down
		};
		checkOneIpPort("80", values, ips, View.LOCAL_PORT);

		// Port 110
		ips = new String[] { "10.0.156.110", "10.0.156.130" };
		values = new long[][] { { 0, -800, 0, 0 }, // 10.0.156.110 up
				{ 0, 700, 0, 0 }, // 10.0.156.110 down
				{ 0, 0, 0, 0 }, // 10.0.156.130 up
				{ 100, 500, 0, 0 }, // 10.0.156.130 down
		};
		checkOneIpPort("110", values, ips, View.LOCAL_PORT);

		// Port 443
		ips = new String[] { "10.0.156.131", "10.0.156.130", "10.0.156.110" };
		values = new long[][] { { -50, -100, -600, 0 }, // 10.0.156.131 up
				{ 100, 50, 300, 0 }, // 10.0.156.131 down
				{ 0, 0, -150, 0 }, // 10.0.156.120 up
				{ 0, 0, 75, 0 }, // 10.0.156.120 down
				{ 0, 0, 0, 0 }, // 10.0.156.110 up
				{ 0, 0, 100, 0 }, // 10.0.156.110 down
		};
		checkOneIpPort("443", values, ips, View.LOCAL_PORT);

	}

	public static Test suite()
	{
		return new TestSuite(SpecificGraphsTest.class);
	}

}
