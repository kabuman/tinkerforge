package de.kabuman.tinkerforge.alarm.config;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

public class CfgAlertUnitRemote extends CfgUnit{
	
	private int lcd;
	private String lcdUsedFor;
	
	/**
	 * @param host
	 * @param port
	 * @param unitName
	 * @param mb
	 * @param mbUsedFor
	 * @param io
	 * @param ioUsedFor
	 */
	public CfgAlertUnitRemote(
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName,
			int mb, 
			String mbUsedFor, 
			int io, 
			String ioUsedFor,
			int lcd,
			String lcdUsedFor){

		super(cfgRemoteSwitchData,host,port,unitName,mb,mbUsedFor,io,ioUsedFor);
		
		this.lcd = lcd;
		this.lcdUsedFor = lcdUsedFor;
	}

	public int getLcd() {
		return lcd;
	}

	public String getLcdUsedFor() {
		return lcdUsedFor;
	}
}
