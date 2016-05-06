package org.aptivate.bmotools.pmgraph;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

/**
 * This test checks if the colours in the legend and in the graph match
 * 
 * @author blancab
 * 
 */
public class ColourTest extends PmGraphTestBase
{
	private TestUtils m_testUtil;

	public ColourTest() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {

		m_testUtil = new TestUtils();
		// Create a table and insert rows into it
		m_testUtil.CreateTable();
		m_testUtil.InsertSampleData();
	}

	public void testColourGraphAndLegend() throws Exception
	{
		// Open a graph page and get the tab
		// Obtain the upload page on the website
		WebRequest request = new GetMethodWebRequest(m_testUtil.getUrlPmgraph()
				+ "?start=0&end=300000");
		WebResponse response = m_conversation.getResponse(request);
		// Get the table data from the page
		WebTable legend = (WebTable) response.getElementWithID(TestUtils.LEGEND_TBL);
		
		// Get the data from the chart. We use the renderer object to get the colour.
		JFreeChart chart;
		GraphFactory graphFactory = new GraphFactory();		
		RequestParams requestParams = new RequestParams(0, 300000, View.LOCAL_IP, 5);
		chart = graphFactory.stackedThroughputGraph(requestParams);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYItemRenderer renderer = plot.getRenderer();

		// The two first rows are for the header
		for (int i = 2; i < legend.getRowCount() - 1; i++)
		{
			// Get the first cell of each row in html format and get the style
			// attribute which is the one used to define the colour in the
			// legend
			TableCell colourLegend = legend.getTableCell(i, 0);
			String legendStyle = colourLegend.getAttribute("style");
			
			// Go through the legendStyle string and extract the colour substring which starts with #
			String colourHex = null;
			for (int n = 0; n < legendStyle.length(); n++)
			{
				if (legendStyle.charAt(n) == '#')
				{
					colourHex = legendStyle.substring(n, n + 7);
				}
			}
			// Now we can compare the Graph Colour to the Legend Colour
			//
			// Get the colour for each graph series in RGB format, and 
			// convert it to hexadecimal format in the assertEquals
			Color graphColour = (Color) renderer.getSeriesPaint(i - 2);
			assertEquals("Colour check", "#"
					+ Integer.toHexString(graphColour.getRGB() & 0x00ffffff), colourHex);
		}
	}

	public void testColourNotRepeated() throws Exception
	{
		// access the chart and get the series with a renderer element to be
		// able to access the colour
		JFreeChart chart;
		GraphFactory graphFactory = new GraphFactory();
		RequestParams requestParams = new RequestParams(0, 300000, View.LOCAL_IP, 5);
		chart = graphFactory.stackedThroughputGraph(requestParams);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYItemRenderer renderer = plot.getRenderer();
		
		// we use quarter of the series because we only need to check either the uploaded or downloaded part
		String colour[] = new String[plot.getSeriesCount() / 4];
		boolean repeated = false;
		for (int i = 0; i < (plot.getSeriesCount() / 4); i++)
		{
			Color singleColour = (Color) renderer.getSeriesPaint(i);
			String singleHexColour = Integer.toHexString(singleColour.getRGB() & 0x00ffffff);
			// We store the colour we obtain in an array and check that each new colour is not already on the list
			for (int j = 0; j <= i; j++)
			{
				if (singleHexColour.equals(colour[j]))
					repeated = true;
			}
			if (repeated == false)
				colour[i] = singleHexColour;
			assertTrue("Check if some colour in the graph is repeated", !repeated);
		}
	}

	public static Test suite()
	{
		return new TestSuite(ColourTest.class);
	}
}
