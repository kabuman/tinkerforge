package de.kabuman.tinkerforge.aquarium.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.kabuman.common.services.ConfigFileReader;
import de.kabuman.common.services.DateTimeService;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;
import de.kabuman.tinkerforge.services.config.CreateStdCfgFromInputFile;

public class CreateCfgFromInputFile extends CreateStdCfgFromInputFile{
	// Record Types
	static private final int RECTYPE_UNIT = 1;
	static private final int RECTYPE_SERVO = 2;
	static private final int RECTYPE_POSITION = 3;
	static private final int RECTYPE_TIMER = 4;
	static private final int RECTYPE_TIMER41 = 41;
	static private final int RECTYPE_TIMER42 = 42;
	static private final int RECTYPE_TIMER43 = 43;

	// cfgUnit: Connection, MasterBrick, ServoBrick
	static private int unitPos = 3;
	static private final int UNIT_NAME = ++unitPos;
	static private final int UNIT_HOST = ++unitPos;
	static private final int UNIT_PORT = ++unitPos;
	static private final int UNIT_MB_NAME = ++unitPos;
	static private final int UNIT_MB = ++unitPos;
	static private final int UNIT_SV_NAME = ++unitPos;
	static private final int UNIT_SV = ++unitPos;
	static private final int UNIT_ALIVE_SEQUENCE_NAME = ++unitPos;
	static private final int UNIT_ALIVE_SEQUENCE = ++unitPos;
			
	// cfgTimer: 3 Timer per Servo
	static private int timerPos = 1;
	static private final int TIMER_SERVOID = ++timerPos;
	static private final int TIMER_TIMER1 = ++timerPos;
	static private final int TIMER_TIMER2 = ++timerPos;
	static private final int TIMER_TIMER3 = ++timerPos;

	// cfgTimer: 3 Timer per Servo
	// 41,ServoID=,0,11:35:00
	static private int timer41Pos = 1;
	static private final int TIMER41_SERVOID = ++timer41Pos;
	static private final int TIMER41_TIME = ++timer41Pos;

	// cfgTimer: 3 Timer per Servo
	// 42,ServoID=,0,10.06.2014 11:37:01
	static private int timer42Pos = 1;
	static private final int TIMER42_SERVOID = ++timer42Pos;
	static private final int TIMER42_TIMER = ++timer42Pos;

	// cfgTimer: 3 Timer per Servo
	// 43,ServoID=,0
	static private int timer43Pos = 1;
	static private final int TIMER43_SERVOID = ++timer43Pos;

	// cfgPosition: 3 Positions per Servo
	static private int posPos = 1;
	static private final int POSITION_SERVOID = ++posPos;
	static private final int POSITION_POS1 = ++posPos;
	static private final int POSITION_POS2 = ++posPos;
	static private final int POSITION_POS3 = ++posPos;
	static private final int POSITION_POS4 = ++posPos;

	// cfgServo: Configuration of one Servo
	static private int servoPos = 1;
	static private final int SERVO_SERVOID = ++servoPos;
	static private final int SERVO_NAME = ++servoPos;
	static private final int SERVO_VELOCITY_NAME = ++servoPos;
	static private final int SERVO_VELOCITY = ++servoPos;
	static private final int SERVO_ACCELERATION_NAME = ++servoPos;
	static private final int SERVO_ACCELERATION = ++servoPos;
	static private final int SERVO_DEGREE_MIN_NAME = ++servoPos;
	static private final int SERVO_DEGREE_MIN = ++servoPos;
	static private final int SERVO_DEGREE_MAX_NAME = ++servoPos;
	static private final int SERVO_DEGREE_MAX = ++servoPos;
	static private final int SERVO_PERIOD_NAME = ++servoPos;
	static private final int SERVO_PERIOD = ++servoPos;

	// Configuration File Content as objects
	private CfgUnit cfgUnit = null;
	private CfgTimer cfgTimer = null;
	private List<CfgTimer> cfgTimerList = new ArrayList<CfgTimer>();
	private CfgPosition cfgPosition = null;
	private List<CfgPosition> cfgPositionList = new ArrayList<CfgPosition>();
	private CfgServo cfgServo = null;
	private List<CfgServo> cfgServoList = new ArrayList<CfgServo>();
	
	private CfgRemoteSwitchData cfgRemoteSwitchData = null;

	
	// Constructor Parameter
	String configPathFileName;
	
	
	/**
	 * Constructor  <br>
	 * Reads configuration parameter from the given config file  <br>
	 * and creates the required config objects
	 * 
	 * @param configPathFileName - path and file name
	 */
	public CreateCfgFromInputFile(String configPathFileName) {
		super();
		this.configPathFileName = configPathFileName;
		readParameter();
	}
	
	
	/**
	 * Read Parameter and create config objects
	 */
	private void readParameter(){
		ConfigFileReader cfgFile = new ConfigFileReader();
		cfgFile.openFile(configPathFileName,",");

		while (cfgFile.readLine() != null) {

			switch (cfgFile.getRecordType()) {
			case RECTYPE_UNIT:
				cfgUnit = new CfgUnit(
						createCfgRemoteSwitchData(cfgFile)
						, cfgFile.getString(UNIT_HOST)
						, cfgFile.getInt(UNIT_PORT)
						, cfgFile.getString(UNIT_NAME)
						, cfgFile.getInt(UNIT_MB)
						, cfgFile.getInt(UNIT_SV)
						, cfgFile.getInt(UNIT_ALIVE_SEQUENCE));
				cfgRemoteSwitchData = createCfgRemoteSwitchData(cfgFile);
				break;

			case RECTYPE_TIMER:
				cfgTimer = new CfgTimer(
						cfgFile.getShort(TIMER_SERVOID),
						cfgFile.getDateTime(TIMER_TIMER1),
						cfgFile.getDateTime(TIMER_TIMER2),
						cfgFile.getDateTime(TIMER_TIMER3));
				cfgTimerList.add(cfgTimer);
				break;

			case RECTYPE_TIMER41:
				cfgTimer = createTimer41(
						cfgFile.getShort(TIMER41_SERVOID),
						cfgFile.getTime(TIMER41_TIME));
				
				cfgTimerList.add(cfgTimer);
				break;

			case RECTYPE_TIMER42:
				cfgTimer = createTimer42(
						cfgFile.getShort(TIMER42_SERVOID),
						cfgFile.getDateTime(TIMER42_TIMER));
				
				cfgTimerList.add(cfgTimer);
				break;

			case RECTYPE_TIMER43:
				cfgTimer = createTimer43(
						cfgFile.getShort(TIMER43_SERVOID));
				
				cfgTimerList.add(cfgTimer);
				break;

			case RECTYPE_POSITION:
				cfgPosition = new CfgPosition(
						cfgFile.getShort(POSITION_SERVOID),
						cfgFile.getShort(POSITION_POS1),
						cfgFile.getShort(POSITION_POS2),
						cfgFile.getShort(POSITION_POS3),
						cfgFile.getShort(POSITION_POS4));
				cfgPositionList.add(cfgPosition);
				break;

			case RECTYPE_SERVO:
				cfgServo = new CfgServo(
						cfgFile.getString(SERVO_NAME)
						, cfgFile.getShort(SERVO_SERVOID)
						, cfgFile.getInt(SERVO_VELOCITY)
						, cfgFile.getInt(SERVO_ACCELERATION)
						, cfgFile.getShort(SERVO_DEGREE_MIN)
						, cfgFile.getShort(SERVO_DEGREE_MAX)
						, cfgFile.getInt(SERVO_PERIOD));
				cfgServoList.add(cfgServo);
				break;

			default:
				// Email, Remote Switch Cfg Records
				createStdCfg(cfgFile);
				break;
			}
		}
	}


	private CfgTimer createTimer41(Short servoId, Date time) {
		Date nextDay = DateTimeService.add(new Date(),DateTimeService.DAYS,1);
		Date timer1 = DateTimeService.replaceTime(nextDay, time);
		
		// create Timer with one Date only
		CfgTimer cfgTimer = new CfgTimer(servoId, timer1);
		
		return replicateTimer(cfgTimer,DateTimeService.DAYS,1);
	}

	private CfgTimer createTimer42(Short servoId, Date timer) {
		// create Timer with one Date only
		CfgTimer cfgTimer = new CfgTimer(servoId, timer);
		
		return replicateTimer(cfgTimer,DateTimeService.DAYS,1);
	}

	private CfgTimer createTimer43(Short servoId) {
		// create Timer with one Date only
		CfgTimer cfgTimer = new CfgTimer(servoId, DateTimeService.add(new Date(),DateTimeService.SECONDS,10));
		
		return replicateTimer(cfgTimer,DateTimeService.SECONDS,5);
	}

	private CfgTimer replicateTimer(CfgTimer cfgTimer, long dim, int number){
		// replicate this one Date to another 2 Dates by adding one day each
		for (int i = 1; i < 3; i++) {
			Date dayBefore = cfgTimer.getTimerList().get(i-1);
			Date thisDay = DateTimeService.add(dayBefore,dim,number); 
			cfgTimer.getTimerList().add(i, thisDay);
		}
		return cfgTimer;
	}
	
	/**
	 * Returns the position for which the given servo is to set
	 * 
	 * @param servoId - the given servo id (0-6)
	 * @param timerId - the timerId (0-2)
	 * @return Short - position to set the servo 
	 */
	public Short findCfgPosition(short servoId, int timerId){
		for (CfgPosition cfgPosition : cfgPositionList) {
			if (cfgPosition.getServoId() == servoId){
				if (cfgPosition.getPositionList().get(timerId) != null){
					return cfgPosition.getPositionList().get(timerId);
				} else {
					break;
				}
			}
		}
		throw new IllegalArgumentException("No timer entry found. servoId="+servoId+" timerId="+timerId);
	}
	
	/**
	 * Returns the cfg record for the servo identified by the servoId (0-7)
	 * 
	 * @param servoId - servo id (0-7)
	 * @return cfg record - to configure the servo
	 */
	public CfgServo findCfgServo(short servoId){
		for (CfgServo cfgServo : cfgServoList) {
			if (cfgServo.getServoId() == servoId){
				return cfgServo;
			}
		}
		throw new IllegalArgumentException("No servo entry found. servoId="+servoId);
	}
	
	

	/**
	 * Returns the timer list (contains one or more cfg timer records
	 * 
	 * @return List of timer records
	 */
	public List<CfgTimer> getCfgTimerList() {
		if (cfgTimerList.size()<1){
			throw new IllegalArgumentException("No timer entries found");
		}
		return cfgTimerList;
	}

	/**
	 * Returns the servo list (contains one or more cfg servo records 0-7 max)
	 * 
	 * @return List of timer records
	 */
	public List<CfgServo> getCfgServoList() {
		if (cfgServoList.size()<1){
			throw new IllegalArgumentException("No servo entries found");
		}
		return cfgServoList;
	}

	/**
	 * Returns the cfg unit record (master brick, servo brick, connection configuration)
	 * 
	 * @return cfg unit record
	 */
	public CfgUnit getCfgUnit() {
		if (cfgUnit == null){
			throw new IllegalArgumentException("No Unit configuration found");
		}
		return cfgUnit;
	}


	public CfgRemoteSwitchData getCfgRemoteSwitchData() {
		return cfgRemoteSwitchData;
	}


	public List<CfgPosition> getCfgPositionList() {
		return cfgPositionList;
	}

}
