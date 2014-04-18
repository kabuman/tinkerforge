package de.kabuman.tinkerforge.legoschiff.services;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.StackServiceImpl;
import de.kabuman.tinkerforge.services.VehicleService;
import de.kabuman.tinkerforge.services.VehicleServiceImpl;

/**
 * Implementation LegoSchiffServiceImpl
 * 
 * Instantiates and maps the devices
 * 
 * Provides further services arround stack and chibi network
 *
 */
public class LegoSchiffServiceImpl implements LegoSchiffService {

	// Devices LegoSchiff
	private BrickMaster brickMaster;
	private BrickDC motor;
	private BrickServo rudder;
	
	// Common Services
	private VehicleService vehicleService;
	private StackService stackService;

	// Specific Services
	private MotorAndRudderService motorAndRudderService;
	
	/**
	 * Constructor
	 * Instantiates and configures the devices and services
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public LegoSchiffServiceImpl() throws TimeoutException, IOException{
		// Devices
		brickMaster = (BrickMaster) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.MB4, "LegoSchiff Master", 6.5);
		motor = (BrickDC) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DC1, "LegoSchiff DC Motor Control");
		rudder = (BrickServo) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.SV1, "LegoSchiff Servo Ruder Control");
		
		// Common Services
		vehicleService = new VehicleServiceImpl();
		stackService = new StackServiceImpl(brickMaster);

		// Specific Services
		motorAndRudderService = new MotorAndRudderServiceImpl((short)32700, motor, rudder);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getMotor()
	 */
	public BrickDC getMotor() {
		return motor;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getRudder()
	 */
	public BrickServo getRudder() {
		return rudder;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getMotorAndRudderService()
	 */
	public MotorAndRudderService getMotorAndRudderService() {
		return motorAndRudderService;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getStackService()
	 */
	public StackService getStackService() {
		return stackService;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getVehicleService()
	 */
	public VehicleService getVehicleService() {
		return vehicleService;
	}

}
