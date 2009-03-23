package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;

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
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			// Get the parameters for graph building from the request string
			// TODO make these conditionals...
			String graphType = req.getParameter("graph");
			long start = Long.parseLong(req.getParameter("start"));
			long end = Long.parseLong(req.getParameter("end"));
			int width = Integer.parseInt(req.getParameter("width"));
			int height = Integer.parseInt(req.getParameter("height"));
			int limitResult = Integer.parseInt(req.getParameter("resultLimit"));

			// Create graph of appropriate type and write to response stream
			if (graphType.equals("total"))
			{
				res.setContentType("image/png");
				GraphFactory graphFactory = new GraphFactory();
				ChartUtilities
						.writeChartAsPNG(res.getOutputStream(), graphFactory
								.totalThroughput(start, end), width, height);
			}
			else if (graphType.equals("cumul"))
			{
				res.setContentType("image/png");
				GraphFactory graphFactory = new GraphFactory();
				ChartUtilities.writeChartAsPNG(res.getOutputStream(),
						graphFactory.stackedThroughput(start, end, limitResult), width,
						height);
			}
			else
			{
				throw new ServletException("Unrecognised request string: "
						+ graphType);
				// Do nothing to output stream, browser will handle broken link
			}
		}
		// SQL, ClassNotFound, IllegalAccess, Instantiation Exceptions
		catch (Exception excep)
		{
			m_logger.error("GraphServlet failed", excep);
			throw new ServletException("GraphServlet failed", excep);
		}
	}
}