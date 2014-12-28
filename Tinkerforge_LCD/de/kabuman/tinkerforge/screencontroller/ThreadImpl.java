package de.kabuman.tinkerforge.screencontroller;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;



/**
 * Displays a clock at the given line and position in the specified format
 * on a 20x4 LCD Bricklet
 */
public class ThreadImpl extends Thread implements SwitchOffBacklight{

	private BrickletLCD20x4 lcd = null;
	
	// Calculated sleep time in milliseconds
	private long switchOffAfter;

	// activates/deactivates the display of the clock
	private boolean active = true;

	
	/**
	 * Constructor and Starter
	 * 
	 * @param switchOffAfter - switch off after "switchOffAfter"- milliseconds
	 */
	public ThreadImpl(BrickletLCD20x4 lcd, long switchOffAfter) {
		this.lcd = lcd;
		this.switchOffAfter = switchOffAfter;
		
		start();  // calls the run() method
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.DisplayClock#terminate()
	 */
	public void terminate(){
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
		while (true) {
			
			if (active){
				goSleep(switchOffAfter);
				if (active){
					backlightOff();
				}
			}
			
			goSleep(1000);
		}
	}


	private void backlightOff(){
		if (lcd == null){
			if (ScreenControllerImpl.getInstance() == null){
				System.out.println("No BrickletLCD20x4 defined and no ScreenController Instance detected");
			} else {
				ScreenControllerImpl.getInstance().backlightOff();
			}
		} else {
			try {
				lcd.backlightOff();
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean isActive() {
		return active;
	}
	
	public synchronized void  startBacklight(){
		active = true;
		this.interrupt();
	}
	
	private void goSleep(long sleepTime){
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			if (active){
				goSleep(sleepTime);
				System.out.println("Refresh");
			} else {
				System.out.println("Termination");
			}
		}
	}
	
}
