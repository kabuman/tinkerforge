package de.kabuman.tinkerforge.services.config;

import de.kabuman.common.services.ConfigFileReader;

public class CreateStdCfgFromInputFile {
	static private final int RECTYPE_EMAIL = 5;
	static private final int RECTYPE_REMOTESWITCH = 7;
	
	static private final int REMOTE_SWITCH_DATA_SWITCH_TYPE = 1;
	static private final int REMOTE_SWITCH_DATA_SYSTEM_CODE = 2;
	static private final int REMOTE_SWITCH_DATA_DEVICE_CODE = 3;



	// cfgEmail: email configuration
	static private int emailPos = 0;
	static private final int EMAIL_REQUESTED = ++emailPos;
	static private final int EMAIL_HOST = ++emailPos;
	static private final int EMAIL_PORT = ++emailPos;
	static private final int USER = ++emailPos;
	static private final int PASSWORD = ++emailPos;
	static private final int SEND_FROM = ++emailPos;
	static private final int SEND_TO = ++emailPos;

	// cfgRemoteSwitch: sequence of comma separated vars
	static private int rsPos = 0;
	static private final int RS_NAME = ++rsPos;
	static private final int RS_HOST = ++rsPos;
	static private final int RS_PORT = ++rsPos;
	static private final int RS_MB_USEDFOR = ++rsPos;
	static private final int RS_MB = ++rsPos;
	static private final int RS_RS_USEDFOR = ++rsPos;
	static private final int RS_RS = ++rsPos;
	static private final int RS_REPEAT = ++rsPos;
	static private final int RS_SLEEP = ++rsPos;
	

	// Configuration File Content as objects
	private CfgEmail cfgEmail = null;
	private CfgRemoteSwitch cfgRemoteSwitch = null;


	
	// Constructor Parameter
	String configPathFileName;
	
	
	/**
	 * Read Parameter and create config objects
	 */
	public void createStdCfg(ConfigFileReader cfgFile){

			switch (cfgFile.getRecordType()) {
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

	public CfgRemoteSwitchData createCfgRemoteSwitchData(ConfigFileReader cfgFile){
		return new CfgRemoteSwitchData(
				cfgFile.getShort(REMOTE_SWITCH_DATA_SWITCH_TYPE),
				cfgFile.getLong(REMOTE_SWITCH_DATA_SYSTEM_CODE),
				cfgFile.getShort(REMOTE_SWITCH_DATA_DEVICE_CODE));
	}
	

	
	/**
	 * Returns the cfg record for email
	 * 
	 * @return cfg record for email
	 */
	public CfgEmail getCfgEmail() {
		return cfgEmail;
	}



	public CfgRemoteSwitch getCfgRemoteSwitch() {
		return cfgRemoteSwitch;
	}


}
