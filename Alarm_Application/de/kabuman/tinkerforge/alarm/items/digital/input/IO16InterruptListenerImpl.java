package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.services.IOService;

public class IO16InterruptListenerImpl implements BrickletIO16.InterruptListener {

	int interrupt;
	char port;
	InterruptConsumerSwitch interruptConsumerSwitch;

	public IO16InterruptListenerImpl(
			InterruptConsumerSwitch interruptConsumer,
			char port,
			int interrupt){
		
		this.interruptConsumerSwitch = interruptConsumer;
		this.port = port;
		this.interrupt = interrupt;
	}

	
	public void test(){
		short mask = IOService.setBitON(interrupt);
		interrupt(port, mask,mask);
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletIO4.InterruptListener#interrupt(short, short)
	 */
	public void interrupt(char port, short interruptMask, short valueMask) {
		if (this.port == port && IOService.isInterruptedBy(interrupt, interruptMask)) {
			if (IOService.isSwitchedON(interrupt, valueMask)) {
				interruptConsumerSwitch.switchedOFF();
			} else {
				interruptConsumerSwitch.switchedON();
			}
		}
	}

}
