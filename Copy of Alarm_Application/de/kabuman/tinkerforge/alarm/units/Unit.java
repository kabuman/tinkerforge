package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;

/**
 * Interface for all kinds of Units:
 * - Alert Unit
 * - Protect Unit
 */
public interface Unit {
	
	/**
	 * @return BrickMaster - the brick master of the unit
	 */
	BrickMaster getBrickMaster();
	
	/**
	 * @return String - the name of the unit
	 */
	String getUnitName();

	/**
	 * Deactivate Alert and Reset LED and Beeper after alert signal
	 */
	void reset();
	
	/**
	 * Deactivate Alert 
	 */
	void deactivateAlert();
	
	/**
	 * Used by Interval Alert to start again the alert 
	 */
	public void activateAlert();


	/**
	 * Reconnect the complete unit including all Bricklets
	 */
	public void reconnect();
	
	/**
	 * @return - returns true if connected (brickmaster and all other bricks and bricklets in this unit
	 */
	public boolean isConnected();
}
