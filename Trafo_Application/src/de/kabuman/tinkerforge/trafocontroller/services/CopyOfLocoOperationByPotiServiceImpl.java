
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
public class CopyOfLocoOperationByPotiServiceImpl
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
    CopyOfLocoOperationByPotiServiceImpl(
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
        	loco.setVelocity(velocity);
			loco.enable();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * Set up the power control
     * 
     * position of poti --> new velocity for loco/loco slave
     */
    private void configLocoPowerControl() {
        try {
			poti.setPositionCallbackPeriod(callbackPeriod);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
        
		// Add and implement position listener (called if position changes)
		poti.addPositionListener(new BrickletRotaryPoti.PositionListener() {
			public void position(short position) {
				System.out.println("Position: " + position);
				
				velocity = 0;
				
				if (oldPos1 == oldPos3 && oldPos2 == position){
					// fibrillation detected:
//					System.out.println("=> Erkannt: Limit-Regler flimmert. Unterdrückt.");
					
				} else {
		            if(position < -10 || position > 10){
		            	// Geschwindigkeitsänderung erkannt
		            	velocity = calculatePowerLok(position);
		            } else {
		            	// Null-Stellung erkannt: Geschwindigkeit auf Null setzen
		            	velocity = 0;
		            }

		            
	            	setVelocity(loco);

	            	
	            	// BEHANDLUNG SLAVE LOKOMOTIVE
	            	if (locoSlave != null){
	            		// Slave existiert und muß gleich geregelt werden
		            	setVelocity(locoSlave);
	            		System.out.println("Slave Lokomotive erkannt und auf gleiche Geschwindigkeit gesetzt");
	            	} else {
	            		System.out.println("Keine Slave Lokomotive erkannt");
	            	}
				}
				
				// Maintain Fillibration Detecter
				oldPos1 = oldPos2;
				oldPos2 = oldPos3;
				oldPos3 = position;

				// access$0=oldPos1
				// access$1=oldPos3
				// access$2=oldPos2
				// access$4=
				// access$5=loco  (Device dieser Lokomotive)
				// access$6=
				// access$7=locoSlave
				// access$8
				// access$9=oldPos1
				// access$10=oldPos2
				// access$11=oldPos3
//ok	        if(LocoOperationByPotiServiceImpl.access$0(LocoOperationByPotiServiceImpl.this) != LocoOperationByPotiServiceImpl.access$1(LocoOperationByPotiServiceImpl.this) || LocoOperationByPotiServiceImpl.access$2(LocoOperationByPotiServiceImpl.this) != position)
//		        {
//ok	            if(position < -10 || position > 10)
//		                LocoOperationByPotiServiceImpl.access$4(LocoOperationByPotiServiceImpl.this, LocoOperationByPotiServiceImpl.access$3(LocoOperationByPotiServiceImpl.this, position));
//		            else
//		                LocoOperationByPotiServiceImpl.access$4(LocoOperationByPotiServiceImpl.this, (short)0);
//ok	            LocoOperationByPotiServiceImpl.access$5(LocoOperationByPotiServiceImpl.this).setVelocity(LocoOperationByPotiServiceImpl.access$6(LocoOperationByPotiServiceImpl.this));
//ok	            LocoOperationByPotiServiceImpl.access$5(LocoOperationByPotiServiceImpl.this).enable();
//		            if(LocoOperationByPotiServiceImpl.access$7(LocoOperationByPotiServiceImpl.this) != null)
//		            {
//		                LocoOperationByPotiServiceImpl.access$7(LocoOperationByPotiServiceImpl.this).setVelocity(LocoOperationByPotiServiceImpl.access$6(LocoOperationByPotiServiceImpl.this));
//		                LocoOperationByPotiServiceImpl.access$7(LocoOperationByPotiServiceImpl.this).enable();
//		                System.out.println((new StringBuilder(String.valueOf(LocoOperationByPotiServiceImpl.access$8(LocoOperationByPotiServiceImpl.this)))).append("/Slave=").append(LocoOperationByPotiServiceImpl.access$6(LocoOperationByPotiServiceImpl.this)).toString());
//		            } else
//		            {
//		                System.out.println((new StringBuilder(String.valueOf(LocoOperationByPotiServiceImpl.access$8(LocoOperationByPotiServiceImpl.this)))).append("=").append(LocoOperationByPotiServiceImpl.access$6(LocoOperationByPotiServiceImpl.this)).toString());
//		            }
//		        }
//		        LocoOperationByPotiServiceImpl.access$9(LocoOperationByPotiServiceImpl.this, LocoOperationByPotiServiceImpl.access$2(LocoOperationByPotiServiceImpl.this));
//		        LocoOperationByPotiServiceImpl.access$10(LocoOperationByPotiServiceImpl.this, LocoOperationByPotiServiceImpl.access$1(LocoOperationByPotiServiceImpl.this));
//		        LocoOperationByPotiServiceImpl.access$11(LocoOperationByPotiServiceImpl.this, position);
//		    }
				
				
			}
		});

    }

    private short calculatePowerLok(short position)
    {
        if(position < 0)
            return (short)((position + 10) * 141 - 13000);
        else
            return (short)((position - 10) * 141 + 13000);
    }

    private short calculatePowerLok2(short position)
    {
        short result = (short)(int)(Math.sqrt(position) * 2675D + (double)((300 - 2 * position) * 20) + 6D);
        if(position < 0)
            return (short)(result * -1);
        else
            return result;
    }

    private short calculatePowerLok3(short position)
    {
        short positionABS = (short)Math.abs(position);
        short result = (short)(int)(Math.sqrt(positionABS) * 2510D + (double)((300 - 2 * positionABS) * 20) + 2026D);
        if(position < 0)
            return (short)(-1 * result);
        else
            return result;
    }

    private short calculatePowerLok4(short position)
    {
        short positionABS = (short)Math.abs(position);
        short result = (short)(int)(Math.sqrt(positionABS) * 2675D + (double)((300 - 2 * positionABS) * 20) + 6D);
        if(position < 0)
            return (short)(-1 * result);
        else
            return result;
    }

    public void setLocoSlave(BrickDC locoSlave)
    {
        this.locoSlave = locoSlave;
    }

    public void setCallbackPeriod(long callbackPeriod)
    {
        this.callbackPeriod = callbackPeriod;
        try {
			poti.setPositionCallbackPeriod(callbackPeriod);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public String getLocoName() {
		return locoName;
	}

	public short getVelocity() {
		return velocity;
	}

}