package de.kabuman.tinkerforge.legoschiff.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickServo;

import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.VehicleService;

/**
 * Interface LegoSchiffService
 */
public interface LegoSchiffService {
	
	/**
	 * @return BrickDC - the motor device
	 */
	public BrickDC getMotor();
	
	/**
	 * @return BrickServo - the rudder device
	 */
	public BrickServo getRudder();
	
	/**
	 * @return MotorAndRudderService - the motor and rudder service
	 */
	public MotorAndRudderService getMotorAndRudderService();
	
	/**
	 * @return StackService - service arround the stack and chibi network
	 */
	public StackService getStackService();

	/**
	 * @return VehicleService - service arround the vehicle
	 */
	public VehicleService getVehicleService();

}
