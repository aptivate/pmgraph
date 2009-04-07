package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.aptivate.bmotools.pmgraph.GraphFactory;
import org.aptivate.bmotools.pmgraph.PageUrl.View;
import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Noe Andres Rodriguez Gonzalez
 * 
 */
public class GraphsTest extends GraphTestBase
{
	private static Logger m_logger = Logger.getLogger(GraphsTest.class
			.getName());

	public GraphsTest() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException
	{
		super();
	}

	public void setUp() throws Exception
	{
		m_testUtils.CreateTable();
		m_testUtils.InsertSampleData();
	}

	private void checkGraph(View view) throws Exception
	{
		JFreeChart chart;
		long values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();

		switch (view)
		{
		default:
			m_logger.warn(" View Unknown assumed default view IP");
		case IP:
			chart = graphFactory.stackedThroughput(m_testUtils.t1.getTime(),
					m_testUtils.t4.getTime(), 15);
			assertEquals("Network Throughput Per IP", chart.getTitle()
					.getText());
			// check values per each serie.
			rows = new String[] { "10.0.156.120", "10.0.156.110",
					"10.0.156.131", "10.0.156.132", "10.0.156.133",
					"10.0.156.134", "10.0.156.135", "10.0.156.136",
					"10.0.156.137", "10.0.156.138", "10.0.156.139",
					"10.0.156.140" };
			// init array to zero values
			values = new long[2 * rows.length][4];

			// IP 10.0.156.120 just values differents of cero
			values[0][1] = -500 * 11;
			values[1][1] = 5550;
			values[1][3] = 75;

			// IP 10.0.156.110
			values[2][0] = -2000;
			values[3][0] = 90;
			values[3][1] = 80;
			values[3][3] = 70;

			// IPs 10.0.156.131 10.0.156.140
			for (int n = 2; n < rows.length; n++)
			{
				assertEquals("10.0.156." + (130 + n - 1), rows[n]);
				values[n * 2][3] = Long.valueOf(-100 * (12 - n) - 50);
			}

			break;

		case PORT:
			chart = graphFactory.stackedThroughputPerPort(m_testUtils.t1
					.getTime(), m_testUtils.t4.getTime(), 15);
			assertEquals("Network Throughput Per Port", chart.getTitle()
					.getText());
			// check values per each serie.
			rows = new String[] { "90", "10000", "12300", "23500", "23400" };
			// init array to zero values
			values = new long[2 * rows.length][4];
			// port 90
			values[0][1] = -5500; // upload
			values[0][3] = -6000; // download
			// port 10000
			values[3][1] = 5500;

			// port 12300
			values[4][0] = -2000;
			values[5][0] = 90;
			values[5][1] = 80;
			values[5][3] = 70;
			// port 23500
			values[7][3] = 75;
			// port 23400
			values[9][1] = 50;
			break;
		}
		checkChartData(values, rows, chart);
	}

	public void testCumulativeGraphIpView() throws Exception
	{
		checkGraph(View.IP);
		checkGraph(View.PORT);

	}

	public static Test suite()
	{
		return new TestSuite(GraphsTest.class);
	}
}
