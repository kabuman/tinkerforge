package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class DisplayObserverThreadImpl extends Thread implements DisplayObserver{

	// Keep display object
	BrickletLCD20x4 lcd = null;
	
	// duration in milli-sec before switch-off
	long duration;

	boolean isAlive = false;
	
	boolean active = true;

	/**
	 * Constructor
	 * 
	 * @param lcd -
	 *            the instantiated lcd device object (used to switch off the
	 *            backlight)
	 */
	public DisplayObserverThreadImpl(BrickletLCD20x4 lcd, long duration) {
		this.lcd = lcd;
		this.duration = duration;

		try {
			lcd.backlightOn();
			lcd.backlightOn();
			lcd.backlightOn();
			lcd.backlightOn();
			lcd.backlightOn();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
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
		// Sleep a few seconds and then switch OFF
		try {
			Thread.sleep(duration);
			if (active){
				lcd.backlightOff();
			}
			deactivate();
		} catch (InterruptedException e) {
			// Will happen if backlight is on and is switch on again
//			System.out.println("DisplayObserverThreadImpl:: run: InterruptedException. ");
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
