package de.kabuman.tinkerforge.services;

import com.tinkerforge.TimeoutException;

/**
 * @author Karsten Buchmann
 *
 */
public interface StackService {
	
	/**
	 * Writes the measured Chibi and BrickMaster data to console
	 * @throws TimeoutException 
	 */
	public void writeStatusToConsole() throws TimeoutException;
	
	/**
	 * @return String - "H:MM" operating time of stack
	 */
	public String getLCDFormOperatingTime();

	/**
	 * @return ChibiService - returns null, if chibi modul does not exist on this stack
	 */
	public ChibiService getChibiService();

	
	// ================================================================================================================
	// Voltage Measures
	/**
	 * @return double - Stack voltage in milli volt 
	 * @throws TimeoutException 
	 */
	public double getVoltage() throws TimeoutException;

	/**
	 * @return String - Stack voltage in volt formated as "#0.000"
	 * @throws TimeoutException 
	 */
	public String getLongFormVoltage() throws TimeoutException;
	
	/**
	 * @return String - Stack voltage in volt formated as "#0.0"
	 * @throws TimeoutException 
	 */
	public String getShortFormVoltage() throws TimeoutException;
	
	/**
	 * @return String - Smallest measured stack voltage in volt formated as "#0.000"
	 */
	public String getLongFormMinVoltage();

	/**
	 * @return String - Smallest measured stack voltage in volt formated as "#0.0"
	 */
	public String getShortFormMinVoltage();
	
	/**
	 * @return String - Highest measured stack voltage in volt formated as "#0.000"
	 */
	public String getLongFormMaxVoltage();

	/**
	 * @return String - Highest measured stack voltage in volt formated as "#0.0"
	 */
	public String getShortFormMaxVoltage();

	
	
	// ================================================================================================================
	// Minimum Voltage Alert
	
	/**
	 * Sets the alert threshold for the smallest voltage in volt
	 * Is set by Constructor too. 
	 */
	public void setAlertThresholdMinVoltage(double thresholdMinVoltage);
	
	/**
	 * Indicates if the alert is set or not
	 * It is designed to indicate the alert on a LCD display as a single character with low consumption of space
	 * 
	 * @return char - '!' if measured voltage was <= threshold; otherwise blank 
	 */
	public char getAlertIndicatorMinVoltage();
	
	/**
	 * @return boolean - true if measured voltage was <= threshold; otherwise false
	 */
	public boolean isAlertMinVoltage();
	
	/**
	 * @return double - the alert thresold for the smallest voltage in volt
	 */
	public double getAlertThresholdMinVoltage();
	
	
	
	// ================================================================================================================
	// Current Measure
	/**
	 * @return double - Stack current in milli ampere
	 * @throws TimeoutException 
	 */
	public double getCurrent() throws TimeoutException;
	
	/**
	 * @return String - Stack current in ampere formated as "#0.000"
	 * @throws TimeoutException 
	 */
	public String getLongFormCurrent() throws TimeoutException;
	
	/**
	 * @return String - Stack current in ampere formated as "#0.0"
	 * @throws TimeoutException 
	 */
	public String getShortFormCurrent() throws TimeoutException;
	
	/**
	 * @return String - Highest measured stack current in ampere formated as "#0.000"
	 */
	public String getLongFormMaxCurrent();

	/**
	 * @return String - Highest measured stack current in ampere formated as "#0.0"
	 */
	public String getShortFormMaxCurrent();

}
