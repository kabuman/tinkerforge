package de.kabuman.tinkerforge.alarm.controller;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.tinkerforge.alarm.AlarmAppl;
import de.kabuman.tinkerforge.alarm.threads.AliveObserverImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;

/**
 * Reset Controller
 * - triggers the reset() of all units (AlertUnits & ProtectionUnits)
 */
public class ResetControllerImpl implements ResetController {
	
	private static ResetControllerImpl instance = null;
	

	private List<Unit> unitList = new ArrayList<Unit>();
	private AlarmAppl alarmAppl;
	

	public ResetControllerImpl(AlarmAppl alarmAppl, List<Unit> unitList) {
		this.alarmAppl = alarmAppl;
		this.unitList = unitList;
	}

	public void reset(){
		AlertControllerImpl.getInstance().deactivateAlert();
		AlertControllerImpl.getInstance().setAlertOccurred(false);
		for (int i = 0; i < unitList.size(); i++) {
			unitList.get(i).reset();
		}
	}

	public static ResetControllerImpl getNewInstance(AlarmAppl alarmAppl,List<Unit> unitList){
		instance = new ResetControllerImpl(alarmAppl,unitList);
		return instance;
	}
	
	public static ResetControllerImpl getInstance() {
		return instance;
	}

	public void restart() {
		LogControllerImpl.getInstance().createUserLogMessage("AU Remote","Restart-Taster",LogController.MSG_RESTART);
		alarmAppl.launcher(true);
	}

}
