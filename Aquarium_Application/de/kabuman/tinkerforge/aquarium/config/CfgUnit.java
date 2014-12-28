package de.kabuman.tinkerforge.aquarium.config;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

public class CfgUnit {

	CfgRemoteSwitchData cfgRemoteSwitchData;
	String host; 
	int port;
	String unitName;
	int mb;
	int sv;
	int aliveSequence;

	public CfgUnit(
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName,
			int mb, 
			int sv,
			int aliveSequence){
		
		this.cfgRemoteSwitchData = cfgRemoteSwitchData;
		this.host = host;
		this.port = port;
		this.unitName = unitName;
		this.mb = mb;
		this.sv = sv;
		this.aliveSequence = aliveSequence;

	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUnitName() {
		return unitName;
	}

	public int getMb() {
		return mb;
	}

	public int getSv() {
		return sv;
	}

	public int getAliveSequence() {
		return aliveSequence;
	}

	public void setAliveSequence(int aliveSequence) {
		this.aliveSequence = aliveSequence;
	}

	public CfgRemoteSwitchData getCfgRemoteSwitchData() {
		return cfgRemoteSwitchData;
	}

	public void setCfgRemoteSwitchData(CfgRemoteSwitchData cfgRemoteSwitchData) {
		this.cfgRemoteSwitchData = cfgRemoteSwitchData;
	}
}
