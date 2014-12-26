package de.kabuman.tinkerforge.alarm.config;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.common.services.ConfigFileReader;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;
import de.kabuman.tinkerforge.services.config.CreateStdCfgFromInputFile;

/**
 * Creates the Cfg Objects from text input file
 *
 */
public class CreateCfgFromInputFile extends CreateStdCfgFromInputFile{
	// Record Types
	static private final int RECTYPE_ALERTSIGNAL = 1;
	static private final int RECTYPE_ALERTUNITREMOTE = 2;
	static private final int REC_TYPE_ALERTUNIT = 3;
	static private final int RECTYPE_PROTECTUNIT = 4;
	static private final int RECTYPE_WEATHERUNIT = 8;

	// cfgUnit: sequence/index (1,..,7)  of comma separated vars for the common part
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
	
	// cfgAlertUnit: 
//	static private final int TP_USEDFOR = 13;		// Name Temperatur-Sensor (not used)
	static private final int ALERT_TP = 14;				// Temperatur-Sensor
//	static private final int HM_USEDFOR = 15;		// Name Rel.Luftfeuchtigkeits-Sensor (not used)
	static private final int ALERT_HM = 16;				// Rel.Luftfeuchtigkeits-Sensor
//	static private final int AL_USEDFOR = 17;		// Name Ambilight Senso (not used)
	static private final int ALERT_AL = 18;				// Ambilight
	static private final int ALERT_ALTHRESHOLD = 19;	// Ambilight

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
//	static private final int VC_USEDFOR = 23;		// Name Rauchmelder-Sensor [Voltage/Current] (not used)
	static private final int VC = 24;				// Voltage/Current
	static private final int VC_VOLTAGE_THRESHOLD = 25; // Voltage Threshold Rauchmelder
	static private final int VC_CURRENT_THRESHOLD_OK = 26; // Current Threshold Rauchmelder
	static private final int VC_CURRENT_THRESHOLD_ALERT = 27; // Current Threshold Rauchmelder

	// cfgAlertSignal:  sequence of comma separated vars
	static private final int QUIET = 5;
	static private final int ALERT_DURATION_INTRUSION = 7;
	static private final int ALERT_DURATION_WATER = 9;
	static private final int ALERT_DURATION_FIRE = 11;
	static private final int ALIVE_SEQUENCE = 13;

	
	// Configuration File Content as objects
	private CfgAlertSignal cfgAlertSignal;
	private CfgAlertUnitRemote cfgAlertUnitRemote;
	private List<CfgAlertUnit> cfgAlertUnitList = new ArrayList<CfgAlertUnit>();
	private List<CfgProtectUnit> cfgProtectUnitList = new ArrayList<CfgProtectUnit>();
	private List<CfgWeatherUnit> cfgWeatherUnitList = new ArrayList<CfgWeatherUnit>();
	private List<CfgRemoteSwitchData> cfgRemoteSwitchDataList = new ArrayList<CfgRemoteSwitchData>();

	
	
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
//				3,AU Mobil,192.168.0.43,4223,Master,29,Summer,46,LCD68
				cfgAlertUnitList.add(
						new CfgAlertUnit(
								createCfgRemoteSwitchData(cfgFile),
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME),
								cfgFile.getInt(MB),
								cfgFile.getString(MB_USEDFOR),
								cfgFile.getInt(IO),
								cfgFile.getString(IO_USEDFOR),
								cfgFile.getInt(LCD),
								cfgFile.getString(LCD_USEDFOR),
								cfgFile.getInt(ALERT_TP),
								cfgFile.getInt(ALERT_HM),
								cfgFile.getInt(ALERT_AL),
								cfgFile.getShort(ALERT_ALTHRESHOLD))
						);
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
				break;
			case RECTYPE_PROTECTUNIT:
//				  1        2         3    4      5  6             7  8               9 10  11          12 13         14 15                   16 17                 18
//				4,PU Mobil,localhost,4223,Master,30,Kontaktsensor,51,Bewegungssensor,0,400,Wassermelder,0,Temperatur,53,Rel.Luftfeuchtigkeit,56,IR Bewegungsmelder,0,Rauchmelder,1,14,30,300
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
								cfgFile.getInteger(MD),
								cfgFile.getInteger(VC),
								cfgFile.getShort(VC_VOLTAGE_THRESHOLD),
								cfgFile.getShort(VC_CURRENT_THRESHOLD_OK),
								cfgFile.getShort(VC_CURRENT_THRESHOLD_ALERT)
								)
						
						);
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
				break;

			case RECTYPE_WEATHERUNIT:
//				  1        2         3    4      5  6             7  8               9 10  11          12 13         14 15                   16
//				4,WU Outside,localhost,4223
				cfgWeatherUnitList.add(
						new CfgWeatherUnit(
								createCfgRemoteSwitchData(cfgFile),
								cfgFile.getString(HOST),
								cfgFile.getInt(PORT),
								cfgFile.getString(UNITNAME)
								)
						
						);
				cfgRemoteSwitchDataList.add(createCfgRemoteSwitchData(cfgFile));
				break;

			default:
				createStdCfg(cfgFile);
				break;
			}
		}
	}

	public List<CfgRemoteSwitchData> getCfgRemoteSwitchDataList() {
		return cfgRemoteSwitchDataList;
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


	public CfgAlertSignal getCfgAlertSignal() {
		return cfgAlertSignal;
	}

	public void setCfgAlertSignal(CfgAlertSignal cfgAlertSignal) {
		this.cfgAlertSignal = cfgAlertSignal;
	}

	public List<CfgWeatherUnit> getCfgWeatherUnitList() {
		return cfgWeatherUnitList;
	}


}
