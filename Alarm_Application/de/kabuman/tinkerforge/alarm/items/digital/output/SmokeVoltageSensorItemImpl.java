package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.items.digital.input.CallbackIntConsumer;
import de.kabuman.tinkerforge.alarm.items.digital.input.ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.SmokeVoltageCallbackListenerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.WaterCallbackListenerImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPullAlarm;

public class SmokeVoltageSensorItemImpl extends ItemImpl implements SmokeSensorItem, CallbackIntConsumer, ItemSourceToPullAlarm{

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletVoltageCurrent smokeSensor = null;
	private long debouncePeriod;
	private char option;
	private short threshold;

	// Callback Listener 
	private SmokeVoltageCallbackListenerImpl callbackListener = null;
	
	// others
	private static final char OFF_OPTION = 'x';
	private static final short SHORT_ZERO = 0;
	
	// state
	boolean active = false;

	public SmokeVoltageSensorItemImpl(
			ProtectUnit protectUnit, 
			BrickletVoltageCurrent smokeSensor,
			long debouncePeriod,
			char option,
			short threshold,
			boolean enable){
		super();
		
		this.protectUnit = protectUnit;
		this.smokeSensor = smokeSensor;
		this.debouncePeriod = debouncePeriod;
		this.option = option;
		this.threshold = threshold;
		
		installSensor();
		
		if (enable){
			activateSensor();
		}
	}

	public boolean isActive(){
		return active;
	}
	
	public void activateSensor(){
		active = true;
		setCallbackThreshold(option);
	}
	
	public void deactivateSensor(){
		active = false;
		setCallbackThreshold(OFF_OPTION);
	}
	
	private void setCallbackThreshold(char option){
		try {
			smokeSensor.setDebouncePeriod(debouncePeriod);
			smokeSensor.setVoltageCallbackThreshold(option, threshold, SHORT_ZERO);
    		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Rauchsensor (Volt)", "threshold gesetzt. option="+option+" threshold="+threshold);

		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	private void installSensor(){
		active = false;
		
		setCallbackThreshold(OFF_OPTION);
		
		callbackListener = new SmokeVoltageCallbackListenerImpl(this);
		
		smokeSensor.addVoltageReachedListener(callbackListener);
	}


	@Override
	public double getCurrentValue() {
		try {
			double current = (double) smokeSensor.getVoltage();
			regardValue(current);
			return current;
		} catch (TimeoutException | NotConnectedException e) {
			return 0;
		}
	}

	@Override
	public void valueChanged(int value) {
		// Take over as Maximum value
		regardValue(value);
    	protectUnit.activateAlert("Rauchsensor (Volt)", LogController.MSG_FIRE_VOLTAGE, AlertController.ALERT_TYPE_FIRE);
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Rauchsensor (Volt)", "voltage reached. voltage="+value+" threshold="+protectUnit.getCfgProtectUnit().getVcVoltageThreshold());
	}

}
