package de.kabuman.tinkerforge.alarm.config;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

public class CfgAlertUnit extends CfgUnit{

	private int lcd;
	private String lcdUsedFor;
	private Integer tp;
	private Integer hm;
	private Integer al;
	private Short   alThreshold;


	public CfgAlertUnit(
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName,
			int mb, 
			String mbUsedFor, 
			int io, 
			String ioUsedFor,
			int lcd,
			String lcdUsedFor,
			Integer tp,
			Integer hm,
			Integer al,
			Short alThreshold){
		super(cfgRemoteSwitchData,host,port,unitName,mb,mbUsedFor,io,ioUsedFor);
		this.lcd = lcd;
		this.lcdUsedFor = lcdUsedFor;
		this.tp = tp;
		this.hm = hm;
		this.al = al;
		this.alThreshold = alThreshold;

	}

	public int getLcd() {
		return lcd;
	}

	public String getLcdUsedFor() {
		return lcdUsedFor;
	}

	public Integer getTp() {
		return tp;
	}

	public Integer getHm() {
		return hm;
	}

	public Integer getAl() {
		return al;
	}

	public Short getAlThreshold() {
		return alThreshold;
	}
}
