package de.kabuman.tinkerforge.aquarium.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;

public interface AquariumUnit {
	public BrickMaster getBrickMaster();
	
	
	public BrickServo getBrickServo();
	
	
	public String getUnitName();
	
	
	public void reconnect();
	
	
	public boolean isConnected();

}
