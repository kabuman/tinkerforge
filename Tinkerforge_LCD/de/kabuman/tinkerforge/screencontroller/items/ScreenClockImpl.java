package de.kabuman.tinkerforge.screencontroller.items;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

import de.kabuman.common.services.DateTimeService;


/**
 * Displays a clock at the given line and position in the specified format
 * on a 20x4 LCD Bricklet
 */
public class ScreenClockImpl extends AbstractItem implements ScreenClock{

	// Constructor Parameter
	private Integer screenId;
	private int format;
	private int lineId;
	private int position;

	// Calculated sleep time in milliseconds
	private long sleepTime;

	// activates/deactivates the display of the clock
	private boolean active = true;

	// the formated date
	private StringBuffer text = null;
	
	ScreenAliveIndicator aliveIndicator1 = null;
	
	
	/**
	 * Defines a clock item to display on a 20x4 LCD Display <br>
	 * Constructor and Starter
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param format - format (see interface DisplayClock for usable constants)
	 * @param screenId - screen id (if Null: Clock becomes immediately active state)
	 * @param lineId - line number: 0,..,3
	 * @param position - start position to display: 0,..,19
	 */
	public ScreenClockImpl(BrickletLCD20x4 lcd
			, int format
			, Integer screenId
			, int lineId
			, int position) {
		super(lcd);
		
		check(screenId,"screenId", 0, 999, true);
		check(lineId,"lineId",0,3, false);
		check(position,"postion",0,19, false);
		
		this.screenId = screenId;
		this.format = format;
		this.lineId = lineId;
		this.position = position;
		
		if (screenId == null){
			// not operated by ScreenController
			active = true;
		} else {
			// operated by ScreenController
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
	 * Display the date with the selected format (if active)
	 * Calculates the new sleeping time for this thread
	 * 
	 * @param active - true: yes this thread is active, false: not active
	 * @return long - the new calculated sleeping time for this thread
	 */
	private long displayAndCalculate(boolean active){
		Date displayDate = new Date();

		switch (format) {
		case FORMAT_DATE_TIME_L:
			// dd.MM.yyyy-kk:mm:ss
			text = new StringBuffer(DateTimeService.DF_DATE_TIME_L.format(displayDate));
			sleepTime = getNextSecondDate() - new Date().getTime();
			break;

		case FORMAT_DATE_TIME_M:
			// dd.MM.yy-kk:mm:ss
			text = new StringBuffer(DateTimeService.DF_DATE_TIME_M.format(displayDate));
			sleepTime = getNextSecondDate() - new Date().getTime();
			break;

		case FORMAT_DATE_TIME_S:
			// dd.MM.yy-kk:mm
			text = new StringBuffer(DateTimeService.DF_DATE_TIME_S.format(displayDate));
			sleepTime = getNextMinuteDate() - new Date().getTime();
			break;

		case FORMAT_DATE_TIME_XS:
			// dd.MM-kk:mm
			text = new StringBuffer(DateTimeService.DF_DATE_TIME_XS.format(displayDate));
			sleepTime = getNextMinuteDate() - new Date().getTime();
			break;

		case FORMAT_TIME_L:
			// kk:mm:ss
			text = new StringBuffer(DateTimeService.DF_TIME_L.format(displayDate));
			sleepTime = 0;
			break;

		case FORMAT_TIME_M:
			// kk:mm:ss
			text = new StringBuffer(DateTimeService.DF_TIME_M.format(displayDate));
			sleepTime = getNextSecondDate() - new Date().getTime();
			break;

		case FORMAT_TIME_S:
			// HH:MM
			text = new StringBuffer(DateTimeService.DF_TIME_S.format(displayDate));
			sleepTime = getNextMinuteDate() - new Date().getTime();
			break;

		case FORMAT_DATE_L:
			text = new StringBuffer(DateTimeService.DF_DATE_L.format(displayDate));
			sleepTime = getNextDayDate() - new Date().getTime();
			break;

		case FORMAT_DATE_M:
			// dd.MM.yy
			text = new StringBuffer(DateTimeService.DF_DATE_M.format(displayDate));
			sleepTime = getNextDayDate() - new Date().getTime();
			break;

		case FORMAT_DATE_S:
			// dd.MM
			text = new StringBuffer(DateTimeService.DF_DATE_S.format(displayDate));
			sleepTime = getNextDayDate() - new Date().getTime();
			break;

		case FORMAT_WEEK_DAY_S:
			// Day of the week in 2 letter: Mo,Di,Mi,Do,Fr,Sa,So
			text = new StringBuffer(String.format("%ta", displayDate));
			sleepTime = getNextDayDate() - new Date().getTime();
			break;

		default:
			throw new IllegalArgumentException("DisplayClockImpl:: displayAndCalculate: not valid clock format="+format);
		}
		
		if (active){
			writeClock();
		}

		if (sleepTime < 0){
			sleepTime = 0;
		}
		
		return sleepTime; 
	}

	
	/**
	 * Writes the clock to the LCD 
	 */
	private void writeClock(){
			writeLine((short)lineId, (short)position, text.toString());
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
	
	
	/**
	 * Calculates the date for the next second
	 * Set milliseconds to zero
	 * 
	 * @return date - the calculated future date
	 */
	@SuppressWarnings("deprecation")
	private long getNextSecondDate(){
		Date dateToCut = new Date();
		return DateTimeService.replaceTime(dateToCut
				, dateToCut.getHours()
				, dateToCut.getMinutes()
				, dateToCut.getSeconds() + 1
				, 0)
				.getTime();
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
		
		if (active && text != null){
			writeClock();
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
		return lineId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#getPosition()
	 */
	public int getPosition() {
		return position;
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
