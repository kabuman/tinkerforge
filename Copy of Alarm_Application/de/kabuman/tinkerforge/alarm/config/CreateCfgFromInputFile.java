package de.kabuman.tinkerforge.alarm.config;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.common.services.ConfigFileReader;

public class CreateCfgFromInputFile {
	// Record Types
	static private final int RECTYPE_ALERTSIGNAL = 1;
	static private final int RECTYPE_ALERTUNITREMOTE = 2;
	static private final int REC_TYPE_ALERTUNIT = 3;
	static private final int RECTYPE_PROTECTUNIT = 4;
	static private final int RECTYPE_EMAIL = 5;

	// cfgUnit
	static private final int UNITNAME = 1;
	static private final int HOST = 2;
	static private final int PORT = 3;
	static private final int MB_USEDFOR = 4;
	static private final int MB = 5;
	static private final int IO_USEDFOR = 6;
	static private final int IO = 7;

	// cfgAlertUnitRemote
	static private final int LCD_USEDFOR = 8;
	static private final int LCD = 9;

	// cfgProtectUnit
//	static private final int IR_USEDFOR =8;		// not used
	static private final int IR = 9;
	static private final int DISTANCE = 10;
//	static private final int AI_USEDFOR = 11;	// not used
	static private final int AI = 12;
//	static private final int LOCAL_BEEPER_DURATION = 13;		// not used

	// cfgEmail
	static private final int EMAIL_REQUESTED = 1;
	static private final int EMAIL_HOST = 2;
	static private final int EMAIL_PORT = 3;
	static private final int USER = 4;
	static private final int PASSWORD = 5;
	static private final int SEND_FROM = 6;
	static private final int SEND_TO = 7;

	// cfgAlertSignal
	static private final int QUIET = 1;
	static private final int ALERT_DURATION_INTRUSION = 3;
	static private final int ALERT_DURATION_WATER = 5;
	static private final int ALERT_DURATION_FIRE = 7;
	static private final int ALIVE_SEQUENCE = 9;

	
	
	// Configuration File Content as objects
	private CfgAlertSignal cfgAlertSignal;
	private CfgAlertUnitRemote cfgAlertUnitRemote;
	private List<CfgAlertUnit> cfgAlertUnitList = new ArrayList<CfgAlertUnit>();
	private List<CfgProtectUnit> cfgProtectUnitList = new ArrayList<CfgProtectUnit>();
	private CfgEmail cfgEmail;
	
	
	
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
			case RECTYPE_ALERTSIGNAL:
// 				1,0,Einbruch,30000,Wasser,15000,Feuer,30000,Alive,5000
				cfgAlertSignal = new CfgAlertSignal(
						cfgFile.getBoolean(QUIET),
						cfgFile.getInt(ALERT_DURATION_INTRUSION),
						cfgFile.getInt(ALERT_DURATION_WATER),
						cfgFile.getInt(ALERT_DURATION_FIRE),
						cfgFile.getInt(ALIVE_SEQUENCE));
				
				break;
			case RECTYPE_ALERTUNITREMOTE:
//				2,AU Remote,192.168.0.32,4223,Master,23,Summer,21,LCD,19
				cfgAlertUnitRemote = new CfgAlertUnitRemote(
						cfgFile.getString(HOST),
						cfgFile.getInt(PORT),
						cfgFile.getString(UNITNAME),
						cfgFile.getInt(MB),
						cfgFile.getString(MB_USEDFOR),
						cfgFile.getInt(IO),
						cfgFile.getString(IO_USEDFOR),
						cfgFile.getInt(LCD),
						cfgFile.getString(LCD_USEDFOR));
				break;
			case REC_TYPE_ALERTUNIT:
//				3,AU Mobil,192.168.0.43,4223,Master,29,Summer,46
			cfgAlertUnitList.add(
						new CfgAlertUnit(
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME),
								cfgFile.getInt(MB),
								cfgFile.getString(MB_USEDFOR),
								cfgFile.getInt(IO),
								cfgFile.getString(IO_USEDFOR))
						);
				break;
			case RECTYPE_PROTECTUNIT:
//				4,PU Mobil,192.168.0.46,4223,Master,30,Kontaktsensor,51,Bewegungssensor,0,40,Wassermelder,36
				cfgProtectUnitList.add(
						new CfgProtectUnit(
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME),
								cfgFile.getInt(MB),
								cfgFile.getString(MB_USEDFOR),
								cfgFile.getInt(IO),
								cfgFile.getString(IO_USEDFOR),
								cfgFile.getInteger(IR),
								cfgFile.getShort(DISTANCE),
								cfgFile.getInteger(AI))
						);
				break;
			case RECTYPE_EMAIL:
//				5,0,smtp.1und1.de,25,kabumobil@online.de,KVrF92V8RBiGXs0f4ghfz2be2,kabuman@online.de,kabumobil@online.de,kabuman@online.de,karsten.buchmann@online.de
				String[] sendTo = new String[cfgFile.getVars().length - SEND_TO];
				int j = 0;
				for (int i = SEND_TO; i < cfgFile.getVars().length; i++) {
					sendTo[j++] = cfgFile.getString(i);
				}
				cfgEmail = new CfgEmail(
						cfgFile.getBoolean(EMAIL_REQUESTED),
						cfgFile.getString(EMAIL_HOST),
						cfgFile.getInt(EMAIL_PORT),
						cfgFile.getString(USER),
						cfgFile.getString(PASSWORD),
						cfgFile.getString(SEND_FROM),
						sendTo);
				break;

			default:
				break;
			}
		}
	}


	public CfgAlertUnitRemote getCfgAlertUnitRemote() {
		return cfgAlertUnitRemote;
	}


	public List<CfgAlertUnit> getCfgAlertUnitList() {
		return cfgAlertUnitList;
	}


	public List<CfgProtectUnit> getCfgProtectUnitList() {
		return cfgProtectUnitList;
	}


	public CfgEmail getCfgEmail() {
		return cfgEmail;
	}

	public CfgAlertSignal getCfgAlertSignal() {
		return cfgAlertSignal;
	}

	public void setCfgAlertSignal(CfgAlertSignal cfgAlertSignal) {
		this.cfgAlertSignal = cfgAlertSignal;
	}

}
