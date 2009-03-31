package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aptivate.bmotools.pmgraph.PageUrl.View;
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
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		try
		{
			JFreeChart chart = null;
			// Get the parameters for graph building from the request string
			// TODO make these conditionals...
			String graphType = req.getParameter("graph");
			long start = Long.parseLong(req.getParameter("start"));
			long end = Long.parseLong(req.getParameter("end"));
			int width = Integer.parseInt(req.getParameter("width"));
			int height = Integer.parseInt(req.getParameter("height"));
			int limitResult = Integer.parseInt(req.getParameter("resultLimit"));
			String ip = null;
			if ((req.getParameter("ip") != null)
					&& (!"".equalsIgnoreCase(req.getParameter("ip"))))
			{
				ip = req.getParameter("ip");
			}
			Integer port = null;
			if ((req.getParameter("port") != null)
					&& (!"".equalsIgnoreCase(req.getParameter("port"))))
			{
				port = Integer.valueOf(req.getParameter("port"));
			}

			View view;
			if ((req.getParameter("view") != null)
					&& (!"".equalsIgnoreCase(req.getParameter("view"))))
			{
				try
				{
					view = View.valueOf(req.getParameter("view"));
				}
				catch (IllegalArgumentException e)
				{
					view = View.IP; // Default is Ip view
				}
			}
			else
			{
				view = View.IP; // Default is Ip view
			}

			GraphFactory graphFactory = new GraphFactory();

			// Create graph of appropriate type
			if (graphType.equals("total"))
			{
				chart = graphFactory.totalThroughput(start, end);
			}
			else if (graphType.equals("cumul"))
			{
				if (ip != null)
				{ // Ip chart view Ignored
					chart = graphFactory.stackedThroughputOneIp(start, end,
							limitResult, ip);
				}
				else
				{
					if (port != null)
					{ // Port chart view Ignored
						chart = graphFactory.stackedThroughputOnePort(start,
								end, limitResult, port);
					}
					else
					{ // View Aplied

						switch (view)
						{
						case IP:
							chart = graphFactory.stackedThroughput(start, end,
									limitResult);
							break;
						case PORT: // Query is different for port View
							chart = graphFactory.stackedThroughputPerPort(
									start, end, limitResult);
							break;
						default:
							m_logger.error("Unexpected view in query");
						}
					}
				}
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