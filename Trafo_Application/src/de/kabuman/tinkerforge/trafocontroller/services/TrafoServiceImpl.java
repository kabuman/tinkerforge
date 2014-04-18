package de.kabuman.tinkerforge.trafocontroller.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Set up power sources and power controls
 * 
 * On detail:
 * - Configures both locomotive power sources (BrickDC's)
 * - Set up and configures the power controls (BrickletRotary's)
 * - Set up and configures the switch for drive mode: master/master or master/slave
 * 
 * I04 Channels:
 * 	0: not used
 * 	1: switch
 * 	2: Master/Master LED
 * 	3: not used
 * 
 * 3,3 Volt: used by Power LED of Trafo (integrated in On/Off-Switch
 */
public class TrafoServiceImpl implements TrafoService{

    // Devices
    private BrickletIO4 modeSwitch;
    private BrickDC lok2PowerSource;

    // Services
    private LocoOperationByPotiService lok1;
    private LocoOperationByPotiService lok2;
    
    final long callbackPeriod = 200;

    
    /**
     * Constructor 
     * 
     * @param lok1PowerControl 	- Lok 1: BrickletRotaryPoti
     * @param lok1PowerSource 	- Lok 1: BrickDC
     * @param lok2PowerControl 	- Lok 2: BrickletRotaryPoti
     * @param lok2PowerSource 	- Lok 2: BrickDC
     * @param modeSwitch 		- Drive mode switch BrickletIO4: master/master  <=> master/slave 
     */
    public TrafoServiceImpl(
    		BrickletRotaryPoti lok1PowerControl
    		, BrickDC lok1PowerSource
    		, BrickletRotaryPoti lok2PowerControl
    		, BrickDC lok2PowerSource
    		, BrickletIO4 modeSwitch){
    	
        this.lok2PowerSource = lok2PowerSource;
        this.modeSwitch = modeSwitch;
        
        // Configures and installs the locomotive instances
        lok1 = configLoco(
        		lok1PowerSource
        		, lok1PowerControl
        		, "lok1"
        		);
        
        lok2 = configLoco(
        		lok2PowerSource
        		, lok2PowerControl
        		, "lok2"
        		);
        
        // Configures the drive mode switch
        controlSwitches();
    }

    /**
     * Creates a Locomotive Instance 
     */
    private LocoOperationByPotiServiceImpl configLoco(
    		BrickDC locoPowerSource
    		, BrickletRotaryPoti locoPowerControl
    		, String locoName){
    	
        try {
        	locoPowerSource.setPWMFrequency(15000);
        	locoPowerSource.setDriveMode((short)1);
        	locoPowerSource.setAcceleration(65535);
        	locoPowerSource.setVelocity((short)-19000);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}

        return new LocoOperationByPotiServiceImpl(
        		locoPowerSource
        		, null				// slave instance
        		, locoPowerControl
        		, 200l				// callbackPeriod
        		, locoName);
    }

    /**
     * Set up the switch for drive mode
     * Uses Channel 1 - configured as Input
     */
    private void controlSwitches()
    {
        System.out.println("controlSwitches");

        // Handle the current switch position
        short valueMask;
		try {
			valueMask = modeSwitch.getValue();
	        if(isSwitchedON(1, valueMask)){
	            setMasterSlaveMode();
	        } else {
	            setMasterMasterMode();
	        }
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			e1.printStackTrace();
		}
        
		
		// Install the switch listener 
        modeSwitch.addInterruptListener(new BrickletIO4.InterruptListener() {
			public void interrupt(short interruptMask, short valueMask) {
		        if(isInterruptedBy(1, interruptMask))
		            if(isSwitchedON(1, valueMask)){
			            setMasterSlaveMode();
			        } else {
			            setMasterMasterMode();
			        }
				System.out.println("Interrupt by: " + Integer.toBinaryString(interruptMask));
				System.out.println("Value: " + Integer.toBinaryString(valueMask));
			}
		});

        
        // Activate the switch listener
        // TODO - is the interrupt number 2 correct??? The switch is configured to interrupt number 1 !
        try {
			modeSwitch.setConfiguration((short)2, 'i', true);
	        modeSwitch.setInterrupt((short)2);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }

    /**
     * Set drive mode MASTER/MASTER
     * 
     * Every locomotive has its own power control
     * A former MASTER/SLAVE drive mode will be set back
     */
    private void setMasterMasterMode()
    {
    	// power control of lok1 not longer controls the velocity of lok2 
        lok1.setLocoSlave(null);
        
    	// lok2 get back its own power control
        lok2.setCallbackPeriod(200L);

        System.out.println("=> Schalter1: EIN: Master/Master Betrieb aktiviert");
        
        // switch ON the MASTER-LED
        try {
			modeSwitch.setConfiguration((short)4, 'o', true);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }

    /**
     * Set drive mode MASTER/SLAVE
     * 
     *  The velocity of lok2 will be controlled by power control of lok1 too
     *  The power control of lok2 is without function
     */
    private void setMasterSlaveMode()
    {
    	// transfer the velocity control of lok2 to lok1 / BrickletRotaryPoti of lok1 controls lok2 too
        lok1.setLocoSlave(lok2PowerSource);
        
        // deactivate the power control of lok2 / BrickletRotaryPoti of lok2 is without function
        lok2.setCallbackPeriod(0L);
        
        System.out.println("=> Schalter1: AUS: Master/Slave-Betrieb aktiviert.");

        // switch OFF the MASTER-LED
        try {
			modeSwitch.setConfiguration((short)4, 'o', false);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
    }

    /**
     * Returns true if the given interrupt is triggered
     *  
     * @param switchPos 
     * @param interruptMask
     * @return
     */
    private boolean isInterruptedBy(int switchPos, int interruptMask)
    {
        int mask = 1 << switchPos;
        return (interruptMask & mask) == mask;
    }

    /**
     * 
     * @param switchPos
     * @param valueMask
     * @return
     */
    private boolean isSwitchedON(int switchPos, int valueMask)
    {
        int mask = 1 << switchPos;
        return (valueMask & mask) == mask;
    }


}
