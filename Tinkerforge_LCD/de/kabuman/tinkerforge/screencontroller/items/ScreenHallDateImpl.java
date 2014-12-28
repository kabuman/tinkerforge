package de.kabuman.tinkerforge.screencontroller.items;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;


/**
 * Displays a large Date which take place over the whole display
 * on a 20x4 LCD Bricklet
 */
public class ScreenHallDateImpl extends AbstractItem implements ScreenHallDate{

	// Constructor Parameter
	private BrickletLCD20x4 lcd;
	private Integer screenId;

	// Calculated sleep time in milliseconds
	private long sleepTime;

	// activates/deactivates the display of the clock
	private boolean active = true;

	CustomCharDriverLargeNumbers hallClockNumber = null;
	
	Date displayDate = null;	
	
	Item aliveIndicatorDotsItem;

	
	/**
	 * Defines a large clock item to display on a 20x4 LCD Display <br>
	 * Constructor and Starter
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param screenId - screen id (if Null: Clock becomes immediately active state)
	 */
	public ScreenHallDateImpl(BrickletLCD20x4 lcd
			, Integer screenId) {
		super(lcd);
		
		check(screenId,"screenId", 0, 999, true);
		
		this.lcd = lcd;
		this.screenId = screenId;
		
		// Define custom characters on LCD device
		hallClockNumber = new CustomCharDriverLargeNumbers(lcd);
		
		// Start thread (not active) to display the colon between hours and minutes 
		aliveIndicatorDotsItem = new ScreenAliveIndicatorImpl(lcd, screenId, ScreenAliveIndicator.TYPE_HALLDATE_DOTS);
		

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
		active = false;
		this.interrupt();
		aliveIndicatorDotsItem.setActive(false);
		aliveIndicatorDotsItem.terminate();

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
		sleepTime = getNextDayDate() - new Date().getTime();
		
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
		if (lcd != null){
			try {
				lcd.clearDisplay();
			} catch (TimeoutException | NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (ScreenControllerImpl.getInstance() != null){
				ScreenControllerImpl.getInstance().clearDisplay();
			}
		}
		
		Date date = new Date();
		hallClockNumber.writeWeekMonthDay(date);

	}
	
	
	/**
	 * Calculates the date for the next day
	 * Set hours, minutes, seconds and milliseconds to zero
	 * 
	 * @return date - the calculated future date
	 */
	private long getNextDayDate(){
		Date dateToCut = new Date();
		dateToCut = DateTimeService.add(dateToCut, DateTimeService.DAYS, 1);
		return DateTimeService.replaceTime(dateToCut,0,0,0,0).getTime();
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
			aliveIndicatorDotsItem.setActive(true);
		} else {
			aliveIndicatorDotsItem.setActive(false);
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
		aliveIndicatorDotsItem.setLcd(lcd);
	}
	
}
