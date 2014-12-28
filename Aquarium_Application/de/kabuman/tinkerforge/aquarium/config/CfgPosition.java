package de.kabuman.tinkerforge.aquarium.config;

import java.util.ArrayList;
import java.util.List;

public class CfgPosition {

	List<Short> positionList = new ArrayList<Short>(3);
	short servoId;
	
	CfgPosition(short servoId, Short... position){
		this.servoId = servoId;
		
		for (int i = 0; i < position.length; i++) {
			this.positionList.add(position[i]);
		}
	}

	public List<Short> getPositionList() {
		return positionList;
	}

	public short getServoId() {
		return servoId;
	}
	
}
