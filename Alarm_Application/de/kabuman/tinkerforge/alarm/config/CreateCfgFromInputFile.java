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
	static private final int RECTYPE_REMOTESWITCH = 7;

	// cfgUnit: sequence/index (1,..,7)  of comma separated vars for the common part
	static private final int REMOTE_SWITCH_DATA_SWITCH_TYPE = 1;
	static private final int REMOTE_SWITCH_DATA_SYSTEM_CODE = 2;
	static private final int REMOTE_SWITCH_DATA_DEVICE_CODE = 3;
	static private final int UNITNAME = 4;
	static private final int HOST = 5;
	static private final int PORT = 6;
	static private final int MB_USEDFOR = 7;
	static private final int MB = 8;
	static private final int IO_USEDFOR = 9;
	static private final int IO = 10;

	// cfgAlertUnitRemote:  sequence of comma separated vars
	static private final int LCD_USEDFOR = 11;
	static private final int LCD = 12;

	// cfgProtectUnit:  sequence of comma separated vars
//	static private final int IR_USEDFOR =11;		// not used
	static private final int IR = 12;				// Bewegungsmelder
	static private final int DISTANCE = 13;			// Entfernung für Bewegungsmelder
//	static private final int AI_USEDFOR = 14;		// Name Wasser-Sensr (not used)
	static private final int AI = 15;				// Wasser-Sensor
	private static final int AI_VOLTAGE_THRESHOLD = 16; // Volt Threshold für Wasser-Sensor
//	static private final int TP_USEDFOR = 17;		// Name Temperatur-Sensor (not used)
	static private final int TP = 18;				// Temperatur-Sensor
//	static private final int HM_USEDFOR = 19;		// Name Rel.Luftfeuchtigkeits-Sensor (not used)
	static private final int HM = 20;				// Rel.Luftfeuchtigkeits-Sensor
//	static private final int MD_USEDFOR = 21;		// Name IR Bewegungsmelder (not used)
	static private final int MD = 22;				// IR Bewegungsmelder

	// cfgEmail: sequence of comma separated vars
	static private final int EMAIL_REQUESTED = 1;
	static private final int EMAIL_HOST = 2;
	static private final int EMAIL_PORT = 3;
	static private final int USER = 4;
	static private final int PASSWORD = 5;
	static private final int SEND_FROM = 6;
	static private final int SEND_TO = 7;

	// cfgAlertSignal:  sequence of comma separated vars
	static private final int QUIET = 5;
	static private final int ALERT_DURATION_INTRUSION = 7;
	static private final int ALERT_DURATION_WATER = 9;
	static private final int ALERT_DURATION_FIRE = 11;
	static private final int ALIVE_SEQUENCE = 13;

	// cfgRemoteSwitch: sequence of comma separated vars
	static private final int RS_NAME = 1;
	static private final int RS_HOST = 2;
	static private final int RS_PORT = 3;
	static private final int RS_MB_USEDFOR = 4;
	static private final int RS_MB = 5;
	static private final int RS_RS_USEDFOR = 6;
	static private final int RS_RS = 7;
	static private final int RS_REPEAT = 8;
	static private final int RS_SLEEP = 9;
	
	
	// Configuration File Content as objects
	private CfgAlertSignal cfgAlertSignal;
	private CfgRemoteSwitch cfgRemoteSwitch;
	private CfgAlertUnitRemote cfgAlertUnitRemote;
	private List<CfgAlertUnit> cfgAlertUnitList = new ArrayList<CfgAlertUnit>();
	private List<CfgProtectUnit> cfgProtectUnitList = new ArrayList<CfgProtectUnit>();
	private List<CfgRemoteSwitchData> cfgRemoteSwitchDataList = new ArrayList<CfgRemoteSwitchData>();
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
						createCfgRemoteSwitchData(cfgFile),
						cfgFile.getBoolean(QUIET),
						cfgFile.getInt(ALERT_DURATION_INTRUSION),
						cfgFile.getInt(ALERT_DURATION_WATER),
						cfgFile.getInt(ALERT_DURATION_FIRE),
						cfgFile.getInt(ALIVE_SEQUENCE));
				
				break;
			case RECTYPE_ALERTUNITREMOTE:
//				2,AU Remote,192.168.0.32,4223,Master,23,Summer,21,LCD,19
				cfgAlertUnitRemote = new CfgAlertUnitRemote(
						createCfgRemoteSwitchData(cfgFile),
						cfgFile.getString(HOST),
						cfgFile.getInt(PORT),
						cfgFile.getString(UNITNAME),
						cfgFile.getInt(MB),
						cfgFile.getString(MB_USEDFOR),
						cfgFile.getInt(IO),
						cfgFile.getString(IO_USEDFOR),
						cfgFile.getInt(LCD),
						cfgFile.getString(LCD_USEDFOR));
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
				break;
			case REC_TYPE_ALERTUNIT:
//				3,AU Mobil,192.168.0.43,4223,Master,29,Summer,46
				cfgAlertUnitList.add(
						new CfgAlertUnit(
								createCfgRemoteSwitchData(cfgFile),
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME),
								cfgFile.getInt(MB),
								cfgFile.getString(MB_USEDFOR),
								cfgFile.getInt(IO),
								cfgFile.getString(IO_USEDFOR))
						);
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
				break;
			case RECTYPE_PROTECTUNIT:
//				  1        2         3    4      5  6             7  8               9 10  11          12 13         14 15                   16
//				4,PU Mobil,localhost,4223,Master,30,Kontaktsensor,51,Bewegungssensor,0,400,Wassermelder,0,Temperatur,53,Rel.Luftfeuchtigkeit,56
				cfgProtectUnitList.add(
						new CfgProtectUnit(
								createCfgRemoteSwitchData(cfgFile),
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME),
								cfgFile.getInt(MB),
								cfgFile.getString(MB_USEDFOR),
								cfgFile.getInt(IO),
								cfgFile.getString(IO_USEDFOR),
								cfgFile.getInteger(IR),
								cfgFile.getShort(DISTANCE),
								cfgFile.getInteger(AI),
								cfgFile.getShort(AI_VOLTAGE_THRESHOLD),
								cfgFile.getInteger(TP),
								cfgFile.getInteger(HM),
								cfgFile.getInteger(MD)
								)
						
						);
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
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

			case RECTYPE_REMOTESWITCH:
//				  1             2         3    4      5  6             7  8  9
//				7,Remote Switch,localhost,4223,Master,60,Remote Switch,63,50,2000
				cfgRemoteSwitch = 
						new CfgRemoteSwitch(
								cfgFile.getString(RS_HOST),
								cfgFile.getInt(RS_PORT),
								cfgFile.getString(RS_NAME),
								cfgFile.getInt(RS_MB),
								cfgFile.getString(RS_MB_USEDFOR),
								cfgFile.getInt(RS_RS),
								cfgFile.getString(RS_RS_USEDFOR),
								cfgFile.getShort(RS_REPEAT),
								cfgFile.getLong(RS_SLEEP));
				break;
			default:
				break;
			}
		}
	}


	private CfgRemoteSwitchData createCfgRemoteSwitchData(ConfigFileReader cfgFile){
		return new CfgRemoteSwitchData(
				cfgFile.getShort(REMOTE_SWITCH_DATA_SWITCH_TYPE),
				cfgFile.getLong(REMOTE_SWITCH_DATA_SYSTEM_CODE),
				cfgFile.getShort(REMOTE_SWITCH_DATA_DEVICE_CODE));
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

	public CfgRemoteSwitch getCfgRemoteSwitch() {
		return cfgRemoteSwitch;
	}

	public List<CfgRemoteSwitchData> getCfgRemoteSwitchDataList() {
		return cfgRemoteSwitchDataList;
	}

}
