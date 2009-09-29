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
		m_testUtils.InsertLongSampleData();
		m_testUtils.InsertVeryLongSampleData();
	}
	
	public void setData(RequestParams requestParams) throws Exception
	{
		if(TimeSpanUtils.longGraphIsAllowed() && TimeSpanUtils.needsLongGraph(requestParams.getStartTime(), requestParams.getEndTime()))
		{
			if(requestParams.getStartTime() >= m_testUtils.vlt1.getTime())
			{
				m_testUtils.InsertVeryLongSampleData();
			}
			else
			{
				m_testUtils.InsertLongSampleData();
			}
		}		
	}

	private void checkGraph(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		// values will store the different throughput for each Ip/port for upload
		// (even) and download (odd) for four different times
		float values[][];
		String rows[];
		int offset, length; // This is a multiplier to adjust data series for long time periods
		boolean isLong;
		float bytesRateAdjustment; // This is a divisor to adjust data rates covering long time periods
		GraphFactory graphFactory = new GraphFactory();
		
		// Set the values for the variables based on the range of data being displayed
		Object[] graphParameters = setValues(requestParams);
		isLong = ((Boolean)graphParameters[0]).booleanValue();
		offset = ((Integer)graphParameters[1]).intValue();
		length = ((Integer)graphParameters[2]).intValue();
		bytesRateAdjustment = ((Float)graphParameters[3]).floatValue();
		chart = graphFactory.stackedThroughputGraph(requestParams);

		switch (requestParams.getView())
		{
		default:
			m_logger.warn(" View Unknown assumed default view IP");
		
		case LOCAL_IP:		// a local IP is selected
			
			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values for each series.
			rows = new String[] { "10.0.156.120", "10.0.156.110", "10.0.156.131", "10.0.156.132",
					"10.0.156.133", "10.0.156.134", "10.0.156.135", "10.0.156.136", "10.0.156.137",
					"10.0.156.138", "10.0.156.139", "10.0.156.140" };
			// init array to zero values
			values = new float[2 * rows.length][length];

			// IP 10.0.156.120 just non-zero values
			values[0][1 * offset] = (-500 * 11)/bytesRateAdjustment; 	// upload at time 1
			values[1][1 * offset] = 5550/bytesRateAdjustment;
			values[1][3 * offset] = 75/bytesRateAdjustment; 			// download at time 3

			// IP 10.0.156.110
			values[2][0 * offset] = -2000/bytesRateAdjustment;
			values[3][0 * offset] = 90/bytesRateAdjustment;
			values[3][1 * offset] = 80/bytesRateAdjustment;
			values[3][3 * offset] = 70/bytesRateAdjustment;

			// IPs 10.0.156.131 10.0.156.140
			for (int n = 2; n < rows.length; n++)
			{
				assertEquals("10.0.156." + (130 + n - 1), rows[n]);
				values[n * 2][3 * offset] = Float.valueOf((-100 * (12 - n) - 50)/bytesRateAdjustment);
			}

			break;

		case LOCAL_PORT: // a local port is selected
			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values per each serie.
			rows = new String[] { "90tcp", "10000tcp", "12300tcp", "23500tcp", "23400tcp" };
			// init array to zero values
			values = new float[2 * rows.length][length];
			// port 90
			values[0][1 * offset] = -5500/bytesRateAdjustment; // upload (is negative in the graph)
			values[0][3 * offset] = -6000/bytesRateAdjustment; // upload
			// port 10000
			values[3][1 * offset] = 5500/bytesRateAdjustment;

			// port 12300
			values[4][0 * offset] = -2000/bytesRateAdjustment;
			values[5][0 * offset] = 90/bytesRateAdjustment;
			values[5][1 * offset] = 80/bytesRateAdjustment;
			values[5][3 * offset] = 70/bytesRateAdjustment;
			// port 23500
			values[7][3 * offset] = 75/bytesRateAdjustment;
			// port 23400
			values[9][1 * offset] = 50/bytesRateAdjustment;
			break;

		case REMOTE_IP: // Remote ip view
			assertEquals("Network Throughput", chart.getTitle().getText());
			// check values for each series.
			rows = new String[] { "4.2.2.2", "4.2.2.3", "4.2.2.4", "4.2.2.5", "4.2.2.6", "4.2.2.7",
					"4.2.2.8", "4.2.2.9", "4.2.2.10", "4.2.2.11", "4.2.2.12" };
			// init array to zero values
			values = new float[2 * rows.length][length];

			values[0][3 * offset] = -6000/bytesRateAdjustment;
			values[1][3 * offset] = 145/bytesRateAdjustment;
			values[0][0 * offset] = -2000/bytesRateAdjustment;
			values[1][0 * offset] = 90/bytesRateAdjustment;
			values[0][1 * offset] = -500/bytesRateAdjustment;
			values[1][1 * offset] = 130/bytesRateAdjustment;

			values[2][1 * offset] = -500/bytesRateAdjustment;
			values[3][1 * offset] = 1000/bytesRateAdjustment;

			values[4][1 * offset] = -500/bytesRateAdjustment;
			values[5][1 * offset] = 900/bytesRateAdjustment;

			values[6][1 * offset] = -500/bytesRateAdjustment;
			values[7][1 * offset] = 800/bytesRateAdjustment;
			values[8][1 * offset] = -500/bytesRateAdjustment;
			values[9][1 * offset] = 700/bytesRateAdjustment;
			values[10][1 * offset] = -500/bytesRateAdjustment;
			values[11][1 * offset] = 600/bytesRateAdjustment;
			values[12][1 * offset] = -500/bytesRateAdjustment;
			values[13][1 * offset] = 500/bytesRateAdjustment;
			values[14][1 * offset] = -500/bytesRateAdjustment;
			values[15][1 * offset] = 400/bytesRateAdjustment;
			values[16][1 * offset] = -500/bytesRateAdjustment;
			values[17][1 * offset] = 300/bytesRateAdjustment;
			values[18][1 * offset] = -500/bytesRateAdjustment;
			values[19][1 * offset] = 200/bytesRateAdjustment;

			values[20][1 * offset] = -500/bytesRateAdjustment;
			values[21][1 * offset] = 100/bytesRateAdjustment;
			break;

		case REMOTE_PORT: // Remote port view
			assertEquals("Network Throughput", chart.getTitle().getText());

			// check values for each series.
			rows = new String[] { "10000tcp", "90tcp", "80tcp" };

			// init array to zero values
			values = new float[2 * rows.length][length];

			// port 10000
			values[0][1 * offset] = -5500/bytesRateAdjustment;
			values[0][3 * offset] = -6000/bytesRateAdjustment;
			// port 90
			values[3][1 * offset] = 5500/bytesRateAdjustment;
			// port 80 uploadvalues
			values[4][0 * offset] = -2000/bytesRateAdjustment;
			// download
			values[5][0 * offset] = 90/bytesRateAdjustment;
			values[5][3 * offset] = 145/bytesRateAdjustment;
			values[5][1 * offset] = 130/bytesRateAdjustment;

			break;
		}
		checkChartData(values, rows, chart, isLong);
	}

	// With local Ip already selected
	private void checkGraphOneParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();
		boolean isLong;
		int offset, length;
		float bytesRateAdjustment;
		
		Object[] graphParameters = setValues(requestParams);
		isLong = ((Boolean)graphParameters[0]).booleanValue();
		offset = ((Integer)graphParameters[1]).intValue();
		length = ((Integer)graphParameters[2]).intValue();
		bytesRateAdjustment = ((Float)graphParameters[3]).floatValue();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		assertEquals("Network Throughput For Local Ip = " + requestParams.getIp(), chart.getTitle()
				.getText());

		switch (requestParams.getView())
		{
		default:
		case LOCAL_PORT: // a local port is selected
			// check values for each series.
			rows = new String[] { "90tcp", "10000tcp", "23500tcp", "23400tcp" };
			// init array to zero values
			values = new float[2 * rows.length][length];

			values[0][1 * offset] = -5500/bytesRateAdjustment;
			values[3][1 * offset] = 5500/bytesRateAdjustment;
			values[5][3 * offset] = 75/bytesRateAdjustment;
			values[7][1 * offset] = 50/bytesRateAdjustment;

			break;

		case REMOTE_IP: // Remote ip view
			// check values for each series.

			rows = new String[] { "4.2.2.3", "4.2.2.4", "4.2.2.5", "4.2.2.6", "4.2.2.7", "4.2.2.8",
					"4.2.2.9", "4.2.2.10", "4.2.2.11", "4.2.2.2", "4.2.2.12", };
			values = new float[2 * 11][length];
			values[1][1 * offset] = 1000.0f/bytesRateAdjustment;
			values[3][1 * offset] = 900.0f/bytesRateAdjustment;
			values[5][1 * offset] = 800.0f/bytesRateAdjustment;
			values[7][1 * offset] = 700.0f/bytesRateAdjustment;
			values[9][1 * offset] = 600.0f/bytesRateAdjustment;
			values[11][1 * offset] = 500.0f/bytesRateAdjustment;
			values[13][1 * offset] = 400.0f/bytesRateAdjustment;
			values[15][1 * offset] = 300.0f/bytesRateAdjustment;
			values[17][1 * offset] = 200.0f/bytesRateAdjustment;
			values[19][1 * offset] = 50.0f/bytesRateAdjustment;
			values[19][3 * offset] = 75.0f/bytesRateAdjustment; // Download values Time 3
			values[21][1 * offset] = 100.0f/bytesRateAdjustment;
			// upload series
			values[0][1 * offset] = -500.0f/bytesRateAdjustment; // Upload values Time 1
			values[2][1 * offset] = -500.0f/bytesRateAdjustment;
			values[4][1 * offset] = -500.0f/bytesRateAdjustment;
			values[6][1 * offset] = -500.0f/bytesRateAdjustment;
			values[8][1 * offset] = -500.0f/bytesRateAdjustment;
			values[10][1 * offset] = -500.0f/bytesRateAdjustment;
			values[12][1 * offset] = -500.0f/bytesRateAdjustment;
			values[14][1 * offset] = -500.0f/bytesRateAdjustment;
			values[16][1 * offset] = -500.0f/bytesRateAdjustment;
			values[18][1 * offset] = -500.0f/bytesRateAdjustment;
			values[20][1 * offset] = -500.0f/bytesRateAdjustment;
			break;

		case REMOTE_PORT: // Remote port view

			// check values for each series.
			rows = new String[] { "10000tcp", "90tcp", "80tcp" };

			// init array to zero values
			values = new float[2 * rows.length][length];

			values[0][1 * offset] = -5500/bytesRateAdjustment;
			values[3][1 * offset] = 5500/bytesRateAdjustment;
			values[5][3 * offset] = 75/bytesRateAdjustment;
			values[5][1 * offset] = 50/bytesRateAdjustment;

			break;
		}
		checkChartData(values, rows, chart, isLong);
	}

	// Local IP and Remote Ip already selected
	private void checkGraphTwoParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();
		int offset, length;
		boolean isLong;
		float bytesRateAdjustment;
		
		Object[] graphParameters = setValues(requestParams);
		isLong = ((Boolean)graphParameters[0]).booleanValue();
		offset = ((Integer)graphParameters[1]).intValue();
		length = ((Integer)graphParameters[2]).intValue();
		bytesRateAdjustment = ((Float)graphParameters[3]).floatValue();

		chart = graphFactory.stackedThroughputGraph(requestParams);

		assertEquals("Network Throughput For Local Ip = " + requestParams.getIp()
				+ " For Remote Ip = " + requestParams.getRemoteIp(), chart.getTitle().getText());

		switch (requestParams.getView())
		{
		default:
		case LOCAL_PORT: // A local port is selected
			// check values for each series.
			rows = new String[] { "10000tcp", "90tcp" };
			// init array to zero values
			values = new float[2 * rows.length][length];
			values[1][1 * offset] = 1000/bytesRateAdjustment;
			values[2][1 * offset] = -500/bytesRateAdjustment;
			break;

		case REMOTE_PORT: // Remote port view

			// check values for each series.
			rows = new String[] { "90tcp", "10000tcp" };
			// init array to zero values
			values = new float[2 * rows.length][length];
			values[1][1 * offset] = 1000/bytesRateAdjustment;
			values[2][1 * offset] = -500/bytesRateAdjustment;
			break;
		}
		checkChartData(values, rows, chart, isLong);
	}

	// Local IP, local Port and Remote IP already selected
	private void checkGraphThreeParameter(RequestParams requestParams) throws Exception
	{
		JFreeChart chart;
		float values[][];
		String rows[];
		GraphFactory graphFactory = new GraphFactory();
		int offset, length;
		boolean isLong;
		float bytesRateAdjustment;
		
		Object[] graphParameters = setValues(requestParams);
		isLong = ((Boolean)graphParameters[0]).booleanValue();
		offset = ((Integer)graphParameters[1]).intValue();
		length = ((Integer)graphParameters[2]).intValue();
		bytesRateAdjustment = ((Float)graphParameters[3]).floatValue();

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
			values = new float[2 * rows.length][length];

			values[1][1 * offset] = 1000/bytesRateAdjustment;
			break;
		}
		checkChartData(values, rows, chart, isLong);
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
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4
				.getTime(), View.LOCAL_IP, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.vlt1.getTime(), m_testUtils.vlt4.getTime(),
				View.LOCAL_IP, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.LOCAL_PORT, 15);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
				View.LOCAL_PORT, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);

		requestParams = new RequestParams(m_testUtils.vlt1.getTime(), m_testUtils.vlt4.getTime(),
				View.LOCAL_PORT, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_IP, 15);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
				View.REMOTE_IP, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.vlt1.getTime(), m_testUtils.vlt4.getTime(),
				View.REMOTE_IP, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
				View.REMOTE_PORT, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
		
		requestParams = new RequestParams(m_testUtils.vlt1.getTime(), m_testUtils.vlt4.getTime(),
				View.REMOTE_IP, 15);
		m_testUtils.ClearTable();
		setData(requestParams);
		checkGraph(requestParams);
	}

	public void testCumulativeGraphOneParameter() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_IP, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
				View.REMOTE_IP, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
				View.REMOTE_PORT, 15, "10.0.156.120");
		checkGraphOneParameter(requestParams);
	}

	public void testCumulativeGraphTwoParameter() throws Exception
	{
		RequestParams requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphTwoParameter(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4
				.getTime(), View.LOCAL_PORT, 15, "10.0.156.120");
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphTwoParameter(requestParams);

		requestParams = new RequestParams(m_testUtils.t1.getTime(), m_testUtils.t4.getTime(),
				View.REMOTE_PORT, 15, "10.0.156.120");
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphTwoParameter(requestParams);
		
		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4.getTime(),
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

		requestParams = new RequestParams(m_testUtils.lt1.getTime(), m_testUtils.lt4
				.getTime(), View.REMOTE_PORT, 15, "10.0.156.120");
		requestParams.setPort(10000);
		requestParams.setRemoteIp("4.2.2.3");
		checkGraphThreeParameter(requestParams);
	}
	
	private Object[] setValues(RequestParams requestParams)
	{
		// This method assigns the correct values to a set of values and retruns the as an object array
		Object[] values = new Object[4];
		
		// The first value determines whether or not a long value should be used, the second one used for adjusting x-axis values, the third one is for determining the length of the x-axis and the fourth determines y-axis values 
		
		boolean isLong = TimeSpanUtils.longGraphIsAllowed() && TimeSpanUtils.needsLongGraph(requestParams.getStartTime(), requestParams.getEndTime());
		values[0] = new Boolean(isLong);
		if(isLong)
		{
			if(requestParams.getStartTime() < m_testUtils.vlt1.getTime())
			{
				values[1] = new Integer(24);
				values[2] = new Integer(73);
				values[3] = new Float(60.0f);
			}
			else
			{
				values[1] = new Integer(168);
				values[2] = new Integer(505);
				values[3] = new Float(60.0f);
			}
		}
		else
		{
			values[1] = new Integer(1);
			values[2] = new Integer(4);
			values[3] = new Float(1.0f);
		}
		return values;
	}

	public static Test suite()
	{
		return new TestSuite(GraphsTest.class);
	}
}
