package de.kabuman.tinkerforge.alarm.controller;

import de.kabuman.tinkerforge.alarm.units.ProtectUnit;



/**
 * Alert Controller
 * - de-activates / activates all connected Alert Units
 * - triggers alert to all connected Alert Units
 * - automatically switch off beeper after alert and the defined amount of alert time
 *
 */
public interface AlertController{
	
	final static short ALERT_TYPE_INTRUSION = 1;
	final static short ALERT_TYPE_WATER = 2;
	final static short ALERT_TYPE_FIRE = 3;
 
	/**
	 * Activates the alert. Triggered by a Protect Unit
	 * Synchronized.
	 * 
	 * @param protectionUnit - the triggering Protect Unit
	 * @param sensorName - description (name of sensor etc)
	 * @param alertType - the alert type: intrusion, water, fire. See "final static" definitions above
	 */
	void activateAlert(ProtectUnit protectionUnit, String sensorName, int msgId, short alertType);
	
	/**
	 * Stops the alert (Beeper) immediately of all connected Alert Units
	 * 
	 * Triggered by Quiet-Switch, OnOff-Switch of the Remote control via AlertController
	 * See methods switchOn(); switchOff()
	 * See method  setQuiet();
	 */
	void deactivateAlert();
	
	/**
	 * Switch on the application. All connected sensors will be activated.
	 * 
	 * Triggered by OnOff-Switch of the Remote Control
	 */
	void switchOn();
	
	/**
	 * Switch OFF the application. All connected sensors will be de-activated. 
	 * 
	 * Triggered by OnOff-Switch of the Remote Control
	 */
	void switchOff();
	
	/**
	 * Returns true if application is active (switched on)
	 * See method switchOn()
	 */
	boolean isOn();
	
	/**
	 * Returns true if the alert is still active
	 * 
	 * Provided for the Intervall Observer (false: terminates the Intervall Observer)
	 * 
	 * @return boolean - true: alert is active
	 */
	public boolean isAlertActive();
	
	/**
	 * Paused the alert until the startAlert() method is called. 
	 * This pause has no effect for the duration time of the alert. 
	 * It does not suspend the running "Alert Duration" timer. 
	 * 
	 * Provided for the Intervall Observer
	 */
	public void pauseAlert();
	
	/**
	 * Starts or continues the alert 
	 * 
	 * Provided for the Intervall Observer
	 */
	public void startAlert();
	
	/**
	 * Set the whole alarm application quiet (beeper off) or not quiet (beeper on)
	 * Triggered by Quiet-Switch of the Remote Control
	 * 
	 * @param isQuiet - boolean true: quiet; false: not quiet
	 */
	public void setQuiet(boolean isQuiet);
	
	
	/**
	 * Returns true if the alarm application is switched to quiet
	 * 
	 * @return boolean - true: quiet; false: not quiet
	 */
	public boolean isQuiet();
}
