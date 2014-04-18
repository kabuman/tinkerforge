package de.kabuman.tinkerforge.alarm.devicehelper;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.IOService;

public class IO16device {
	
	private BrickletIO16 io16 = null;
	
	public IO16device(BrickletIO16 io16) {
		this.io16 = io16;
	}

	/**
	 * Set Interrupt (must be merged or added before calling this method)
	 * @param interrupt
	 */
	public void setInterrupt(char port, short interrupt, long debouncePeriod){
		try {
			io16.setDebouncePeriod(debouncePeriod);
			io16.setPortInterrupt(port, interrupt);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	private Short getCurrentInterrupts(char port){
		short currentInterrupts = 0;
		try {
			currentInterrupts = io16.getPortInterrupt(port);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return currentInterrupts;
	}
	
	public short getAddedInterrupt(char port, short interrupt){
		short addedInterrupt = (short) (getCurrentInterrupts(port) | IOService.setBitON(interrupt));
		return addedInterrupt;
	}

	public short getSubtractedInterrupt(char port, short interrupt){
		short subtractedInterrupt = (short) (getCurrentInterrupts(port) ^ IOService.setBitON(interrupt));  // XOR: exclusive or
		return subtractedInterrupt;
	}


}
