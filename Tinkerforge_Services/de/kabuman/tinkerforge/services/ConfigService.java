package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

/**
 * ConfigService for Tinkerforge Hardware (bricks, bricklets)
 * 
 * Maps the internal used device id to the seral number (device can be used in client code independently from the real used device)
 * Instantiates the brick or bricklet object
 * Creates the needed IP-Connection
 */
/**
 * @author Karsten Buchmann
 *
 */
public interface ConfigService extends DeviceIdentifier{
	
	// connection parameter
	public static final String WE1 = "192.168.178.32";
	public static final String WE2 = "192.168.178.43";		// Protected Object
	public static final String WE3 = "192.168.178.44";
	public static final String WE4 = "192.168.178.45";
	public static final String lh = "localhost";
	public static final String HOST = new String(WE2);

	public static final int PORT = 4223;

	/**
	 * Returns the connection
	 * 
	 * It will be create if it does not exist
	 * Can return NULL if exception occurred.
	 * 
	 * @return IPConnection - the established connection
	 */
	public IPConnection getConnect();
	
	/**
	 * Create device, add it to the given connection and returns it
	 * Stores the device into internal map (see method "getDevice(id)" to retrieve)
	 * 
	 * @param id - device id
	 * @return device
	 * @throws TimeoutException 
	 */
	public Device createAndConnect(int id) throws TimeoutException;
	
	/**
	 * Create device, add it to the given connection and returns it
	 * Stores the device into internal map (see method "getDevice(id)" to retrieve)
	 * Stores the "usedFor" into internal map (see method "setUsedFor(device,string)")
	 * Stores the "thresholdMinVoltage" into internal map (see method "setThresholdMinVoltage(device,string)")
	 * 
	 * @param id - device id
	 * @param usedFor - the name for what it is used
	 * @param thresholdMinVoltage - the threshold minimum voltage for the device
	 * @return device 
	 * @throws TimeoutException 
	 */
	public Device createAndConnect(int id, String usedFor, double thresholdMinVoltage) throws TimeoutException;
	
	/**
	 * Create device, add it to the given connection and returns it
	 * Stores the device into internal map (see method "getDevice(id)" to retrieve)
	 * Stores the "usedFor" into internal map (see method "setUsedFor(device,string)")
	 * 
	 * @param id - device id
	 * @param usedFor - the name for what it is used
	 * @return device 
	 * @throws TimeoutException 
	 */
	public Device createAndConnect(int id, String usedFor) throws TimeoutException;

	/**
	 * Reset all bricks (DC, Servo, IMU, Stepper, Master)
	 * After this method call: All device objects are to create and connect via ConfigService
	 * 
	 * @return int - the number of resets
	 */
	public int reset();


	/**
	 * Writes a report about used devices upon console
	 */
	public void report();
	
	/**
	 * Set the "usedFor" String for created device
	 * Must be created, otherwise IllegalArgumentException will be thrown
	 * 
	 * @param device - the device object
	 * @param usedFor - the "usedFor" string, a name for what it is used
	 */
	public void setUsedFor(Device device, String usedFor);
	
	/**
	 * Returns the "usedFor" string, a name for what it is used
	 * Device must be created via ConfigService, otherwise IllegalArgumentException will be thrown
	 * 
	 * @param device - the device object
	 * @return String - the usedFor string
	 */
	public String getUsedFor(Device device);
	
	/**
	 * Set the threshold minimum voltage for a brickmaster
	 * Device(BrickMaster) must be created via ConfigService, otherwise IllegalArgumentException will be thrown
	 * 
	 * @param brickMaster - the brickMaster object
	 * @param thresholdMinVoltage - the threshold minimum voltage
	 */
	public void setThresholdMinVoltage(BrickMaster brickMaster, double thresholdMinVoltage);
	
	/**
	 * Returns the threshold minimum voltage
	 * 
	 * @param brickMaster - the brickMaster object
	 * @return double - the threshold minimum voltage
	 */
	public double getThresholdMinVoltage(BrickMaster brickMaster);
}
