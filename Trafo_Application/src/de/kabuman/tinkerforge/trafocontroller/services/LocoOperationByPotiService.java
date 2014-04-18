package de.kabuman.tinkerforge.trafocontroller.services;

import com.tinkerforge.BrickDC;

public interface LocoOperationByPotiService
{

    /**
     * Set a slave locomotive
     * If set a velocity change will be transfered to the slave locomotive too
     *  
     * @param locoSlave - the power source of the slave locomotive
     */
    void setLocoSlave(BrickDC locoSlave);

    /**
     * Set a new callback period for the velocity poti (BrickletRotary)
     * The callback period will be immediately set  
     *  
     * @param callbackPeriod - the new call back period 
     */
    void setCallbackPeriod(long callbackPeriod);
    
	/**
	 * Returns the name of the locomotive
	 * 
	 * @return loco name
	 */
	String getLocoName();

	/**
	 * Returns the current velocity 
	 * 
	 * @return velocity
	 */
	short getVelocity();
}
