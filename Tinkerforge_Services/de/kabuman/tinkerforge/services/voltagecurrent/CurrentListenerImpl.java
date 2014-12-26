package de.kabuman.tinkerforge.services.voltagecurrent;

import com.tinkerforge.BrickletVoltageCurrent;


/**
 * Implements the Interface of a Listener for the Bricklet Voltage Current  <br>
 */
public class CurrentListenerImpl implements BrickletVoltageCurrent.CurrentListener {

	// Parameter
	private VoltageConsumer consumer;
	
	private int oldCurrent = 0;
	
	
	/**
	 * Constructor  <br>
	 * 
	 * @param consumer
	 */
	public CurrentListenerImpl(
			VoltageConsumer consumer){
		this.consumer = consumer;
	}


	@Override
	public void current(int current) {
		consumer.currentValueChanged(current, oldCurrent);
		oldCurrent = current;
	}

}
