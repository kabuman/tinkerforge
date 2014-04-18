package de.kabuman.tinkerforge.alarm.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;

import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.TableFormatterService;
import de.kabuman.common.services.TableFormatterServiceImpl;

public class LogControllerImpl implements LogController {

	private static LogControllerImpl instance = null;

	// List<LogMessage> logMessageList = new ArrayList<LogMessage>();

	HashMap<Integer, LogMessage> logMessageList = new HashMap<Integer, LogMessage>();
	
	private String path = null;
	private String consoleRecord = null;
	
	// user log file
	private String userLogFileName = null;
	private Writer userLogFileFW = null;
	private Writer userLogFileBW = null;
	private PrintWriter userLogFilePW = null;

	// technical log file
	private String technicalLogFileName = null;
	private Writer technicalLogFileFW = null;
	private Writer technicalLogFileBW = null;
	private PrintWriter technicalLogFilePW = null;

	// temperature log file
	private String temperatureLogFileName = null;
	private Writer temperatureLogFileFW = null;
	private Writer temperatureLogFileBW = null;
	private PrintWriter temperatureLogFilePW = null;

	// temperature log file
	private String humidityLogFileName = null;
	private Writer humidityLogFileFW = null;
	private Writer humidityLogFileBW = null;
	private PrintWriter humidityLogFilePW = null;

	// Column positions in log messages
	private int[] pos = { 5, 30, 56, 82 };

	public class LogMessage {
		String msgTxt;
		short msgType;

		LogMessage(String msgTxt, short msgType) {
			this.msgTxt = msgTxt;
			this.msgType = msgType;
		}

		public String getMsgTxt() {
			return msgTxt;
		}

		public short getMsgType() {
			return msgType;
		}
	}

	private LogControllerImpl(String path, String userLogFileName, String technicalLogFileName, String temperatureLogFileName, String humidityLogFileName) {
		this.path = path;
		this.userLogFileName = userLogFileName;
		this.technicalLogFileName = technicalLogFileName;
		this.temperatureLogFileName = temperatureLogFileName;
		this.humidityLogFileName = humidityLogFileName;

		openUserLogFile();
		openTechnicalLogFile();
		openTemperatureLogFile();
		openHumidityLogFile();
		createLogMessageList();

	}
	
	private void openUserLogFile(){
		try
		{
			userLogFileFW = new FileWriter( this.path+this.userLogFileName, true);
			userLogFileBW = new BufferedWriter( userLogFileFW );
			userLogFilePW = new PrintWriter( userLogFileBW );
		}
		catch ( IOException e ) {
			System.err.println( "LogController: Error creating file="+path+userLogFileName );
		}
	}

	private void openTechnicalLogFile(){
		try
		{
			technicalLogFileFW = new FileWriter( this.path+this.technicalLogFileName, true);
			technicalLogFileBW = new BufferedWriter( technicalLogFileFW );
			technicalLogFilePW = new PrintWriter( technicalLogFileBW );
		}
		catch ( IOException e ) {
			System.err.println( "LogController: Error creating file="+path+technicalLogFileName );
		}
	}

	private void openTemperatureLogFile(){
		try
		{
			temperatureLogFileFW = new FileWriter( this.path+this.temperatureLogFileName, true);
			temperatureLogFileBW = new BufferedWriter( temperatureLogFileFW );
			temperatureLogFilePW = new PrintWriter( temperatureLogFileBW );
		}
		catch ( IOException e ) {
			System.err.println( "LogController: Error creating file="+path+temperatureLogFileName );
		}
	}

	private void openHumidityLogFile(){
		try
		{
			humidityLogFileFW = new FileWriter( this.path+this.humidityLogFileName, true);
			humidityLogFileBW = new BufferedWriter( humidityLogFileFW );
			humidityLogFilePW = new PrintWriter( humidityLogFileBW );
		}
		catch ( IOException e ) {
			System.err.println( "LogController: Error creating file="+path+humidityLogFileName );
		}
	}

	public static LogControllerImpl getNewInstance(String path, String userLogFileName, String technicalLogFileName, String temperatureLogFileName, String humidityLogFileName) {
		if (instance == null) {
			instance = new LogControllerImpl(path, userLogFileName, technicalLogFileName, temperatureLogFileName, humidityLogFileName);
		}
		return instance;
	}

	public static LogControllerImpl getInstance() {
		return instance;
	}

	public void createUserLogMessage(String unitName, String triggerName, int msgId) {
		LogMessage logMessage = detectedMessage(msgId);

		createUserLogMessage(logMessage.getMsgType(), unitName, triggerName,
				logMessage.getMsgTxt());
	}

	public void createUserLogMessage(short msgType, String unitName,
			String triggerName, String msg) {

		String msgTypeStrg = createMsgType(msgType);
		Date logDate = new Date();
		
		// Console Output
		prepareAndWriteConsoleRecord(msgTypeStrg, logDate, unitName,
				triggerName, msg);

		// csv file Output
		prepareAndWriteUserLogFileRecord(msgTypeStrg, logDate, unitName,
				triggerName, msg);
		
		prepareAndWriteTechnicalLogFileRecord(msgTypeStrg, logDate, unitName,
				triggerName, msg);

	}
	
	public void createTechnicalLogMessage(String unitName,
			String triggerName, String msg) {

		Date logDate = new Date();
		
		String msgTypeStrg = "    ";
		
		// CSV File Output
		prepareAndWriteTechnicalLogFileRecord(msgTypeStrg, logDate, unitName,
				triggerName, msg);
		
		// TODO - Remove "TechnicalLog to Console Output
		prepareAndWriteConsoleRecord(msgTypeStrg, logDate, unitName,
				triggerName, msg);

	}
	
	public void createTemperatureLogMessage(String logData) {

		Date logDate = new Date();
		
		// CSV File Output
		prepareAndWriteTemperatureLogFileRecord(logDate, logData);
	}
	
	public void createHumidityLogMessage(String logData) {

		Date logDate = new Date();
		
		// CSV File Output
		prepareAndWriteHumidityLogFileRecord(logDate, logData);
	}
	
	private void prepareAndWriteConsoleRecord(String msgTypeStrg, Date logDate, String unitName,
			String triggerName, String msg){
		TableFormatterService tbform = new TableFormatterServiceImpl(133);
		tbform.addValue(0, msgTypeStrg);
		tbform.addValue(pos[0], FormatterService.getDate(logDate));
		tbform.addValue(pos[1], unitName);
		tbform.addValue(pos[2], triggerName);
		tbform.addValue(pos[3], msg);
		consoleRecord = tbform.getRow();
		writeLogMessageToConsole(consoleRecord);
		
	}
	
	public String getConsoleRecord(){
		return consoleRecord;
	}

	private void prepareAndWriteUserLogFileRecord(String msgTypeStrg, Date logDate, String unitName,
			String triggerName, String msg){
		char sep = ';';
		StringBuffer sb = new StringBuffer(msgTypeStrg);
		sb.append(sep);
		sb.append(FormatterService.getDateDDMMYYYY(logDate));
		sb.append(sep);
		sb.append(" " + FormatterService.getDateHHMMSSS(logDate));
		sb.append(sep);
		sb.append(unitName);
		sb.append(sep);
		sb.append(triggerName);
		sb.append(sep);
		sb.append(msg);
		writeMessageToUserLogFile(sb.toString());
	}
	
	private void prepareAndWriteTechnicalLogFileRecord(String msgTypeStrg, Date logDate, String unitName,
			String triggerName, String msg){
		char sep = ';';
		StringBuffer sb = new StringBuffer(msgTypeStrg);
		sb.append(sep);
		sb.append(FormatterService.getDateDDMMYYYY(logDate));
		sb.append(sep);
		sb.append(" " + FormatterService.getDateHHMMSSS(logDate));
		sb.append(sep);
		sb.append(unitName);
		sb.append(sep);
		sb.append(triggerName);
		sb.append(sep);
		sb.append(msg);
		
		technicalLogFilePW.println(sb.toString());
		technicalLogFilePW.flush();
	}


	private void prepareAndWriteTemperatureLogFileRecord(Date logDate, String logData){
		char sep = ';';
		StringBuffer sb = new StringBuffer();
		sb.append(FormatterService.getDateDDMMYYYY(logDate));
		sb.append(sep);
		sb.append(" " + FormatterService.getDateHHMMSSS(logDate));
		sb.append(" " + logData);
		
		temperatureLogFilePW.println(sb.toString());
		temperatureLogFilePW.flush();
	}


	private void prepareAndWriteHumidityLogFileRecord(Date logDate, String logData){
		char sep = ';';
		StringBuffer sb = new StringBuffer();
		sb.append(FormatterService.getDateDDMMYYYY(logDate));
		sb.append(sep);
		sb.append(" " + FormatterService.getDateHHMMSSS(logDate));
		sb.append(" " + logData);
		
		humidityLogFilePW.println(sb.toString());
		humidityLogFilePW.flush();
	}


	private void writeLogMessageToConsole(String msg){
		System.out.println(msg);
	}

	private LogMessage detectedMessage(int msgId) {
		return logMessageList.get(msgId);
	}

	private String createMsgType(short msgType) {
		switch (msgType) {
		case MSG_INFO:
			return "*   ";
		case MSG_WARN:
			return "**  ";
		case MSG_ERROR:
			return "*** ";
		case MSG_ALERT:
			return "****";
		default:
			return "?   ";
		}
	}

	public void createLogMessageList() {
		logMessageList.put(MSG_STARTED, new LogMessage("Alarmanlage gestartet",
				MSG_INFO));
		logMessageList.put(MSG_WATER, new LogMessage(
				"Wasser? Feuchtigkeit festgestellt!", MSG_ALERT));
		logMessageList.put(MSG_OPEN, new LogMessage(
				"Kontakt geöffnet! Einbruch?", MSG_ALERT));
		logMessageList.put(MSG_MOTION, new LogMessage(
				"Bewegung festgestellt! Einbruch?", MSG_ALERT));
		logMessageList.put(MSG_FIRE, new LogMessage(
				"Rauchentwicklung festgestellt! Feuer?", MSG_ALERT));
		logMessageList.put(MSG_RESET, new LogMessage("Reset durchgeführt.",
				MSG_INFO));
		logMessageList.put(MSG_ON, new LogMessage("EIN", MSG_INFO));
		logMessageList.put(MSG_OFF, new LogMessage("AUS", MSG_WARN));
		logMessageList.put(MSG_QUIET_ON,
				new LogMessage("Lautlos EIN", MSG_WARN));
		logMessageList.put(MSG_QUIET_OFF, new LogMessage("Lautlos AUS",
				MSG_INFO));
		logMessageList.put(MSG_UNIT_LOST, new LogMessage(
				"Meldet sich nicht mehr", MSG_WARN));
		logMessageList.put(MSG_UNIT_DISCONNECTED, new LogMessage(
				"Zwangsabgemeldet", MSG_ERROR));
		logMessageList.put(MSG_UNIT_RECONNECTED, new LogMessage(
				"Wiederanmeldung erfolgreich", MSG_INFO));
		logMessageList.put(MSG_UNIT_RECONNECT_FAILED, new LogMessage(
				"Wiederanmeldung fehlgeschlagen", MSG_WARN));
		logMessageList.put(MSG_RESTART, new LogMessage(
				"Restart angefordert", MSG_INFO));
		logMessageList.put(MSG_RESTARTED, new LogMessage(
				"Restart durchgeführt", MSG_INFO));
		logMessageList.put(MSG_REMOTE, new LogMessage(
				"Remote verbunden", MSG_INFO));
		logMessageList.put(MSG_EMAIL_REQUESTED, new LogMessage(
				"Email wird erstellt bei Alarm", MSG_INFO));
		logMessageList.put(MSG_EMAIL_NOT_REQUESTED, new LogMessage(
				"Keine Email bei Alarm (deaktiviert)", MSG_INFO));
		logMessageList.put(MSG_STOPPED, new LogMessage("Alarmanlage gestoppt",
				MSG_INFO));
		logMessageList.put(MSG_RESET_PRESSED, new LogMessage("Reset Taster länger als 2 Sekunden gedrückt!",
				MSG_ERROR));
	}
	
	private void writeMessageToUserLogFile(String msg){
//		System.out.println("writeLogMessageToCsvFile:: msg="+msg);
		userLogFilePW.println(msg);
		userLogFilePW.flush();
	}
}
