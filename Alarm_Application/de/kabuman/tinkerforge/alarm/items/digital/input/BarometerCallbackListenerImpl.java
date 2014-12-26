package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletBarometer;

public class BarometerCallbackListenerImpl implements BrickletBarometer.AirPressureListener {

	private CallbackIntConsumer callbackIntConsumer;

	/**
	 * Constructor  <br>
	 * Implements the Callback Method which will be called if the listener is triggered
	 *  
	 * @param callbackShortConsumer - the consumer of this callback <br> 
	 * 		Will be called back if the temperature method of this class is called. 
	 */
	public BarometerCallbackListenerImpl(
			CallbackIntConsumer callbackIntConsumer){
		
		this.callbackIntConsumer = callbackIntConsumer;
	}


	@Override
	public void airPressure(int airPressure) {
		callbackIntConsumer.valueChanged((int)(airPressure/100));
	}

}
