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

	final static String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy and the time format should be : hh:mm:ss ";

	final static String RESULT_LIMIT_FORMAT_ERROR = "The number of results should be numeric ! Default number of results assumed.";

	final static String PORT_FORMAT_ERROR = "Port number must be a positive number !";

	final static String NEGATIVE_PORT_NUMBER = "Port number can't be negative !";
	
	final static String PORT_NUMBER_TOO_BIG = "Port number is too big (max is 65535) !";

	final static String TIME_NOT_ENOUGH = "The From Date and Time should be at least one minute before the To Date and Time";

	final static String TIME_IN_FUTURE = "The From and To Date and Time cannot be in the future.";

	final static String VIEW_FORMAT_ERROR = "Wrong view selected - default view assumed !";
	
	final static String IP_FORMAT_ERROR = "Invalid IP format entered";

}
