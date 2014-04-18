package de.kabuman.tinkerforge.rp6.services;


public interface MotorControlByJoystickService {

	
	// Constants for method: calculateVelocity([MOTOR_DRIVE|MOTOR_ROTATE],x,y,right)
	final int MODE_DRIVE = 1;
	final int MODE_ROTATE = 2;
	
	// Contstants for method: calculateVelocity(mode,x,y,[MOTOR_RIGHT|MOTOR_LEFT])
	final boolean MOTOR_RIGHT = true;
	final boolean MOTOR_LEFT = false;

	// Constant for method: setVecolictyMax([VELOCITY_MAX]) 
	final short VELOCITY_MAX = 32767;
	

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
	short calculateVelocity(int mode, int x, int y, boolean right);
	
	/**
	 * Calculates the velocities per motor and set it to motors
	 * Call this method for each joystick position change
	 * 
	 * @param mode - use constants MODE_DRIVE (normal mode), MODE_ROTATE (rotate on place) to set up
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 */
	void calculateAndSetVelocity(int mode, int x, int y);
	
	/**
	 * Returns the current maximum velocity which may be used be method "calculateVelocity(..)"
	 * 
	 * @return velocity - the maximum
	 */
	short getVelocityMax();
	
	/**
	 * Set maximum velocity after instantiation of class "MotorControllJoystickImpl"
	 * 
	 * @param velocityMax - the maximum velocity which may be used for the motors
	 */
	void setVelocityMax(short velocityMax);
	
	/**
	 * Stops all motors immediately
	 */
	public void stopAllMotors();

	/**
	 *  Creates a Report with mappings between x,y-coordinats and the assigned velocity for the given mode (MOTOR_DRIVE or MOTOR_ROTATE
	 *  Write it out via sysout
	 */
	public void report();
}
