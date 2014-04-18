package de.kabuman.tinkerforge.alarm.threads;

import de.kabuman.tinkerforge.alarm.controller.AlertController;




/**
 * @author Karsten Buchmann
 *
 */
public class AlertIntervallObserverImpl extends Thread{

	// Alarm Signal Object
	AlertController alertController = null;
	
	boolean isAlive = false;
	
	boolean active = true;

	short alertType = 0;
	
	/**
	 * Constructor 
	 * 
	 * This thread will be terminated implicit by the AlertController
	 * Implicit means that this thread will ask the Alert Controller after each internval if the Alert is still active.
	 * 
	 * @param alertController - the AlertController instance
	 */
	public AlertIntervallObserverImpl(AlertController alertController, short alertType) {
		this.alertController = alertController;
		this.alertType = alertType;
		
		start();  // calls the run() method
	}

	/**
	 * Deactivates the thread 
	 */
	private void deactivate(){
		active = false;
		this.interrupt();
	}
	
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (active){
			
			switch (alertType) {
			case AlertController.ALERT_TYPE_INTRUSION:
				alertController.startAlert();
				break;

			case AlertController.ALERT_TYPE_WATER:
				while (alertController.isAlertActive()) {
					intervallAlert(2000,2000);
				}
				break;

			case AlertController.ALERT_TYPE_FIRE:
				while (alertController.isAlertActive()) {
					intervallAlert(500,500);
				}
				
				break;

			default:
				break;
			}
			
		}
		deactivate();
	}
	
	
	/**
	 * Implements alert intervall for different kinds of alerts
	 *  
	 * @param alertDuration - in msec
	 * @param pauseDuration - in msec
	 */
	private void intervallAlert(long alertDuration, long pauseDuration){
		
		// Start Alarm
		alertController.startAlert();
		
		// Duration of alert
		alertPause(alertDuration); 	
		
		// Start Pause
		alertController.pauseAlert();
		
		// Duration of pause
		alertPause(pauseDuration);
	}
	
	
	/**
	 * Creates a pause with the given duration in msec.
	 * 
	 * @param duration - pause duration in msec.
	 */
	private void alertPause(long duration){
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
