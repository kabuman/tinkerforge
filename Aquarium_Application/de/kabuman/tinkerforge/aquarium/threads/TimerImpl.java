package de.kabuman.tinkerforge.aquarium.threads;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogControllerImpl;



public class TimerImpl extends Thread{
	
	TimerData timerData;

	long milliSec;
	
	DateFormat simpleDateTimeFormat = new SimpleDateFormat("dd.MM.yy kk:mm:ss");
	
	boolean interrupted = false;

	
	/**
	 * Configures and starts the Timer
	 * 
	 * @param timerData - timer data
	 */
	public TimerImpl(TimerData timerData) {
		this.timerData = timerData;

		LogControllerImpl.getInstance().createTechnicalLogMessage(getID(), "started", "Execution="+simpleDateTimeFormat.format(timerData.getDateTime()));

		start();  // calls the run() method
	}


	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		milliSec = timerData.getDateTime().getTime() - new Date().getTime();
		LogControllerImpl.getInstance().createTechnicalLogMessage(getID(), "calculated sleep", "milliSec="+milliSec);
		
		if (milliSec < 100){
			LogControllerImpl.getInstance().createTechnicalLogMessage(getID(), "stopped", "timer in the past");
			handleEndOfLive();
			return;
		}
		
		try {
			Thread.sleep(milliSec);
			try {
				timerData.getBrickServo().setPosition(timerData.getServoId(), timerData.getPosition());
				LogControllerImpl.getInstance().createTechnicalLogMessage(getID(), "executed", "Servo-Position="+timerData.getPosition());
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		handleEndOfLive();
		return;

	}
	
	private void handleEndOfLive(){
		interrupted = true;
		interrupt();
		
	}
//	private short getTimerId(){
//		return (short) (timerData.getTimerId()+1);
//	}

	private String getID(){
		String s = "Thread "
				+ timerData.getThreadId()
				+ ": Servo "
				+ (short) (timerData.getServoId())
				+ "/"
				+ "Timer"
				+ (short) (timerData.getTimerId());

		return s;
	}


	public boolean isInterrupted() {
		return interrupted;
	}
	
}
