package de.kabuman.tinkerforge.alarm.config;

public class CfgRemoteSwitch {
	
	String host; 
	int port;
	String unitName;
	int mb;
	String mbUsedFor; 
	int rs;
	String rsUsedFor; 
	short repeat;
	long sleep;

	public CfgRemoteSwitch(
			String host, 
			int port, 
			String unitName,
			int mb, 
			String mbUsedFor,
			int rs, 
			String rsUsedFor,
			short repeat,
			long sleep){
		this.host = host;
		this.port = port;
		this.unitName = unitName;
		this.mb = mb;
		this.mbUsedFor = mbUsedFor;
		this.rs = rs;
		this.rsUsedFor = rsUsedFor;
		this.repeat = repeat;
		this.sleep = sleep;

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

	public String getMbUsedFor() {
		return mbUsedFor;
	}

	public int getRs() {
		return rs;
	}

	public String getRsUsedFor() {
		return rsUsedFor;
	}

	public short getRepeat() {
		return repeat;
	}

	public long getSleep() {
		return sleep;
	}
}
