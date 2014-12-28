package de.kabuman.tinkerforge.aquarium.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CfgTimer {

	List<Date> timerList = new ArrayList<Date>(3);
	short servoId;
	
	CfgTimer(short servoId, Date... foodTimer){
		this.servoId = servoId;
		
		for (int i = 0; i < foodTimer.length; i++) {
			this.timerList.add(foodTimer[i]);
		}
	}

	public List<Date> getTimerList() {
		return timerList;
	}

	public short getServoId() {
		return servoId;
	}
	
}
