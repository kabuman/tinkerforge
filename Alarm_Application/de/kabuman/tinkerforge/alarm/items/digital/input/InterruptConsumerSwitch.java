package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface InterruptConsumerSwitch {
	
	/**
	 * To implement by consumer of the interrupt triggered by the listener
	 * This method is call by interrupt listener in the case of an interrupt  
	 */
	public void switchedON();

	/**
	 * To implement by consumer of the interrupt triggered by the listener
	 * This method is call by interrupt listener in the case of an interrupt  
	 */
	public void switchedOFF();
}
