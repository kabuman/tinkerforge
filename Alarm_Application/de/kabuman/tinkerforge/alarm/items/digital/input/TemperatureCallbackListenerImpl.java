package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletTemperature;


public class TemperatureCallbackListenerImpl implements BrickletTemperature.TemperatureListener {

	private CallbackShortConsumer callbackShortConsumer;

	/**
	 * Constructor  <br>
	 * Implements the Callback Method which will be called if the listener is triggered
	 *  
	 * @param callbackShortConsumer - the consumer of this callback <br> 
	 * 		Will be called back if the temperature method of this class is called. 
	 */
	public TemperatureCallbackListenerImpl(
			CallbackShortConsumer callbackShortConsumer){
		
		this.callbackShortConsumer = callbackShortConsumer;
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletTemperature.TemperatureListener#temperature(short)
	 */
	public void temperature(short temperature) {
		callbackShortConsumer.valueChanged(temperature);
	}

}
