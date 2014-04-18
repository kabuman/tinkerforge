package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface CallbackIntConsumer {
	
	/**
	 * To implement by consumer of the callback triggered by the listener
	 * This method is call by callback listener in the case of an changed value  
	 */
	public void valueChanged(int value);
}
