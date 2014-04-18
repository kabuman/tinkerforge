package de.kabuman.tinkerforge.rp6.services;

import com.tinkerforge.BrickMaster;


public interface PowerControlService {
	
	/**
	 * Returns the stack voltage of the given brick master
	 * 
	 * @param brickMaster - the given brick master object
	 * @return double - the stack voltage (in milli)
	 */
	public double getVoltage(BrickMaster brickMaster);
	
	/**
	 * Returns the stack current of the given brick master
	 * 
	 * @param brickMaster - the given brick master object
	 * @return double - the stack current (in milli)
	 */
	public double getCurrent(BrickMaster brickMaster);

	public String getLongFormVoltageCP();
	public String getShortFormVoltageCP();
	public String getLongFormCurrentCP();
	public String getShortFormCurrentCP();
	
	public String getLongFormVoltageRC();
	public String getShortFormVoltageRC();
	public String getLongFormCurrentRC();
	public String getShortFormCurrentRC();
	public String getLongFormMinVoltageRC();
	public String getShortFormMinVoltageRC();
	public String getLongFormMaxVoltageRC();
	public String getShortFormMaxVoltageRC();
	public String getLongFormMaxCurrentRC();
	public String getShortFormMaxCurrentRC();
	public String getAlertMinVoltageRC();
	public boolean isAlertMinVoltageRC();
	
	
	public String getLongFormVoltageRP6();
	public String getShortFormVoltageRP6();
	public String getLongFormCurrentRP6();
	public String getShortFormCurrentRP6();
	public String getLongFormMinVoltageRP6();
	public String getShortFormMinVoltageRP6();
	public String getLongFormMaxVoltageRP6();
	public String getShortFormMaxVoltageRP6();
	public String getLongFormMaxCurrentRP6();
	public String getShortFormMaxCurrentRP6();
	public String getAlertMinVoltageRP6();
	public boolean isAlertMinVoltageRP6();

}
