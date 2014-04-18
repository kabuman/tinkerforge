package de.kabuman.tinkerforge.services;

public interface DisplayObserver {


	/**
	 * Stops the thread and set alive flag to false
	 */
	public void deactivate();

}
