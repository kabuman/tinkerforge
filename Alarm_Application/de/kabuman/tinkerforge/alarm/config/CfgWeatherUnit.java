package de.kabuman.tinkerforge.alarm.config;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

public class CfgWeatherUnit extends CfgUnit{


	public CfgWeatherUnit(
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName){
		super(cfgRemoteSwitchData,host,port,unitName,0,"mbUsedFor",0,"ioUsedFor");

	}

}
