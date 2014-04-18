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
 */
public class CopyOfTrafoServiceImpl implements TrafoService{

    // Devices
    private BrickletIO4 modeSwitch;

	// Devices Lok 1
    private BrickletRotaryPoti lok1PowerControl;
    private BrickDC lok1PowerSource;
    
    // Devices Lok 2
    private BrickletRotaryPoti lok2PowerControl;
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
    public CopyOfTrafoServiceImpl(
    		BrickletRotaryPoti lok1PowerControl
    		, BrickDC lok1PowerSource
    		, BrickletRotaryPoti lok2PowerControl
    		, BrickDC lok2PowerSource
    		, BrickletIO4 modeSwitch){
    	
        this.lok1PowerControl = lok1PowerControl;
        this.lok1PowerSource = lok1PowerSource;
        this.lok2PowerControl = lok2PowerControl;
        this.lok2PowerSource = lok2PowerSource;
        this.modeSwitch = modeSwitch;
        
        // Configuration Devices
        configLokPowerSource(lok1PowerSource);
        configLokPowerSource(lok2PowerSource);
        
        // Configuration Services
        configLoco1();
        configLoco2();
        
        // Configuration Switch
        controlSwitches();
    }

    /**
     * Creates the Loco 1 Instance 
     */
    private void configLoco1()
    {
        lok1 = new LocoOperationByPotiServiceImpl(
        		lok1PowerSource
        		, null				
        		, lok1PowerControl
        		, callbackPeriod
        		, "Lok1");
    }

    /**
     * Creates the Loco 2 Instance 
     */
    private void configLoco2()
    {
        lok2 = new LocoOperationByPotiServiceImpl(
        		lok2PowerSource
        		, null
        		, lok2PowerControl
        		, callbackPeriod
        		, "Lok2");
    }

    /**
     * Configures a loco power source
     * 
     * @param lokPowerSource - power source (BrickDC)
     */
    private void configLokPowerSource(BrickDC lokPowerSource)
    {
        try {
			lokPowerSource.setPWMFrequency(15000);
	        lokPowerSource.setDriveMode((short)1);
	        lokPowerSource.setAcceleration(65535);
	        lokPowerSource.setVelocity((short)-19000);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void controlSwitches()
    {
        System.out.println("controlSwitches");

        short valueMask;
		try {
			valueMask = modeSwitch.getValue();
	        if(isSwitchedON(1, valueMask)){
	            setMasterSlaveMode();
	        } else {
	            setMasterMasterMode();
	        }
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		// Add and implement listener for interrupt (called if pin 0 changes)
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

        
        
        try {
			modeSwitch.setConfiguration((short)2, 'i', true);
	        modeSwitch.setInterrupt((short)2);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void setMasterMasterMode()
    {
        lok1.setLocoSlave(null);
        lok2.setCallbackPeriod(200L);
        System.out.println("=> Schalter1: EIN: Master/Master Betrieb aktiviert");
        try {
			modeSwitch.setConfiguration((short)4, 'o', true);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void setMasterSlaveMode()
    {
        lok1.setLocoSlave(lok2PowerSource);
        lok2.setCallbackPeriod(0L);
        System.out.println("=> Schalter1: AUS: Master/Slave-Betrieb aktiviert.");
        try {
			modeSwitch.setConfiguration((short)4, 'o', false);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    boolean isInterruptedBy(int switchPos, int interruptMask)
    {
        int mask = 1 << switchPos;
        return (interruptMask & mask) == mask;
    }

    boolean isSwitchedON(int switchPos, int valueMask)
    {
        int mask = 1 << switchPos;
        return (valueMask & mask) == mask;
    }


}
