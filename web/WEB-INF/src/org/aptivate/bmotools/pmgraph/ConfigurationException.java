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
	private static final long serialVersionUID = 6894195620443302817L;

	public ConfigurationException(String message)
	{

		super(message);
	}

}