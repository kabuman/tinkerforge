package de.kabuman.tinkerforge.screencontroller.items;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;


/**
 * Displays a short name for the day of the week above the colon  <br>
 * and the day of the month below the colon  <br>
 * on a 20x4 LCD Bricklet
 */
public class ScreenHallDayImpl extends AbstractItem implements ScreenHallClock{

	// Constructor Parameter
	private Integer screenId;

	// Calculated sleep time in milliseconds
	private long sleepTime;

	// activates/deactivates the display of the clock
	private boolean active = true;

	CustomCharDriverLargeNumbers hallClockNumber = null;
	
	Date displayDate = null;	
	
	boolean showColon = true;
	
	
	/**
	 * Defines the colon for the large clock item to display on a 20x4 LCD Display <br>
	 * Constructor and Starter
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param screenId - screen id (if Null: Clock becomes immediately active state)
	 */
	public ScreenHallDayImpl(BrickletLCD20x4 lcd
			, Integer screenId) {
		super(lcd);
		
		check(screenId,"screenId", 0, 999, true);
		
		this.screenId = screenId;
		
		if (screenId == null){
			// not operated by ScreenController: set active
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

	
	/**
	 * Display the week day (if active)
	 * Calculates the new sleeping time for this thread
	 * 
	 * @param active - true: yes this thread is active, false: not active
	 * @return long - the new calculated sleeping time for this thread
	 */
	private long displayAndCalculate(boolean active){
		displayDate = new Date();

		sleepTime = getNextDayDate() - new Date().getTime();
		
		if (sleepTime < 0){
			sleepTime = 0;
		}


		if (active){
			writeWeekDay();
		}

		return sleepTime; 
	}

	
	/**
	 * Writes the weekday and the day of the month to the LCD 
	 */
	private void writeWeekDay(){
//			System.out.println("ScreenHallDayImpl::writeWeekDay");
		writeLine((short)0, (short) 8, String.format("%ta", new Date()) + ".");
		writeLine((short)3, (short) 8, String.format("%td", new Date()) + ".");
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
			writeWeekDay();
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
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#getLineId()
	 */
	public int getLineId() {
		return 0;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#getPosition()
	 */
	public int getPosition() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.ScreenItem#replaceItemSource(java.lang.Object)
	 */
//	@Override
	public boolean replaceItemSource(Object oldItemSource, Object newItemSource) {
			return false;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setLcd(com.tinkerforge.BrickletLCD20x4)
	 */
	@Override
	public void setLcd(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
	}

}
