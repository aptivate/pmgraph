package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.Layer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * Abstract Class which contains the method which checks if the values of a
 * JFreechart correspond with the rows and values array passed. It can be used
 * whenever we try to check the values of a graph then it is used for the port
 * graph and for the Ip graph.
 * 
 * @author Noe A. Rodriguez Gonzalez.
 * 
 */
abstract class GraphTestBase extends TestCase
{
	protected TestUtils m_testUtils;

	public GraphTestBase() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{

		m_testUtils = new TestUtils();
	}

	/**
	 *  Check that the chart contains the expected data points.
	 *  First it checks if the graph has an upload and download 
	 *  series for each of the values of the array "rows".
	 *    
	 *  Secondly checks if each one has the expected number of time points.
	 *    
	 *  Finally checks the data points.
	 *  
	 * @param values values of each series.
	 * @param rows 
	 * @param chart
	 */
	protected void checkChartData(float values[][], String[] rows,
			JFreeChart chart, boolean isLong)
	{	
		XYPlot plot = (XYPlot) chart.getPlot();
		assertEquals(PlotOrientation.VERTICAL, plot.getOrientation());
		Collection markers = plot.getRangeMarkers(Layer.FOREGROUND);
		Iterator i = markers.iterator();
		assertEquals(i.next(), new ValueMarker(0));
		assertFalse(i.hasNext());

		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		assertEquals("Throughput (kb/s)", yAxis.getLabel());
		DefaultTableXYDataset dataset = (DefaultTableXYDataset) plot
				.getDataset();
		
		// Check if there are a series for each port for upload and another for
		// download data.		
		int inc = 0;
		if ((dataset.getSeries(rows.length).getKey().equals("Othersnull<down>")) ||(dataset.getSeries(rows.length).getKey().equals("Others<down>")))
			inc = 1;
		for (int n = 0; n < rows.length; n++)
		{					
			assertTrue("missing item " + rows[n],
					dataset.getSeriesCount() > (n << 1));			
			assertEquals(rows[n] + "<down>", dataset.getSeries(n).getKey());						
			assertEquals(rows[n] + "<up>", dataset.getSeries(((rows.length) + n + inc))
					.getKey());
		}
		
		Map<String, XYSeries> series = new HashMap<String, XYSeries>();

		// check the number of elements of each series.
		for (int n = 0; n < dataset.getSeriesCount(); n++)
		{
			XYSeries s = dataset.getSeries(n);
			if(isLong)
			{
				if(s.getX(0).longValue() < m_testUtils.vlt1.getTime())
				{
					assertEquals(73, s.getItemCount());
					assertEquals(m_testUtils.lt1.getTime(), s.getX(0));
					assertEquals(m_testUtils.lt2.getTime(), s.getX(24));
					assertEquals(m_testUtils.lt3.getTime(), s.getX(48));
					assertEquals(m_testUtils.lt4.getTime(), s.getX(72));
					series.put(s.getKey().toString(), s);
				}
				else
				{
					assertEquals(505, s.getItemCount());
					assertEquals(m_testUtils.vlt1.getTime(), s.getX(0));
					assertEquals(m_testUtils.vlt2.getTime(), s.getX(168));
					assertEquals(m_testUtils.vlt3.getTime(), s.getX(336));
					assertEquals(m_testUtils.vlt4.getTime(), s.getX(504));
					series.put(s.getKey().toString(), s);
				}
			}
			else
			{
				assertEquals(4, s.getItemCount());
				assertEquals(m_testUtils.t1.getTime(), s.getX(0));
				assertEquals(m_testUtils.t2.getTime(), s.getX(1));
				assertEquals(m_testUtils.t3.getTime(), s.getX(2));
				assertEquals(m_testUtils.t4.getTime(), s.getX(3));
				series.put(s.getKey().toString(), s);
			}
		}

		// check that each series has the correct values for each time.
		for (int n = 0; n < rows.length; n++)
		{
			XYSeries s = series.get(rows[n] + "<up>"); // check Up series
			// values.
			for (int j = 0; j < (values[2 * n]).length; j++)
			{
				assertEquals(values[2 * n][j], s.getY(j));
			}
			s = series.get(rows[n] + "<down>"); // check down series values.
			for (int j = 0; j < (values[2 * n + 1]).length; j++)
			{
				assertEquals(values[2 * n + 1][j], s.getY(j));
			}
		}
	}

}
