package de.kabuman.tinkerforge.services;

import de.kabuman.common.services.StopWatchServiceImpl;

/**
 * Provides a Singleton Pattern for the StopWatch APPLICATION 
 */
public class StopWatchApplService extends StopWatchServiceImpl {
	
	// Used for Singleton Pattern
	static StopWatchApplService instance = null;
	
	/**
	 * @return instance - returns a new instance if null
	 */
	public static synchronized StopWatchApplService getInstance(){
		if (instance == null){
			instance = new StopWatchApplService();
		}
		return instance;
	}
}
