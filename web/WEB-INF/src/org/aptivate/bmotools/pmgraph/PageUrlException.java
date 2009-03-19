package org.aptivate.bmotools.pmgraph;
/**
 *  Just a simple exception class to be able to create a 
 *  exception when is not posible to collect data for 
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