package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletHumidity;

public class HumidityCallbackListenerImpl implements BrickletHumidity.HumidityListener {

	private CallbackIntConsumer callbackConsumer;

	/**
	 * Constructor  <br>
	 * Implements the Callback Method which will be called if the listener is triggered
	 *  
	 * @param callbackConsumer - the consumer of this callback <br> 
	 * 		Will be called back if the temperature method of this class is called. 
	 */
	public HumidityCallbackListenerImpl(
			CallbackIntConsumer callbackConsumer){
		
		this.callbackConsumer = callbackConsumer;
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletHumidity.HumidityListener#humidity(int)
	 */
	public void humidity(int humidity) {
		callbackConsumer.valueChanged(humidity);
	}

}
