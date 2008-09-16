package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class prototype_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
      out.write("<!DOCTYPE html PUBLIC \n");
      out.write("    \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
      out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
      out.write("\n");

    String report = request.getParameter("report");
    int scale = Integer.parseInt(request.getParameter("scale"));
    int period = Integer.parseInt(request.getParameter("period")) * scale;
    
    String servletURL =
        "/pmgraph/graphservlet?"+
                "graph="+report+
                "&period="+period+
                "&width=760&height=350&max=16"; //Need to accommodate for different params

      out.write("\n");
      out.write("\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
      out.write("    <head>\n");
      out.write("      <title>pmGraph</title>\n");
      out.write("      <link rel=\"Stylesheet\" href=\"styles/main.css\" type=\"text/css\" />\n");
      out.write("    </head>\n");
      out.write("  \n");
      out.write("    <body>\n");
      out.write("        <div id=\"container\">\n");
      out.write("            <div id=\"header\">\n");
      out.write("                <img alt=\"Logo Banner\" src=\"images/header.png\" width=\"760\" height=\"75\" />\n");
      out.write("            </div>\n");
      out.write("            \n");
      out.write("            <div id=\"main\">\n");
      out.write("                <div id=\"params\">\n");
      out.write("                    <form id=\"graphparams\" action=\"/pmgraph/prototype.jsp\" method=\"get\">\n");
      out.write("                        <p>\n");
      out.write("                        <label for=\"report\">Report type:</label>\n");
      out.write("                        <select id=\"report\" name=\"report\">\n");
      out.write("                            <option value=\"toplocalhosts\">Top local hosts</option>\n");
      out.write("                            <option value=\"topremotehosts\">Top remote hosts</option>\n");
      out.write("                            <option value=\"toplocalports\">Top local ports</option>\n");
      out.write("                            <option value=\"topremoteports\">Top remote ports</option>\n");
      out.write("                        </select>\n");
      out.write("                        \n");
      out.write("                        <label for=\"period\">From</label>\n");
      out.write("                        <input  id=\"period\" type=\"text\" name=\"period\" size=\"3\" value=\"0\" />\n");
      out.write("                        \n");
      out.write("                        <select name=\"scale\">  <!-- check fitting of 'minute' option -->\n");
      out.write("                            <option value=\"1\">minutes</option>\n");
      out.write("                            <option value=\"60\">hours</option>\n");
      out.write("                            <option value=\"1440\">days</option>\n");
      out.write("                        </select>\n");
      out.write("                        \n");
      out.write("                        ago to now\n");
      out.write("                        \n");
      out.write("                        <input type=\"submit\" id=\"go\" value=\"Update\" />\n");
      out.write("                        </p>\n");
      out.write("                        \n");
      out.write("                        <p class=\"radios\">\n");
      out.write("                        <label for=\"cumul\">Cumulative</label>\n");
      out.write("                        <input id=\"cumul\" type=\"radio\" name=\"style\" value=\"cumul\" checked=\"checked\" onclick=\"javascript:alert('Make this update the graph')\" />\n");
      out.write("                        <label for=\"line\">Line</label>\n");
      out.write("                        <input id=\"line\" type=\"radio\" name=\"style\" value=\"line\" onclick=\"javascript:alert('Make this update the graph')\" />\n");
      out.write("                        <label for=\"pie\">Pie</label>\n");
      out.write("                        <input id=\"pie\" type=\"radio\" name=\"style\" value=\"pie\" onclick=\"javascript:alert('Make this update the graph')\" />\n");
      out.write("                        </p>\n");
      out.write("                    </form>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div id=\"graph\">\n");
      out.write("                    <img alt=\"Bandwith Graph\" src=\"");
      out.print(servletURL);
      out.write("\" width=\"760\" height=\"350\" />\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div id=\"controls\">\n");
      out.write("                    <input type=\"button\" name=\"prev\" value=\"Prev. x\" />\n");
      out.write("\n");
      out.write("                    <div id=\"controlscenter\">\n");
      out.write("                        <input type=\"button\" name=\"out\" value=\"Zoom -\" />\n");
      out.write("                        <input type=\"button\" name=\"in\" value=\"Zoom +\" />\n");
      out.write("                    </div>\n");
      out.write("                    \n");
      out.write("                    <input type=\"button\" name=\"next\" value=\"Next x\" />\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div id=\"legend\">\n");
      out.write("                    <table>\n");
      out.write("                        <thead>\n");
      out.write("                            <tr>\n");
      out.write("                                <th>Host IP</th>\n");
      out.write("                                <th>Down</th>\n");
      out.write("                                <th>Up</th>\n");
      out.write("                                <th>Top ports</th>\n");
      out.write("                                <th>Up kB/s</th>\n");
      out.write("                                <th>Down kB/s</th>\n");
      out.write("                            </tr>\n");
      out.write("                        </thead>\n");
      out.write("                        <tr class=\"evenrow\">\n");
      out.write("                            <td><a href=\"anotherquery\">192.168.1.5</a></td>\n");
      out.write("                            <td>200</td>\n");
      out.write("                            <td>50</td>\n");
      out.write("                            <td><a href=\"anotherquery\">80</a>, <a href=\"anotherquery\">25</a></td>\n");
      out.write("                            <td>6</td>\n");
      out.write("                            <td>3</td>\n");
      out.write("                        </tr>\n");
      out.write("                        <tr class=\"oddrow\">\n");
      out.write("                            <td><a href=\"anotherquery\">192.168.1.6</a></td>\n");
      out.write("                            <td>598</td>\n");
      out.write("                            <td>13</td>\n");
      out.write("                            <td><a href=\"anotherquery\">80</a>, <a href=\"anotherquery\">25</a></td>\n");
      out.write("                            <td>23</td>\n");
      out.write("                            <td>3</td>\n");
      out.write("                        </tr>\n");
      out.write("                        <tr class=\"evenrow\">\n");
      out.write("                            <td><a href=\"anotherquery\">192.168.1.7</a></td>\n");
      out.write("                            <td>600</td>\n");
      out.write("                            <td>43</td>\n");
      out.write("                            <td><a href=\"anotherquery\">80</a>, <a href=\"anotherquery\">25</a></td>\n");
      out.write("                            <td>15</td>\n");
      out.write("                            <td>9</td>\n");
      out.write("                        </tr>\n");
      out.write("                    </table>\n");
      out.write("                </div> \n");
      out.write("            </div>\n");
      out.write("            \n");
      out.write("            <div id=\"footer\">\n");
      out.write("                <p>TODO: Complete legend w/ database read and colour coding</p>\n");
      out.write("                <p>TODO: Make graph params match the graph currently showing</p>\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>");
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
