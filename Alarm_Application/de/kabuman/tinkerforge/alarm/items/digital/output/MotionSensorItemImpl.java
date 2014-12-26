package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceReachedListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;

public class MotionSensorItemImpl implements MotionSensorItem {

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletDistanceIR motionSensor = null;
	private long debouncePeriod;
	private char option;
	private short distance;

	// others
	private final char offOption = 'x';
	private final short shortZero = 0;
	
	// state
	boolean active = false;

	public MotionSensorItemImpl(
			ProtectUnit protectUnit, 
			BrickletDistanceIR motionSensor,
			long debouncePeriod,
			char option,
			short distance){
		
		this.protectUnit = protectUnit;
		this.motionSensor = motionSensor;
		this.debouncePeriod = debouncePeriod;
		this.option = option;
		this.distance = distance;
		
		installMotionSensor();
	}

	public boolean isActive(){
		return active;
	}
	
	public void activateMotionSensor(){
		active = true;
		setCallbackThreshold(option);
	}
	
	public void deactivateMotionSensor(){
		active = false;
		setCallbackThreshold(offOption);
	}
	
	private void setCallbackThreshold(char option){
		try {
			motionSensor.setDebouncePeriod(debouncePeriod);
    		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Motion Sensor", "debounce period is set to="+debouncePeriod);
			motionSensor.setDistanceCallbackThreshold(option, distance, shortZero);
    		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Motion Sensor", "distance threshold gesetzt. option="+option + " threshold="+distance);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	

	private void installMotionSensor(){
		active = false;
		
		setCallbackThreshold(offOption);

		motionSensor.addDistanceReachedListener(new DistanceReachedListener() {
            public synchronized void distanceReached(int distance) {
            	protectUnit.activateAlert("Bewegungssensor",LogController.MSG_MOTION, AlertController.ALERT_TYPE_INTRUSION);
        		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Motion Sensor", "distance reached. distance="+distance);
            }
        });
	}

}
