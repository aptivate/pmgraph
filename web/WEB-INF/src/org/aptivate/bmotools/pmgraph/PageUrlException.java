package org.aptivate.bmotools.pmgraph;

/**
 *  Just a simple exception class to create an 
 *  exception when is not possible to collect data for 
 *  the date and time. 
 * @author noeg
 *
 */
public class PageUrlException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7522075288522558739L;

	public PageUrlException( String message) {
		
		super(message);
	}
	
}