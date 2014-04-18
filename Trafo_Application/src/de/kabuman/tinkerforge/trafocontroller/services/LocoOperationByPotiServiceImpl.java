
package de.kabuman.tinkerforge.trafocontroller.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Represents a locomotive
 * 
 * Calculates the velocity depending on the position of the rotary potentiometer
 * Transfers the new velocity to a slave locomotive if defined
 */
public class LocoOperationByPotiServiceImpl
    implements LocoOperationByPotiService
{

	// Devices
	private BrickDC loco;
	private BrickDC locoSlave;
	private BrickletRotaryPoti poti;
	
	
	private long callbackPeriod;
	private String locoName;
	
	private short oldPos1;
	private short oldPos2;
	private short oldPos3;
	private short velocity;

    /**
     * Constructor
     * Set up and configures the locomotive
     * 
     * @param loco - the locomotive power source
     * @param locoSlave - the locomotive power source (may be null)
     * @param poti - the velocity control for "loco"
     * @param callbackPeriod - the callback period of "poti"
     * @param locoName - the name of the locomotive
     */
    LocoOperationByPotiServiceImpl(
    		BrickDC loco
    		, BrickDC locoSlave
    		, BrickletRotaryPoti poti
    		, long callbackPeriod
    		, String locoName){
    	
        this.loco = loco;
        this.locoSlave = locoSlave;
        this.poti = poti;
        this.callbackPeriod = callbackPeriod;
        this.locoName = locoName;
        
		// vars for fibrillation detection
        oldPos1 = 0;
        oldPos2 = 0;
        oldPos3 = 0;
        
        velocity = 0;
        
        configLocoPowerControl();
    }

    
    /**
     * Set the new velocity for
     * loco or loco slave
     *  
     * @param loco - the power source
     */
    private void setVelocity(BrickDC loco){
    	try {
    		if (velocity < 21000){
        		setPreVelocity(loco, velocity);
    		}
			
        	loco.setVelocity(velocity);
			loco.enable();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }
    
    private void setPreVelocity(BrickDC loco, short velocity){
    	try {
    		short startPreVelocity = (short) (velocity * 1.2);
    		short endPreVelocity = (short)(velocity * 1.0);
    		short preVelocityAddOn = (short)(velocity * 0.1);
    		
    		for (short preVelocity = startPreVelocity; preVelocity < endPreVelocity; preVelocity = (short)(preVelocity + preVelocityAddOn)) {
            	loco.setVelocity(preVelocity);
    			loco.enable();
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Set up the power control
     * 
     * Set the callback period of BrickletRotary Listener
     * Installs the BrickletRotary Listener for position changes
     * 
     * position of poti --> new velocity for loco/loco slave
     */
    private void configLocoPowerControl() {
    	setCallbackPeriod(callbackPeriod);

    	// Add and implement position listener (called if position changes)
		poti.addPositionListener(new BrickletRotaryPoti.PositionListener() {
			public void position(short position) {
//				System.out.println("Position: " + position);
				
				velocity = 0;
				
				if (oldPos1 == oldPos3 && oldPos2 == position){
					// fibrillation detected:
//					System.out.println("=> Erkannt: Limit-Regler flimmert. Unterdrückt.");

				} else {
		            if(position < -10 || position > 10){
		            	// Velocity is to change
		            	velocity = calculatePowerLok(position);
		            } else {
		            	// poti position is zero or nearly zero
		            	velocity = 0;
		            }

		            setVelocity(loco);

	            	// Handling of slave locomotive
	            	if (locoSlave != null){
	            		// Slave exists 
		            	setVelocity(locoSlave);
	            		System.out.println("Slave Lokomotive erkannt und auf gleiche Geschwindigkeit gesetzt");
	            	} else {
	            		System.out.println("Keine Slave Lokomotive erkannt");
	            	}
				}
				
				// Maintain Fillibration Detection
				oldPos1 = oldPos2;
				oldPos2 = oldPos3;
				oldPos3 = position;
			}
		});

    }

    /**
     * Calculates the new velocity depending on the poti position
     * 
     * @param position - poti position (-150,..,0,..,+150)
     * @return velocity
     */
    private short calculatePowerLok(short position){
    	// Calculate absolute position
        short positionABS = (short)Math.abs(position);
        
//        = Wurzel aus POS * 2675 + 20 * (300 - 2 * POS) + 6
//        = Wurzel aus 25 * 2675 + 20 * (300 - 2 * 25)
//        short result = (short)(int)(Math.sqrt(positionABS) * 2675D + (double)((300 - 2 * positionABS) * 20) + 6D);
        
//      // Velocity 13295 .. 30618
        final double velocityLimitFactor = 2500;
        final double velocityStartFactor = 36;
        		
//        // Velocity 13.321 .. 32767
//        final double velocityLimitFactor = 2675.414;
//        final double velocityStartFactor = 32;
        
        short result = (short)(int)(Math.sqrt(positionABS) * velocityLimitFactor + (double)(-velocityStartFactor*(positionABS-150)));
        
        
        System.out.println(position + " : " + result);
        
        // Handle Negative / Positive
        if(position < 0){
        	// negative position
        	return (short)(-1 * result);
        } else {
        	// positive position
            return result;
        }
    }


    /* (non-Javadoc)
     * @see de.kabuman.tinkerforge.trafocontroller.services.LocoOperationByPotiService#setLocoSlave(com.tinkerforge.BrickDC)
     */
    public void setLocoSlave(BrickDC locoSlave){
        this.locoSlave = locoSlave;
    }

    /* (non-Javadoc)
     * @see de.kabuman.tinkerforge.trafocontroller.services.LocoOperationByPotiService#setCallbackPeriod(long)
     */
    public void setCallbackPeriod(long callbackPeriod)
    {
        this.callbackPeriod = callbackPeriod;
        try {
			poti.setPositionCallbackPeriod(callbackPeriod);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.trafocontroller.services.LocoOperationByPotiService#getLocoName()
	 */
	public String getLocoName() {
		return locoName;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.trafocontroller.services.LocoOperationByPotiService#getVelocity()
	 */
	public short getVelocity() {
		return velocity;
	}

}