package de.kabuman.tinkerforge.alarm.units;

import de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;


/**
 * Interface for Protect Units
 */
public interface ProtectUnit extends Unit{
	
	/**
	 * Activates the alert signals(LED, Beeper) which are connected to this Protect Unit
	 * 
	 * @param sensorName - the sensor which triggers the alert
	 * @param msgId - the id of the message which is to log
	 * @param alertType - kind of alert: intrusion, water, ...
	 */
	void activateAlert(String sensorName, int msgId, short alertType);
	
	/**
	 * Returns the open sensor item
	 * 
	 * @return OpenSensorItem - the open sensor object
	 */
	OpenSensorItem getOpenSensorItem();
	
	/**
	 * Returns the alert LED item
	 * 
	 * @return OutputItem
	 */
	OutputItem getLEDItem();

}
