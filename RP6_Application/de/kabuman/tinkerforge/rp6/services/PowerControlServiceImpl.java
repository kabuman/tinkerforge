package de.kabuman.tinkerforge.rp6.services;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;

/**
 * @author Karsten Buchmann
 *
 */
public class PowerControlServiceImpl implements PowerControlService{
	
	BrickMaster brickMasterCP;
	BrickMaster brickMasterRP6;
	BrickMaster brickMasterRC;

	boolean alertVoltageRC = false;
	boolean alertVoltageRP6 = false;
	
	
	// Minimum measured Voltage
	double minVoltageRC = Double.MAX_VALUE;
	double minVoltageRP6 = Double.MAX_VALUE;
	double thresholdMinVoltageRC = 6;
	double thresholdMinVoltageRP6 = 10;
	
	// Maximum measured Current
	double maxCurrentRC = Double.MIN_VALUE;
	double maxVoltageRC = 0;
	double maxCurrentRP6 = Double.MIN_VALUE;
	double maxVoltageRP6 = 0;
	
	public PowerControlServiceImpl(BrickMaster brickMasterCP, BrickMaster brickMasterRC, BrickMaster brickMasterRP6){
		this.brickMasterCP = brickMasterCP;
		this.brickMasterRC = brickMasterRC;
		this.brickMasterRP6 = brickMasterRP6;
	}

	/**
	 * Helper method to convert values from e.g. mVolt to Vol or mAmpere to Ampere
	 * 
	 * @param val - the value in milli volt/ampere 
	 * @return double - the result of the conversion in volt/ampere
	 */
	private double calc(int val){
		double valDouble = (double) val;
		return valDouble / 1000;
	}
	

	/**
	 * Returns the stack voltage of the given brick master
	 * 
	 * @param brickMaster - the given brick master object
	 * @return double - the stack voltage (in milli)
	 */
	public double getVoltage(BrickMaster brickMaster){
		double voltage;
		try {
			voltage = calc(brickMaster.getStackVoltage());
			checkMinVoltage(brickMaster, voltage);
	        return voltage;
		} catch (TimeoutException e) {
	        System.out.println("getVoltage:: TimeoutException BrickMaster.getStackVoltage()");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.00;
	}

	/**
	 * Checks the given voltage measure against the stored minimum voltage
	 * Stores a new minimum value and set an alert flag
	 * 
	 * @param brickMaster - the brickmaster which stack voltage is to check
	 * @param voltage - the current measured voltage
	 */
	private void checkMinVoltage(BrickMaster brickMaster, double voltage){
		if (brickMaster.equals(brickMasterRC)){
			minVoltageRC = setMinVoltage(minVoltageRC, voltage);
			alertVoltageRC = setAlertVoltage(minVoltageRC,thresholdMinVoltageRC);
		}
		if (brickMaster.equals(brickMasterRP6)){
			minVoltageRP6 = setMinVoltage(minVoltageRP6, voltage);
			alertVoltageRP6 = setAlertVoltage(minVoltageRP6,thresholdMinVoltageRP6);
		}
	}
	
	/**
	 * Checks the given current measure against the stored maximum current
	 * Stores a new maximum value
	 * 
	 * @param brickMaster - the brickmaster which stack voltage is to check
	 * @param voltage - the current measured voltage
	 */
	private void checkMaxCurrent(BrickMaster brickMaster, double current){
		if (brickMaster.equals(brickMasterRC)){
			maxCurrentRC = setMaxCurrent(maxCurrentRC, current);
			if (maxCurrentRC != current){
				maxVoltageRC = getVoltage(brickMasterRC);
			}
		}
		if (brickMaster.equals(brickMasterRP6)){
			maxCurrentRP6 = setMaxCurrent(maxCurrentRP6, current);
			if (maxCurrentRP6 != current){
				maxVoltageRP6 = getVoltage(brickMasterRP6);
			}
		}
	}
	
	/**
	 * Returns true or false, if minimum voltage falls below threshold
	 * 
	 * @param minVoltage - the current minimum voltage
	 * @param thresholdVoltage - the threshold voltage
	 * @return boolean - true: if voltage falls below threshold / false if not
	 */
	private boolean setAlertVoltage(double minVoltage, double thresholdVoltage){
		if (minVoltage < thresholdVoltage){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the smallest voltage value
	 * 
	 * @param minVoltage - the stored minimum voltage
	 * @param voltage - the current voltage
	 * @return double - the new or old minimum voltage
	 */
	private double setMinVoltage(double minVoltage, double voltage){
		if (voltage < minVoltage){
			return voltage;
		} else {
			return minVoltage;
		}
	}
	
	/**
	 * Returns the largest current value
	 * 
	 * @param maxCurrent - the stored maximum current
	 * @param current - the actual current
	 * @return double - the new or old maximum current
	 */
	private double setMaxCurrent(double maxCurrent, double current){
		if (current > maxCurrent){
			return current;
		} else {
			return maxCurrent;
		}
	}
	
	/**
	 * Returns the stack current of the given brick master
	 * 
	 * @param brickMaster - the given brick master object
	 * @return double - the stack current (in milli)
	 */
	public double getCurrent(BrickMaster brickMaster){
		double current;
		try {
			current = calc(brickMaster.getStackCurrent());
			checkMaxCurrent(brickMaster, current);
	        return current;
		} catch (TimeoutException e) {
	        System.out.println("getCurrent:: TimeoutException BrickMaster.getStackCurrent()");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// in the case of exception:
		return 0.00;
	}

	// CP Methods
	public String getLongFormVoltageCP(){
		return " " + FormatterService.getLongFormVoltage(getVoltage(brickMasterCP));
	}
	public String getShortFormVoltageCP(){
		return " " + FormatterService.getShortFormVoltage(getVoltage(brickMasterCP));
	}
	public String getLongFormCurrentCP(){
		return FormatterService.getLongFormCurrent(getCurrent(brickMasterCP));
	}
	public String getShortFormCurrentCP(){
		return FormatterService.getShortFormCurrent(getCurrent(brickMasterCP));
	}

	// RC Methods
	public String getLongFormMinVoltageRC(){
		return " " + FormatterService.getLongFormVoltage(minVoltageRC);
	}
	public String getShortFormMinVoltageRC(){
		return " " + FormatterService.getShortFormVoltage(minVoltageRC);
	}
	public String getLongFormVoltageRC(){
		return " " + FormatterService.getLongFormVoltage(getVoltage(brickMasterRC));
	}
	public String getShortFormVoltageRC(){
		return " " + FormatterService.getShortFormVoltage(getVoltage(brickMasterRC));
	}
	public String getLongFormCurrentRC(){
		return FormatterService.getLongFormCurrent(getCurrent(brickMasterRC));
	}
	public String getShortFormCurrentRC(){
		return FormatterService.getShortFormCurrent(getCurrent(brickMasterRC));
	}
	public String getLongFormMaxCurrentRC(){
		return FormatterService.getLongFormCurrent(maxCurrentRC);
	}
	public String getShortFormMaxCurrentRC(){
		return FormatterService.getShortFormCurrent(maxCurrentRC);
	}
	public String getLongFormMaxVoltageRC(){
		return FormatterService.getLongFormCurrent(maxVoltageRC);
	}
	public String getShortFormMaxVoltageRC(){
		return FormatterService.getShortFormCurrent(maxVoltageRC);
	}
	public String getAlertMinVoltageRC(){
		if (alertVoltageRC){
			return " ";
		} else {
			return "!";
		}
	}
	public boolean isAlertMinVoltageRC(){
		return alertVoltageRC;
	}

	// RP6 Methods
	public String getLongFormMinVoltageRP6(){
		return " " + FormatterService.getLongFormVoltage(minVoltageRP6);
	}
	public String getShortFormMinVoltageRP6(){
		return " " + FormatterService.getShortFormVoltage(minVoltageRP6);
	}
	public String getLongFormVoltageRP6(){
		return FormatterService.getLongFormVoltage(getVoltage(brickMasterRP6));
	}
	public String getShortFormVoltageRP6(){
		return FormatterService.getShortFormVoltage(getVoltage(brickMasterRP6));
	}
	public String getLongFormCurrentRP6(){
		return FormatterService.getLongFormCurrent(getCurrent(brickMasterRP6));
	}
	public String getShortFormCurrentRP6(){
		return FormatterService.getShortFormCurrent(getCurrent(brickMasterRP6));
	}
	public String getLongFormMaxCurrentRP6(){
		return FormatterService.getLongFormCurrent(maxCurrentRP6);
	}
	public String getShortFormMaxCurrentRP6(){
		return FormatterService.getShortFormCurrent(maxCurrentRP6);
	}
	public String getLongFormMaxVoltageRP6(){
		return FormatterService.getLongFormCurrent(maxVoltageRP6);
	}
	public String getShortFormMaxVoltageRP6(){
		return FormatterService.getShortFormCurrent(maxVoltageRP6);
	}
	public String getAlertMinVoltageRP6(){
		if (alertVoltageRP6){
			return " ";
		} else {
			return "!";
		}
	}
	public boolean isAlertMinVoltageRP6(){
		return alertVoltageRP6;
	}
}
