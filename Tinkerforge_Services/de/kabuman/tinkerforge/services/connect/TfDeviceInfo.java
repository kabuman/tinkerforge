package de.kabuman.tinkerforge.services.connect;
import java.util.Date;

import com.tinkerforge.IPConnection;

import de.kabuman.common.services.FormatterService;


public class TfDeviceInfo {
	
	// Tinkerforge Parameter
	String uid;
	String connectedUid;
	char position;
	short[] hardwareVersion;
	short[] firmwareVersion;
	int deviceIdentifier;
	short enumerationType;
	IPConnection ipcon;
	
	// Additional Parameter
	String name = null;

	public TfDeviceInfo(
			String uid,
			String connectedUid,
			char position,
			short[] hardwareVersion,
			short[] firmwareVersion,
			int deviceIdentifier,
			short enumerationType,
			IPConnection ipcon) {
		this.uid = uid;
		this.connectedUid = connectedUid;
		this.position = position;
		this.hardwareVersion = hardwareVersion;
		this.firmwareVersion = firmwareVersion;
		this.deviceIdentifier = deviceIdentifier;
		this.enumerationType = enumerationType;
		this.ipcon = ipcon;
	}
	

	public String getUid(){
		return uid;
	}
	
	public String getConnectedUid(){
		return connectedUid;
		
	}
	
	public String getHardwareVersion(){
		return  hardwareVersion[0] + "." +
                hardwareVersion[1] + "." +
                hardwareVersion[2];
	}

	public String getFirmwareVersion(){
		return	firmwareVersion[0] + "." +
                firmwareVersion[1] + "." +
                firmwareVersion[2];
	}
	
	public char getPosition(){
		return position;
	}
	
	public int getDeviceIdentifier(){
		return deviceIdentifier;
	}
	
	public IPConnection getIpcon(){
		return ipcon;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void println(){
		if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
			System.out.println("DisConnected at: "+FormatterService.getDateHHMMSSS(new Date()));
		} else {
			System.out.println("ReConnected at: "+FormatterService.getDateHHMMSSS(new Date()));
		}
	
		System.out.println("UID:               " + uid);
		if (name != null){
			System.out.println("Name            :  " + name);
		}
		System.out.println("Enumeration Type:  " + enumerationType);
		System.out.println("Connected UID:     " + connectedUid);
		System.out.println("Position:          " + position);
		System.out.println("Hardware Version:  " + hardwareVersion[0] + "." +
		                                           hardwareVersion[1] + "." +
		                                           hardwareVersion[2]);
		System.out.println("Firmware Version:  " + firmwareVersion[0] + "." +
		                                           firmwareVersion[1] + "." +
		                                           firmwareVersion[2]);
		System.out.println("Device Identifier: " + deviceIdentifier);
		System.out.println("");
	}
}
