package org.aptivate.bmotools.pmgraph;

/**
 * Just a simple exception to show Configuration errors like the error due to
 * java security.
 * 
 * @author Noe A. Rodriguez Gonzalez.
 * 
 */
public class ConfigurationException extends Exception
{
	//You need a serialVersionId, if your class implements "Serializable".  HttpServlet does so, 
	//and so you need such an id. (It is used, if the Servlet-Context is relaunched and your sessions 
	//should be made persistant / restored). 

	private static final long serialVersionUID = 6894195620443302817L;


	public ConfigurationException(String message, Throwable cause)
	{

		super(message, cause);
	}
	
}