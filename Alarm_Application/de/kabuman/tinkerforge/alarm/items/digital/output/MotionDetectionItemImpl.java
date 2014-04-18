package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletMotionDetector;

import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;

public class MotionDetectionItemImpl implements MotionDetectionItem {

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletMotionDetector motionDetection = null;
	
	// state
	boolean active = false;

	public MotionDetectionItemImpl(
			ProtectUnit protectUnit, 
			BrickletMotionDetector motionDetection){
		
		this.protectUnit = protectUnit;
		this.motionDetection = motionDetection;
		
		installMotionDetection();
	}

	public boolean isActive(){
		return active;
	}
	
	public void activateMotionDetection(){
		active = true;
	}
	
	public void deactivateMotionDetection(){
		active = false;
	}
	
	private void installMotionDetection(){
		active = false;
		
		motionDetection.addMotionDetectedListener(new BrickletMotionDetector.MotionDetectedListener() {
			public void motionDetected() {
				if (active){
	            	protectUnit.activateAlert("Bewegungsmelder",LogController.MSG_MOTION, AlertController.ALERT_TYPE_INTRUSION);
	        		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Motion Detection", "motion detected");
				}
			}
		});
	}

}
