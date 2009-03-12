package org.aptivate.bmotools.pmgraph;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;

/**
 * A Get request to this servlet's URL prompts the creation of a GraphBuilder
 * object which then queries the database and returns graphs showing logged
 * traffic to the browser.
 * 
 * @author Thomas Sharp
 * @version 0.1
 */
public class GraphServlet extends HttpServlet
{	
	/**
	 * Creates a GraphBuilder object which queries the database and returns 
	 * graphs (using the JFreeChart library) showing logged traffic to the 
	 * browser.
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException
	{									
		try
		{			
			// Get the parameters for graph building from the request string
			//TODO make these conditionals...
			String graphType = req.getParameter("graph");
			long start = Long.parseLong(req.getParameter("start"));
			long end = Long.parseLong(req.getParameter("end"));
			int width = Integer.parseInt(req.getParameter("width"));
			int height = Integer.parseInt(req.getParameter("height"));
			
			// Create graph of appropriate type and write to response stream
			if (graphType.equals("total"))
			{
				res.setContentType("image/png");
				GraphFactory graphFactory = new GraphFactory();
				ChartUtilities.writeChartAsPNG(res.getOutputStream(),
						graphFactory.totalThroughput(start, end),
						width,
						height);
			}
			else if(graphType.equals("cumul"))
			{
				res.setContentType("image/png");
				GraphFactory graphFactory = new GraphFactory();
				ChartUtilities.writeChartAsPNG(res.getOutputStream(),
						graphFactory.stackedThroughput(start, end),
						width,
						height);
			}
			else
			{
				throw new ServletException("Unrecognised request string: " +
					graphType);
				// Do nothing to output stream, browser will handle broken link
			}
		}
		// SQL, ClassNotFound, IllegalAccess, Instantiation Exceptions
		catch(Exception excep)
		{
			excep.printStackTrace();
			throw new ServletException("GraphServlet failed", excep);
		}
	}
}