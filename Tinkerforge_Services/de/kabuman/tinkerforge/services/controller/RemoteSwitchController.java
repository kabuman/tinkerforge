package de.kabuman.tinkerforge.services.controller;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

/**
 * Remote Switch Controller - switch off/on the power of USB Wall Power Supply
 * (Steckernetzteil)
 */
public interface RemoteSwitchController {

	final static short SWITCH_TYPE_A = 1;	// Elro / mumbi
	final static short SWITCH_TYPE_B = 2;
	final static short SWITCH_TYPE_C = 3; 	// Intertech
	
	final static short SWITCH_ON = 1;
	final static short SWITCH_OFF = 0;


	void switchPower(CfgRemoteSwitchData cfgRemoteSwitchData, short switchTo);

	/**
	 * Switch the power for the given power supply defined by configuration record
	 * 
	 * @param cfgRemoteSwitchData - vars from configuration record
	 * @param switchTo - 1:ON, 0:OFF
	 */
	void switchPowerSecurely(CfgRemoteSwitchData cfgRemoteSwitchData, short switchTo);


	/**
	 * Set the state of the RemoteSwitchController to active=false or active=true
	 * 
	 * @param active - the requested state
	 */
	void setActive(boolean active);


	/**
	 * Returns true if RemoteSwitchController is set up sucessfully and active. Returns false, if not
	 * 
	 * @return boolean - true: active, false: not active
	 */
	boolean isActive();


	/**
	 * Returns true if RemoteSwitchController is connected. Otherwise false.
	 * 
	 * @return boolean - true: connected, false: not connected
	 */
	boolean isConnected();


	/**
	 * Makes the Thread sleeping for the given duration in milli seconds.
	 * Wil have not effect if RemoteSwitchController is in state active=false.
	 * 
	 * @param msec - the duration
	 */
	void sleep(long msec);

}
