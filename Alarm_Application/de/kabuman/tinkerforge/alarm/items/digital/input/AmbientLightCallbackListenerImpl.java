package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletAmbientLight;

public class AmbientLightCallbackListenerImpl implements BrickletAmbientLight.IlluminanceReachedListener {

	private CallbackIntConsumer callbackConsumer;

	/**
	 * Constructor  <br>
	 * Implements the Callback Method which will be called if the listener is triggered
	 *  
	 * @param callbackConsumer - the consumer of this callback <br> 
	 * 		Will be called back if the temperature method of this class is called. 
	 */
	public AmbientLightCallbackListenerImpl(CallbackIntConsumer callbackConsumer){
		this.callbackConsumer = callbackConsumer;
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletAmbientLight.IlluminanceReachedListener#illuminanceReached(int)
	 */
	@Override
	public void illuminanceReached(int illuminance) {
		callbackConsumer.valueChanged(illuminance);
	}

}
