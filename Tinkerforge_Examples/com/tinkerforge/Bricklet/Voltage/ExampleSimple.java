package com.tinkerforge.Bricklet.Voltage;

import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.IPConnection;

public class ExampleSimple {
	private static final String host = "localhost";
	private static final int port = 4223;
	private static final String UID = "ABC"; // Change to your UID
	
	// Note: To make the example code cleaner we do not handle exceptions. Exceptions you
	//       might normally want to catch are described in the documentation
	public static void main(String args[]) throws Exception {
		IPConnection ipcon = new IPConnection(); // Create IP connection
		BrickletVoltage vol = new BrickletVoltage(UID, ipcon); // Create device object

		ipcon.connect(host, port); // Connect to brickd
		// Don't use device before ipcon is connected

		// Get current voltage (unit is mV)
		int voltage = vol.getVoltage(); // Can throw com.tinkerforge.TimeoutException

		System.out.println("Voltage: " + voltage/1000.0 + " V");

		System.console().readLine("Press key to exit\n");
		ipcon.disconnect();
	}
}
