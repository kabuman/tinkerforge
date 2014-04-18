package de.kabuman.tinkerforge.rp6.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.MotorAbstractService;

/**
 * Motor Control for RP6
 */
public class MotorControlByJoystickServiceImpl extends MotorAbstractService implements MotorControlByJoystickService{
	final short SHORT_ZERO = 0;
	
	short velocityMax = 0;	// max velocity 
	short velocityInt = 0;	// used velociytMax internal (velocityMax / 100)

	// For stop mechanism after lost of connection
	Boolean inMotion = false;
	BrickDC motorLeft = null;
	BrickDC motorRight = null;
	
	/**
	 * Basic Constructor - set to private to avoid the usage 
	 */
	public MotorControlByJoystickServiceImpl(BrickDC motorLeft, BrickDC motorRight){
		this.motorLeft = motorLeft;
		this.motorRight = motorRight;
	}
	
	/**
	 * Constructor 
	 * Sets up the maximum velocity which may be used
	 * 
	 * @param velocityMax - the maximum allowed velocity
	 */
	public MotorControlByJoystickServiceImpl(short velocityMax){
		setVelocityMax(velocityMax);
	}
	
	/**
	 * Returns the calculated velocity for the requested motor
	 * 
	 * @param velocityLeft - the velocity for the left motor
	 * @param velocityRight - the velocity for the right motor
	 * @param right - the mode (MOTOR_DRIVE|MOTOR_ROTATE]
	 * @return velocity - of the requested motor (left or right mtor)
	 */
	private short getRequiredVelocity(short velocityLeft, short velocityRight, boolean right){
		if (right == true){
			return velocityRight;
		} else {
			return velocityLeft;
		}
	}
	
	/**
	 * Checks if the parameter are within the expected range
	 * Throws IllegalArgumentException if not.
	 * 
	 * @param mode - the mode [MOTOR_DRIVE|MOTOR_ROTATE]
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 */
	private void checkArguments(int mode, int x, int y){
		if (mode != MODE_DRIVE && mode != MODE_ROTATE){
			throw new IllegalArgumentException("Not valid mode detected. mode="+mode+" Use MotorControlByJoystick.MODE_DRIVE or MOD_ROTATE only.");
		}
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
	 * Checks if the motors are not null
	 * Throws IllegalArgumentException if they are.
	 * 
	 * @param motorLeft - BrickDC for left motor
	 * @param motorRight - BrickDC for right motor
	 * 
	 */
	private void checkBrickDC(BrickDC motorLeft, BrickDC motorRight){
		if (motorLeft == null){
			throw new IllegalArgumentException("BrickDC for left motor is NULL");
		}
		if (motorRight == null){
			throw new IllegalArgumentException("BrickDC for right motor is NULL");
		}
	}
	
	/**
	 * Calculates the velocities per motor and set it to motors
	 * Call this method for each joystick position change
	 * 
	 * @param mode - use constants MODE_DRIVE (normal mode), MODE_ROTATE (rotate on place) to set up
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 */
	public void calculateAndSetVelocity(int mode, int x, int y){
		checkBrickDC(motorLeft, motorRight);
		checkArguments(mode, x, y);
		
		short velocityLeft = calculateVelocity(mode,x, y, MotorControlByJoystickService.MOTOR_LEFT);
		short velocityRight = calculateVelocity(mode,x, y, MotorControlByJoystickService.MOTOR_RIGHT);
		
		try {
			motorLeft.setVelocity(velocityLeft);
			motorRight.setVelocity(velocityRight);
			
			motorLeft.enable();
			motorRight.enable();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the calculated velocity
	 * Call this method for each motor
	 * Do not change the "mode" between the calls for the left and the right motor
	 * 
	 * @param mode - use constants MODE_DRIVE (normal mode), MODE_ROTATE (rotate on place) to set up
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 * @param right - the motor for which it is to calculate; use MOTOR_RIGHT, MOTOR_LEFT to set up
	 * @return velocity - the calculated velocity
	 */
	public short calculateVelocity(int mode, int x, int y, boolean right){
		
		// checks the valid values and ranges
		checkArguments(mode, x, y);

		// calculates the velocity depending on the given mode
		switch (mode) {
		case MODE_DRIVE:
			return calculateVelocityModeDrive(x,y,right);
		case MODE_ROTATE:
			return calculateVelocityModeRotate(x,right);
		default:
			throw new IllegalArgumentException("calculateVelocity:: program error: unexpected switch value");
		}
	}
	
	/**
	 * Returns the calculated velocity for mode MOTOR_ROTATE
	 * 
	 * @param x - the joystick x-coordinate
	 * @param right - the motor for which it is to calculate; use MOTOR_RIGHT, MOTOR_LEFT to set up
	 * @return velocity - the calculated velocity for mode MOTOR_ROTATE
	 */
	private short calculateVelocityModeRotate(int x, boolean right){
		if (x ==  0){
			// Central point of joystick: stop all motors
			return SHORT_ZERO;		// it does not matter which motor was requested
		} 
		
		short velocityRight = 0;
		short velocityLeft = 0;
		
		
		// Umrechnung: X-Position in Geschwindigkeit und Richtung (Vorzeichen)
		short velocityMain = (short)(x *  velocityInt) ; // (100 * 327) + 67 = 32.700 (max)
		if (velocityMain > 0){
			velocityMain = (short) (velocityMain + 67);
		}
		if (velocityMain < 0){
			velocityMain = (short) (velocityMain - 67);
		}
		
		velocityLeft = velocityMain;
		velocityRight = (short)(velocityMain * -1);
		return getRequiredVelocity(velocityLeft, velocityRight, right);
	}
	
	/**
	 * Returns the calculated velocity for mode MOTOR_DRIVE
	 * 
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 * @param right - the motor for which it is to calculate; use MOTOR_RIGHT, MOTOR_LEFT to set up
	 * @return velocity - the calculated velocity for mode MOTOR_DRIVE
	 */
	private short calculateVelocityModeDrive(int x, int y, boolean right){
//		y-Achse: Geschwindigkeit und Richtung
//		100
//		 !
//		 !
//		 !
//-100 ----0---- 100    x-Achse: Geschwindigkeitsreduzierung des jeweiligen Motors
//		 !
//		 !
//		 !
//	   -100
		
		short velocityRight = 0;
		short velocityLeft = 0;
		
		
		// Umrechnung: Y-Position in Geschwindigkeit und Richtung (Vorzeichen)
		short velocityMain = (short)(y *  velocityInt) ; // (100 * 327) + 67 = 32.700 (max)
		if (velocityMain > 0){
			velocityMain = (short) (velocityMain + 67);
		}
		if (velocityMain < 0){
			velocityMain = (short) (velocityMain - 67);
		}
		
	
		// Berechnung: Geschwindigkeitskorrektur des linken oder rechten Motors (Lenk-Bewegung)
		short velocityCorr = (short) (x * (velocityMain) / 100);

		if (x ==  0 && y == 0){
			// Central point of joystick: stop all motors
			return SHORT_ZERO;		// it does not matter which motor was requested
		} 
		
		
		// Vorwärts- und Rückwärtsfahrt ohne Lenkung
		if (x ==  0 && y != 0){
			// Quadrant 2: Straight on (without direction correction)
			velocityLeft = velocityMain;
			velocityRight = velocityMain;
			return getRequiredVelocity(velocityLeft, velocityRight, right);
		} 
		
		
		if (x < 0 && y >0){
			// Quadrant 1: Vorwärts + Links
			// linker Motor ist zu drosseln
			velocityLeft = (short)(velocityMain + velocityCorr);
			velocityRight = velocityMain;
			return getRequiredVelocity(velocityLeft, velocityRight, right);
		} 
		if (x > 0 && y > 0){
			// Quadrant 2: Vorwärts + Rechts
			// Rechter Motor ist zu drosseln
			velocityRight = (short)(velocityMain - velocityCorr);
			velocityLeft = velocityMain;
			return getRequiredVelocity(velocityLeft, velocityRight, right);
		} 
		if (x > 0 && y < 0){
			// Quadrant 3: Rückwärts + Rechts
			// Linker Motor ist zu drosseln
			velocityRight = (short)(velocityMain - velocityCorr);
			velocityLeft = velocityMain;
			return getRequiredVelocity(velocityLeft, velocityRight, right);
		} 
		if (x < 0 && y < 0){
			// Quadrant 4: Vorwärts + Rechts
			// linker Motor ist zu drosseln
			velocityLeft = (short)(velocityMain + velocityCorr);
			velocityRight = velocityMain;
			return getRequiredVelocity(velocityLeft, velocityRight, right);
		} 

		if (y == 0){
			// velocity = 0
			return 0;
		}

		throw new IllegalArgumentException("calculateVelocityModeDrive:: program error: unexpected coordinates. x="+x+ " y="+y+" velocityMax="+velocityMax);
		
	}
	
	/**
	 * Returns the current maximum velocity which may be used be method "calculateVelocity(..)"
	 * 
	 * @return velocity - the maximum
	 */
	public short getVelocityMax() {
		return velocityMax;
	}
	
	/**
	 * Set maximum velocity after instantiation of class "MotorControllJoystickImpl"
	 * 
	 * @param velocityMax - the maximum velocity which may be used for the motors
	 */
	public void setVelocityMax(short velocityMax) {
		if (velocityMax < 1){
			throw new IllegalArgumentException("velocityMax < 1 detected. velocityMax="+velocityMax+" velocityMax must be > 0.");
		}
		this.velocityMax = velocityMax;
		this.velocityInt = (short) (velocityMax / 100);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.rp6.services.MotorControlByJoystickService#stopAllMotors()
	 */
	public void stopAllMotors() {
		try {
			motorLeft.setVelocity(SHORT_ZERO);
			motorRight.setVelocity(SHORT_ZERO);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  Creates a Report with mappings between x,y-coordinats and the assigned velocity for the given mode (MOTOR_DRIVE or MOTOR_ROTATE
	 *  Write it out via sysout
	 */
	public void report(){
		final short SHORT_50 = 50;
		final short SHORT_100 = 100;
		final short SHORT_MINUS_50 = -50;
		final short SHORT_MINUS_100 = -100;
		
		System.out.println("Report");
		System.out.println("Mode=MODE_DRIVE");
		System.out.println("velocityMax="+velocityMax);
		System.out.println("velocityInt (max /100)="+velocityInt);
		
		
		
		System.out.println("\nTable 'Forward -> backward / straight on");
		for (int y = 100; y > -101; y--) {
			System.out.println("x=0 y="+y+" motorVelocity(left/right)="+calculateVelocityModeDrive(SHORT_ZERO, y, MOTOR_LEFT));
		}
		System.out.println("\nTable 'Forward -> backward / 50% left");
		for (int y = 100; y > -101; y--) {
			System.out.println("x="+SHORT_50+" y="+y+" motorVelocity(left/right)="+calculateVelocityModeDrive(SHORT_50, y, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(SHORT_50, y, MOTOR_RIGHT));
		}

		System.out.println("\nTable 'Forward -> backward / 50% right");
		for (int y = 100; y > -101; y--) {
			System.out.println("x="+SHORT_MINUS_50+" y="+y+" motorVelocity(left/right)="+calculateVelocityModeDrive(SHORT_MINUS_50, y, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(SHORT_MINUS_50, y, MOTOR_RIGHT));
		}
		System.out.println("\nTable 'Forward -> backward / 100% left");
		for (int y = 100; y > -101; y--) {
			System.out.println("x="+SHORT_100+" y="+y+" motorVelocity(left/right)="+calculateVelocityModeDrive(SHORT_100, y, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(SHORT_100, y, MOTOR_RIGHT));
		}
		System.out.println("\nTable 'Forward -> backward / 100% right");
		for (int y = 100; y > -101; y--) {
			System.out.println("x="+SHORT_MINUS_100+" y="+y+" motorVelocity(left/right)="+calculateVelocityModeDrive(SHORT_MINUS_100, y, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(SHORT_MINUS_100, y, MOTOR_RIGHT));
		}
		System.out.println("\nTable ' 50% Forward / from right to left");
		for (int x = 100; x > -101; x--) {
			System.out.println("x="+x+" y="+SHORT_50+" motorVelocity(left/right)="+calculateVelocityModeDrive(x, SHORT_50, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(x, SHORT_50, MOTOR_RIGHT));
		}
		System.out.println("\nTable ' 50% Backward / from right to left");
		for (int x = 100; x > -101; x--) {
			System.out.println("x="+x+" y="+SHORT_MINUS_50+" motorVelocity(left/right)="+calculateVelocityModeDrive(x, SHORT_MINUS_50, MOTOR_LEFT)+"/"+calculateVelocityModeDrive(x, SHORT_MINUS_50, MOTOR_RIGHT));
		}

		System.out.println("\n\nMode=MODE_DRIVE");
		for (int x = 100; x > -101; x--) {
			System.out.println("x="+x+"  motorVelocity(left/right)="+calculateVelocityModeRotate(x, MOTOR_LEFT)+"/"+calculateVelocityModeRotate(x, MOTOR_RIGHT));
		}
	}
}
