package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.util.Date;

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
public class GraphServlet extends HttpServlet {
	
	/**
	 * Creates a GraphBuilder object which queries the database and returns 
	 * graphs (using the JFreeChart library) showing logged traffic to the 
	 * browser.
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res) 
										throws ServletException, IOException {
		try {			
			// Get the parameters for graph building from the request string
			//TODO make these conditionals...
			String graphType = req.getParameter("graph");
			long start = Long.parseLong(req.getParameter("start"));
			long end = Long.parseLong(req.getParameter("end"));
			int width = Integer.parseInt(req.getParameter("width"));
			int height = Integer.parseInt(req.getParameter("height"));
			
			// Create graph of appropriate type and write to response stream
			if(graphType.equals("total")) {
				ChartUtilities.writeChartAsPNG(res.getOutputStream(),
						GraphFactory.totalThroughput(start, end),
						width,
						height);
			}
			if(graphType.equals("cumul")) {
				ChartUtilities.writeChartAsPNG(res.getOutputStream(),
						GraphFactory.stackedThroughput(start, end),
						width,
						height);
			}
			else {
				System.err.println("Unrecognised request string: " + graphType);
				// Do nothing to output stream, browser will handle broken link
			}
		}
		// SQL, ClassNotFound, IllegalAccess, Instantiation Exceptions
		catch(Exception excep) {
			excep.printStackTrace();
		}
	}
}

//else if(graphType.equals("toplocalhosts")) {
//	int maxThroughput = Integer.parseInt(req.getParameter("max"));
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(), 
//			gb.stackedThroughput(now, period, maxThroughput),
//			width,
//			height);
//}

//else if(type.equals(STACKED)) {
//	int maxThroughput = Integer.parseInt(req.getParameter("max"));
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(), 
//			gb.stackedThroughput(now, period, maxThroughput, false),
//			width,
//			height);
//}
//else if(type.equals(PIE)) {
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(),
//			gb.pieThroughput(now, period, false),
//			width,
//			height);
//}
//else if(type.equals(HYBRID)) {
//	int maxThroughput = Integer.parseInt(req.getParameter("max"));
//	// Create an area graph and a pie chart
//	BufferedImage area = 
//		gb.stackedThroughput(now, period, maxThroughput, true).
//									createBufferedImage(width, height);
//	BufferedImage pie = 
//		gb.pieThroughput(now, period, true).
//							createBufferedImage(height - height / 4,
//									            height - height / 4);
//	// Draw the pie chart on top of the area chart (could be neater)
//	area.createGraphics().drawImage(
//					pie, null, width / 2 - (height - height / 4) / 2, 
//								height / 2 - (height - height / 4) / 2);
//	ImageIO.write(area, ImageFormat.PNG, res.getOutputStream());
//}
//else if(type.equals(PROTOCOL)) {
//	int maxThroughput = Integer.parseInt(req.getParameter("max"));
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(),
//			gb.protocolThroughput(now, period, maxThroughput),
//			width,
//			height);
//}
//else if(type.equals(PORT)) {
//	int maxThroughput = Integer.parseInt(req.getParameter("max"));
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(),
//			gb.portThroughput(now, period, maxThroughput),
//			width,
//			height);
//}
//else if(type.equals(UPDOWN)) {
//	int maxDown = Integer.parseInt(req.getParameter("maxdown"));
//	int maxUp = Integer.parseInt(req.getParameter("maxup"));
//	ChartUtilities.writeChartAsPNG(res.getOutputStream(),
//			gb.totalDownUp(now, period, maxDown, maxUp),
//			width,
//			height);
//}