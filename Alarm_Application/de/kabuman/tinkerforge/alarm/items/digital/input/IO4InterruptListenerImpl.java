package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;

import de.kabuman.tinkerforge.services.IOService;

public class IO4InterruptListenerImpl implements BrickletIO4.InterruptListener {

	private int interrupt;
	private InterruptConsumerSwitch interruptConsumerSwitch;

	/**
	 * Constructor  <br>
	 * Implements the Interrupt Method which will be called if the listener is triggered
	 *  
	 * @param interruptConsumer - the consumer of this interrupt <br> 
	 * 		Will be called back if the interrupt method of this class is called. 
	 * @param interrupt - the bitmask of the interrupt to filter out the correct interrupt
	 */
	public IO4InterruptListenerImpl(
			InterruptConsumerSwitch interruptConsumer,
			int interrupt){
		
		this.interruptConsumerSwitch = interruptConsumer;
		this.interrupt = interrupt;
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletIO4.InterruptListener#interrupt(short, short)
	 */
	public void interrupt(short interruptMask, short valueMask) {

		if (IOService.isInterruptedBy(interrupt, interruptMask)) {
			if (IOService.isSwitchedON(interrupt, valueMask)) {
				interruptConsumerSwitch.switchedOFF();
			} else {
				interruptConsumerSwitch.switchedON();
			}
		}
	}
	
	/**
	 * Tests the interrupt by triggering it  <br>
	 * This method is to call for test purpose only
	 */
	public void test(){
		short mask = IOService.setBitON(interrupt);
		interrupt(mask,mask);
	}

}
