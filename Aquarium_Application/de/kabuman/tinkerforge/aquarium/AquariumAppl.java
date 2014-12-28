package de.kabuman.tinkerforge.aquarium;

public interface AquariumAppl {
	
	/**
	 * Launches the aquarium application <br>
	 *   <br>
	 * @param restart - true: yes, it is a restart
	 * @return Exception - the thrown exception if it was not a regular end of application
	 */
	public Exception launcher(boolean restart);

}
