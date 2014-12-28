package de.kabuman.tinkerforge.screencontroller.items;

import com.tinkerforge.BrickletLCD20x4;

/**
 * Super Interface for both types of ScreenItems: ScreenItem and ScreenClock
 */
public interface Item {
	
	/**
	 * Set this thread active or not
	 * @param active - true: active / false: not active
	 */
	void setActive(boolean active);
	
	/**
	 * Returns if thread is active or not
	 * @return active - true: active / false: not active
	 */
	boolean isActive();
	
	/**
	 * Set screen Id (may be null)
	 * @param screenId
	 */
	void setScreenId(Integer screenId);
	
	/**
	 * Returns screen id
	 * @return screen id
	 */
	Integer getScreenId();
	
	/**
	 * Terminates this thread
	 */
	public void terminate();
	
	/**
	 * Sets the bricklet LCD
	 * @param lcd
	 */
	void setLcd(BrickletLCD20x4 lcd);

	/**
	 * Returns the LCD Bricklet
	 * @return lcd
	 */
	BrickletLCD20x4 getLcd();
	
}
