package de.kabuman.tinkerforge.rp6.tests;

import de.kabuman.tinkerforge.rp6.services.MotorControlByJoystickService;
import de.kabuman.tinkerforge.rp6.services.MotorControlByJoystickServiceImpl;


public class MotorControlByJoystickServiceTestMain {
	
	public static void main(String args[]) throws Exception {
		
		MotorControlByJoystickService motorControlByJoystickService = new MotorControlByJoystickServiceImpl(MotorControlByJoystickService.VELOCITY_MAX);
		motorControlByJoystickService.report(); 
	}
		
}
