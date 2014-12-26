package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.units.Unit;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPullAlarm;

public class BarometerSensorItemImpl extends ItemImpl implements BarometerSensorItem, CallbackIntConsumer, ItemSourceToPullAlarm {

	// Parameter Values
	@SuppressWarnings("unused")
	private Unit unit;
	private BrickletBarometer barometerSensor = null;
	private long callbackPeriod;
	
	// State
	private boolean active = false;
	
	// Callback Listener 
	private BarometerCallbackListenerImpl callbackListener = null;
	
	private final long zero = 0;
	private int currentValue = 0; 
	
	/**
	 * @param unit - Alert- or Protect Unit
	 * @param barometerSensor - Bricklet Temperature
	 * @param callbackPeriod - in msec
	 */
	public BarometerSensorItemImpl(
			Unit unit,
			BrickletBarometer barometerSensor,
			long callbackPeriod){
		super();
		
		this.callbackPeriod = callbackPeriod;
		this.barometerSensor = barometerSensor;
		this.unit = unit;
		
		if (this.callbackPeriod == 0){
			throw new IllegalArgumentException("BarometerSensorItemImpl::Constructor: callbackPeriod = 0");
		}
		
		installSensor();
		
		if (unit == null){
			activateSensor();
		} else {
			deactivateSensor();
		}
	}
	
	private void installSensor(){
		
		callbackListener = new BarometerCallbackListenerImpl(this);
		
		barometerSensor.addAirPressureListener(callbackListener);
		
	}
	

	private void setCallbackPeriod(long callbackPeriod){
		if (callbackListener == null){
			return;
		}

		try {
			barometerSensor.setAirPressureCallbackPeriod(callbackPeriod);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}

	}
	
	public void activateSensor() {
		active = true;
		
//		try {
//			// set first value for this active period
////			barometerSensor.getAirPressure();
////			valueChanged((short)barometerSensor.getAirPressure());
//		} catch (TimeoutException e) {
//			e.printStackTrace();
//		} catch (NotConnectedException e) {
//			e.printStackTrace();
//		}
//		
		// activate the sensor
		setCallbackPeriod(callbackPeriod);
	}
	
	public void deactivateSensor(){
		active = false;
		setCallbackPeriod(zero);
	}
	
	public void removeListener(){
		barometerSensor.removeAirPressureListener(callbackListener);
	}
	
	public boolean isActive(){
		return active;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.CallbackConsumer#valueChanged(short)
	 */
//	public void valueChanged(short value) {
//		short newValue = (short)Math.round(value * 0.1);  // Round down: 2234 -> 223.4 -> 223  //   Round up: 2235 -> 223.5 -> 224  
//		currentValue = newValue;
//		regardValue(getCurrentValue());
//	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem#getCurrentValue()
	 */
	public double getCurrentValue() {
		if (!active){
			System.out.println("TemperatureSensor::getCurrentValue: Unit is not active!");
		}
		return (double) currentValue / 10;
	}

	@Override
	public void valueChanged(int value) {
//		int newValue = (short)Math.round(value);  // Round down: 2234 -> 223.4 -> 223  //   Round up: 2235 -> 223.5 -> 224
		
		// transfer current value to this instance 
		this.currentValue = value;
		
		// Maintain the statistic values
		regardValue(getCurrentValue());
	}

}
