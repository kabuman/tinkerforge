package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface OpenSensorItem {

	final static char OPTION_SMALLER = '<';
	final static char OPTION_GREATER = '>';
	final static long DEBOUNCE_PERIOD_STANDARD = 500l;  // 500 l (for long)
	
	/**
	 * Activates the OpenSensor 
	 */
	void activateOpenSensor();
	
	/**
	 * Deactivates the OpenSensor 
	 */
	void deactivateOpenSensor();
	
	/**
	 * Returns true if the OpenSensor is active
	 * 
	 * @return boolean
	 */
	boolean isActive();
	
	/**
	 * Triggers the interrupt
	 */
	void test();
	
	void removeListener();
	
	/**
	 * Checks if the Sensor is opened (means: Alert)
	 * 
	 * @return boolean - true: if sensor opened (Alert!) / false: if not
	 */
	public boolean checkSensorOpened();
	

}
