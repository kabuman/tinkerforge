package de.kabuman.tinkerforge.services.rotaryencoder;

public interface RotaryEncoderConsumer {
	
	/**
	 * Called if counter values has changed  <br>
	 * 
	 * @param newValue - the current value
	 * @param oldValue - the old value
	 */
	void rotaryEncoderCounterValueChanged(int newValue, int oldValue);
	
	
	/**
	 * Called if pressed 
	 */
	void rotaryEncoderPressed();
	
	
	/**
	 * Called if released  <br>
	 *  <br>
	 * But will be not called if:  <br>
	 * - a time period is defined  <br>
	 * - and time period is elapsed  <br> 
	 * In this case the event rotaryEncoderPressedTimeReached() is triggered  <br>
	 *  <br>
	 * Use method  <br>
	 * 		RotaryEncoderSupplierImpl.setPressedTimer(Long)  <br>
	 * to define the time period.   <br>
	 * 
	 * @param pressedDuration - the time period in milliseconds since pressed event
	 */
	void rotaryEncoderReleased(long pressedDuration);
	
	
	/**
	 * Called if released after a defined time period  <br>
	 * In this case the event "rotaryEncoderReleased(..)" will not be triggered  <br>
	 *  <br>
	 * Use method  <br>
	 * 		RotaryEncoderSupplierImpl.setPressedTimer(Long)  <br>
	 * to define the time period.  
	 */
	void rotaryEncoderPressedTimeReached();

}
