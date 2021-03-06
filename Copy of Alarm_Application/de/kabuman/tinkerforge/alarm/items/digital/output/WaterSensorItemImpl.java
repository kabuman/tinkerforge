package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletAnalogIn.VoltageReachedListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;

public class WaterSensorItemImpl implements WaterSensorItem {

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletAnalogIn waterSensor = null;
	private long debouncePeriod;
	private char option;
	private short threshold;

	// others
	private final char offOption = 'x';
	private final short shortZero = 0;
	
	// state
	boolean active = false;

	public WaterSensorItemImpl(
			ProtectUnit protectUnit, 
			BrickletAnalogIn waterSensor,
			long debouncePeriod,
			char option,
			short threshold,
			boolean enable){
		
		this.protectUnit = protectUnit;
		this.waterSensor = waterSensor;
		this.debouncePeriod = debouncePeriod;
		this.option = option;
		this.threshold = threshold;
		
		installWaterSensor();
		
		if (enable){
			activateWaterSensor();
		}
	}

	public boolean isActive(){
		return active;
	}
	
	public void activateWaterSensor(){
		active = true;
		setCallbackThreshold(option);
	}
	
	public void deactivateWaterSensor(){
		active = false;
		setCallbackThreshold(offOption);
	}
	
	private void setCallbackThreshold(char option){
		try {
			waterSensor.setDebouncePeriod(debouncePeriod);
			waterSensor.setVoltageCallbackThreshold(option, threshold, shortZero);
    		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Wassersensor", "threshold gesetzt. option="+option+" threshold="+threshold);

		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	

	private void installWaterSensor(){
		active = false;
		
		setCallbackThreshold(offOption);

		waterSensor.addVoltageReachedListener(new VoltageReachedListener() {
			public void voltageReached(int voltage) {
            	protectUnit.activateAlert("Wassersensor", LogController.MSG_WATER, AlertController.ALERT_TYPE_WATER);
        		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Wassersensor", "voltage reached. voltage="+voltage);
			}
		});
	}

}
