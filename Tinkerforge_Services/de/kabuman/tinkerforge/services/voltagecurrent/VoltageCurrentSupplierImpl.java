package de.kabuman.tinkerforge.services.voltagecurrent;

import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Provides Bricklet Rotary Encoder  <br>
 * for Applications.  <br>
 *  <br>
 * The Application must implement the Interface "RotaryEncoderConsumer".
 */
public class VoltageCurrentSupplierImpl implements VoltageConsumer{
	
	// Parameter Values
	private VoltageConsumer consumer;
	private BrickletVoltageCurrent tfVoltageCurrent = null;
	
	int callbackPeriod = 100;

	// State
	private boolean active = false;

	// Services
	private VoltageListenerImpl voltageListener;
	private CurrentListenerImpl currentListener;

	
	/**
	 * Constructor  <br>
	 * Creates this object but does not activate it. <br>
	 * Use method activateRotaryEncoder to activate it. <br>
	 *  <br>
	 * @param consumer - the caller of this object
	 * @param tfRotaryEncoder - the bricklet rotary encoder
	 */
	public VoltageCurrentSupplierImpl(
			VoltageConsumer consumer,
			BrickletVoltageCurrent tfVoltageCurrent) {

		this.consumer = consumer;
		this.tfVoltageCurrent = tfVoltageCurrent;

		installListener();
		deactivate();
	}

	
	/**
	 * Set the callback period for the listener  <br>
	 * If set to zero the listener is deactivated.
	 * 
	 * @param value - int: the callback period
	 */
	private void setCallbackPeriod(int value){
		try {
			tfVoltageCurrent.setVoltageCallbackPeriod(value);
			tfVoltageCurrent.setCurrentCallbackPeriod(value);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Deactivate the Rotary Encoder  <br>
	 * After this no event will be triggered anymore. 
	 */
	public void deactivate() {
		active = false;
		setCallbackPeriod(0);
	}

	
	/**
	 * Returns the supplier state
	 * 
	 * @return boolean - true/false
	 */
	public boolean isActive() {
		return active;
	}

	
	/**
	 * Installs all required listener
	 */
	private void installListener() {
		voltageListener = new VoltageListenerImpl(this);
		tfVoltageCurrent.addVoltageListener(voltageListener);

		currentListener = new CurrentListenerImpl(this);
		tfVoltageCurrent.addCurrentListener(currentListener);
	}
	

	/**
	 * Activates the supplier  <br>
	 * Set the count to zero 
	 */
	public synchronized void activate() {
		active = true;
		setCallbackPeriod(callbackPeriod);
	}


	/**
	 * Returns the current voltage  <br>
	 * 
	 * @return Integer - the voltage in milli seconds
	 */
	public Integer getVoltage(){
		try {
			return tfVoltageCurrent.getVoltage();
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Replaces the bricklet voltage current <br>
	 * - installs the listener again
	 * - keeps the supplier state (active or not)
	 * @param tfVoltageCurrent - the new Voltage Current Bricklet
	 */
	public void replace(BrickletVoltageCurrent tfVoltageCurrent){
		this.tfVoltageCurrent = tfVoltageCurrent;
		
		installListener();

		if (active){
			activate();
		} else {
			deactivate();
		}
	}


	@Override
	public void voltageValueChanged(int newValue, int oldValue) {
		if (newValue != oldValue){
			consumer.voltageValueChanged(newValue, oldValue);
		}
	}


	@Override
	public void currentValueChanged(int newValue, int oldValue) {
		if (newValue != oldValue){
			consumer.currentValueChanged(newValue, oldValue);
		}
	}


}
