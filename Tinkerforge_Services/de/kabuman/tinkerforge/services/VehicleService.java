package de.kabuman.tinkerforge.services;


/**
 * Interface for "VehicleService"
 *
 */
public interface VehicleService {
	
	/**
	 * @return MaxVelocity - the maximum velocity
	 */
	public short getMaxVelocity();
	
	/**
	 * @return String - formated ("####0") max velocity 
	 */
	public String getFormMaxVelocity();

	/**
	 * Set maximum possible velocity
	 * @param maxVelocity - the maximum velocity
	 */
	public void setMaxVelocity(short maxVelocity);
	
}
