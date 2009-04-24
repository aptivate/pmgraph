package org.aptivate.bmotools.pmgraph;

/**
 * A Class to put together all the error messages in orther to make then more
 * easy to change.
 * 
 * @author Noe Andres Rodriguez Glez.
 * 
 */
class ErrorMessages
{

	final static String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy !\\n The time format should be : hh:mm:ss !";

	final static String START_END_FORMAT_ERROR = "Start and End parameters Should be numbers ! \\n Default start end parameters assumed.";

	final static String RESULT_LIMIT_FORMAT_ERROR = "ResultLimit parameter should by a number ! \\n Default resultLimit value assumed.";

	final static String PORT_FORMAT_ERROR = "Port number must be a positive Integer !";

	final static String NEGATIVE_PORT_NUMBER = "Port number can't be negative !";

	final static String TIME_NOT_ENOUGH = "THE FROM DATE AND TIME HAVE TO BE AT LEAST 1 MINUTE BEFORE THE TO DATE AND TIME.";

	final static String TIME_IN_FUTURE = "The From and To Date and Time cannot be in the future.";

	final static String VIEW_FORMAT_ERROR = "Wrong view selected default view assumed !";
	
	final static String IP_FORMAT_ERROR = "Invalid IP format used";

}
