package de.kabuman.tinkerforge.rp6.services;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.StackServiceImpl;
import de.kabuman.tinkerforge.services.VehicleService;
import de.kabuman.tinkerforge.services.VehicleServiceImpl;

/**
 * Implementation RP6ServiceImpl
 * 
 * Instantiates and maps the devices
 * 
 * Provides further services arround stack and chibi network
 *
 */
public class RP6ServiceImpl implements RP6Service {
 
	// Devices RP6
	private BrickMaster brickMaster;
	private BrickDC motorLeft;
	private BrickDC motorRight;
	private BrickletDualRelay soundTrigger;
	private BrickletDualRelay commonTrigger;
	private BrickletCurrent12 motorLeftCurrent;
	private BrickletCurrent12 motorRightCurrent;
	
	// Common Services
	private VehicleService vehicleService;
	private StackService stackService;
	
	// Constants: Helper
	final short SHORT_ZERO = 0;
	final long CALLBACK_PERIOD_OFF = 0;


	// Constants: operation parameter for motors
	final short VELOCITY_NORMAL = 18000;
	final short PWM_FREQUENCY = 15000;
	final int   ACCELERATION = 0;

	/**
	 * Constructor
	 * Instantiates and configures the devices and services
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public RP6ServiceImpl() throws TimeoutException, IOException {
		// Devices
		brickMaster = (BrickMaster) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.MB2, "RP6", 13.5);
		motorLeft = (BrickDC) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DC2,"Motor left");
		motorRight  = (BrickDC) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DC3,"Motor right");
		soundTrigger = (BrickletDualRelay) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DR2,"Sound trigger");
		commonTrigger = (BrickletDualRelay) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.DR1,"Common trigger");
		motorLeftCurrent = (BrickletCurrent12) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.CU121,"Motor left current");
		motorRightCurrent = (BrickletCurrent12) ConfigServiceImpl.getInstance().createAndConnect(ConfigService.CU122,"Motor right current");
		
		// Common Services
		vehicleService = new VehicleServiceImpl();
		stackService = new StackServiceImpl(brickMaster);
		
		// Configuration
		configMotor(motorLeft);
		configMotor(motorRight);
		try {
			motorLeftCurrent.setCurrentCallbackPeriod(CALLBACK_PERIOD_OFF);
			motorRightCurrent.setCurrentCallbackPeriod(CALLBACK_PERIOD_OFF);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Configuration for motor device
	 */
	private void configMotor(BrickDC motor){
		try {
			motor.setPWMFrequency(PWM_FREQUENCY);
			motor.setDriveMode(SHORT_ZERO); 		
			motor.setAcceleration(ACCELERATION); 				
			motor.setVelocity(SHORT_ZERO);			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getMotorLeft()
	 */
	public BrickDC getMotorLeft() {
		return motorLeft;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getMotorLeftCurrent()
	 */
	public BrickletCurrent12 getMotorLeftCurrent() {
		return motorLeftCurrent;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getMotorRight()
	 */
	public BrickDC getMotorRight() {
		return motorRight;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getMotorRightCurrent()
	 */
	public BrickletCurrent12 getMotorRightCurrent() {
		return motorRightCurrent;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getSoundTrigger()
	 */
	public BrickletDualRelay getSoundTrigger() {
		return soundTrigger;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getCommonTrigger()
	 */
	public BrickletDualRelay getCommonTrigger() {
		return commonTrigger;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getVehicleService()
	 */
	public VehicleService getVehicleService() {
		return vehicleService;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.RP6Service#getStackService()
	 */
	public StackService getStackService() {
		return stackService;
	}

}
