package de.kabuman.tinkerforge.alarm.units;


/**
 * Interface for Alert Units
 */
public interface AlertUnit extends Unit{
	
	/**
	 * Activate Alert for this Alert Unit
	 * 
	 * @param protectUnit - the Protect Unit which triggers the alert
	 * @param message - the alert giving sensor 
	 * @param isQuiet - true if beeper is not to activate for the alert
	 */
	public void activateAlert(ProtectUnit protectUnit, String message, boolean isQuiet);
	
	/**
	 * Deactivate the alert (switch of the beeper)
	 */
	public void deactivateAlert();
	
}
