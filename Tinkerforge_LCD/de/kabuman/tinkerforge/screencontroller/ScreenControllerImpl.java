package de.kabuman.tinkerforge.screencontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserver;
import de.kabuman.common.services.CommonObserverImpl;
import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.tinkerforge.customchar.CustomCharDefinitionLargeNumbers;
import de.kabuman.tinkerforge.screencontroller.items.HallClockDateSwapImpl;
import de.kabuman.tinkerforge.screencontroller.items.Item;
import de.kabuman.tinkerforge.screencontroller.items.ScreenAliveIndicator;
import de.kabuman.tinkerforge.screencontroller.items.ScreenAliveIndicatorImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallClock;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallDate;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemReplace;
import de.kabuman.tinkerforge.services.IOService;
import de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer;
import de.kabuman.tinkerforge.services.multitouch.MultiTouchSupplierImpl;
import de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer;
import de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderSupplierImpl;

/**
 * ScreenController - Singleton  <br>
 * Manages the output to the given LCD Display <br>
 *  <br>
 * See Interface for definitions and API.  <br>
 *  <br>
 *  
 * So, 28.09.
 * replaceItem() aufgeräumt
 * replaceCloneLcd() implementiert
 * 
 * So, 14.09
 * Entprellzeit vom LcdListener wieder von 100 auf 500 zurück geändert.
 * War am 7.9. im Zuge der parametrisiert von 500 auf 100 gesetzt worden.
 * 
 * Einbau BrickletMultiTouch
 * 
 * So, 07.09.
 * Entprellzeit (debounce) für die LCD Listener (pressed & released) parametrisiert
 * 
 * Backlight Status ermitteln und in die var backlightON setzen wenn:
 * - ScreenController instanziiert wird
 * - replaceLcd(..) aufgerufen wird
 * 
 * BarometerSensorItem / WetterApp
 * - das Auslesen des ersten Wertes optimiert (getAirPressure liefert falschen Wert)
 * 
 * ScreenItem
 * ItemSourceToPullAlarm: Auch diese können jetzt mit Formatierungsparametern formatiert werden.
 * - siehe hierzu auch WetterApp. Dort ist es für die Anzeige des Barometer Wertes implementiert
 * 
 * 
 */
public class ScreenControllerImpl implements 
	ScreenController
	, CommonCallback
	, RotaryEncoderConsumer
	, MultiTouchConsumer{
	
	// Instance
	private static ScreenControllerImpl instance = null;
	
	
	
	
	// GUI OBJECTS: SCREENS, ITEMS, HALL CLOCK, HALL DATE

	// GUI elements and controls
	private List<Item> itemList = new ArrayList<Item>();
	private String screenMask[][] = new String[1000][4];
	private List<Integer> screenSequence = new ArrayList<Integer>();
	private Integer activeScreenId = null;
	private boolean backlightON = false;
	private Integer defaultScreenId = null; // // Assignment to button3 (0-3) of LCD / variable Electrode of MultiTouch Bricklet

	// Hall Clock & Hall Date
	private HallClockDateSwapImpl hallClockDateSwapController = null;
	private Long durationHallClock = null;
	private Long durationHallDate = null;
	private boolean activateHallClockDateSwap = false;
	private Integer screenIdHallClock = null;
	private Integer screenIdHallDate = null;
	private boolean customCharAlreadyLoaded = false;
	private ScreenAliveIndicator aliveIndicator;
	private boolean useAliveIndicator = true;

	
	
	// SUPPORT BRICKLETs
	
	// Support: BrickletLCD20x4 (master) for ScreenController
	private BrickletLCD20x4 lcd;

	// Support: BrickletLCD20x4: Clone LCDs
	private List<BrickletLCD20x4> cloneLcdList = new ArrayList<BrickletLCD20x4>();
	
	// Support: BrickletRotaryEncoder
	private BrickletRotaryEncoder tfRotaryEncoder;
	private RotaryEncoderSupplierImpl tfRotaryEncoderSupplier;	
		
	// Support: BrickletMultiTouch
	private BrickletMultiTouch tfMultiTouch;
	private MultiTouchSupplierImpl tfMultiTouchSupplier;
	private Integer multiTouchScreenIdArray[]= new Integer[7];
	
	
	
	// OBSERVER, TIMER, STOPWATCH
	
	// StopWatch: LCD Button 0-3 Pressed Timer
	private StopWatchService lcdButtonPressedTimer = new StopWatchServiceImpl();
	private final long lcdButton0PressedThreshold = 1000;
	private final int lcdButtonDebounce = 500;
	
	// Observer: to switch off the backlight automatically
	private final static int BACKLIGHT_OBSERVER = 1;
	private final long backlightSwitchedOnDuration = 11000;
	private CommonObserver backLightObserver = new CommonObserverImpl(this, BACKLIGHT_OBSERVER, backlightSwitchedOnDuration, "Backlight Observer");

	// Observer: to switch back to default screen
	private Long autoSwitchBackTime = 10000l;
	private boolean autoSwitchBackToDefaultScreen = false;
	private final static int RETURN_TO_DEFAULT_SCREEN = 2;
	private CommonObserver returnToDefaultScreen = new CommonObserverImpl(this, RETURN_TO_DEFAULT_SCREEN, autoSwitchBackTime,"ScreenController: AutoSwitchBack");


	
	/**
	 * Constructor 
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param tfRotaryEncoder - the rotary encoder bricklet / may be null if not to support
	 * @param tfMultitouch - the multi touch bricklet / may be null if not support 
	 * @param itemList - list with items to display / may be null (in this case must be set by method 
	 */
	public ScreenControllerImpl(BrickletLCD20x4 lcd, BrickletRotaryEncoder tfRotaryEncoder, BrickletMultiTouch tfMultitouch, List<Item> itemList) {
		if (lcd == null){
			throw new IllegalArgumentException("ScreenControllerImpl::Constructor: lcd is null");
		}
		
		this.lcd = lcd;
		this.tfRotaryEncoder = tfRotaryEncoder;
		this.tfMultiTouch = tfMultitouch;
		instance = this;
		
		// Detect the backlight state
		backlightON = isBacklightOn();

		
		if (itemList != null){
			this.itemList = itemList;
		}
		
		addButtonPressedListener();
		addButtonReleasedListener();
		

		if (useAliveIndicator){
			aliveIndicator = new ScreenAliveIndicatorImpl(lcd, null, "?", 0, 0);
			aliveIndicator.setActive(false);
		}
		
//		backLightObserver = new CommonObserverImpl(this, BACKLIGHT_OBSERVER, backlightSwitchedOnDuration, "Backlight Observer");
		
		if (tfRotaryEncoder != null){
			createRotaryEncoderSupport();
		}

		if (tfMultitouch != null){
			createMultiTouchSupport();
		}
	}

	
	public void report(){
		String title = "ScreenController: Report:";
		
		String tBacklight = "Backlight State=";
		String tIsBacklightOn = Boolean.toString(isBacklightOn());
		
		String tAutoBacklightOffTime = "AutoBacklightOffTime=";
		String tAutoBacklightOffTimeValue = Long.toString(backlightSwitchedOnDuration);
		
		String tAutoSwitchBack = "AutoSwitchBack=";
		String tIsAutoSwitchBack = Boolean.toString(isAutoSwitchBackOn());
		
		String tAutoSwitchBackTime = "AutoSwitchBackTime=";
		String tAutoSwitchBackTimeValue = Long.toString(autoSwitchBackTime);
		
		if (LogControllerImpl.getInstance() == null){
			System.out.println(getTS()+"  "+ title + tBacklight + tIsBacklightOn);
			System.out.println(getTS()+"  "+ title + tAutoBacklightOffTime + tAutoBacklightOffTimeValue);
			System.out.println(getTS()+"  "+ title + tAutoSwitchBack + tIsAutoSwitchBack);
			System.out.println(getTS()+"  "+ title + tAutoSwitchBackTime + tAutoSwitchBackTimeValue);
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(title, tBacklight, tIsBacklightOn);
			LogControllerImpl.getInstance().createTechnicalLogMessage(title, tAutoBacklightOffTime, tAutoBacklightOffTimeValue);
			LogControllerImpl.getInstance().createTechnicalLogMessage(title, tAutoSwitchBack, tIsAutoSwitchBack);
			LogControllerImpl.getInstance().createTechnicalLogMessage(title, tAutoSwitchBackTime, tAutoSwitchBackTimeValue);
		}
	}
	
	
	/**
	 * Returns a timestamp to write out for logging purpose
	 * @return string - formated as "kk:mm:ss.SSS"
	 */
	private String getTS(){
		return FormatterService.getDateHHMMSSS(new Date());
	}
	

	/**
	 *  Creates the BrickletRotaryEncoder Support
	 */
	private void createRotaryEncoderSupport(){
		tfRotaryEncoderSupplier = new RotaryEncoderSupplierImpl(this, tfRotaryEncoder);
		tfRotaryEncoderSupplier.activate();
		tfRotaryEncoderSupplier.setPressedTimer(1000l, true);
	}

	
	/**
	 * Creates the BrickletMultiTouchSupport 
	 */
	private void createMultiTouchSupport(){
		tfMultiTouchSupplier = new MultiTouchSupplierImpl(this, tfMultiTouch, MultiTouchSupplierImpl.ALL_ELECTRODES_WITHOUT_13, 1000l);
		tfMultiTouchSupplier.setElectrodeSensitivity(100);
		tfMultiTouchSupplier.activate();
	}

	
	/**
	 * Returns the instance
	 * @return instance
	 */
	public static ScreenControllerImpl getInstance(){
		return instance;
	}


	/**
	 * Returns a new Instance of this class
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param itemList - list with item threads (may be null)
	 * @return Instance of this class
	 */
	public static ScreenControllerImpl getNewInstance(BrickletLCD20x4 lcd, BrickletRotaryEncoder tfRotaryEncoder, List<Item> itemList){
		instance = new ScreenControllerImpl(lcd, tfRotaryEncoder, null, itemList);
		return instance;
	}


	/**
	 * Returns a new Instance of this class
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param tfRotaryEncoder - the Rotary Encoder Bricklet
	 * @param tfMultiTouch - the Multi Touch Bricklet
	 * @param itemList - list with item threads (may be null)
	 * @return Instance of this class
	 */
	public static ScreenControllerImpl getNewInstance(BrickletLCD20x4 lcd, BrickletRotaryEncoder tfRotaryEncoder, BrickletMultiTouch tfMultitouch, List<Item> itemList){
		instance = new ScreenControllerImpl(lcd, tfRotaryEncoder, tfMultitouch, itemList);
		return instance;
	}


	/**
	 * Returns a new Instance of this class
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param tfRotaryEncoder - the Rotary Encoder Bricklet
	 * @param tfMultiTouch - the Multi Touch Bricklet
	 * @return Instance of this class
	 */
	public static ScreenControllerImpl getNewInstance(BrickletLCD20x4 lcd, BrickletRotaryEncoder tfRotaryEncoder, BrickletMultiTouch tfMultitouch){
		instance = new ScreenControllerImpl(lcd, tfRotaryEncoder, tfMultitouch, null);
		return instance;
	}


	/**
	 * Returns a new Instance of this class  <br>
	 * This requires the setting of the itemList later by an additional method call
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @return new Instance of this class
	 */
	public static ScreenControllerImpl getNewInstance(BrickletLCD20x4 lcd){
		instance = new ScreenControllerImpl(lcd, null, null, null);
		return instance;
	}


	/**
	 * Returns a new Instance of this class  <br>
	 * This requires the setting of the itemList later by an additional method call
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param tfRotaryEncoder - the Rotary Encoder Bricklet
	 * @return new Instance of this class
	 */
	public static ScreenControllerImpl getNewInstance(BrickletLCD20x4 lcd, BrickletRotaryEncoder tfRotaryEncoder){
		instance = new ScreenControllerImpl(lcd, tfRotaryEncoder, null, null);
		return instance;
	}


	/**
	 * Installs ButtonPressedListener
	 */
	private void addButtonPressedListener(){
		lcd.addButtonPressedListener(new LcdButtonPressedListener(this, lcdButtonDebounce));

		addButtonPressedListenerForCloneLcds();
	}

	
	/**
	 * Installs ButtonPressedListener for Clone LCDs
	 */
	private void addButtonPressedListenerForCloneLcds(){
		for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
			cloneLcd.addButtonPressedListener(new LcdButtonPressedListener(this, lcdButtonDebounce));
		}
	}

	
	/**
	 * Automatic Backlight Switch OFF Control  <br>
	 * Start the "LCD Button Pressed Timer" <br> 
	 *  <br>
	 * Used by LcdButtonPressedListener
	 */
	public void startLcdButtonPressedTimer(){
		lcdButtonPressedTimer.restart();
		lcdButtonPressedTimer.start();
	}

	/**
	 * Switch backlight depending on the current state
	 */
	public void switchBacklightONorOFF(){
		// Switch backlight on or off
		if (isBacklightOn()){
				backlightOff();
		} else {
				backlightOn();
		}
	}
	
	
	/**
	 * <pre>
	 * Switch to next or previous screen 
	 *  
	 * If backlight is currently OFF: 
	 * - The backlight will be switched ON temporarily only.   
	 * - No switch to next screen. 
	 * 
	 * If backlight is ON:
	 * - It will be switched to next screen or previous screen
	 * </pre> 
	 * @param nextScreen - true: nextScreen / false: previousScreen
	 */
	public void switchToNextOrPreviousScreen(boolean nextScreen){
		// Switch to next screen defined by screenSequence
		if (isBacklightOn()){
			if (nextScreen){
				activateNextScreen();
			} else {
				activatePreviousScreen();
			}
			refreshTimeForTemporarilyBacklight();
		} else {
			switchTemporarilyBacklightOn(backlightSwitchedOnDuration);
		}
	}

	
	public void switchToDefaultScreen(){
		// Switch to default screen defined by screenSequence
		if (isBacklightOn()){
			activateDefaultScreen();
			refreshTimeForTemporarilyBacklight();
		} else {
			switchTemporarilyBacklightOn(backlightSwitchedOnDuration);
		}
	}
	

	/**
	 * Refreshes the time of the temporarily switched on Backlight. <br>
	 *  <br>
	 * If no thread for controlling temporarily switched on thread is active, <br>
	 * nothing is to do.  
	 */
	public void refreshTimeForTemporarilyBacklight(){
		if (backLightObserver.isObservationActive()){
			backLightObserver.startObservation();
		}
	}
	
	
	/**
	 * Installs ButtonReleasedListener 
	 */
	private void addButtonReleasedListener(){
		lcd.addButtonReleasedListener(new LcdButtonReleasedListener(this, lcdButtonDebounce));
		addButtonReleasedListenerForCloneLCDs();
	}
	

	/**
	 * Installs ButtonReleasedListener 
	 */
	private void addButtonReleasedListenerForCloneLCDs(){
		for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
			cloneLcd.addButtonReleasedListener(new LcdButtonReleasedListener(this, lcdButtonDebounce));
		}
	}
	

	/**
	 * Automatic Backlight Switch OFF Control  <br>
	 * Release Button 0 Handling <br>
	 *  <br>
	 * If Button 0 was pressed shorter than the threshold  <br>
	 * the backlight will be switched ON temporarily only. <br>
	 * Otherwise it will be switched ON permanent. <br>
	 * 
	 * Used by LcdButtonReleasedListener
	 */
	public void handleReleasedLcdButton0(){
		// Switch backlight: permament or temporarily
		if (isBacklightOn()){
			
			// Backlight was switched on by pressing this button
			
			lcdButtonPressedTimer.stopOver();
			if (lcdButtonPressedTimer.getCurrent() < lcdButton0PressedThreshold){
				
				// LCD button 0 was released within the given threshold for temporarily ON time
				// So the the Switch-OFF-Thread to control it is to start
				switchTemporarilyBacklightOn(backlightSwitchedOnDuration);
			}
		}
	}
	
	
	/**
	 * Switch ON the backlight temporarily.  <br>
	 *  <br>
	 * - Terminates the running "backlight switch off thread" (if exists)  <br>
	 * - Creates a new one  <br> 
	 * - Switch on the backlight  <br>
	 * @param backlightSwitchedOnDuration - duration of backlight in milliseconds
	 */
	private void switchTemporarilyBacklightOn(long backlightSwitchedOnDuration){
		backlightOn();
		backLightObserver.startObservation(); 
		activate(getActiveScreenId(), true);
	}

	
	/**
	 * Refreshes the backlight observation if it is running
	 * @return boolean - true: yes, refreshed  /  false: no, not required caused by not running
	 */
	private boolean refreshBacklightObservation(){
		if (backLightObserver.isObservationActive()){
			backLightObserver.stopObservation(); 
			backLightObserver.startObservation();
			return true;
		}
		return false;
	}
	
	
	/**
	 * @return boolean - true: yes, stop executed  /  false: no, no stop required caused by not running
	 */
	private boolean stopBacklightObservation(){
		if (backLightObserver.isObservationActive()){
			backLightObserver.stopObservation(); 
			return true;
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#backlightOn()
	 */
	public boolean backlightOn(){
		try {
			lcd.backlightOn();
			backlightON = true;

			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				cloneLcd.backlightOn();
			}

			if (activeScreenId != null){
				activate(activeScreenId, true);
			}
			return true;
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
			return false;
		}
	}

	
//	/* (non-Javadoc)
//	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#backlightOn()
//	 */
//	public synchronized boolean backlightOn(){
//		try {
//			if (backlightON == false){
//				lcd.backlightOn();
//				backlightON = true;
//				
//				for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
//					cloneLcd.backlightOn();
//				}
//
//				if (activeScreenId != null){
//					activate(activeScreenId, true);
//				}
//				return true;
//			} else {
//				return false;
//			}
//		} catch (TimeoutException | NotConnectedException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	
//	/* (non-Javadoc)
//	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#backlightOff()
//	 */
//	public synchronized boolean backlightOff(){
//		try {
//			if (backlightON){
//				lcd.backlightOff();
//				backlightON = false;
//
//
//				for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
//					cloneLcd.backlightOff();
//				}
//
//				// deactivate all items for the current active screen
//				activate(activeScreenId, false);
//				
//				// deactivate the possible activated auto-switch-back to default screen
//				if (returnToDefaultScreen.isObservationActive()){
//					returnToDefaultScreen.stopObservation();
//				}
//				
//				return true;
//			} else {
//				return false;
//			}
//		} catch (TimeoutException | NotConnectedException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//	
//	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#backlightOff()
	 */
	public boolean backlightOff(){
		try {
			lcd.backlightOff();
			backlightON = false;


			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				cloneLcd.backlightOff();
			}

			// deactivate all items for the current active screen
			activate(activeScreenId, false);

			// deactivate the possible activated auto-switch-back to default screen
			if (returnToDefaultScreen.isObservationActive()){
				returnToDefaultScreen.stopObservation();
			}

			return true;
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#isBacklightOn()
	 */
	public boolean isBacklightOn(){
		return backlightON;
	}
	
	public boolean isAutoSwitchBackOn(){
		return autoSwitchBackToDefaultScreen;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#setScreenLine(int, int, java.lang.String)
	 */
	@Override
	public void addMaskLine(int screenId, int lineId, String maskLine){
		screenMask[screenId][lineId] = maskLine;
	}
	

	/**
	 * Activates the default screen.
	 */
	private void activateDefaultScreen(){
		activateDefaultScreen(true);
	}
	
	
	/**
	 * Activates the default screen.
	 */
	private void activateDefaultScreen(Boolean backlightON){
		int screenId;
		
		if (defaultScreenId != null){
			// activate the screen
			screenId = defaultScreenId;
		} else {
			// if defaultScreenId not specified... take the first screen
			screenId = screenSequence.get(0);
		}

		activateScreen(screenId, backlightON, null);
	}
	
	
	/**
	 * Activates the next screen.  <br>
	 * The sequence is given by list of screenSequence.
	 */
	private void activateNextScreen(){
		// switch to next screen
		int next = findPositionOfActiveScreenId() + 1;

		// switch to number one if there is no next screen
		if (next >= screenSequence.size()){
			next = 0;
		} 

		// activate the screen
		activateScreen(screenSequence.get(next));
	}
	
	
	/**
	 * Activates the next screen.
	 * The sequence is given by list of screenSequence.
	 */
	private void activatePreviousScreen(){
		// switch to next screen
		int previous = findPositionOfActiveScreenId() - 1;

		// switch to number one if there is no next screen
		if (previous < 0){
			previous = screenSequence.size() - 1;
		} 

		// activate the screen
		activateScreen(screenSequence.get(previous));
	}
	
	
	/**
	 * Returns the position of the active screen
	 * @return int - the position of the active screen
	 */
	private int findPositionOfActiveScreenId(){
		checkScreenSequence();
		
		// find position of active screen
		for (int i = 0; i < screenSequence.size(); i++) {
			if (screenSequence.get(i).equals(activeScreenId)){
				return i;
			}
		}
		throw new IllegalArgumentException("ScreenController:: findPositionOfActiveScreenId: active screenId not found.");
	}


	/**
	 * Deactivate all screen items
	 */
	public void deactivateAll(){
		for (Item screenItem : itemList) {
			screenItem.setActive(false);
		}
	}


	/**
	 * Activates or deactivates all screen items for the given screen id  <br>
	 *  <br>
	 * @param screenId - the target screen id
	 * @param active - true: activate / false: deactivate
	 * @return Item - the last activated or deactivated screen item of the given screen id
	 */
	private Item activate(int screenId, boolean active){
		Item lastFoundItem = null;
		
		// activate/deactivates the display threads which have the given screen id 
		for (Item item : itemList) {
			if (item.getScreenId() == screenId){
				item.setActive(active);
				lastFoundItem = item;  // required for the "Hall Clock Date" Thread
			} 		
		}
		return lastFoundItem;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#activateScreen(int)
	 */
	@Override
	public void activateScreen(int screenId){
		activateScreen(screenId, true, null);
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#activateScreen(int)
	 */
//	@Override
	public void activateScreen(int screenId, Boolean userRequestedBacklightOn, Long userRequestedBacklightSwitchedOnDuration){
		
		// deactivated: allows the refresh of an already active screen after adding item / replacing source etc.
//		if (activeScreenId != null 
//				&& activeScreenId == screenId 
//				&& activateScreenAfterReplaceItemSource == false){
//			return;
//		}
		
		activeScreenId = screenId;
		
		deactivateAll();
		
		if (useAliveIndicator){
			aliveIndicator.setActive(false);
		} 

		clearDisplay();
		writeMaskLines(screenId);
		
		// activate the display threads which have the given screen id 
		Item lastFoundItem = activate(screenId, true);
		
		// Write the screenId to the left upper corner of the display
		writeScreenIdToDisplay(screenId, lastFoundItem);
		
		// Check and switch on or of th backlight
		handleUserRequestedBackLightOn(userRequestedBacklightOn, userRequestedBacklightSwitchedOnDuration);

		refreshBacklightObservation();
		
		handleAutoSwitchBackToDefaultScreen();
		
		// report the screen change to the "Hall Clock Date" Controller
		if (activateHallClockDateSwap){
			hallClockDateSwapController.reportScreenChange(activeScreenId);
		}

	}


	/**
	 * Handles the "Auto Switch Back" to a default screen 
	 */
	private void handleAutoSwitchBackToDefaultScreen(){
		// default screen is active: nothing to do
		if (activeScreenId == defaultScreenId){
			if (returnToDefaultScreen.isObservationActive()){
				returnToDefaultScreen.stopObservation();
			}
			return;
		} 
		
		// no auto-switch-back requested
		if (autoSwitchBackToDefaultScreen == false){
			if (returnToDefaultScreen.isObservationActive()){
				returnToDefaultScreen.stopObservation();
			}
			return;
		}

		// Start observation
		returnToDefaultScreen.startObservation();
	}
	
	
	/**
	 * Checks and switch ON or OFF if required in the given duration in milliseconds  <br>
	 * 
	 * @param userRequestedBacklightOn - Null: no action / True: backlight ON / False: backlight OFF 
	 * @param userRequestedBacklightSwitchedOnDuration - Null: permantely to switch / duration in milliseconds: temporarily to switch
	 */
	private void handleUserRequestedBackLightOn(Boolean userRequestedBacklightOn, Long userRequestedBacklightSwitchedOnDuration){
		if (userRequestedBacklightOn != null){
			if (userRequestedBacklightOn){
				backlightOn();

				if (userRequestedBacklightSwitchedOnDuration != null){
					// temporarily backlight requested
					switchTemporarilyBacklightOn(userRequestedBacklightSwitchedOnDuration);
				}
			} else {
				backlightOff();
			}
		}
	}

	
	/**
	 * Write the screenId to the left upper corner of the display  <br>
	 *  <br>
	 * But not if one of the following special screen items are active:  <br>
	 * - Hall Clock
	 * - Hall Date
	 *  <br>
	 * @param screenId - the screen id to write 
	 * @param lastFoundItem
	 */
	private void writeScreenIdToDisplay(int screenId, Item lastFoundItem){
		if (lastFoundItem instanceof ScreenHallClock
				|| lastFoundItem instanceof ScreenHallDate){
			if (useAliveIndicator){
				aliveIndicator.setActive(false);
			}
		} else {
			if (useAliveIndicator){
				aliveIndicator.setAliveIndicator(screenId+" ");
				aliveIndicator.setActive(true);
			} else {
				if (screenSequence.size() > 1){
					writeLine((short)0, (short)0, screenId+" ");
				}
			}
		}
	}
	
	
	/**
	 * Writes the mask lines to the given screen id 
	 * @param screenId
	 */
	private void writeMaskLines(int screenId){
		for (short lineId = 0; lineId < 4; lineId++) {
				writeLine(lineId, 0, screenMask[screenId][lineId]);
		}
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#getItemList(de.kabuman.tinkerforge.services.threads.Display[])
	 */
	@Override
	public List<Item> getItemList() {
		return itemList;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#setItemList(java.util.List)
	 */
	@Override
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
		checkItemList();
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#addItem(de.kabuman.tinkerforge.services.threads.Display)
	 */
	@Override
	public void addItem(Item item) {
		itemList.add(item);
		checkAndloadCustomCharDefinitionLargeNumbers(item);
	}

	
	/**
	 * Check item type and loads custom char definitions for Hall Clock and Hall Date if required
	 * @param item - the item to check
	 */
	private void checkAndloadCustomCharDefinitionLargeNumbers(Item item){
		if (customCharAlreadyLoaded){
			return;
		}
		
		if (item.getLcd() != null){
			return;
		}
		
		
		if (item instanceof ScreenHallClock
				|| item instanceof ScreenHallDate){

			// item does not know the LCD bricklet
			// so custom character could not exists on LCD

			// define and load
			new CustomCharDefinitionLargeNumbers(lcd);

			// set flag 
			customCharAlreadyLoaded = true;
		}
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.controller.ScreenController#setScreenSequence(int[])
	 */
	@Override
	public void setScreenSequence(List<Integer> screenIdList) {
		this.screenSequence = screenIdList;
		checkScreenSequence();
	}


	/**
	 * Checks if item list is not empty
	 * Checks if the load of custom char required
	 */
	private void checkItemList(){
		if (itemList.size()<1){
			createIllegalArgumentException("itemList is empty");
		}
		for (Item item : itemList) {
			checkAndloadCustomCharDefinitionLargeNumbers(item);
		}
	}
	
	
	/**
	 * Checks the screen sequence list is not empty 
	 */
	private void checkScreenSequence(){
		if (screenSequence.size()<1){
			createIllegalArgumentException("screenSequence is empty");
		}
	}

	
	/**
	 * Throws illegal argument exception
	 * 
	 * @param reason - the reason for that
	 */
	private void createIllegalArgumentException(String reason){
		throw new IllegalArgumentException("ScreenController:: "+reason);
	}


//	@Override
//	public void setDefaultScreenId(Integer defaultScreenId, Long...switchBackToDefaultScreens) {
//		this.defaultScreenId = defaultScreenId;
//
//		// take over the time to switch back automatically
//		if (switchBackToDefaultScreens != null){
//			switch (switchBackToDefaultScreens.length) {
//			case 0:
//				// no time parameter
//				break;
//
//			case 1:
//				// time parameter specified
//				this.switchBackToDefaultScreen = switchBackToDefaultScreens[0];
//				break;
//
//			default:
//				// to many parameter
//				throw new IllegalArgumentException("ScreenController::setDefaultScreenId: max. one switchBackToDefaultScreens parameter allowed");
//			}
//		}
//		
//		
//		if (this.defaultScreenId == null || this.switchBackToDefaultScreen == null){
//			returnToDefaultScreen.stopObservation();
//		} else {
//			returnToDefaultScreen.setObservationTime(switchBackToDefaultScreen);
//		}
//	}
//
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#assignLcdButton3ToScreenId(java.lang.Integer)
	 */
	public void assignLcdButton3ToScreenId(Integer defaultScreenId) {
		this.defaultScreenId = defaultScreenId;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#assignMultiTouchElectrodeToScreenId(int, int)
	 */
	public void assignMultiTouchElectrodeToScreenId(int electrodeId, int screenId) {
			multiTouchScreenIdArray[electrodeId] = screenId;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#activateAutoSwitchBackToDefaultScreen(boolean)
	 */
	public void activateAutoSwitchBackToDefaultScreen(boolean autoSwitchBackToDefaultScreen){
		this.autoSwitchBackToDefaultScreen = autoSwitchBackToDefaultScreen;
		handleAutoSwitchBackToDefaultScreen();
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#replaceItemSource(java.lang.Object, java.lang.Object)
	 */
	public void replaceItemSource(Object oldItemSource, Object newItemSource) {
		for (Item item : itemList) {
			
			if (item instanceof ScreenItem){
				if (((ScreenItemReplace)item).replaceItemSource(oldItemSource, newItemSource)){
					
					// item replaced
					if (item.isActive()){
						
						// item is active
						if (item.getScreenId() == activeScreenId){
							
							// item is on the current active screen: activate it 
							// Important here: including temp. backlight ON, if it is not permanent ON
							activateScreen(activeScreenId, null, null);
						}
					}
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#getFreeScreenId()
	 */
	public int getFreeScreenId(){
		int freeScreenId = 1;
		for (Item item : itemList) {
			freeScreenId = (item.getScreenId() > freeScreenId) ? item.getScreenId() : freeScreenId;
		}
		
		for (Integer screenId : screenSequence) {
			freeScreenId = (screenId > freeScreenId) ? screenId : freeScreenId;
		}

		return (freeScreenId == 1) ? freeScreenId : ++freeScreenId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#addNewScreenToSequence(int)
	 */
	public void addNewScreenToSequence(int screenId) {
		screenSequence.add(screenId);
		
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setLcd(com.tinkerforge.BrickletLCD20x4)
	 */
	public synchronized void replaceLcd(BrickletLCD20x4 lcd) {
		if (backLightObserver.isObservationActive()){
			backLightObserver.stopObservation(); 
		}
		if (returnToDefaultScreen != null 
				&& returnToDefaultScreen.isObservationActive()){
			returnToDefaultScreen.stopObservation();
		}

		this.lcd = lcd;
		
		
//		if (backlightON){
//			// backlight was ON before LCD get lost
//			
//			// force backlight ON
//			backlightON = false; 
//			
//			// switch ON
//			backlightOn(); 
//		} else {
//			// backlight was OFF before LCD get lost
//			
//			// force backlight OFF
//			backlightON = true;
//			
//			// switch OFF
//			backlightOff();
//		}

		// switch ON
		backlightOn(); 

		for (Item display : itemList) {
			display.setLcd(lcd);
		}
		addButtonPressedListener();
		addButtonReleasedListener();
		
		// Refresh ScreenAliveIndicator
		if (aliveIndicator != null){
			aliveIndicator.setLcd(lcd);
		}
		

		if (backlightON){
			activateScreen(activeScreenId, null, null);
		} else {
			writeMaskLines(activeScreenId);
		}
		
	}
	

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setCloneLcdList(com.tinkerforge.BrickletLCD20x4[])
	 */
	public void setCloneLcdList(BrickletLCD20x4...lcds) {
		cloneLcdList = new ArrayList<BrickletLCD20x4>(Arrays.asList(lcds));
		aliveIndicator.setCloneLcdList(lcds);
		
		addButtonPressedListenerForCloneLcds();
		addButtonReleasedListenerForCloneLCDs();
		
		if (activeScreenId != null){
			activateScreen(activeScreenId);
		}
		if (isBacklightOn()){
			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				try {
					cloneLcd.backlightOn();
				} catch (TimeoutException | NotConnectedException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				try {
					cloneLcd.backlightOff();
				} catch (TimeoutException | NotConnectedException e) {
					e.printStackTrace();
				}
			}
		}
	}
		

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#writeLine(int, int, java.lang.String)
	 */
	public void writeLine(int line, int position, String text){
		try {
			lcd.writeLine((short)line, (short)position, text);
			
		} catch (TimeoutException | NotConnectedException e) {
			log("writeLine","lcd.writeLine: Timeout or NotConnectedException. text="+text);
//			e.printStackTrace();
		}

		try {
			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				cloneLcd.writeLine((short)line, (short)position, text);
			}
		} catch (TimeoutException | NotConnectedException e) {
			log("writeLine","cloneLcd.writeLine: Timeout or NotConnectedException. text="+text);
//			e.printStackTrace();
		}
	}


	/**
	 * Write log 
	 * 
	 * @param method - the caller method
	 * @param msg - the message to log
	 */
	private void log(String method, String msg){
		if (LogControllerImpl.getInstance() == null){
			System.out.println(FormatterService.getDate(new Date())+" ScreenController::"+method+": "+msg);
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage("ScreenController", method, msg);
		}

	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setCustomCharacter(short, short[])
	 */
	public void setCustomCharacter(short index, short[] character) {
		try {
			lcd.setCustomCharacter(index, character);
			
			for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
				cloneLcd.setCustomCharacter(index, character);
			}

		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#clearDisplay()
	 */
	@Override
	public void clearDisplay() {
		try {
			lcd.clearDisplay();
			
		} catch (TimeoutException | NotConnectedException e) {
			log("clearDisplay","lcd.clearDisplay(): Timeout or NotConnectedException");
		}
		
		for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
			try {
				cloneLcd.clearDisplay();
			} catch (TimeoutException | NotConnectedException e) {
				log("clearDisplay","cloneLcd.clearDisplay(): Timeout or NotConnectedException for cloneLcd="+cloneLcd.toString());
			}

		}
	}


	/**
	 *  Creates a Thread to control the swap between Hall Clock and Hall Date
	 */
	private void createHallClockDateSwap(){
		
		screenIdHallClock = null;
		screenIdHallDate = null;
		
		for (Item item : itemList) {
			if (item instanceof ScreenHallClock){
				screenIdHallClock = item.getScreenId();
			}
			if (item instanceof ScreenHallDate){
				screenIdHallDate = item.getScreenId();
			}
			
		}

		if (screenIdHallClock == null || screenIdHallDate == null){
			return;
		}
		
		// Create and start the Thread
		hallClockDateSwapController = new HallClockDateSwapImpl(screenIdHallClock, durationHallClock, screenIdHallDate, durationHallDate);
		
		if (activeScreenId != null){
			hallClockDateSwapController.reportScreenChange(activeScreenId);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setHallClockDateSwap(boolean, java.lang.Long, java.lang.Long)
	 */
	public void setHallClockDateSwap(boolean activate, Long durationHallClock, Long durationHallDate) {
		this.activateHallClockDateSwap = activate;
		this.durationHallClock = durationHallClock;
		this.durationHallDate = durationHallDate;
		
		createHallClockDateSwap();
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setCustomCharAlreadyLoaded()
	 */
	public void setCustomCharAlreadyLoaded() {
		this.customCharAlreadyLoaded = true;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonCallback#commonObserverTriggeredMethod(java.lang.Integer)
	 */
	@Override
	public void commonObserverTriggeredMethod(Integer functionCode) {
		switch (functionCode) {
		case RETURN_TO_DEFAULT_SCREEN:
			if (isBacklightOn()){
				activateDefaultScreen();
			}
			break;

		case BACKLIGHT_OBSERVER:
			backlightOff();
			break;

		default:
			break;
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#isUseAliveIndicator()
	 */
	public boolean isUseAliveIndicator() {
		return useAliveIndicator;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#setUseAliveIndicator(boolean)
	 */
	public void setUseAliveIndicator(boolean useAliveIndicator) {
		this.useAliveIndicator = useAliveIndicator;
	}


	public Integer getActiveScreenId() {
		return activeScreenId;
	}


	@Override
	public void setAutoSwitchBackToDefaultScreenTime(long autoSwitchBackTime) {
		autoSwitchBackToDefaultScreen = true;
		this.autoSwitchBackTime = autoSwitchBackTime;

		// Stopp the current observation
		if (returnToDefaultScreen.isObservationActive()){
			returnToDefaultScreen.stopObservation();
		}

		// Set new observation time
		returnToDefaultScreen.setObservationTime(autoSwitchBackTime);
		
		// Activates "Auto Switch Back" if it is required with the new time
		handleAutoSwitchBackToDefaultScreen();
	}


	@Override
	public void replaceCloneLcd(BrickletLCD20x4 oldCloneLcd, BrickletLCD20x4 newCloneLcd) {
		List<BrickletLCD20x4> cloneLcdList = new ArrayList<BrickletLCD20x4>();
		
		for (BrickletLCD20x4 cloneLcdFromList : cloneLcdList) {
			if (cloneLcdFromList == oldCloneLcd){
				cloneLcdFromList = newCloneLcd;
			}
		}
	}

	
	@Override
	public void rotaryEncoderCounterValueChanged(int newValue, int oldValue) {
		if (newValue > oldValue){
			switchToNextOrPreviousScreen(true);
		} else {
			switchToNextOrPreviousScreen(false);
		}
	}

	@Override
	public void rotaryEncoderPressed() {
		if (isBacklightOn()){
			backlightOff();
		} else {
			switchTemporarilyBacklightOn(backlightSwitchedOnDuration);
		}
	}

	@Override
	public void rotaryEncoderReleased(long pressedDuration) {
		System.out.println("rotaryEncoderReleased:: not implemented method");
	}

	@Override
	public void rotaryEncoderPressedTimeReached() {
		if (isBacklightOn()){
			if (backLightObserver.isObservationActive()){
				backLightObserver.stopObservation(); 
			}

			backlightOn();
		}
	}

	@Override
	public void replaceRotaryEncoder(BrickletRotaryEncoder tfRotaryEncoder) {
		tfRotaryEncoderSupplier.replaceRotaryEncoder(tfRotaryEncoder);
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.ScreenController#replaceMultiTouch(com.tinkerforge.BrickletMultiTouch)
	 */
	@Override
	public void replaceMultiTouch(BrickletMultiTouch tfMultiTouch) {
		tfMultiTouchSupplier.replaceMultiTouch(tfMultiTouch);
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer#multiTouchStateValueChanged(int, int)
	 */
	@Override
	public synchronized void multiTouchStateValueChanged(int newValue, int oldValue) {

		// Iterate over the 12 multitouch electrodes
		for (int i = 0; i < 12; i++) {
			
			// Electrode touched?
			if (IOService.isSwitchedON(i, newValue) && multiTouchScreenIdArray[i] != null){
				if (multiTouchScreenIdArray[i] > -1){
					
					// Activate the assigned screen
					activateScreen(multiTouchScreenIdArray[i]);
				} else {
					
					// Special action is assigned
					switch (multiTouchScreenIdArray[i]) {
					case ScreenController.NEXT_SCREEN:
						switchToNextOrPreviousScreen(true);
						break;

					case ScreenController.PREV_SCREEN:
						switchToNextOrPreviousScreen(false);
						break;

					case ScreenController.BACKLIGHT_ON:
						stopBacklightObservation();
						backlightOn();
						break;

					case ScreenController.BACKLIGHT_ON_TEMP:
						switchTemporarilyBacklightOn(backlightSwitchedOnDuration);
						break;

					case ScreenController.BACKLIGHT_OFF:
						backlightOff();
						break;

					case ScreenController.DEFAULT_SCREEN:
						switchToDefaultScreen();
						break;

					default:
						break;
					}
				}
				break;
			} else {
				// Electrode was not touched
				// Check if it was released
				
//				if (IOService.isSwitchedON(i,  oldValue)){
//					System.out.println("ScreenController::multiTouchStateValueChanged: released electrode="+i);
//				}
			}
		}
		
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer#multiTouchStateValueTimerReached(int)
	 */
	@Override
	public void multiTouchStateValueTimerReached(int state) {
		System.out.println("multiTouchStateValueTimerReached:: not implemented method");
	}
	
	
}
