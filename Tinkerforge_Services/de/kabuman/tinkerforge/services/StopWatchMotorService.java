package de.kabuman.tinkerforge.services;

import de.kabuman.common.services.StopWatchServiceImpl;

/**
 * Provides a Singleton Pattern for the StopWatch MOTOR 
 */
public class StopWatchMotorService extends StopWatchServiceImpl {
	
	// Used for Singleton Pattern
	static StopWatchMotorService instance = null;
	
	/**
	 * @return instance - returns a new instance if null
	 */
	public static synchronized StopWatchMotorService getInstance(){
		if (instance == null){
			instance = new StopWatchMotorService();
		}
		return instance;
	}
}
