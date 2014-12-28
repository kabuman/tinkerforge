package de.kabuman.tinkerforge.screencontroller.items;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;


/**
 * Displays a large clock which take place over the whole display
 * on a 20x4 LCD Bricklet
 */
public class ScreenHallClockImpl extends AbstractItem implements ScreenHallClock{

	// Constructor Parameter
	private Integer screenId;

	// Calculated sleep time in milliseconds
	private long sleepTime;

	// activates/deactivates the display of the clock
	private boolean active = true;

	CustomCharDriverLargeNumbers hallClockNumber = null;
	
	Date displayDate = new Date();	
	
	Item hallColonItem;
	ScreenHallClock hallDayItem;
	
	/**
	 * Defines a large clock item to display on a 20x4 LCD Display <br>
	 * Constructor and Starter
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param screenId - screen id (if Null: Clock becomes immediately active state)
	 */
	public ScreenHallClockImpl(BrickletLCD20x4 lcd
			, Integer screenId) {
		super(lcd);
		
		check(screenId,"screenId", 0, 999, true);
		
		this.lcd = lcd;
		this.screenId = screenId;
		
		// Define custom characters on LCD device
		hallClockNumber = new CustomCharDriverLargeNumbers(lcd);

		// Start thread (not active) to display the colon between hours and minutes 
		hallColonItem = new ScreenAliveIndicatorImpl(lcd, screenId, ScreenAliveIndicator.TYPE_HALLCLOCK_COLON);
		
		// Start thread (not active) to display the weekday and the day of month 
		hallDayItem = new ScreenHallDayImpl(lcd, screenId);
		
		if (screenId == null){
			// not operated by ScreenController
			active = true;
		} else {
			// operated by ScreenController: has to wait for activation
			active = false;
		}

		start();  // calls the run() method
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.DisplayClock#terminate()
	 */
	public void terminate(){
		hallColonItem.setActive(false);
		hallColonItem.terminate();

		hallDayItem.setActive(false);
		hallDayItem.terminate();

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

		try {
			while (true) {
				sleepTime = displayAndCalculate(active);
				Thread.sleep(sleepTime);
			}
		} catch (InterruptedException e) {
			// terminates the while loop
		} 
	}
	
	
	/**
	 * Display the date 
	 * Calculates the new sleeping time for this thread
	 * 
	 * @param active - true: yes this thread is active, false: not active
	 * @return long - the new calculated sleeping time for this thread
	 */
	private long displayAndCalculate(boolean active){
		displayDate = new Date();

		// HH:MM
		sleepTime = getNextMinuteDate() - new Date().getTime();
		
		if (sleepTime < 0){
			sleepTime = 0;
		}


		if (active){
			writeClock();
		}

		return sleepTime; 
	}

	
	/**
	 * Writes the clock to the LCD 
	 */
	private void writeClock(){
//		System.out.println("ScreenHallClockImpl::writeClock: displayDate="+displayDate);
		hallClockNumber.writeDate(displayDate);
	}
	
	
	/**
	 * Calculates the date for the next minute
	 * Set seconds and milliseconds to zero
	 * 
	 * @return date - the calculated future date
	 */
	@SuppressWarnings("deprecation")
	private long getNextMinuteDate(){
		Date dateToCut = new Date();
		return DateTimeService.replaceTime(dateToCut
				, dateToCut.getHours()
				, dateToCut.getMinutes() + 1
				, 0
				, 0
				).getTime();
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.Display#isActive()
	 */
	public boolean isActive() {
		return active;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.Display#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
		
		if (active){
			writeClock();
			hallColonItem.setActive(true);
			hallDayItem.setActive(true);
		} else {
			hallColonItem.setActive(false);
			hallDayItem.setActive(false);
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.Display#getScreenId()
	 */
	public Integer getScreenId() {
		return screenId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.Display#setScreenId(java.lang.Integer)
	 */
	public void setScreenId(Integer screenId) {
		this.screenId = screenId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setLcd(com.tinkerforge.BrickletLCD20x4)
	 */
	@Override
	public void setLcd(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
		hallClockNumber.setLcd(lcd);
		hallColonItem.setLcd(lcd);
		hallDayItem.setLcd(lcd);
	}
	
}
