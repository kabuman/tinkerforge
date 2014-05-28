package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface TemperatureSensorItem extends Item{
	
	/**
	 * Activates the Sensor 
	 */
	void activateSensor();
	
	/**
	 * Returns the current value. E.g.: 23.1 
	 * @return double - the current temperature value
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
