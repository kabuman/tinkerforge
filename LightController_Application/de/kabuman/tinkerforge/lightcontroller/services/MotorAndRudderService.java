package de.kabuman.tinkerforge.lightcontroller.services;


public interface MotorAndRudderService {

	// Constant for method: setVecolictyMax([VELOCITY_MAX]) 
	public final short VELOCITY_MAX = 32767;

	/**
	 * Calculates the velocities and servo position and set them
	 * Call this method for each joystick position change
	 * 
	 * @param x - the joystick x-coordinate
	 * @param y - the joystick y-coordinate
	 */
	public void calculateAndSet(int x, int y);
	
	/**
	 * Returns the current maximum velocity which may be used be method "calculateVelocity(..)"
	 * 
	 * @return velocity - the maximum
	 */
	public short getVelocityMax();
	
	/**
	 * Set maximum velocity after instantiation of class "MotorControllJoystickImpl"
	 * 
	 * @param velocityMax - the maximum velocity which may be used for the motors
	 */
	public void setVelocityMax(short velocityMax);
	
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
