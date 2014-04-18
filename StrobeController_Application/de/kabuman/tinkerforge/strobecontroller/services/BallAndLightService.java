package de.kabuman.tinkerforge.strobecontroller.services;


public interface BallAndLightService {

	/**
	 * Set the velocity
	 */
	public void setVelocity(short velocity);
	
	public short getFrequency();
	
	public short getPosition();
	
	public short getLEDVelocity();
	
}
