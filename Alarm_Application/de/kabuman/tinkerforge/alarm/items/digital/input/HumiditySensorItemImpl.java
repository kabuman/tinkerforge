package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;

public class HumiditySensorItemImpl implements HumiditySensorItem, CallbackIntConsumer {

	// Parameter Values
	private Unit unit;
	private BrickletHumidity humiditySensor = null;
	private long callbackPeriod;
	
	// State
	private boolean active = false;
	
	// Callback Listener 
	private HumidityCallbackListenerImpl callbackListener = null;

	private final long zero = 0;
	private int currentValue = 0; 
	
	/**
	 * @param unit - Alert- or Protect Unit
	 * @param temperatureSensor - Bricklet Temperature
	 * @param callbackPeriod - in msec
	 */
	public HumiditySensorItemImpl(
			Unit unit,
			BrickletHumidity humiditySensor,
			long callbackPeriod){
		
		this.callbackPeriod = callbackPeriod;
		this.humiditySensor = humiditySensor;
		this.unit = unit;
		
		installSensor();
		
	}
	
	private void installSensor(){
		deactivateSensor();
		
		callbackListener = new HumidityCallbackListenerImpl(this);
		
		humiditySensor.addHumidityListener(callbackListener);
		
	}
	

	private void setCallbackPeriod(long callbackPeriod){
		try {
			humiditySensor.setHumidityCallbackPeriod(callbackPeriod);
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
			valueChanged(humiditySensor.getHumidity());
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
		humiditySensor.removeHumidityListener(callbackListener);
	}
	
	public boolean isActive(){
		return active;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.CallbackConsumer#valueChanged(short)
	 */
	public void valueChanged(int value) {
		int newValue = (int)Math.round(value * 0.1);  // Round down: 354 -> 35.4 -> 35  //   Round up: 355 -> 35.5 -> 36  

//		if (currentValue != newValue){
//			LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "Humidity Sensor", "Reported value = " + value + "  Prepared value = "+newValue);
//		}

		currentValue = newValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

}
