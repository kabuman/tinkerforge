package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;

import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;

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
	 * Activates the unit
	 * Dependes on the kind of unit:
	 * - Activates the sensors (Motion Sensor, Open Sensor) which are connected to this Protect Unit
	 * - Confirmes the activation by LED blinking
	 */
	void activateUnit();

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
	
	/**
	 * @return - returns the Temperature Sensor Item
	 */
	TemperatureSensorItem getTemperatureSensorItem();
	
	/**
	 * @return - returns the Humidity Sensor Item
	 */
	HumiditySensorItem getHumiditySensorItem();
	
	/**
	 * @param switchOn
	 */
	void power(boolean switchOn);
	
	/**
	 * @return - returns the LED Item
	 */
	Object getLEDItem();

}
