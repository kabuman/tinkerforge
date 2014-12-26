package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;

public abstract class AbstractUnit {
	
	// ScreenController
	private Object previousInstance;
	
	protected void storePreviousInstance(Object previousInstance){
		this.previousInstance = previousInstance;  
	}
	
	protected boolean replacePreviousInstance(Object newInstance){
		if (previousInstance != null && ScreenControllerImpl.getInstance() != null){
			ScreenControllerImpl.getInstance().replaceItemSource(previousInstance, newInstance);
			return true;
		} else {
			return false;
		}
	}
	

	protected void logWifiInfo(BrickMaster brickmaster, String unitName){
		try {
			if (brickmaster.isWifiPresent()){
				if (LogControllerImpl.getInstance() != null){
//					LogControllerImpl.getInstance().createTechnicalLogMessage(unitName, "tfStackReConnected", "Wifi master lcd replaced for screen controller "+brickmaster.g);

				}
				
			}
		} catch (TimeoutException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
