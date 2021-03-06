package de.kabuman.tinkerforge.alarm.config;

public class CfgUnit {
	
	String host; 
	int port;
	String unitName;
	int mb;
	String mbUsedFor; 
	int io;
	String ioUsedFor; 

	public CfgUnit(
			String host, 
			int port, 
			String unitName,
			int mb, 
			String mbUsedFor,
			int io, 
			String ioUsedFor){
		this.host = host;
		this.port = port;
		this.unitName = unitName;
		this.mb = mb;
		this.mbUsedFor = mbUsedFor;
		this.io = io;
		this.ioUsedFor = ioUsedFor;

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

	public int getIo() {
		return io;
	}

	public String getIoUsedFor() {
		return ioUsedFor;
	}
}
