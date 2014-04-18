package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.devicehelper.IO4device;
import de.kabuman.tinkerforge.services.IOService;

/**
 * Provides input functions for IO4 Devices
 * Some of them abstract and to implement by extending class 
 *
 */
public abstract class InputIO4ItemImpl extends IO4device implements InterruptConsumerSwitch{

	BrickletIO4 io4 = null;
	long debouncePeriod = 0;
	Short interrupt = null;
	
	boolean enable = false;
	
	// Interrupt Listener with test
	IO4InterruptListenerImpl interruptListener = null;
	
	public InputIO4ItemImpl(
			BrickletIO4 io4,
			long debouncePeriod,
			short interrupt,
			boolean enable){
		super(io4);
		this.io4 = io4;
		this.debouncePeriod = debouncePeriod;
		this.interrupt = interrupt;
		
		install();
		
		if (enable){
			enable();
			setStartingPostion();
		} else {
			disable();
		}
	}

	/**
	 * Configurate and add the Interrupt Listener  <br>
	 * but does not activates it (no setInterrupt(..) method call)
	 */
	private void install(){
		enable = false;
		
		try {
			io4.setConfiguration(IOService.setBitON(interrupt), 'i', true);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		
		interruptListener = new IO4InterruptListenerImpl(this, interrupt);
		io4.addInterruptListener(interruptListener);

	}
	
	/**
	 * Removes the Interrupt Listener
	 */
	public void removeListener(){
		io4.removeInterruptListener(interruptListener);
	}
	

	/**
	 * Determines the start value of the interrupt  <br>
	 * and calls the associated method depending of the value
	 */
	private void setStartingPostion(){
		try {
			if (IOService.isSwitchedON(interrupt, io4.getValue())){
                switchedOFFbeforeStart();

			} else {
                switchedONbeforeStart();
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enables the interrupt (listener will be active)
	 */
	public void enable(){
		enable = true;
		setInterrupt(getAddedInterrupt(interrupt), debouncePeriod);
	}

	/**
	 * Disables the interrupt. (Removes the interrupt bit from IO4 Device) 
	 */
	public void disable(){
		enable = false;
		setInterrupt(getSubtractedInterrupt(interrupt), debouncePeriod);
	}
	
	/**
	 * Returns the enable flag
	 * 
	 * @return boolean - true, if enabled / false: if not
	 */
	public boolean isEnabled(){
		return enable;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InterruptConsumerSwitch#switchedOFF()
	 */
	public abstract void switchedOFF();
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InterruptConsumerSwitch#switchedON()
	 */
	public abstract void switchedON();
	
	/**
	 * Defines the action if the input channel is Off (open) after start 
	 * Can be overridden by class which extends
	 */
	public void switchedOFFbeforeStart(){
		switchedOFF();
	}
	
	/**
	 * Defines the action if the input channel is ON (closed) after start 
	 * Can be overridden by class which extends
	 */
	public void switchedONbeforeStart(){
		switchedON();
	}
	

}
