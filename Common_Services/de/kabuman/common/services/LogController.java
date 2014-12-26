package de.kabuman.common.services;



public interface LogController {
	
	// Predefined user log messages:
	final static Integer MSG_STARTED = 1;
	final static Integer MSG_WATER = 2;
	final static Integer MSG_OPEN = 3;
	final static Integer MSG_MOTION = 4;
	final static Integer MSG_FIRE = 5;
	final static Integer MSG_RESET = 6;
	final static Integer MSG_ON = 7;
	final static Integer MSG_OFF = 8;
	final static Integer MSG_QUIET_ON = 9;
	final static Integer MSG_QUIET_OFF = 10;
	final static Integer MSG_UNIT_LOST = 11;
	final static Integer MSG_UNIT_DISCONNECTED = 12;
	final static Integer MSG_UNIT_RECONNECTED = 13;
	final static Integer MSG_UNIT_RECONNECT_FAILED = 14;
	final static Integer MSG_RESTART = 15;
	final static Integer MSG_RESTARTED = 16;
	final static Integer MSG_REMOTE = 17;
	final static Integer MSG_EMAIL_REQUESTED = 19;
	final static Integer MSG_EMAIL_NOT_REQUESTED = 20;
	final static Integer MSG_STOPPED = 21;
	final static Integer MSG_RESET_PRESSED = 22;
	final static Integer MSG_FIRE_VOLTAGE = 23;
	
	final static String UNIT_ALERT_RC = "AU Remote";
	final static String SWIITCH_ON_OFF = "Ein-Aus-Schalter";
	final static String TASTER_RESET = "Reset-Taster";
	final static String TASTER_RESTART = "Restart-Taster";
	final static String SWITCH_QUIET = "Lautlos-Schalter";
	final static String CONFIG = "Konfiguration";
	final static String ALERT_CONTROLLER = "Alert Controller";
	

	final static String SENSOR_OPEN = "Kontaktsensor";
	
	// Category of message
	final static short MSG_INFO = 1;
	final static short MSG_WARN = 2;
	final static short MSG_ERROR = 3;
	final static short MSG_ALERT = 4;


	/**
	 * Creates predefined USER log message (for the given msgNr) with informational character only.
	 * It informs about several events which are not linked to an alert, warning, error or something like that
	 * 
	 * @param unitName - unit name
	 * @param triggerName - trigger name
	 * @param msgNr - predefined message number 
	 */
	void createUserLogMessage(
			String unitName, 
			String triggerName, 
			int msgNr);
	
	/**
	 * Creates not predefined USER Log message with official character. For one of these categories: 
	 * - MSG_INFO:  message will be indicated in user log with *
	 * - MSG_WARN:  message will be indicated in user log with ** 
	 * - MSG_ERROR: message will be indicated in user log with ***
	 * - MSG_ALERT: message will be indicated in user log with ****
	 * 
	 * The message text should be in german due to the fact that it is a end user message.
	 * 
	 * @param msgType - predefined message type. Use the defined vars in this interface:
	 * 					MSG_INFO, MSG_WARN, MSG_ERROR, MSG_ALERT
	 * @param unitName - unit name
	 * @param triggerName - trigger name
	 * @param msg - message in german
	 */
	void createUserLogMessage(
			short msgType, 
			String unitName,
			String triggerName, 
			String msg);

	/**
	 * Creates not predefined TECHNICAL log message.
	 * 
	 * @param unitName - unit name
	 * @param triggerName - trigger name
	 * @param msg - the message in englisch
	 */
	public void createTechnicalLogMessage(
			String unitName,
			String triggerName, 
			String msg);
	
	/**
	 * Creates a temerature log message which will be written to a specific comma-separated-variables file
	 * 
	 * @param logData - the log data
	 */
	void createTemperatureLogMessage(String logData);

	
	/**
	 * Creates a humidity log message which will be written to a specific comma-separated-variables file
	 * 
	 * @param logData - the log data
	 */
	void createHumidityLogMessage(String logData);
}
