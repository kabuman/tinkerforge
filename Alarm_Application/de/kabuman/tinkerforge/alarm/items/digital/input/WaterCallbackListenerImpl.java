package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletAnalogIn;

public class WaterCallbackListenerImpl implements BrickletAnalogIn.VoltageReachedListener {

	private CallbackIntConsumer callbackConsumer;

	/**
	 * Constructor  <br>
	 * Implements the Callback Method which will be called if the listener is triggered
	 *  
	 * @param callbackShortConsumer - the consumer of this callback <br> 
	 * 		Will be called back if the temperature method of this class is called. 
	 */
	public WaterCallbackListenerImpl(CallbackIntConsumer callbackConsumer){
		
		this.callbackConsumer = callbackConsumer;
	}

	@Override
	public void voltageReached(int voltage) {
		callbackConsumer.valueChanged(voltage);
	}

}
