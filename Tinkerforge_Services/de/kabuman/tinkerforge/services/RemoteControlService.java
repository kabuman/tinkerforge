package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.TimeoutException;

public interface RemoteControlService {
	// Constants
//	public final short PINMASK_OFF = (short)0;
	public final short PINMASK_REFRESH = (short)((1 << 0));
	public final short PINMASK_SWITCH = (short)((1 << 1));
	public final short PINMASK_RESTART = (short)((1 << 2));
	public final short PINMASK_RESET = (short)((1 << 3));
	public final short PINMASK_KEEP = (short)((1 << 4));
	public final short PINMASK_TEST = (short)((1 << 5));
	public final short PINMASK_ALL = PINMASK_REFRESH | PINMASK_SWITCH | PINMASK_RESTART | PINMASK_RESET | PINMASK_KEEP | PINMASK_TEST;
	public final short PINMASK_REFRESH_RESTART_RESET = (short)(PINMASK_REFRESH | (1 << 2) | (1 << 3));
	public final short PINMASK_ACTIVE_KEEP = PINMASK_REFRESH | PINMASK_RESTART | PINMASK_RESET;
	
	// Devices
	public BrickletIO16 getIO16();
	public BrickletJoystick getJoystick();
	public BrickletLCD20x4 getLcd();
	public BrickletRotaryPoti getRotaryPoti();

	
	// LCD Updates
	public void refreshAllLcd(
			HostService hostService,
			VehicleService clientVehicleService,
			StackService clientStackService) throws TimeoutException;
	public void refreshMsgLcd(String msg);
	public void refreshMaxVelocity(VehicleService clientVehicleService);
	public void switchLCDBackLight();
	
	public void refreshObserverStart(
			HostService hostService,
			VehicleService clientVehicleService,
			StackService clientStackService);
	public void refreshObserverStop();


	// Reports
	public void reportIo16();


	// LCD Observer to observer the backlight
	public void checkAndStartDisplayObserver();
	public void checkAndStopDisplayObserver();
	
	public StackService getStackService();

}
