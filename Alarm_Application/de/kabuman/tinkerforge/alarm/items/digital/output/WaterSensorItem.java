package de.kabuman.tinkerforge.alarm.items.digital.output;

import de.kabuman.tinkerforge.alarm.items.digital.input.Item;

public interface WaterSensorItem extends Item{

	final static char OPTION_SMALLER = '<';
	final static char OPTION_GREATER = '>';
	final static long DEBOUNCE_PERIOD_STANDARD = 500l;  // 500 l (for long)
	
	/**
	 * Activates the sensor 
	 */
	public void activateSensor();
	
	/**
	 * Deactivates the sensor 
	 */
	public void deactivateSensor();
	
	public boolean isActive();
	
	/**
	 * Returns the current value. E.g.: 23.1 
	 * @return double - the current voltage value in milli second
	 */
	double getCurrentValue();


}
