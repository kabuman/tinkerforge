package de.kabuman.tinkerforge.alarm.threads;

import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.AlertController;




public class AlertObserverImpl extends Thread implements AlertObserver{

	// Alarm Signal Object
	AlertController alertController = null;
	
	// duration in milli-sec before switch-off
	int duration;

	boolean isAlive = false;
	
	boolean active = true;

	/**
	 * Constructor
	 */
	public AlertObserverImpl(AlertController alertController, int duration) {
		this.alertController = alertController;
		this.duration = duration;
		
		LogControllerImpl.getInstance().createTechnicalLogMessage("AlertObserver", "Start", "duration="+duration);


		start();  // calls the run() method
	}


	public void deactivate(){
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
		// Sleeps and then switch OFF the Alarm Signal
		try {
			Thread.sleep(duration);
			if (active){
				alertController.deactivateAlert();
				LogControllerImpl.getInstance().createTechnicalLogMessage("AlertObserver", "End", "AlertController.deactivateAlert() triggered");
			}
			deactivate();
		} catch (InterruptedException e) {
//			System.out.println("AlertObserverImpl: Sleep interrupted by Reset-Switch");
		} 
	}
}
