package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface InterruptConsumer {
	
	/**
	 * To implement by consumer of the interrupt triggered by the listener
	 * This method is call the interrupt listener in the case of an interrupt  
	 */
	public void listenerInterrupt();

}
