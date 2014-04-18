package de.kabuman.tinkerforge.alarm.devicehelper;

import com.tinkerforge.BrickletIO4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.IOService;

/**
 * Provides basic functions for a IO4 Device
 */
public class IO4device {
	
	private BrickletIO4 io4 = null;
	
	
	/**
	 * Constructor
	 * Provides methods to set, merge or subtract the given interrupt from the current interrupts of the given IO4 Device
	 * 
	 * @param io4
	 */
	public IO4device(BrickletIO4 io4) {
		this.io4 = io4;
	}

	/**
	 * Set Interrupt (must be merged or added before calling this method)
	 * 
	 * @param interrupt
	 */
	public void setInterrupt(short interrupt, long debouncePeriod){
		try {
			io4.setDebouncePeriod(debouncePeriod);
			io4.setInterrupt(interrupt);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the created interrupts of the given IO4 Device
	 * 
	 * @return short - the created interrupts
	 */
	private Short getCurrentInterrupts(){
		short currentInterrupts = 0;
		try {
			currentInterrupts = io4.getInterrupt();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return currentInterrupts;
	}
	
	/**
	 * Adds the given interrupt to the current interrupts of the given IO4 Device
	 * 
	 * @param interrupt - the interrupt to merge/to set
	 * @return short -the merged interrupt value
	 */
	public short getAddedInterrupt(short interrupt){
		short addedInterrupt = (short) (getCurrentInterrupts() | IOService.setBitON(interrupt));
		return addedInterrupt;
	}

	/**
	 * Subtract the given interrupt from the current interrupts
	 * 
	 * @param interrupt - the interrupt to subtract
	 * @return
	 */
	public short getSubtractedInterrupt(short interrupt){
		short subtractedInterrupt = (short) (getCurrentInterrupts() ^ IOService.setBitON(interrupt));  // XOR: exclusive or
		return subtractedInterrupt;
	}
}
