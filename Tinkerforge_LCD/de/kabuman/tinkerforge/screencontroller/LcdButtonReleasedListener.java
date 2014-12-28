package de.kabuman.tinkerforge.screencontroller;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

public class LcdButtonReleasedListener implements BrickletLCD20x4.ButtonReleasedListener{

	ScreenControllerImpl sc;
	Date previousSwitch;
	int debounce; 
	
	
	public LcdButtonReleasedListener(ScreenControllerImpl sc, int debounce) {
		this.sc = sc;
		this.debounce = debounce;
		
		// Set previous switch to current time
		// It is for the debounce detection
		previousSwitch = new Date();
	}

	@Override
	public synchronized void buttonReleased(short button) {
		
		// Consider the debounce time.
		// LCD Buttons have no debounce parameter so it must be manually considered
		if (new Date().getTime() - previousSwitch.getTime() < debounce){
			// consider the released button 0 only once as released
			return;
		}
		

		switch (button) {
		case 0:
			sc.handleReleasedLcdButton0();
			previousSwitch = new Date();

			break;

		default:
			break;
		}
	}

}
