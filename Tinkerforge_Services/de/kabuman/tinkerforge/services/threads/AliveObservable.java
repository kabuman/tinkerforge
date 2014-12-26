package de.kabuman.tinkerforge.services.threads;

public interface AliveObservable {
	/**
	 * @return String - the name of the unit
	 */
	String getUnitName();

	/**
	 * Reconnect the complete unit including all Bricklets
	 */
	public void reconnect();
	
	/**
	 * @return - returns true if connected (brickmaster and all other bricks and bricklets in this unit
	 */
	public boolean isConnected();
	
}
