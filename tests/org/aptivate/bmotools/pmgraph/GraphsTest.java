package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;

/**
 * Test the graph for the different options: one/two/three parameter/s already
 * selected
 * 
 * @author Noe Andres Rodriguez Gonzalez
 * 
 */

public class GraphsTest extends GraphTestBase
{
	private static Logger m_logger = Logger.getLogger(GraphsTest.class.getName());

	public GraphsTest() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		super();
	}

	public void setUp() throws Exception
	{
		m_testUtils.CreateTable();
		m_testUtils.InsertSampleData();
	}

	private void checkGraph(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		// values will keep the different throughput for each Ip/port for upload
		// (even) and download (odd) for four different times
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		switch (requestParams.getView())
		{
		default:
			m_logger.warn(" View Unknown assumed default view IP");

		case LOCAL_IP: // is selected a local IP

			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values per each serie.
			rows = new String[] { "10.0.156.120", "10.0.156.110", "10.0.156.131", "10.0.156.132",
					"10.0.156.133", "10.0.156.134", "10.0.156.135", "10.0.156.136", "10.0.156.137",
					"10.0.156.138", "10.0.156.139", "10.0.156.140" };
			// init array to zero values
			values = new float[2 * rows.length][4];

			// IP 10.0.156.120 just values differents of zero
			values[0][1] = -500 * 11; // upload at time 1
			values[1][1] = 5550;
			values[1][3] = 75; // download at time 3

			// IP 10.0.156.110
			values[2][0] = -2000;
			values[3][0] = 90;
			values[3][1] = 80;
			values[3][3] = 70;

			// IPs 10.0.156.131 10.0.156.140
			for (int n = 2; n < rows.length; n++)
			{
				assertEquals("10.0.156." + (130 + n - 1), rows[n]);
				values[n * 2][3] = Float.valueOf(-100 * (12 - n) - 50);
			}

			break;

		case LOCAL_PORT: // a local port is selected
			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values per each serie.
			rows = new String[] { "90tcp", "10000tcp", "12300tcp", "23500tcp", "23400tcp" };
			// init array to zero values
			values = new float[2 * rows.length][4];
			// port 90
			values[0][1] = -5500; // upload (is negative in the graph)
			values[0][3] = -6000; // upload
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

		case REMOTE_IP: // Remote ip view
			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values for each series.
			rows = new String[] { "4.2.2.2", "4.2.2.3", "4.2.2.4", "4.2.2.5", "4.2.2.6", "4.2.2.7",
					"4.2.2.8", "4.2.2.9", "4.2.2.10", "4.2.2.11", "4.2.2.12" };
			// init array to zero values
			values = new float[2 * rows.length][4];

			values[0][3] = -6000;
			values[1][3] = 145;
			values[0][0] = -2000;
			values[1][0] = 90;
			values[0][1] = -500;
			values[1][1] = 130;

			values[2][1] = -500;
			values[3][1] = 1000;

			values[4][1] = -500;
			values[5][1] = 900;

			values[6][1] = -500;
			values[7][1] = 800;
			values[8][1] = -500;
			values[9][1] = 700;
			values[10][1] = -500;
			values[11][1] = 600;
			values[12][1] = -500;
			values[13][1] = 500;
			values[14][1] = -500;
			values[15][1] = 400;
			values[16][1] = -500;
			values[17][1] = 300;
			values[18][1] = -500;
			values[19][1] = 200;

			values[20][1] = -500;
			values[21][1] = 100;
			break;

		case REMOTE_PORT: // Remote port view
			assertEquals("Network Throughput", chart.getTitle().getText());

			// check values for each series.
			rows = new String[] { "10000tcp", "90tcp", "80tcp" };

			// init array to zero values
			values = new float[2 * rows.length][4];

			// port 10000
			values[0][1] = -5500;
			values[0][3] = -6000;
			// port 90
			values[3][1] = 5500;
			// port 80 uploadvalues
			values[4][0] = -2000;
			// download
			values[5][0] = 90;
			values[5][3] = 145;
			values[5][1] = 130;

			break;
		}
		checkChartData(values, rows, chart);
	}

	// With local Ip already selected
	private void checkGraphOneParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		assertEquals("Network Throughput For Local Ip = " + requestParams.getIp(), chart.getTitle()
				.getText());

		switch (requestParams.getView())
		{
		default:
		case LOCAL_PORT: // is selected a local port
			// check values per each serie.
			rows = new String[] { "90tcp", "10000tcp", "23500tcp", "23400tcp" };
			// init array to zero values
			values = new float[2 * rows.length][4];

			values[0][1] = -5500;
			values[3][1] = 5500;
			values[5][3] = 75;
			values[7][1] = 50;

			break;

		case REMOTE_IP: // Remote ip view
			// check values for each series.

			rows = new String[] { "4.2.2.3", "4.2.2.4", "4.2.2.5", "4.2.2.6", "4.2.2.7", "4.2.2.8",
					"4.2.2.9", "4.2.2.10", "4.2.2.11", "4.2.2.2", "4.2.2.12", };
			values = new float[2 * 11][4];
			values[1][1] = 1000.0f;
			values[3][1] = 900.0f;
			values[5][1] = 800.0f;
			values[7][1] = 700.0f;
			values[9][1] = 600.0f;
			values[11][1] = 500.0f;
			values[13][1] = 400.0f;
			values[15][1] = 300.0f;
			values[17][1] = 200.0f;
			values[19][1] = 50.0f;
			values[19][3] = 75.0f; // Download values Time 3
			values[21][1] = 100.0f;
			// upload series
			values[0][1] = -500.0f; // Upload values Time 1
			values[2][1] = -500.0f;
			values[4][1] = -500.0f;
			values[6][1] = -500.0f;
			values[8][1] = -500.0f;
			values[10][1] = -500.0f;
			values[12][1] = -500.0f;
			values[14][1] = -500.0f;
			values[16][1] = -500.0f;
			values[18][1] = -500.0f;
			values[20][1] = -500.0f;
			break;

		case REMOTE_PORT: // Remote port view

			// check values for each series.
			rows = new String[] { "10000tcp", "90tcp", "80tcp" };

			// init array to zero values
			values = new float[2 * rows.length][4];

			values[0][1] = -5500;
			values[3][1] = 5500;
			values[5][3] = 75;
			values[5][1] = 50;

			break;
		}
		checkChartData(values, rows, chart);
	}

	// Local IP and Remote Ip already selected
	private void checkGraphTwoParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		assertEquals("Network Throughput For Local Ip = " + requestParams.getIp()
				+ " For Remote Ip = " + requestParams.getRemoteIp(), chart.getTitle().getText());

		switch (requestParams.getView())
		{
		default:
		case LOCAL_PORT: // is selected a local port
			// check values per each serie.
			rows = new String[] { "10000tcp", "90tcp" };
			// init array to zero values
			values = new float[2 * rows.length][4];
			values[1][1] = 1000;
			values[2][1] = -500;
			break;

		case REMOTE_PORT: // Remote port view

			// check values for each series.
			rows = new String[] { "90tcp", "10000tcp" };
			// init array to zero values
			values = new float[2 * rows.length][4];
			values[1][1] = 1000;
			values[2][1] = -500;
			break;
		}
		checkChartData(values, rows, chart);
	}

	// Local IP, local Port and Remote IP already selected
	private void checkGraphThreeParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		assertEquals("Network Throughput For Local Ip = " + requestParams.getIp()
				+ " For Local Port = " + requestParams.getPort() + " For Remote Ip = "
				+ requestParams.getRemoteIp(), chart.getTitle().getText());

		switch (requestParams.getView())
		{
		default:
		case REMOTE_PORT: // Remote port view

			// check values for each series.
			rows = new String[] { "90tcp" };
			// init array to zero values
			values = new float[2 * rows.length][4];

			values[1][1] = 1000;
			break;
		}
		checkChartData(values, rows, chart);
	}

	/**
	 * Test values in graph when no parameters have been selected.
	 * 
	 * @throws Exception
	 */
	public void testCumulativeGraph() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.LOCAL_IP, 15);

		checkGraph(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.LOCAL_PORT, 15);
		checkGraph(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_IP, 15);
		checkGraph(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15);
		checkGraph(requestParams);
	}

	public void testCumulativeGraphOneParameter() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_IP, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);

	}

	public void testCumulativeGraphTwoParameter() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphTwoParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15, "10.0.156.120");
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphTwoParameter(requestParams);

	}

	public void testCumulativeGraphThreeParameter() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.REMOTE_PORT, 15, "10.0.156.120");
		requestParams.setPort(10000);
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphThreeParameter(requestParams);

	}

	public static Test suite()
	{
		return new TestSuite(GraphsTest.class);
	}
}
