package de.kabuman.tinkerforge.rp6.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.BrickletDualRelay;

import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.VehicleService;

public interface RP6Service {
	public BrickDC getMotorLeft();

	public BrickletCurrent12 getMotorLeftCurrent();

	public BrickDC getMotorRight();

	public BrickletCurrent12 getMotorRightCurrent();

	public BrickletDualRelay getSoundTrigger();

	public BrickletDualRelay getCommonTrigger();
	
	public VehicleService getVehicleService();

	public StackService getStackService();

}
