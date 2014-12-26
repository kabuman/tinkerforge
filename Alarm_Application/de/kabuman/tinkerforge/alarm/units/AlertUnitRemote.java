package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickletLCD20x4;

import de.kabuman.tinkerforge.alarm.items.digital.input.RcOnOffSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcQuietSwitchItemImpl;

public interface AlertUnitRemote extends AlertUnit {
	
	BrickletLCD20x4 getAlarmDisplay();
	
	RcOnOffSwitchItemImpl getRcOnOffSwitchItemImpl();
	
	RcQuietSwitchItemImpl getRcQuietSwitchItemImpl();

}
