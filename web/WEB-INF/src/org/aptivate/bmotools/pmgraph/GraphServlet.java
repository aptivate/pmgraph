package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * A Get request to this servlet's URL prompts the creation of a GraphBuilder
 * object which then queries the database and returns graphs showing logged
 * traffic to the browser.
 * 
 * @author Thomas Sharp
 * @version 0.1
 * 
 * History:
 * 
 * Noe A. Rodriguez Glez.
 * 
 * Added log4java logging
 * 
 */
public class GraphServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger m_logger = Logger.getLogger(GraphServlet.class
			.getName());

	/**
	 * Creates a GraphBuilder object which queries the database and returns
	 * graphs (using the JFreeChart library) showing logged traffic to the
	 * browser.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			JFreeChart chart = null;
			// Get the parameters for graph building from the request string
			// TODO make these conditionals...
			String graphType = request.getParameter("graph");
			Integer width = Integer.valueOf (request.getParameter("width"));
			Integer height = Integer.valueOf (request.getParameter("height"));
			
			UrlBuilder pageUrl = new UrlBuilder();
			try
			{ 
				 pageUrl.setParameters(request);
			}	
			catch (PageUrlException e)
			{
				e.printStackTrace();
			}		
			
			GraphFactory graphFactory = new GraphFactory();

			// Create graph of appropriate type
			if (graphType.equals("total"))
			{
				// chart = graphFactory.totalThroughput(start, end);
			}
			else if (graphType.equals("cumul"))
			{
				
				chart = graphFactory.stackedThroughputGraph(pageUrl.getParams());	
				
			}
			// If chart created write as png
			if (chart != null)
			{
				res.setContentType("image/png");
				ChartUtilities.writeChartAsPNG(res.getOutputStream(), chart,
						width, height);
			}
			else
			{
				throw new ServletException("Unrecognised request string: "
						+ graphType);
				// Do nothing to output stream, browser will handle broken link
			}
		}// SQL, ClassNotFound, IllegalAccess, Instantiation Exceptions
		catch (Exception excep)
		{
			m_logger.error("GraphServlet failed", excep);
			throw new ServletException("GraphServlet failed", excep);
		}
	}
}