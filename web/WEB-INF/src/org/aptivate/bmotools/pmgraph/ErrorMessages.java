package org.aptivate.bmotools.pmgraph;

/**
 * A Class to hold all the error messages in order to make them easier
 * to change.
 * 
 * @author Noe Andres Rodriguez Glez.
 * 
 */
class ErrorMessages
{

	final static String DATE_TIME_FORMAT_ERROR = "The date format should be : dd/mm/yyyy and the time format should be : hh:mm:ss ";

	final static String START_END_FORMAT_ERROR = "Start and End parameters should be numbers ! Default start and end parameters assumed.";

	final static String RESULT_LIMIT_FORMAT_ERROR = "The number of results should be numeric ! Default number of results assumed.";

	final static String PORT_FORMAT_ERROR = "Port number must be a positive number !";

	final static String NEGATIVE_PORT_NUMBER = "Port number can't be negative !";

	final static String PORT_NUMBER_TOO_BIG = "Port number is too big (max is 65535) !";

	final static String TIME_NOT_ENOUGH = "The From Date and Time should be at least one minute before the To Date and Time";

	final static String TIME_IN_FUTURE = "The From and To Date and Time cannot be in the future.";

	final static String VIEW_FORMAT_ERROR = "Wrong view selected - default view assumed !";

	final static String IP_FORMAT_ERROR = "Invalid IP format entered";

	final static String MYSQL_CONNECTION_ERROR_JAVA_SECURITY = "Unable to get a connection to Mysql server due to java security restriction. "
			+ "Disable java security or add necesary exceptions (check the log file for more info).\n ";

	final static String MYSQL_CONNECTION_ERROR = "Unable to get a mysql connection, please check your database.properties file";

	final static String JFREECHART_ERROR_JAVA_SECURITY = "A Security Exception has occurred while trying to "
			+ "create graph image. This error is caused by Java security policy, disable java security or add "
			+ "a suitable exception in policy file.\n";

	final static String JAVA_AWT_LIBRARY_ERROR_JAVA_SECURITY = "A Security Exception has occurred while trying to access "
			+ "sun.awt.* clasess. This error is caused by Java security policy, disable java security or add "
			+ "a suitable exception in policy file.\n";

	final static String DNS_ERROR_JAVA_SECURITY = "Unable to access DNS server, check java security policy file.\n";

}
