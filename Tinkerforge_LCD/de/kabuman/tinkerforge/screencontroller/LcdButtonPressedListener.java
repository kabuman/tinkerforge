package de.kabuman.tinkerforge.screencontroller;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

public class LcdButtonPressedListener implements BrickletLCD20x4.ButtonPressedListener{

	ScreenControllerImpl sc;
	Date previousSwitch;
	int debounce; 

	
	public LcdButtonPressedListener(ScreenControllerImpl sc, int debounce) {
		this.sc = sc;
		this.debounce = debounce;

		// Set previous switch to current time
		// It is for the debounce detection
		previousSwitch = new Date();
	}

	@Override
	public synchronized void buttonPressed(short button) {
		sc.startLcdButtonPressedTimer();
		
		// Consider the debounce time.
		// LCD Buttons have no debounce parameter so it must be manually considered
		if (new Date().getTime() - previousSwitch.getTime() < debounce){
			// consider the released button 0 only once as released
			return;
		}

		
		switch (button) {
		case 0:
			sc.switchBacklightONorOFF();
			break;
		case 1:
			sc.switchToNextOrPreviousScreen(true);
			break;
			
		case 2:
			sc.switchToNextOrPreviousScreen(false);
			break;
			
		case 3:
			sc.switchToDefaultScreen();
			break;
			
		default:
			break;
		}
		
		previousSwitch = new Date();
	}

}
