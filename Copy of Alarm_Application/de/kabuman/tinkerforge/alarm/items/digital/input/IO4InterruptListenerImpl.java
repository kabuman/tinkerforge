package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;

import de.kabuman.tinkerforge.services.IOService;

public class IO4InterruptListenerImpl implements BrickletIO4.InterruptListener {

	private int interrupt;
	private InterruptConsumerSwitch interruptConsumerSwitch;

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
	
	public void test(){
		short mask = IOService.setBitON(interrupt);
		interrupt(mask,mask);
	}

}
