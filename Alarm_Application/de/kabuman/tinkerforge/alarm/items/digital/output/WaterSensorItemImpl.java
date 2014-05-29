package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.CallbackIntConsumer;
import de.kabuman.tinkerforge.alarm.items.digital.input.ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.WaterCallbackListenerImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;

public class WaterSensorItemImpl extends ItemImpl implements WaterSensorItem, CallbackIntConsumer{

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletAnalogIn waterSensor = null;
	private long debouncePeriod;
	private char option;
	private short threshold;

	// Callback Listener 
	private WaterCallbackListenerImpl callbackListener = null;
	
	// others
	private static final char OFF_OPTION = 'x';
	private static final short SHORT_ZERO = 0;
	private static final short AVERAGE_LENGTH = 255; // 0: without average (peaks!); 1-255: with average
	
	// state
	boolean active = false;

	public WaterSensorItemImpl(
			ProtectUnit protectUnit, 
			BrickletAnalogIn waterSensor,
			long debouncePeriod,
			char option,
			short threshold,
			boolean enable){
		super();
		
		this.protectUnit = protectUnit;
		this.waterSensor = waterSensor;
		this.debouncePeriod = debouncePeriod;
		this.option = option;
		this.threshold = threshold;
		
		installWaterSensor();
		
		if (enable){
			activateSensor();
		}
	}

	public boolean isActive(){
		return active;
	}
	
	public void activateSensor(){
		active = true;
		setAverageLength(AVERAGE_LENGTH);
		setCallbackThreshold(option);
	}
	
	public void deactivateSensor(){
		active = false;
		setCallbackThreshold(OFF_OPTION);
	}
	
	private void setCallbackThreshold(char option){
		try {
			waterSensor.setDebouncePeriod(debouncePeriod);
			waterSensor.setVoltageCallbackThreshold(option, threshold, SHORT_ZERO);
    		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Wassersensor", "threshold gesetzt. option="+option+" threshold="+threshold);

		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	private void setAverageLength(short averageLength){
		try {
			waterSensor.setAveraging(averageLength);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	

	private void installWaterSensor(){
		active = false;
		
		setCallbackThreshold(OFF_OPTION);
		
		callbackListener = new WaterCallbackListenerImpl(this);
		
		waterSensor.addVoltageReachedListener(callbackListener);
		
//		waterSensor.addVoltageReachedListener(new VoltageReachedListener() {
//			public void voltageReached(int voltage) {
//            	protectUnit.activateAlert("Wassersensor", LogController.MSG_WATER, AlertController.ALERT_TYPE_WATER);
//        		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Wassersensor", "voltage reached. voltage="+voltage);
//			}
//		});
	}

	@Override
	public double getCurrentValue() {
		try {
			double current = (double) waterSensor.getVoltage();
			regardValue(current);
			return current;
		} catch (TimeoutException | NotConnectedException e) {
			return 0;
		}
	}

	@Override
	public void valueChanged(int value) {
    	protectUnit.activateAlert("Wassersensor", LogController.MSG_WATER, AlertController.ALERT_TYPE_WATER);
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Wassersensor", "voltage reached. voltage="+value);
	}

}
