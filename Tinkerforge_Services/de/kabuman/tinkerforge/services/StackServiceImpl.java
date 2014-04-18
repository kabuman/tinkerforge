package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.StopWatchService;

public class StackServiceImpl implements StackService {
	
	// BrickMaster
	BrickMaster brickMaster;
	String usedFor;
	
	// Minimum measured Voltage
	double minVoltage = Double.MAX_VALUE;
	double thresholdMinVoltage;
	
	// Maximum measured Current
	double maxCurrent = Double.MIN_VALUE;
	double maxVoltage = 0;

	// Chibi Signal Strength
	short thresholdSignalStrength = 30;
	boolean alertVoltage = false;
	
	// StopWatch 
	StopWatchService stopWatchStack;

	private ChibiService chibiService;

	/**
	 * Constructor
	 * 
	 * @param brickMaster - the brick master
	 */
	public StackServiceImpl (BrickMaster brickMaster){
		this.brickMaster = brickMaster;
		if (ConfigServiceImpl.getInstance() != null){
			this.usedFor = ConfigServiceImpl.getInstance().getUsedFor(brickMaster);
			this.thresholdMinVoltage = ConfigServiceImpl.getInstance().getThresholdMinVoltage(brickMaster);
//			chibiService = new ChibiServiceImpl(brickMaster);
		}
		if (ConnectServiceImpl.getInstance() != null){
			this.usedFor = ConnectServiceImpl.getInstance().getUsedFor(brickMaster);
			this.thresholdMinVoltage = ConnectServiceImpl.getInstance().getThresholdMinVoltage(brickMaster);
//			chibiService = new ChibiServiceImpl(brickMaster);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#writeStatusToConsole()
	 */
	public void writeStatusToConsole() throws TimeoutException{
		System.out.println("\n"+usedFor+" "+ConfigServiceImpl.getName(brickMaster));
		System.out.println("Firmware: "+ConfigServiceImpl.getFirmwareVersion(brickMaster));
		System.out.println("Binding : "+ConfigServiceImpl.getBindingVersion(brickMaster));
		System.out.println("Reg. Voltage: "+getLongFormVoltage());
		System.out.println("Reg. Current: "+getLongFormCurrent());
		System.out.println("Min. Voltage: "+getLongFormMinVoltage());
		System.out.println("Alert threshold Min. Voltage: "+getAlertThresholdMinVoltage());
		System.out.println("Alert indicator Min. Voltage: "+getAlertIndicatorMinVoltage());
		System.out.println("Max. Current: "+getLongFormMaxCurrent());
		System.out.println("Max. Voltage: "+getLongFormMaxVoltage());
//		System.out.println("Chibi Master Address: "+chibiService.getChibiMasterAddress());
//		System.out.println("Chibi Address: "+chibiService.getChibiAddress());
//		System.out.println("Chibi Signal Strength: "+chibiService.getChibiSignalStrength());
//		System.out.println("Chibi Error Log: "+chibiService.getChibiErrorLog());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getLongFormVoltage()
	 */
	public String getLongFormVoltage() throws TimeoutException{
		return " " + FormatterService.getLongFormVoltage(getVoltage());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getShortFormVoltage()
	 */
	public String getShortFormVoltage() throws TimeoutException{
		return " " + FormatterService.getShortFormVoltage(getVoltage());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getLongFormCurrent()
	 */
	public String getLongFormCurrent() throws TimeoutException{
		return FormatterService.getLongFormCurrent(getCurrent());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getLCDFormOperatingTime()
	 */
	public String getLCDFormOperatingTime(){
		return stopWatchStack.getLCDFormStopWatch(stopWatchStack.getCurrent());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getShortFormCurrent()
	 */
	public String getShortFormCurrent() throws TimeoutException{
		return FormatterService.getShortFormCurrent(getCurrent());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getVoltage()
	 */
	public double getVoltage() throws TimeoutException{
		double voltage;
		try {
			voltage = calc(brickMaster.getStackVoltage());
			checkMinVoltage(voltage);
			return voltage;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getCurrent()
	 */
	public double getCurrent() throws TimeoutException{
		double current = 0.00;
		
				try {
					current = calc(brickMaster.getStackCurrent());
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				checkMaxCurrent(current);
		
		return current;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getLongFormMaxCurrent()
	 */
	public String getLongFormMaxCurrent(){
		return FormatterService.getLongFormCurrent(maxCurrent);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getShortFormMaxCurrent()
	 */
	public String getShortFormMaxCurrent(){
		return FormatterService.getShortFormCurrent(maxCurrent);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getLongFormMaxVoltage()
	 */
	public String getLongFormMaxVoltage(){
		return FormatterService.getLongFormCurrent(maxVoltage);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getShortFormMaxVoltage()
	 */
	public String getShortFormMaxVoltage(){
		return FormatterService.getShortFormCurrent(maxVoltage);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getAlertIndicatorMinVoltage()
	 */
	public char getAlertIndicatorMinVoltage(){
		if (alertVoltage){
			return '!';
		} else {
			return ' ';
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#isAlertMinVoltage()
	 */
	public boolean isAlertMinVoltage(){
		return alertVoltage;
	}
	/**
	 * Checks the given voltage measure against the stored minimum voltage
	 * Stores a new minimum value and set an alert flag
	 * 
	 * @param voltage - the current measured voltage
	 */
	private void checkMinVoltage(double voltage){
		minVoltage = setMinVoltage(minVoltage, voltage);
		alertVoltage = setAlertVoltage(minVoltage,thresholdMinVoltage);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getLongFormMinVoltage()
	 */
	public String getLongFormMinVoltage(){
		return " " + FormatterService.getLongFormVoltage(minVoltage);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getShortFormMinVoltage()
	 */
	public String getShortFormMinVoltage(){
		return " " + FormatterService.getShortFormVoltage(minVoltage);
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
	 * Checks the given current measure against the stored maximum current
	 * Stores a new maximum value
	 * 
	 * @param brickMaster - the brickmaster which stack voltage is to check
	 * @param voltage - the current measured voltage
	 * @throws TimeoutException 
	 */
	private void checkMaxCurrent(double current) throws TimeoutException{
		maxCurrent = setMaxCurrent(maxCurrent, current);
		if (maxCurrent != current){
			maxVoltage = getVoltage();
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#setAlertThresholdMinVoltage(double)
	 */
	public void setAlertThresholdMinVoltage(double thresholdMinVoltage) {
		this.thresholdMinVoltage = thresholdMinVoltage;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getAlertThresholdMinVoltage()
	 */
	public double getAlertThresholdMinVoltage() {
		return thresholdMinVoltage;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.StackService#getChibiService()
	 */
	public ChibiService getChibiService() {
		return chibiService;
	}





}
