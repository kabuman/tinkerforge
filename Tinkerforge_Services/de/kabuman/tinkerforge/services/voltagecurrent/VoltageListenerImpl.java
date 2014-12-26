package de.kabuman.tinkerforge.services.voltagecurrent;

import com.tinkerforge.BrickletVoltageCurrent;


/**
 * Implements the Interface of a Listener for the Bricklet Voltage Current  <br>
 */
public class VoltageListenerImpl implements BrickletVoltageCurrent.VoltageListener {

	// Parameter
	private VoltageConsumer consumer;
	
	private int oldVoltage = 0;
	
	
	/**
	 * Constructor  <br>
	 * 
	 * @param consumer
	 */
	public VoltageListenerImpl(
			VoltageConsumer consumer){
		this.consumer = consumer;
	}


	@Override
	public void voltage(int voltage) {
		consumer.voltageValueChanged(voltage, oldVoltage);
		oldVoltage = voltage;
	}

}
