package de.kabuman.tinkerforge.strobecontroller.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickServo;

import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.VehicleService;

/**
 * Interface LegoSchiffService
 */
public interface StrobeControllerService {
	
	/**
	 * @return BrickDC - the motor device
	 */
	public BrickDC getMotor();
	
	/**
	 * @return BrickletRotaryPoti - the frequence controller device
	 */
	public BrickServo getRotaryPoti();
	
	/**
	 * @return MotorAndRudderService - the motor and rudder service
	 */
	public BallAndLightService getMotorAndRudderService();
	
	/**
	 * @return StackService - service arround the stack and chibi network
	 */
	public StackService getStackService();

	/**
	 * @return VehicleService - service arround the vehicle
	 */
	public VehicleService getVehicleService();

}
