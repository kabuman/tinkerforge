package de.kabuman.tinkerforge.screencontroller;

import java.util.List;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRotaryEncoder;

import de.kabuman.tinkerforge.screencontroller.items.Item;


/**
 * Interface for ScreenController
 * 
 * screen
 * is the complete LCD with 20 columns and 4 lines.
 * 
 * screenId: 
 * Every screenId has a id to identify it.
 * 
 * item
 * is a variable content/field on a screen.
 * 
 * mask
 * is a string (for one line) which contains not editable constants for a screen.
 * see maskLine.
 * 
 * maskLine
 * is a line of screen which contains not editable contants, 
 * for instance field names or something like that.
 * 
 */
public interface ScreenController {

	// Used to assign a electrode to this functions
	// These values may not be used as function values by other CommonService Consumer
	int NEXT_SCREEN = -1;
	int PREV_SCREEN = -2;
	int BACKLIGHT_OFF = -3;
	int BACKLIGHT_ON = -4;
	int BACKLIGHT_ON_TEMP = -5;
	int DEFAULT_SCREEN = -6;
	
	/**
	 * Set up the sequence of screens <br>
	 * The forward / backward button follows this sequence  <br>
	 *  <br>
	 *  You can set the parm as "new ArrayList<Integer>(Arrays.asList(1,2,3)))" <br>
	 * @param screenIdList - List<Integer> one or more sequcence id's 
	 */
	void setScreenSequence(List<Integer> screenIdList);
	
	
	/**
	 * Adds a new screen to the sequence of screens  <br>
	 * 
	 * @param screenId
	 */
	void addNewScreenToSequence(int screenId);
	
	
	/**
	 * Add a mask line (line with contants) to screen controller <br>
	 * 
	 * @param screenId - screen id 
	 * @param lineId - line number: 0,..,3
	 * @param maskLine - line of mask/screen
	 */
	void addMaskLine(int screenId, int lineId, String maskLine);

	
	/**
	 * Activates the given screen id and starts the ScreenController
	 * 
	 * @param screenId - screen id
	 * @param backlightOn - true: backlight ON; false: backlight OFF; null: no change
	 * @param backlightSwitchedOnDuration - duration in milliseconds of which the backlight is switched on 
	 */
	void activateScreen(int screenId, Boolean backlightOn, Long backlightSwitchedOnDuration);
	
	
	/**
	 * Activates the given screen id (and starts the ScreenController if not)
	 * Backlight will be switched on permanently
	 * 
	 * @param screenId - screen id
	 */
	void activateScreen(int screenId);
	
	
	/**
	 * Returns the actual list of added items
	 * 
	 * @return List<Display> - the items to display
	 */
	List<Item> getItemList();

	
	/**
	 * Set the list of items to display
	 * 
	 * @param item - the screen item
	 */
	void setItemList(List<Item> itemList);
	
	
	/**
	 * Add an item to screen controller
	 * 
	 * @param item - the screen item
	 */
	void addItem(Item item);
	
	
	/**
	 * Switch ON the backlight and returns if switch was really required  <br>
	 * Activates all screen items which are defined for the current active screen  <br>
	 * 
	 * @return boolean -  <br> 
	 * true : backlight was OFF and is switched ON now   <br>
	 * false: backlight was already ON, no switch required
	 */
	public boolean backlightOn();

	
	/**
	 * Switch Off the backlight and returns if switch was really required <br>
	 * Deactivates all screen items which are defined for the current active screen  <br>
	 * 
	 * @return boolean -  <br> 
	 * true : backlight was ON and is switched OFF no  <br>
	 * false: backlight was already OFF, no switch required
	 */
	public boolean backlightOff();
	
	
	/**
	 * Set the default screen id for the 4. button on LCD
	 * @param defaultScreenId - if null the first defined screen becomes the default screen
	 */
	public void assignLcdButton3ToScreenId(Integer defaultScreenId);


	/**
	 * Assigns the given screenId to the given electrodeId
	 * 
	 * @param electrodeId - the bit number of the electrode
	 * @param screenId - the screen Id
	 */
	public void assignMultiTouchElectrodeToScreenId(int electrodeId, int screenId);
	
	
	/**
	 * Enables/disables the auto switch back to default screen
	 * @param autoSwitchBackToDefaultScreen
	 */
	public void activateAutoSwitchBackToDefaultScreen(boolean autoSwitchBackToDefaultScreen);

	
	/**
	 * Enables/disables the auto switch back to default screen
	 * @param autoSwitchBackToDefaultScreen - auto switch back time in milliseconds
	 */
	public void setAutoSwitchBackToDefaultScreenTime(long autoSwitchBackTime);

	
	/**
	 * Replaces the given old item source by the new ones .
	 * Therefore it searches the old item source in the item list.
	 * 
	 * @param oldItemSource - the old item source instance
	 * @param newItemSource - the new item source instance
	 */
	void replaceItemSource(Object oldItemSource, Object newItemSource);
	 
	
	/**
	 * Returns the next free screen id
	 * @return int - next free screen id (will be 1, if there was no screen id before)
	 */
	int getFreeScreenId();

	
	/**
	 * Set a new instance of bricklet LCD
	 * @param lcd - BrickletLCD20x4
	 */
	void replaceLcd(BrickletLCD20x4 lcd);
	
	
	/**
	 * Set a new instance of bricklet rotary encoder
	 * @param tfRotaryEncoder - BrickletRotaryEncoder
	 */
	void replaceRotaryEncoder(BrickletRotaryEncoder tfRotaryEncoder);
	
	
	/**
	 * Set a new instance of bricklet multi touch
	 * @param tfMultiTouch - BrickletMultiTouch
	 */
	public void replaceMultiTouch(BrickletMultiTouch tfMultiTouch);

	
	/**
	 * Writes text to the LCD
	 * @param line - line 0,..,3
	 * @param position - position 0,..,19
	 * @param text - max. length 20
	 */
	void writeLine(int line, int position, String text);

	
	/**
	 * Set custom character to the LCD
	 * @param index - index 0,..,7
	 * @param character - short[]
	 */
	void setCustomCharacter(short index, short[] character);

	
	/**
	 * Returns true if backlight is ON. Otherwise false.
	 * @return boolean - true if backlight is ON. Otherwise false.
	 */
	boolean isBacklightOn();
	
	
	/**
	 * Clears the display 
	 */
	void clearDisplay();
	
	
	/**
	 * Setup the Hall Clock and Hall Date Swap Function
	 * @param activate - boolean true: acticate it / false: deactivate it
	 * @param durationHallClock - long milliseconds: duration to show Hall Clock
	 * @param durationHallDate - long milliseconds: duration to show Hall Date
	 */
	void setHallClockDateSwap(boolean activate, Long durationHallClock, Long durationHallDate);
	
	
	/**
	 *  Set the flag that the custom characters for Hall Clock  <br>
	 *  and Hall Date are already defined and loaded.
	 */
	void setCustomCharAlreadyLoaded();
	
	
	/**
	 * Set clone LCD list
	 * @param lcds
	 */
	public void setCloneLcdList(BrickletLCD20x4...lcds);

	
	/**
	 * Replaces the given old clone lcd by a new one
	 * @param oldClonelcd - BrickletLCD20x4
	 * @param newClonelcd - BrickletLCD20x4
	 */
	void replaceCloneLcd(BrickletLCD20x4 oldClonelcd, BrickletLCD20x4 newCloneLcd);


	/**
	 * Deactivate all screen items without clearDisplay
	 */
	public void deactivateAll();
	
	/**
	 * @return boolean - true: alive indicator is active / false: if not
	 */
	public boolean isUseAliveIndicator();


	/**
	 * Set the alive indicator <br>
	 * true: alive indicator appears in the upper left corner (screenId is blinking)
	 * @param useAliveIndicator
	 */
	public void setUseAliveIndicator(boolean useAliveIndicator);

	
	/**
	 * @return Int - active screen id
	 */
	public Integer getActiveScreenId();
	
	
	/**
	 * Report the current settings of Screen Controller 
	 */
	void report();
	
	boolean isAutoSwitchBackOn();


}
