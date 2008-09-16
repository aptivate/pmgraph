package org.apache.jsp.include;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.awt.Color;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.aptivate.bmotools.pmgraph.*;

public final class legend_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

    long start = Long.parseLong(request.getParameter("start"));
    long end = Long.parseLong(request.getParameter("end"));
    
    // Round our times to the nearest minute
    start = start - (start % 60000);
    end = end - (end % 60000);
    
    // Get database connection and network properties
    Connection conn = GraphUtilities.getConnection();
    String localSubnet = GraphUtilities.getProperties();
    
    // Prepare and execute the query to find all active IPs on the network
    PreparedStatement ipStatement = 
    	   conn.prepareStatement(GraphUtilities.THROUGHPUT_PER_IP);
    ipStatement.setString(1, localSubnet + "%");
    ipStatement.setString(2, localSubnet + "%");
    ipStatement.setString(3, localSubnet + "%");
    ipStatement.setString(4, localSubnet + "%");
    ipStatement.setString(5, localSubnet + "%");
    ipStatement.setTimestamp(6, new Timestamp(start));
    ipStatement.setTimestamp(7, new Timestamp(end));
    System.out.println(ipStatement);
    ResultSet ipResults = ipStatement.executeQuery();
    ipResults.beforeFirst();

      out.write("\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("\t<thead>\n");
      out.write("\t\t<tr>\n");
      out.write("\t\t    <th></th>\n");
      out.write("            <th rowspan=\"2\">Host IP</th>\n");
      out.write("            <th colspan=\"2\">Totals (MB)</th>\n");
      out.write("\t\t</tr>\n");
      out.write("\t\t\n");
      out.write("\t\t<tr>\n");
      out.write("\t\t    <th></th>\n");
      out.write("\t\t    <th>Downloaded</th>\n");
      out.write("\t\t    <th>Uploaded</th>\n");
      out.write("\t\t</tr>\n");
      out.write("\t</thead>\n");
      out.write("\t");

    while(ipResults.next()) {
    	String ip = ipResults.getString("local_ip");
    	byte[] ipBytes = ip.getBytes();
        MessageDigest algorithm = MessageDigest.getInstance("SHA1");
        algorithm.reset();
        algorithm.update(ipBytes);
        byte sha1[] = algorithm.digest();
        Color c = new Color(sha1[0] & 0xFF, sha1[1] & 0xFF, sha1[2] & 0xFF);
        String fillColour = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
    
      out.write("\n");
      out.write("    <tr class=\"row");
      out.print(ipResults.getRow() % 2);
      out.write("\">\n");
      out.write("        <td style=\"background-color: ");
      out.print(fillColour);
      out.write("; width: 5px\"> </td>\n");
      out.write("        <td>");
      out.print(ip);
      out.write("</td>\n");
      out.write("        <td class=\"numval\">");
      out.print((ipResults.getInt("downloaded") / 1048576));
      out.write("</td>\n");
      out.write("        <td class=\"numval\">");
      out.print((ipResults.getInt("uploaded") / 1048576));
      out.write("</td>\n");
      out.write("    </tr>\n");
      out.write("    ");

    }
	
      out.write("\n");
      out.write("</table>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
