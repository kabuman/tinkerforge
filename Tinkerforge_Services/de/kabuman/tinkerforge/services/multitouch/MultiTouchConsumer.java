package de.kabuman.tinkerforge.services.multitouch;

public interface MultiTouchConsumer {

	
	/**
	 * Will be called if the touch state is changed.
	 * 
	 * @param newValue - the current value
	 * @param oldValue - the previous value
	 */
	void multiTouchStateValueChanged(int newValue, int oldValue);
	
	/**
	 * Will be called if a pin is keeped touch along a defined time period
	 * 
	 * @param state - the value which was keep the defined time period
	 */
	void multiTouchStateValueTimerReached(int state);
}
