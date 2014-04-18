package de.kabuman.tinkerforge.alarm;

public interface AlarmAppl {
	
	/**
	 * Launches the application <br>
	 *  <br>
	 * @param restart - true: yes, it is a restart
	 * @return Exception - the thrown exception if it was not a regular end of application
	 */
	public Exception launcher(boolean restart);

}
