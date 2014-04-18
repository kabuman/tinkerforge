package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickletLCD20x4;

public interface AlertUnitRemote extends AlertUnit {
	
	BrickletLCD20x4 getAlarmDisplay();

}
