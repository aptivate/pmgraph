package org.aptivate.pmGraph.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.aptivate.bmotools.pmgraph.GraphFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.Layer;
/**
 *  
 * @author noeg
 *
 */
public class GraphsTest extends TestCase
{


	/**/
	public void testCumulativeGraph() throws Exception
	{
		TestUtils testUtils = new TestUtils();
		testUtils.CreateTable();
		testUtils.InsertSampleData();

		GraphFactory graphFactory = new GraphFactory();

		JFreeChart chart = graphFactory.stackedThroughput(testUtils.t1.getTime(),testUtils.t4
				.getTime());
		assertEquals("Network Throughput Per IP", chart.getTitle().getText());

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
		// assertEquals(24, dataset.getSeriesCount());

		String[] hosts = new String[] { "10.0.156.120", "10.0.156.110",
				"10.0.156.131", "10.0.156.132", "10.0.156.133", "10.0.156.134",
				"10.0.156.135", "10.0.156.136", "10.0.156.137", "10.0.156.138",
				"10.0.156.139", "10.0.156.140" };

		for (int n = 0; n < hosts.length; n++)
		{
			assertTrue("missing item " + hosts[n],
					dataset.getSeriesCount() > (n << 1));
			assertEquals(hosts[n] + "<down>", dataset.getSeries(n << 1)
					.getKey());
			assertEquals(hosts[n] + "<up>", dataset.getSeries((n << 1) + 1)
					.getKey());
		}

		assertEquals(hosts.length * 2, dataset.getSeriesCount());

		Map<String, XYSeries> series = new HashMap<String, XYSeries>();

		for (int n = 0; n < dataset.getSeriesCount(); n++)
		{
			XYSeries s = dataset.getSeries(n);
			assertEquals(4, s.getItemCount());

			assertEquals(testUtils.t1.getTime(), s.getX(0));
			assertEquals(testUtils.t2.getTime(), s.getX(1));
			assertEquals(testUtils.t3.getTime(), s.getX(2));
			assertEquals(testUtils.t4.getTime(), s.getX(3));

			assertEquals(Long.valueOf(0), s.getY(2));

			series.put(s.getKey().toString(), s);
		}

		XYSeries s = series.get("10.0.156.110<up>");
		assertEquals(Long.valueOf(-2000), s.getY(0));
		assertEquals(Long.valueOf(0), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(0), s.getY(3));

		s = series.get("10.0.156.110<down>");
		assertEquals(Long.valueOf(90), s.getY(0));
		assertEquals(Long.valueOf(80), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(70), s.getY(3));

		s = series.get("10.0.156.120<up>");
		assertEquals(Long.valueOf(0), s.getY(0));
		assertEquals(Long.valueOf(-500 * 11), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(0), s.getY(3));

		s = series.get("10.0.156.120<down>");
		assertEquals(Long.valueOf(0), s.getY(0));
		assertEquals(Long.valueOf(5500 + 50), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(75), s.getY(3));

		for (int n = 2; n < hosts.length; n++)
		{
			assertEquals("10.0.156." + (130 + n - 1), hosts[n]);

			s = series.get(hosts[n] + "<up>");
			assertEquals(Long.valueOf(0), s.getY(0));
			assertEquals(Long.valueOf(0), s.getY(1));
			assertEquals(Long.valueOf(0), s.getY(2));
			assertEquals(Long.valueOf(-100 * (12 - n) - 50), s.getY(3));

			s = series.get(hosts[n] + "<down>");
			assertEquals(Long.valueOf(0), s.getY(0));
			assertEquals(Long.valueOf(0), s.getY(1));
			assertEquals(Long.valueOf(0), s.getY(2));
			assertEquals(Long.valueOf(0), s.getY(3));
		}
	}
	
	public static Test suite()
	{
		return new TestSuite(GraphsTest.class);
	}

}
