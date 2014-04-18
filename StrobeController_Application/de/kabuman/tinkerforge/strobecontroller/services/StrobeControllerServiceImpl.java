package de.kabuman.tinkerforge.strobecontroller.services;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.StackServiceImpl;
import de.kabuman.tinkerforge.services.VehicleService;
import de.kabuman.tinkerforge.services.VehicleServiceImpl;

/**
 * Implementation StrobeControllerServiceImpl
 * 
 * Instantiates and maps the devices
 * 
 */
public class StrobeControllerServiceImpl implements StrobeControllerService {

	// Devices LegoSchiff
	private BrickMaster brickMaster;
	private BrickDC ledPowerSource;
	private BrickletVoltage ledVoltmeter;
	private BrickDC discoballPowerSource;
	private BrickletRotaryPoti ledPowerControl;
	private BrickletRotaryPoti discoballPowerControl;
	private BrickletRotaryPoti ledFrequencyControl;

	
	// Common Services
	private VehicleService vehicleService;
	private StackService stackService;

	// Specific Services
	private BallAndLightService motorAndRudderService;
	
	/**
	 * Constructor
	 * Instantiates and configures the devices and services
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public StrobeControllerServiceImpl() throws TimeoutException, IOException{
		// Devices
		brickMaster = (BrickMaster) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.MB4, "StrobeController Master", 6.5);
		ledPowerSource = (BrickDC) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DC1, "StrobeController DC Motor Control");
		ledVoltmeter = (BrickletVoltage) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.VO1, "StrobeController LED Voltage");
		discoballPowerSource = (BrickDC) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DC2, "StrobeController DC Motor Control");
		ledPowerControl = (BrickletRotaryPoti) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.RP2, "StrobeController RotaryPoti LED Power Control (Frequency)");
		discoballPowerControl = (BrickletRotaryPoti) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.RP3, "StrobeController RotaryPoti DiscoBall Power Control");
		ledFrequencyControl = (BrickletRotaryPoti) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.RP4, "StrobeController RotaryPoti LED Power Control (Frequency)");
		
		// Common Services
		vehicleService = new VehicleServiceImpl();
		stackService = new StackServiceImpl(brickMaster);

		// Specific Services
		motorAndRudderService = new BallAndLightServiceImpl(
				ledPowerSource
				, ledVoltmeter
				, ledPowerControl
				, ledFrequencyControl
				, discoballPowerSource
				, discoballPowerControl);
		
//		// Specific configs
//		motor.setAcceleration(0);
//		motor.setDriveMode((short) 1); // drive coast=1
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getMotor()
	 */
	public BrickDC getMotor() {
		return ledPowerSource;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.LegoSchiffService#getMotorAndRudderService()
	 */
	public BallAndLightService getMotorAndRudderService() {
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

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.strobecontroller.services.StrobeControllerService#getRotaryPoti()
	 */
	public BrickServo getRotaryPoti() {
		// TODO Auto-generated method stub
		return null;
	}

}
