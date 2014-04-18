package de.kabuman.tinkerforge.alarm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.EmailServiceImpl;
import de.kabuman.common.services.FormatterService;
import de.kabuman.tinkerforge.alarm.config.CfgEmail;
import de.kabuman.tinkerforge.alarm.threads.AlertIntervallObserverImpl;
import de.kabuman.tinkerforge.alarm.threads.AlertObserver;
import de.kabuman.tinkerforge.alarm.threads.AlertObserverImpl;
import de.kabuman.tinkerforge.alarm.units.AlertUnit;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
import de.kabuman.tinkerforge.alarm.units.Unit;

/**
 * Implementation of Alert Controller
 * @see de.kabuman.tinkerforge.alarm.controller.AlertController
 */
public class AlertControllerImpl implements AlertController {
	
	private static AlertControllerImpl instance = null;
	
//	private int alertDuration;

	private int alertDurationIntrusion;
	private int alertDurationWater;
	private int alertDurationFire;
	private CfgEmail cfgEmail;

	EmailServiceImpl emailService;
	
	private List<Unit> alertUnitList = new ArrayList<Unit>();
	
	private boolean isAlertActive = false;
	
	private boolean isOn = true;
	
	private boolean quiet;

	private boolean isAlertOccurred = false;
	
	// triggered Alert
	ProtectUnit protectUnit = null;
	String sensorName = null;
	int msgId = 0;
	
	AlertObserver alertObserver = null;

	private short alertType;
	
	private BrickletLCD20x4 alarmDisplay = null;
	private boolean messageShown = false; 

	/**
	 * Returns new instance of Alert Controller
	 * 
	 * @param quiet - true: no alert via beeper,  false: alert via beeper too
	 * @param alertDurationIntrusion -alert duration for intrusion (Open Sensor, Motion Sensor)
	 * @param alertDurationWater - alert duration for water (Water Sensor)
	 * @param alertDurationFire - alert duration for fire
	 * @param cfgEmail - configures the email service in the case of an alert
	 * @return new instance
	 */
	public static AlertControllerImpl getNewInstance(boolean quiet, int alertDurationIntrusion, int alertDurationWater, int alertDurationFire, CfgEmail cfgEmail){
		instance = new AlertControllerImpl(quiet, alertDurationIntrusion, alertDurationWater, alertDurationFire, cfgEmail);
		return instance;
	}
	
	/**
	 * Constructor 
	 * Not to use from outside. private
	 * 
	 * @param quiet - true: no alert via beeper,  false: alert via beeper too
	 * @param alertDurationIntrusion -alert duration for intrusion (Open Sensor, Motion Sensor)
	 * @param alertDurationWater - alert duration for water (Water Sensor)
	 * @param alertDurationFire - alert duration for fire
	 * @param cfgEmail - configures the email service in the case of an alert
	 */
	private AlertControllerImpl(boolean quiet, int alertDurationIntrusion, int alertDurationWater, int alertDurationFire, CfgEmail cfgEmail) {
		this.quiet = quiet;
		this.alertDurationIntrusion = alertDurationIntrusion;
		this.alertDurationWater = alertDurationWater;
		this.alertDurationFire = alertDurationFire;
		this.cfgEmail = cfgEmail;
		
		if (cfgEmail.isEmailRequested()){
			emailService = new EmailServiceImpl(
					cfgEmail.getHost(),
					cfgEmail.getPort(),
					cfgEmail.getUser(),
					cfgEmail.getPassword(),
					cfgEmail.getSendFrom());
		}
		if (quiet){
			LogControllerImpl.getInstance().createUserLogMessage(LogController.CONFIG,LogController.ALERT_CONTROLLER,LogController.MSG_QUIET_ON);
		} else {
			LogControllerImpl.getInstance().createUserLogMessage(LogController.CONFIG,LogController.ALERT_CONTROLLER,LogController.MSG_QUIET_OFF);
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#activateAlert(de.kabuman.tinkerforge.alarm.units.ProtectUnit, java.lang.String, short)
	 */
	public synchronized void activateAlert(ProtectUnit protectUnit, String sensorName, int msgId, short alertType) {
		if (!isOn || isAlertActive){
			return;
		}
		
		this.protectUnit = protectUnit;
		this.sensorName = sensorName;
		this.msgId = msgId;
		this.alertType = alertType;
		
		isAlertActive = true;
		isAlertOccurred = true;
		
		LogControllerImpl.getInstance().createUserLogMessage(protectUnit.getUnitName(), sensorName, msgId);
		if (cfgEmail.isEmailRequested()){
			sendEmail(LogControllerImpl.getInstance().getConsoleRecord());
		}


		
		// Start Observer of the Duration of the Alarm Signal
		alertObserver = new AlertObserverImpl(this, selectAlertDuration());

		// Start Observer to create Alarm Signal
		new AlertIntervallObserverImpl(this,alertType);
		
	}
	
	private void sendEmail(String text){
		StringBuffer sb = new StringBuffer();
		sb.append("Alarm wurde ausgelöst!\n\n");
		sb.append(text);
		emailService.sendMail(cfgEmail.getSendTo(), "Alarm!!!", sb.toString());
	}
	
	/**
	 * Returns the alert duration depending on the type of alert
	 * 
	 * @return int - selected alert duration
	 */
	private int selectAlertDuration(){
		switch (alertType) {
		case AlertController.ALERT_TYPE_INTRUSION:
			return alertDurationIntrusion;
		case AlertController.ALERT_TYPE_WATER:
			return alertDurationWater;
		case AlertController.ALERT_TYPE_FIRE:
			return alertDurationFire;
		default:
			return 30000; 
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#pauseAlert()
	 */
	public void pauseAlert(){
		for (int i = 0; i < alertUnitList.size(); i++) {
			Unit alertUnit = alertUnitList.get(i);
			alertUnit.deactivateAlert();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#startAlert()
	 */
	public void startAlert(){
		if (alarmDisplay != null){
			try {
				displayAlert();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < alertUnitList.size(); i++) {
			
			// All Alert Units
			if (alertUnitList.get(i) instanceof AlertUnit){
				AlertUnit alertUnit = (AlertUnit) alertUnitList.get(i);
				alertUnit.activateAlert(protectUnit, sensorName, quiet);
			}
			
			// Only the Protect Unit which signals the alert
			if (alertUnitList.get(i) == protectUnit){
				// it is the alert triggering Protect Unit
				if  (!quiet){
					alertUnitList.get(i).activateAlert();
				}
			}
		}
	}
	
	private void displayAlert() throws TimeoutException, NotConnectedException{
		switch (alertType) {
		case ALERT_TYPE_INTRUSION:
			displayMessages("Pruefe Einbruch!");
			break;

		case ALERT_TYPE_WATER:
			displayMessages("Pruefe Wassereinbruch!");
			break;

		case ALERT_TYPE_FIRE:
			displayMessages("Pruefe Feuer/Rauch!");
			
			break;

		default:
			break;
		}
	}

	private void displayMessages(String msg){
//		if (messageShown){
//			return;
//		}
		try {
			alarmDisplay.clearDisplay();
			alarmDisplay.backlightOn();
			Date date = new Date();
			alarmDisplay.writeLine((short)0, (short)0, FormatterService.getDateDDMMYYYY(date) + "  " + FormatterService.getDateHHMM(date));
			alarmDisplay.writeLine((short)1, (short)0, protectUnit.getUnitName());
			alarmDisplay.writeLine((short)2, (short)0, sensorName);
			alarmDisplay.writeLine((short)3, (short)0, msg);
			messageShown = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void resetAlarmDisplay(){
		messageShown = false;
		try {
			alarmDisplay.clearDisplay();
			alarmDisplay.backlightOff();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#deactivateAlert()
	 */
	public synchronized void deactivateAlert() {
		if (!isAlertActive){
			return;
		}
		
		for (int i = 0; i < alertUnitList.size(); i++) {
			Unit alertUnit = alertUnitList.get(i);
			alertUnit.deactivateAlert();
		}
		
		isAlertActive = false;
		
		// if it is called from Reset-Switch:
		if (alertObserver != null){
			alertObserver.deactivate();
		}
		
	}

	/**
	 * @param alertUnitList
	 */
	public void setAlertUnitList(List<Unit> alertUnitList){
		this.alertUnitList = alertUnitList;
	}
	
	public static AlertControllerImpl getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#switchOn()
	 */
	public void switchOn() {
		isOn = true;
		
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#switchOff()
	 */
	public void switchOff() {
		isOn = false;
		deactivateAlert();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#isOn()
	 */
	public boolean isOn() {
		return isOn;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#setQuiet(boolean)
	 */
	public void setQuiet(boolean isQuiet){
		this.quiet = isQuiet;
		if (isQuiet){
			deactivateAlert();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#isQuiet()
	 */
	public boolean isQuiet(){
		return quiet;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.controller.AlertController#isAlertActive()
	 */
	public boolean isAlertActive(){
		return isAlertActive;
	}

	public boolean isAlertOccurred(){
		return isAlertOccurred;
	}
	
	public void setAlertOccurred(boolean isAlertOccurred) {
		this.isAlertOccurred = isAlertOccurred;
		
		if (messageShown){
			resetAlarmDisplay();
		}
	}

	public void setAlarmDisplay(BrickletLCD20x4 alarmDisplay) {
		this.alarmDisplay = alarmDisplay;
		resetAlarmDisplay();
	}

	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
	}
}
