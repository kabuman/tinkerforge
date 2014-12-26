package de.kabuman.common.services;

public interface CommonObserver {

	/**
	 * Stops the current observing 
	 */
	void stopObservation();
	

	/**
	 * Starts or restarts the observing
	 */
	void  startObservation();

	
	/**
	 * Starts or restarts the observing with an individual temporarily observation time
	 */
	void  startObservation(long observationTime);
	
	
	/**
	 * Sets/changes the observation time permanently
	 * @param observationTime - milliseconds
	 */
	void setObservationTime(long observationTime);
	
	
	/**
	 * @return
	 */
	boolean isObservationActive();
	
	
	/**
	 * @param functionCode
	 */
	public void setFunctionCode(Integer functionCode);


}
