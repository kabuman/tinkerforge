package de.kabuman.tinkerforge.screencontroller.items;

import com.tinkerforge.BrickletLCD20x4;


public interface ScreenAliveIndicator extends Item {
	
	int TYPE_HALLCLOCK_COLON = 1;
	int TYPE_HALLDATE_DOTS = 2;
	int TYPE_INDIVIDUAL_STRG = 3;

	
	/**
	 * Set a new string of alive indicator
	 * To use in connection with alive type=TYPE_INDIVIDUAL_STRG
	 * @param aliveIndicator - the new string of alive indicator
	 */
	public void setAliveIndicator(String aliveIndicator);
	
	public void setCloneLcdList(BrickletLCD20x4...lcds);

}
