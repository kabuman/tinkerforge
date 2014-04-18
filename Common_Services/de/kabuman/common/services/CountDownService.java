package de.kabuman.common.services;

/**
 * Interface for "CountDownService"
 *
 * CountDownService provides a helper class 
 * to administrate a counter to use in a loop
 */
public interface CountDownService {
	
	/**
	 * Makes the compare; Decreases the From counter by 1;  and returns the boolean.
	 * 
	 * @return boolean - TRUE: count down stops (From counter <= 0; FALSE: count down is running (countFrom counter > 0)
	 */
	public boolean down();

	/**
	 * @return boolean - TRUE: count down stops (From counter <= 0; FALSE: count down is running (countFrom counter > 0)
	 */
	public boolean isDown();
	
	/**
	 * Reset the service and set a new FROM counter.
	 * @param from - the From counter
	 */
	public void reset(int from);
	
	/**
	 * Reset the service. The From counter is set to his last known start value. 
	 */
	public void reset();
	
	/**
	 * Stops the count down. From counter is set to zero. 
	 */
	public void stop();

}
