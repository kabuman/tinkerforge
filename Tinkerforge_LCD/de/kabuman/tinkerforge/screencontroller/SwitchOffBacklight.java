package de.kabuman.tinkerforge.screencontroller;

public interface SwitchOffBacklight {

	/**
	 * Interrupts the thread and set it to active=false
	 */
	void terminate();
	
	/**
	 * Returns true if active or false if not
	 */
	boolean isActive();
	
	/**
	 * Restart/Refresh Backlight OFF Timer
	 */
	void  startBacklight();


}
