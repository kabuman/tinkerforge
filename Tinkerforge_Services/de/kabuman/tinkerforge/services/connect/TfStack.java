package de.kabuman.tinkerforge.services.connect;

import com.tinkerforge.IPConnection;


/**
 * Tinkerforge Stack Interface
 * 
 * Every Stack has to implement this interface
 */
public interface TfStack {

	/**
	 * Set the IPConnection
	 * 
	 * @param ipcon - the IPConnection
	 */
	void setIPConnection(IPConnection ipcon);
	
	
	/**
	 * Connects or reconnects one device  <br>
	 * 
	 * @param tfDeviceInfo - the device info container
	 */
	void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo);
	
	
	/**
	 * Disconnects one device  <br>
	 * 
	 * @param uid - the uid of the disconnected device
	 */
	void tfDeviceDisConnected(String uid);
	
	
	/**
	 * Returns true if connected; otherwise false  <br>
	 * 
	 * @return boolean - true if connected
	 */
	boolean isConnected();

}
