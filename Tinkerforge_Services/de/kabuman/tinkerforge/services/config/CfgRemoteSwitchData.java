package de.kabuman.tinkerforge.services.config;

public class CfgRemoteSwitchData {
	short switchType;
	long systemCode;  
	short deviceCode;

	public CfgRemoteSwitchData(
			short switchType,
			long systemCode, 
			short deviceCode){
		
		this.switchType = switchType;
		this.systemCode = systemCode;
		this.deviceCode = deviceCode;
	}

	public long getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(long systemCode) {
		this.systemCode = systemCode;
	}

	public short getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(short deviceCode) {
		this.deviceCode = deviceCode;
	}

	public short getSwitchType() {
		return switchType;
	}

	public void setSwitchType(short switchType) {
		this.switchType = switchType;
	}

}
