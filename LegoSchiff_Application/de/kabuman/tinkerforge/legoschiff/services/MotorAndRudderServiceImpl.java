package de.kabuman.tinkerforge.legoschiff.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickServo;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.MotorAbstractService;

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
public class MotorAndRudderServiceImpl  extends MotorAbstractService implements MotorAndRudderService{
	
	// Devices
	BrickDC motor;
	BrickServo rudder;
	
	// Configuration Paramter for Servo Device: Dymond DS90
	final short SERVO_NUM = 0;
	final int SERVO_VOLTAGE = 6000;
	final int SERVO_PERIOD = 19500;
	final int SERVO_ACCELERATION = 65535;
	final int SERVO_VELOCITY = 65535;
	final short SERVO_MIN_DEGREE = -3000;
	final short SERVO_MAX_DEGREE = 3000;
	final int SERVO_MIN_PULSE = 1000;
	final int SERVO_MAX_PULSE = 2000;
	final short SERVO_START_POSITION = 0;
	
	// Configuration Parameter for Motor Device
	final short MOTOR_VELOCITY = 0;
	final short VELOCITY_NORMAL = 18000;
	final short MOTOR_PWM_FREQUENCY = 15000;
	final int   MOTOR_ACCELERATION = 0;
	final short MOTOR_DRIVE_MODE = 1; // 1: drive coast (recommended) / 0: drive brake

	short velocityMax = 0;	// max velocity 
	short velocityInt = 0;	// used velociytMax internal (velocityMax / 100)

	// old joystick position values for motor and rudder
	int xOld = 0;
	int yOld = 0;
	
	// flag
	Boolean inMotion = false;
	/**
	 * Basic Constructor - set to private to avoid the usage 
	 */
	@SuppressWarnings("unused")
	private MotorAndRudderServiceImpl(){
	}
	
	/**
	 * Constructor 
	 * 
	 * Configures the both devices: motor (BrickDC) and rudder (BrickServo)
	 * Set up the maximum velocity which may be used
	 * 
	 * @param velocityMax - the maximum allowed velocity
	 * @param BrickDC - the motor device
	 * @param BrickServo - the rudder device
	 */
	public MotorAndRudderServiceImpl(short velocityMax, BrickDC motor, BrickServo rudder){
		this.motor = motor;
		this.rudder = rudder;

		checkBrickDC(motor);
		configMotor();
		
		checkBrickServo(rudder);
		configRudder();

		setVelocityMax(velocityMax);
	}

	/**
	 * Configuration for motor device
	 */
	private void configMotor(){
		try {
			motor.setPWMFrequency(MOTOR_PWM_FREQUENCY);
			motor.setDriveMode(MOTOR_DRIVE_MODE);
			motor.setAcceleration(MOTOR_ACCELERATION); 				
			motor.setVelocity(MOTOR_VELOCITY);			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}
	
	/**
	 * Configuration for rudder device
	 * Used servo: Dymond DS90 (Staufenbiel)
	 */
	private void configRudder(){
		try {
			rudder.setOutputVoltage(SERVO_VOLTAGE);
			rudder.setDegree(SERVO_NUM, SERVO_MIN_DEGREE, SERVO_MAX_DEGREE);
			rudder.setPulseWidth(SERVO_NUM, SERVO_MIN_PULSE, SERVO_MAX_PULSE);
			rudder.setPeriod(SERVO_NUM, SERVO_PERIOD);
			rudder.setAcceleration(SERVO_NUM, SERVO_ACCELERATION); 
			rudder.setVelocity(SERVO_NUM, SERVO_VELOCITY); 
			rudder.setPosition(SERVO_NUM,SERVO_START_POSITION);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Checks if the parameter are within the expected range
	 * Throws IllegalArgumentException if not.
	 * 
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 */
	private void checkArguments(int x, int y){
		if (y < -100 || y > 100){
			throw new IllegalArgumentException("joystick y-coordinate < -100 or > 100 detected. y="+y+" y must be <= 100 and >= -100.");
		}
		if (x < -100 || x > 100){
			throw new IllegalArgumentException("joystick x-coordinate < -100 or > 100 detected. x="+x+" x must be <= 100 and >= -100.");
		}
		if (y < -100 || y > 100){
			throw new IllegalArgumentException("joystick y-coordinate < -100 or > 100 detected. y="+y+" y must be <= 100 and >= -100.");
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
	
	/**
	 * Checks if the servo is not null
	 * Throws IllegalArgumentException if they are.
	 * 
	 * @param rudder - BrickServo for rudder
	 * 
	 */
	private void checkBrickServo(BrickServo rudder){
		if (rudder == null){
			throw new IllegalArgumentException("BrickServo for rudder is NULL");
		}
	}
	
	/**
	 * Returns the transformed velocity 
	 * 
	 * @return short - velocity
	 */
	private short calculateVelocity(int y){
		// Umrechnung: Y-Position in Geschwindigkeit und Richtung (Vorzeichen)
		short velocity = (short)(y *  velocityInt) ; // (100 * 327) + 67 = 32.700 (max)
		if (velocity > 0){
			velocity = (short) (velocity + 67);
		}
		if (velocity < 0){
			velocity = (short) (velocity - 67);
		}
//		System.out.println("calculateVelocity:: velocity="+velocity);
		
		return velocity;
	}
	
	/**
	 * Returns the transformed degree for the servo
	 * 
	 * @param x - x-axis position comming from joystick
	 * @return short - the degree to set the rudder
	 */
	private short calculateServoPos(int x){
		// -30 degree = -100 / +30 degree = +100
		short servoPos = (short)(x * 0.3 * 100);
		
		return servoPos;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#calculateAndSet(int, int)
	 */
	public void calculateAndSet(int x, int y){
		
//		y-axis: motor control: velocity (+ forward, - backward)
//		100
//		 !
//		 !
//		 !
//-100 ----0---- 100    x-axis: rudder control: Geschwindigkeitsreduzierung des jeweiligen Motors
//		 !
//		 !
//		 !
//	   -100
		
		checkArguments(x, y);

		// calculate and set Velocity
		if (y != yOld){
			try {
				motor.setVelocity(calculateVelocity(y));
				motor.enable();
				yOld = y;
				if (x != xOld){
					rudder.setPosition(SERVO_NUM, calculateServoPos(x));
					rudder.enable(SERVO_NUM);
					xOld = x;
				}
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// calculate and set Servo Position (degree)
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#getVelocityMax()
	 */
	public short getVelocityMax() {
		return velocityMax;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#setVelocityMax(short)
	 */
	public void setVelocityMax(short velocityMax) {
		if (velocityMax < 1){
			throw new IllegalArgumentException("velocityMax < 1 detected. velocityMax="+velocityMax+" velocityMax must be > 0.");
		}
		this.velocityMax = velocityMax;
		this.velocityInt = (short) (velocityMax / 100);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#report()
	 */
	public void report(){
		
		System.out.println("Report");
		System.out.println("velocityMax="+velocityMax);
		System.out.println("velocityInt (max /100)="+velocityInt);
		
		
		System.out.println("\nTable 'Forward -> backward / straight on");
		for (int y = 100; y > -101; y--) {
			System.out.println("y="+y+" motorVelocity="+calculateVelocity(y));
		}
		System.out.println("\nTable ' from right to left");
		for (int x = 100; x > -101; x--) {
			System.out.println("x="+x+" servoPos="+calculateServoPos(x));
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.legoschiff.services.MotorAndRudderService#stopAllMotors()
	 */
	public void stopAllMotors() {
		try {
			motor.setVelocity((short)0);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
