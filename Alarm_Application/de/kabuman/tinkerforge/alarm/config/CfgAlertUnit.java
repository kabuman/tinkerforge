package de.kabuman.tinkerforge.alarm.config;

public class CfgAlertUnit extends CfgUnit{
	
	public CfgAlertUnit(
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName,
			int mb, 
			String mbUsedFor, 
			int io, 
			String ioUsedFor){
		super(cfgRemoteSwitchData,host,port,unitName,mb,mbUsedFor,io,ioUsedFor);
	}
}
