package de.kabuman.tinkerforge.alarm.controller;

import java.util.Date;


public interface LogController {
	
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
	
	final static String UNIT_ALERT_RC = "AU Remote";
	final static String SWIITCH_ON_OFF = "Ein-Aus-Schalter";
	final static String TASTER_RESET = "Reset-Taster";
	final static String TASTER_RESTART = "Restart-Taster";
	final static String SWITCH_QUIET = "Lautlos-Schalter";
	final static String CONFIG = "Konfiguration";
	final static String ALERT_CONTROLLER = "Alert Controller";
	

	final static String SENSOR_OPEN = "Kontaktsensor";
	
	final static short MSG_INFO = 1;
	final static short MSG_WARN = 2;
	final static short MSG_ERROR = 3;
	final static short MSG_ALERT = 4;


	void createUserLogMessage(
			String unitName, 
			String triggerName, 
			int msgNr);
	
	void createUserLogMessage(
			short msgType, 
			String unitName,
			String triggerName, 
			String msg);

	public void createTechnicalLogMessage(
			String unitName,
			String triggerName, 
			String msg);
	
	void createTemperatureLogMessage(String logData);

	
	void createHumidityLogMessage(String logData);
}
