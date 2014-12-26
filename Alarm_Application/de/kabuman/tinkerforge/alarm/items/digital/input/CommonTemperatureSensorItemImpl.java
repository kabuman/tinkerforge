package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.units.Unit;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPullAlarm;

public class CommonTemperatureSensorItemImpl extends ItemImpl implements TemperatureSensorItem, CallbackShortConsumer, ItemSourceToPullAlarm {

	// Parameter Values
	@SuppressWarnings("unused")
	private BrickletTemperature temperatureSensor = null;
	private long callbackPeriod;
	
	// State
	private boolean active = false;
	
	// Callback Listener 
	private TemperatureCallbackListenerImpl callbackListener = null;
	
	private final long zero = 0;
	private short currentValue = 0; 
	
	/**
	 * @param unit - Alert- or Protect Unit
	 * @param temperatureSensor - Bricklet Temperature
	 * @param callbackPeriod - in msec
	 */
	public CommonTemperatureSensorItemImpl(
			BrickletTemperature temperatureSensor,
			long callbackPeriod){
		super();
		
		this.callbackPeriod = callbackPeriod;
		this.temperatureSensor = temperatureSensor;
		
		installSensor();
		
	}
	
	private void installSensor(){
		deactivateSensor();
		
		callbackListener = new TemperatureCallbackListenerImpl(this);
		
		temperatureSensor.addTemperatureListener(callbackListener);
		
	}
	

	private void setCallbackPeriod(long callbackPeriod){
		try {
			temperatureSensor.setTemperatureCallbackPeriod(callbackPeriod);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	public void activateSensor() {
		active = true;
		
		try {
			// set first value for this active period
			valueChanged(temperatureSensor.getTemperature());
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		
		// activate the sensor
		setCallbackPeriod(this.callbackPeriod);
	}
	
	public void deactivateSensor(){
		active = false;
		setCallbackPeriod(zero);
	}
	
	public void removeListener(){
		temperatureSensor.removeTemperatureListener(callbackListener);
	}
	
	public boolean isActive(){
		return active;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.CallbackConsumer#valueChanged(short)
	 */
	public void valueChanged(short value) {
		short newValue = (short)Math.round(value * 0.1);  // Round down: 2234 -> 223.4 -> 223  //   Round up: 2235 -> 223.5 -> 224  
		currentValue = newValue;
		regardValue(getCurrentValue());
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem#getCurrentValue()
	 */
	public double getCurrentValue() {
		if (!active){
			System.out.println("TemperatureSensor::getCurrentValue: Unit is not active!");
		}
		return (double) currentValue / 10;
	}

}
