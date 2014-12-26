package de.kabuman.tinkerforge.services.voltagecurrent;

public interface VoltageConsumer {
	
	/**
	 * Called if voltage values has changed  <br>
	 * 
	 * @param newValue - the current value
	 * @param oldValue - the old value
	 */
	void voltageValueChanged(int newValue, int oldValue);
	
	
	/**
	 * Called if current values has changed  <br>
	 * 
	 * @param newValue - the current value
	 * @param oldValue - the old value
	 */
	void currentValueChanged(int newValue, int oldValue);
	

}
