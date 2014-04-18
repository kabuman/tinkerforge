package de.kabuman.tinkerforge.strobecontroller.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.VelocityFindingLimitedByVoltage;

/**
 * Controls the motor and the rudder of the LegoSchiff:
 * - left/right navigation with the rudder
 * - velocity for the motor
 * - max. allowed velocity for the motor
 * 
 * Configures both devices with the required parameter by constructor
 * 
 * Converts the control signal (given by joystick on remote control) to 
 * control signals for the motor and the rudder and set them to the devices.
 * 
 * Converts the control signal (given by rotary potentiometer) to
 * the allowed maximum velocity for the motor and set them for the device.
 *
 * Provides a report which contains the used transformation between joystick 
 * and motor and rudder. 
 */
public class BallAndLightServiceImpl  implements BallAndLightService{
	
	// Devices
	BrickDC ledPowerSource;
	BrickletVoltage ledVoltmeter;
	BrickletRotaryPoti ledFrequencyControl;
	BrickletRotaryPoti discoballPowerControl;
	BrickDC discoballPowerSource;
	BrickletRotaryPoti ledPowerControl;
	
	// Configuration Parameter for LED Device
	short LED_POWER = 0;
	final short LED_PWM_FREQUENCY = 15000;
	final int   LED_ACCELERATION = 0;
	final short LED_DRIVE_MODE = 1; // 1: drive coast (recommended) / 0: drive brake
	
	// Configuration Parameter for LED Device
	final short MOTOR_POWER = -19000;
	final short MOTOR_POWER_NORMAL = -19000;
	final short MOTOR_PWM_FREQUENCY = 15000;
	final int   MOTOR_ACCELERATION = 20000;
	final short MOTOR_DRIVE_MODE = 1; // 1: drive coast (recommended) / 0: drive brake
	
	// flag
	Boolean inMotion = false;

	// Callback Periods
	final long CALLBACK_PERIOD_JOYSTICK = 100;
	final long CALLBACK_PERIOD_MAXVELOCITY = 100;
	final long CALLBACK_PERIOD_OFF = 0;

	// Rotary Poti: Fibrillation detecter
	short oldPos1 = 0;
	short oldPos2 = 0;
	short oldPos3 = 0;

	short frequency = 0;
	short position = 0;
	/**
	 * Basic Constructor - set to private to avoid the usage 
	 */
	@SuppressWarnings("unused")
	private BallAndLightServiceImpl(){
	}
	
	/**
	 * Constructor 
	 * 
	 * Configures the devices
	 * Set up the maximum velocity of light (LED) which may be used
	 * 
	 * @param ledPowerSource - the power supply for the LED light
	 * @param ledVoltmeter - the voltmeter to measure the voltage for the LED ligth
	 * @param ledPowerControl - the 
	 * @param ledFrequencyControl
	 * @param discoballPowerSource
	 * @param discoballPowerControl
	 */
	public BallAndLightServiceImpl(
			BrickDC ledPowerSource
			, BrickletVoltage ledVoltmeter
			, BrickletRotaryPoti ledPowerControl
			, BrickletRotaryPoti ledFrequencyControl
			, BrickDC discoballPowerSource
			, BrickletRotaryPoti discoballPowerControl
			){
		this.ledPowerSource = ledPowerSource;
		this.ledVoltmeter = ledVoltmeter;
		this.ledPowerControl = ledPowerControl;
		this.ledFrequencyControl = ledFrequencyControl;
		this.discoballPowerSource = discoballPowerSource;
		this.discoballPowerControl = discoballPowerControl;

		// LED Lamp Light
		checkBrickDC(ledPowerSource);
		configLedPowerSource();
		
		// Disco Ball Motor
		checkBrickDC(discoballPowerSource);
		configDiscoballPowerSource();
		enableDiscoballPowerSource();
		
		// Install Listener for LED Frequency Control
		configLedFrequencyControl();
	}

	/**
	 * Enable Discoball Powersource
	 */
	private void enableDiscoballPowerSource(){
		try {
			discoballPowerSource.enable();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Configuration for LED device (DC1)
	 */
	private void configLedPowerSource(){
		try {
			ledPowerSource.disable();
			ledPowerSource.setPWMFrequency(LED_PWM_FREQUENCY); 	
			ledPowerSource.setDriveMode(LED_DRIVE_MODE); 		// 1: Drive Coast (recommended) / 0: Drive Brake
			ledPowerSource.setAcceleration(LED_ACCELERATION);
			
			VelocityFindingLimitedByVoltage velocityFindingLimitedByVoltage = new VelocityFindingLimitedByVoltage(ledPowerSource, ledVoltmeter);
			LED_POWER = velocityFindingLimitedByVoltage.findVelocity(4500, (short)-10);
			ledPowerSource.setVelocity(LED_POWER);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Configuration for Disco Ball Motor device (DC2)
	 */
	private void configDiscoballPowerSource(){
		try {
			discoballPowerSource.setPWMFrequency(MOTOR_PWM_FREQUENCY);
			discoballPowerSource.setDriveMode(MOTOR_DRIVE_MODE); 		// 1: Drive Coast (recommended) / 0: Drive Brake
			discoballPowerSource.setAcceleration(MOTOR_ACCELERATION); 				
			discoballPowerSource.setVelocity(MOTOR_POWER);			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}
	
	/**
	 * Checks if the motor is not null
	 * Throws IllegalArgumentException if they are.
	 * 
	 * @param motor - BrickDC for motor
	 * 
	 */
	private void checkBrickDC(BrickDC motor){
		if (motor == null){
			throw new IllegalArgumentException("BrickDC for motor is NULL");
		}
	}
	
	private short calculateFrequency(short position){
		return (short)((position/2) + 55);
	}
	
	
	private short retrievePosition(){
		try {
			try {
				return ledFrequencyControl.getPosition();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public short getPosition(){
		return position;
	}
	
	/**
	 * Maximum Velocity Limiter Control
	 * Limits the max velocity choosable by joystick
	 * The limit is set via rotary potentiometer
	 */
	private void configLedFrequencyControl(){
		
		// Detect current rotary position and calculate the frequency
		position = retrievePosition();
		frequency = calculateFrequency(position);
		
		// Set callback period
		try {
			ledFrequencyControl.setPositionCallbackPeriod(CALLBACK_PERIOD_MAXVELOCITY);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Add and implement position listener (called if position changes)
		ledFrequencyControl.addPositionListener(new BrickletRotaryPoti.PositionListener() {
			public void position(short position) {
				if (oldPos1 == oldPos3 && oldPos2 == position){
					// fibrillation detected:
//					System.out.println("=> Erkannt: Frequency-Regler flimmert. Unterdrückt.");
					
				} else {
					
					// do the job
					System.out.println("=> Erkannt: Frequency-Regler wird bewegt.");
					
					// 
					setPosition(position);
					frequency = calculateFrequency(position);
				}
				
				// Maintain Fillibration Detecter
				oldPos1 = oldPos2;
				oldPos2 = oldPos3;
				oldPos3 = position;
				
			}
		});
	}
	
	private void setPosition(short position){
		this.position = position;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#setVelocity(short velocity)
	 */
	public void setVelocity(short velocity){
			try {
				ledPowerSource.setVelocity(velocity);
				ledPowerSource.enable();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public short getFrequency(){
		return frequency;
	}
	
	public short getLEDVelocity(){
		return LED_POWER;
	}
}
