package org.apache.jsp.include;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class params_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("<!-- Div which includes select for report type and radios for graph type -->\n");
      out.write("\n");
      out.write("<div id=\"params\">\n");
      out.write("    <form id=\"graphparams\" action=\"/pmgraph/index.jsp\" method=\"get\">\n");
      out.write("        <p>\n");
      out.write("        <label for=\"report\">Report type:</label> \n");
      out.write("        <select id=\"report\" name=\"report\" disabled=\"disabled\">\n");
      out.write("            <option value=\"totals\">Totals</option>\n");
      out.write("            <option value=\"toplocalhosts\">Top local hosts</option>\n");
      out.write("\t        <option value=\"topremotehosts\">Top remote hosts</option>\n");
      out.write("\t        <option value=\"toplocalports\">Top local ports</option>\n");
      out.write("            <option value=\"topremoteports\">Top remote ports</option>\n");
      out.write("        </select> \n");
      out.write("        <label for=\"period\">From</label> \n");
      out.write("        <input id=\"period\" type=\"text\" name=\"period\" size=\"3\" value=\"0\" disabled=\"disabled\" /> \n");
      out.write("        <select name=\"scale\" disabled=\"disabled\">\n");
      out.write("\t        <option value=\"1\">minutes</option>\n");
      out.write("\t        <option value=\"60\">hours</option>\n");
      out.write("\t        <option value=\"1440\">days</option>\n");
      out.write("        </select> \n");
      out.write("        \n");
      out.write("        ago to now \n");
      out.write("        \n");
      out.write("        <input type=\"submit\" id=\"go\" value=\"Update\" disabled=\"disabled\" />\n");
      out.write("        </p>\n");
      out.write("\n");
      out.write("        <p class=\"radios\">\n");
      out.write("        <label for=\"cumul\">Cumulative</label> \n");
      out.write("        <input id=\"cumul\" type=\"radio\" name=\"style\" value=\"cumul\" checked=\"checked\"\tdisabled=\"disabled\" /> \n");
      out.write("        <label for=\"line\">Line</label> \n");
      out.write("        <input id=\"line\" type=\"radio\" name=\"style\" value=\"line\"\tdisabled=\"disabled\" /> \n");
      out.write("\t    <label for=\"pie\">Pie</label> \n");
      out.write("\t    <input id=\"pie\"\ttype=\"radio\" name=\"style\" value=\"pie\" disabled=\"disabled\" /></p>\n");
      out.write("    </form>\n");
      out.write("</div>");
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
