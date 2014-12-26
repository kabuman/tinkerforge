package de.kabuman.tinkerforge.alarm.items.digital.output;

import de.kabuman.tinkerforge.alarm.items.digital.input.Item;

public interface SmokeSensorItem extends Item{

	final static char OPTION_SMALLER = '<';
	final static char OPTION_GREATER = '>';
	final static long DEBOUNCE_PERIOD_STANDARD = 100l;  // 100 l (for long)
	
	
	/**
	 * Activates the sensor 
	 */
	public void activateSensor();
	
	
	/**
	 * Deactivates the sensor 
	 */
	public void deactivateSensor();
	
	
	/**
	 * Returns true if active
	 * 
	 * @return boolean - true: activce / false: not active
	 */
	public boolean isActive();
	

}
