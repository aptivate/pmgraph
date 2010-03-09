package org.aptivate.bmotools.pmgraph;

/**
 * Just a simple exception class to create an exception when is not possible to
 * collect data for the date and time.
 * 
 * @author noeg
 * 
 */
public class PageUrlException extends Exception
{
	// You need a serialVersionId, if your class implements "Serializable".
	// HttpServlet does so, and so you need such an id. (It is used, if the
	// Servlet-Context is relaunched and your sessions should be made 
	// persistant / restored).
	private static final long serialVersionUID = 7522075288522558739L;

	public PageUrlException(String message) {
		super(message);
	}

}