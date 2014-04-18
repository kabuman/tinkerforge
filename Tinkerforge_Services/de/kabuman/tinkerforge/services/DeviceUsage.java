package de.kabuman.tinkerforge.services;

import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;

public class DeviceUsage {
	
	Device device = null;
	
	IPConnection ipcon = null;
	
	String usedFor = null;
	
	Integer id = null;
	
	String uid = null;
	
	Double thresholdMinVoltage;
	
	public DeviceUsage(Device device, IPConnection ipcon, Integer id, String uid){
		this.device = device;
		this.ipcon = ipcon;
		this.id = id;
		this.uid = uid;
	}

	public DeviceUsage(Device device, Integer id){
		this.device = device;
		this.id = id;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getUsedFor() {
		return usedFor;
	}

	public void setUsedFor(String usedFor) {
		this.usedFor = usedFor;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Double getThresholdMinVoltage() {
		return thresholdMinVoltage;
	}

	public void setThresholdMinVoltage(Double thresholdMinVoltage) {
		this.thresholdMinVoltage = thresholdMinVoltage;
	}

	public IPConnection getIpcon() {
		return ipcon;
	}

	public void setIpcon(IPConnection ipcon) {
		this.ipcon = ipcon;
	}

}
