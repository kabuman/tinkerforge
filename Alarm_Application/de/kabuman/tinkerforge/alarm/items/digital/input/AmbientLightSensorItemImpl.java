package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPullAlarm;

public class AmbientLightSensorItemImpl extends ItemImpl implements AmbientLightSensorItem, CallbackIntConsumer, ItemSourceToPullAlarm  {

	// Parameter Values
	private Unit unit;
	private BrickletAmbientLight ambientLightSensor = null;
	short threshold;
	private long callbackPeriod;
	
	// State
	private boolean active = false;
	
	// Callback Listener 
	private AmbientLightCallbackListenerImpl callbackListener = null;

	private final long zero = 0;

	
	/**
	 * @param unit - Alert- or Protect Unit
	 * @param temperatureSensor - Bricklet Temperature
	 * @param callbackPeriod - in msec
	 */
	public AmbientLightSensorItemImpl(
			Unit unit,
			BrickletAmbientLight ambientLightSensor,
			short threshold,
			long callbackPeriod){
		
		this.threshold = threshold;
		this.callbackPeriod = callbackPeriod;
		this.ambientLightSensor = ambientLightSensor;
		this.unit = unit;
		
		installSensor();
	}
	
	
	private void installSensor(){
		deactivateSensor();
		callbackListener = new AmbientLightCallbackListenerImpl(this);
		ambientLightSensor.addIlluminanceReachedListener(callbackListener);
		setThreshold('<', threshold);
	}
	

	private synchronized void setCallbackPeriod(long callbackPeriod){
		try {
			ambientLightSensor.setIlluminanceCallbackPeriod(callbackPeriod);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void activateSensor() {
		active = true;

		// activate the sensor
		setCallbackPeriod(this.callbackPeriod);
	}
	
	
	private synchronized void setThreshold(char operator, short threshold) {
		
		//from doku:
		// Configure threshold for "greater than 200 Lux" (unit is Lux/10)
		// al.setIlluminanceCallbackThreshold('>', (short)(200*10), (short)0);

		
		try {
			ambientLightSensor.setIlluminanceCallbackThreshold(operator, threshold, (short)0);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void deactivateSensor(){
		active = false;
		setCallbackPeriod(zero);
	}
	
	
	public void removeListener(){
		ambientLightSensor.removeIlluminanceReachedListener(callbackListener);
	}
	
	
	public boolean isActive(){
		return active;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.CallbackConsumer#valueChanged(short)
	 */
	public synchronized void valueChanged(int value) {
		deactivateSensor();
		if (ScreenControllerImpl.getInstance() == null){
//			System.out.println("AmbientLightSensorItemImpl::valueChanged: no ScreenController Instance");
			activateSensor();
			return;
		}
		

		// Flip Flop
		if (value < threshold){
			// Night Mode: switch off the backlight if its on
			if (ScreenControllerImpl.getInstance().isBacklightOn()){
				ScreenControllerImpl.getInstance().backlightOff();
				setThreshold('>', threshold);
				LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "AmbientLightSensorItem", "LCD Backlight switched off. value="+value+" < threshold="+threshold);
			}
		} else {
			// Day Mode: switch on the backlight if its off
			if (!ScreenControllerImpl.getInstance().isBacklightOn()){
				ScreenControllerImpl.getInstance().backlightOn();
				setThreshold('<', threshold);
				LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "AmbientLightSensorItem", "LCD Backlight switched on. value="+value+" > threshold="+threshold);
			}
		}
		activateSensor();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem#getCurrentValue()
	 */
	public double getCurrentValue() {
		if (!active){
			System.out.println("AmbientLightSensor::getCurrentValue: Unit is not active!");
		}

		try {
			return (double) ambientLightSensor.getIlluminance();
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
			return 0.00;
		}
	}

}
