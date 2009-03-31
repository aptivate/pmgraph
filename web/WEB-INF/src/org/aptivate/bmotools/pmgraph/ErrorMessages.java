package org.aptivate.bmotools.pmgraph;

/**
 * A Class to put together all the error messages in orther to make then more
 * easy to change.
 * 
 * @author Noe Andres Rodriguez Glez.
 * 
 */
public class ErrorMessages
{

	public final static String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !\\n The time format should be : hh:mm:ss !";

	public final static String START_END_FORMAT_ERROR = "Start and End parameters Should be numbers ! \\n Default start end parameters assumed.";

	public final static String RESULT_LIMIT_FORMAT_ERROR = "ResultLimit parameter should by a number ! \\n Default resultLimit value assumed.";

	public final static String PORT_FORMAT_ERROR = "Port number must be a positive Integer !";

	public final static String NEGATIVE_PORT_NUMBER = "Port number can't be negative !";

	public final static String TIME_NOT_ENOUGH = "THE FROM DATE AND TIME HAVE TO BE AT LEAST 1 MINUTE BEFORE THE TO DATE AND TIME.";

	public final static String TIME_IN_FUTURE = "The From and To Date and Time cannot be in the future.";

	public final static String VIEW_FORMAT_ERROR = "Wrong view selected default view assumed !";

}
