package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface AmbientLightSensorItem extends Item{
	
	/**
	 * Activates the Sensor 
	 */
	void activateSensor();
	
	/**
	 * Returns the current value. E.g.: 231 means: 23.1 °C
	 * To display: (double)getCurrentValue/10
	 * @return int - the current humidity value
	 */
	double getCurrentValue();

	/**
	 * Deactivates the Sensor 
	 */
	void deactivateSensor();
	
	/**
	 * Returns true if the Sensor is active
	 * 
	 * @return boolean
	 */
	boolean isActive();
	
	/**
	 * Removes the listener
	 */
	void removeListener();
}
