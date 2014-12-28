package de.kabuman.tinkerforge.aquarium.threads;
import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickServo;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.aquarium.config.CfgPosition;



public class TimerObserverImpl extends Thread{
	
	long sleep;
	
	private List<TimerImpl> timerList;
	
	private BrickServo brickServo;
	
	private List<CfgPosition> cfgPositionList;
	
	/**
	 * Configures and starts the Timer
	 * 
	 * @param timerData - timer data
	 */
	public TimerObserverImpl(List<TimerImpl> timerList, long sleep, BrickServo brickServo, List<CfgPosition> cfgPositionList) {
		this.timerList = timerList;
		this.sleep = sleep;
		this.brickServo = brickServo;
		this.cfgPositionList = cfgPositionList;

		start();  // calls the run() method
	}


	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean timerAlive = true;
		
		while (timerAlive) {
			timerAlive = false;
			
			// Searching for Timer which is alive
			for (TimerImpl timerImpl : timerList) {
				if (!timerImpl.isInterrupted()){
					
					// Alive Timer found
					timerAlive = true;
					
					// Sleep until the next alive check is to do
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					break;
				}
			}
		}
		
		// no further alive timer found
		LogControllerImpl.getInstance().createTechnicalLogMessage("TimerObserver", "interrupted", "*** NO FURTHER TIMER ACTIVE ***");
		
		for (CfgPosition cfgPosition : cfgPositionList) {
			try {
				brickServo.setPosition(cfgPosition.getServoId(), cfgPosition.getPositionList().get(0));
				LogControllerImpl.getInstance().createTechnicalLogMessage("TimerObserver: Servo "+cfgPosition.getServoId(), "executed", "Servo-Position="+cfgPosition.getPositionList().get(0));
			} catch (TimeoutException | NotConnectedException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage("TimerObserver: Servo "+cfgPosition.getServoId(), "not executed", "Exception="+e.toString());
			}
		}
		

		interrupt();
		return;
	}
	
	
}
