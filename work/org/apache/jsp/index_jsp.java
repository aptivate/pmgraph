package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Date;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("\n");
      out.write("\n");

    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long startTime = (param = request.getParameter("start")) != null ? Long.parseLong(param) : now - 240 * 60000;
    long endTime = (param = request.getParameter("end")) != null ? Long.parseLong(param) : now;
    
    // URLs to resources requiring further parameters
    String indexURL = "/pmgraph/index.jsp";
    String servletURL = "/pmgraph/graphservlet";
    String legendURL = "/include/legend.jsp";

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
      out.write("                <!-- Graph parameter controls not yet functional -->\n");
      out.write("                <!-- ");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "/include/params.jsp", out, false);
      out.write(" -->\n");
      out.write("                \n");
      out.write("                <div id=\"graph\">\n");
      out.write("                    <img id=\"graphimage\" alt=\"Bandwith Graph\" \n");
      out.write("                            src=\"");
      out.print(servletURL +
                                    "?graph=" + graph +
                                    "&start=" + startTime +
                                    "&end=" + endTime +
                                    "&width=760" +
                                    "&height=350");
      out.write("\" width=\"760\" height=\"350\" />     \n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <!-- Move back/forward or zoom in/out by 2 hours-->\n");
      out.write("                <div id=\"controls\">\n");
      out.write("                    <a href=\"");
      out.print(indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime - 120 * 60000) +
                                "&end=" + (endTime - 120 * 60000));
      out.write("\" class=\"control\">Prev.</a>\n");
      out.write("                    <div id=\"controlscenter\">\n");
      out.write("                        <a href=\"");
      out.print(indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime - 60 * 60000) +
                                    "&end=" + (endTime + 60 * 60000));
      out.write("\" class=\"control\">Zoom -</a>\n");
      out.write("                        <a href=\"");
      out.print(indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime + 60 * 60000) +
                                    "&end=" + (endTime - 60 * 60000));
      out.write("\" class=\"control\">Zoom +</a>\n");
      out.write("                    </div>\n");
      out.write("                    <a href=\"");
      out.print(indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime + 120 * 60000) +
                                "&end=" + (endTime + 120 * 60000));
      out.write("\" class=\"control\">Next</a>\n");
      out.write("                </div>    \n");
      out.write("    \n");
      out.write("                <div id=\"legend\">\n");
      out.write("                    ");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, legendURL + "?start=" + startTime + "&end=" + endTime, out, false);
      out.write("\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            \n");
      out.write("            <!-- <div id=\"footer\"></div> -->\n");
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
