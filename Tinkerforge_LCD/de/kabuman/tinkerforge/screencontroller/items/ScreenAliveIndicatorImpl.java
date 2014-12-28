package de.kabuman.tinkerforge.screencontroller.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.common.services.FormatterService;
import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;


/**
 * Displays a colon between the hours and minutes for the hall clock (large clock)
 * on a 20x4 LCD Bricklet
 */
public class ScreenAliveIndicatorImpl extends AbstractItem implements ScreenAliveIndicator{

	
	private int type;
	
	// Constructor Parameter
	private Integer screenId;
	private String name;

	// Calculated sleep time in milliseconds
	private long sleepTime = 1000;

	// activates/deactivates the display of the clock
	private boolean active = true;

	CustomCharDriverLargeNumbers hallClockNumber = null;
	
	Date displayDate = null;	
	
	boolean showAliveIndicator = true;
	
	boolean isHallColon;
	
	String aliveIndicator;
	String emptyIndicator = "                    ";
	int indicatorMaxLength = 0;
	
	short lineId = 0;
	short position = 0;
	
	// Clone LCDs
	private List<BrickletLCD20x4> cloneLcdList = new ArrayList<BrickletLCD20x4>();
	
	/**
	 * Defines the alive indicator for Hall Clock or Hall Date  <br>
	 * Constructor and Starter  <br>
	 *  <br>
	 * @param lcd - the BrickletLCD20x4
	 * @param screenId - the screen id
	 * @param aliveType - the type of alive Indicator: TYPE_HALL_COLON / TYPE_HALL_DOTS
	 * @param names[] - for logging: max. 1 name allowed. If specified log is enabled
	 */
	public ScreenAliveIndicatorImpl(
			BrickletLCD20x4 lcd
			, Integer screenId
			, int aliveType
			, String...names){
		super(lcd);
		
		
		check(screenId,"screenId", 0, 999, true);
		
		this.screenId = screenId;
		this.type = aliveType;
		
		setName(names);
		
		isHallColon = true;
		
		if (screenId == null){
			// not operated by ScreenController: set active
			active = true;
		} else {
			// operated by ScreenController: has to wait for activation
			active = false;
		}

		start();  // calls the run() method
	}


	/**
	 * Take over the names from Constructor Call  <br>
	 *  <br>
	 * Set an individual name if name from Constructor Call is missing:
	 * 3 last digits from date.getTime + type 
	 * @param names - the names[]
	 */
	private void setName(String[] names){
		if (names.length > 1){
			throw new IllegalArgumentException("ScreenAliveIndicatorImpl:: max. one name allowed");
		}
		
		if (names.length == 1){
			name = names[0];
		} else {
			name = String.valueOf(new Date().getTime());
			name = name.substring(name.length() - 3) + "-" + type;
		}
	}

	
	/**
	 * Defines the alive indicator for an individual string <br>
	 * Constructor and Starter  <br>
	 *  <br>
	 * @param lcd - the BrickletLCD20x4
	 * @param screenId - the screen id
	 * @param aliveIndicator - the string to use to indicate alive
	 * @param lineId - the line id to show
	 * @param position - the position to show
	 * @param names[] - for logging: max. 1 name allowed. If specified log is enabled
	 */
	public ScreenAliveIndicatorImpl(
			BrickletLCD20x4 lcd
			, Integer screenId
			, String aliveIndicator
			, int lineId
			, int position
			, String...names) {
		super(lcd);
		
		check(screenId,"screenId", 0, 999, true);
		
		this.type = TYPE_INDIVIDUAL_STRG;
		this.screenId = screenId;
		this.aliveIndicator = aliveIndicator;
		this.lineId = (short) lineId;
		this.position = (short) position;
		isHallColon = false;
		
		setName(names);
		
		detectIndicatorMaxLength();
		
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
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#terminate()
	 */
	public void terminate(){
		active = false;
		log("terminate: interrupt triggered");
		this.interrupt();
	}
	
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {

				// Waiting for something to do
				while (true) {Thread.sleep(DateTimeService.DAYS);}

			} catch (InterruptedException e) {
				log("run: Interrupted from 'Waiting for something to do'");

				while (active) {
					// There is something to do...
					try {
						while (active) {
							log("run: There is something to do: writeAliveIndicator");
							writeAliveIndicator();
							log("run: There is something to do: sleep");
							Thread.sleep(sleepTime);
						}
					} catch (InterruptedException e1) {
						log("run: Interrupted from 'There is something to do'");
					}
				}

			}			

		}
	}
	

	/**
	 * 
	 */
	private void detectIndicatorMaxLength(){
		if (aliveIndicator.length() > indicatorMaxLength){
			indicatorMaxLength = aliveIndicator.length();
		}
	}
	
	
	/**
	 * Writes choosen indicator to the LCD 
	 */
	private synchronized void writeAliveIndicator(){
		switch (type) {
		case TYPE_HALLCLOCK_COLON:
			writeHallClockColon();
			break;

		case TYPE_HALLDATE_DOTS:
			writeHallDateDots();
			break;

		case TYPE_INDIVIDUAL_STRG:
			writeIndividualStrg();
			break;

		default:
			break;
		}
		
	}
	

	/**
	 * Writes the colon for the Hall Clock 
	 */
	private void writeHallClockColon(){
		if (showAliveIndicator){
			showAliveIndicator = false;
			writeLine((short)1, (short) 9, "\6");
			writeLine((short)2, (short) 9, "\5");
		} else {
			showAliveIndicator = true;
			writeLine((short)1, (short) 9, " ");
			writeLine((short)2, (short) 9, " ");
		}
	}
	

	/**
	 * Writes the date dates for the Hall Date 
	 */
	private void writeHallDateDots(){
		if (showAliveIndicator){
			showAliveIndicator = false;
			writeLine((short)3, (short) 8, "\6");
			writeLine((short)3, (short) 19, "\6");
		} else {
			showAliveIndicator = true;
			writeLine((short)3, (short) 8, " ");
			writeLine((short)3, (short) 19, " ");
		}
	}
	

	/**
	 * Writes individual indicator string 
	 */
	private void writeIndividualStrg(){
		if (showAliveIndicator){
			showAliveIndicator = false;
			writeLine(lineId, position, aliveIndicator);
			writeToClone(aliveIndicator);
			log("There is something to do: Full indicator written");
		} else {
			showAliveIndicator = true;
			writeLine(lineId, position, emptyIndicator.substring(0, indicatorMaxLength));
			writeToClone(emptyIndicator.substring(0, indicatorMaxLength));
			log("There is something to do: Empty indicator written");
		}
	}
	

	private void writeToClone(String text){
		for (BrickletLCD20x4 cloneLcd : cloneLcdList) {
			try {
				cloneLcd.writeLine(lineId, position, text);
			} catch (TimeoutException | NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#isActive()
	 */
	public boolean isActive() {
		return active;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setActive(boolean)
	 */
	public synchronized void setActive(boolean active) {
		this.active = active;
		
		if (active){
			showAliveIndicator = true;
			log("setActive: interrupt triggered from active=true");
			this.interrupt();
		} else {
			log("setActive: interrupt triggered from active=false");
			this.interrupt();
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#getScreenId()
	 */
	public Integer getScreenId() {
		return screenId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setScreenId(java.lang.Integer)
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
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.ScreenAliveIndicator#setAliveIndicator(java.lang.String)
	 */
	public synchronized void setAliveIndicator(String aliveIndicator) {
		if (type == TYPE_INDIVIDUAL_STRG){
			this.aliveIndicator = aliveIndicator;
			detectIndicatorMaxLength();

		} else {
			throw new IllegalArgumentException("ScreenAliveIndicator::setAliveIndicator: set not possible. wrong type of alive indicator");
		}
	}

	
	public void setCloneLcdList(BrickletLCD20x4...lcds) {
		cloneLcdList = new ArrayList<BrickletLCD20x4>(Arrays.asList(lcds));
	}
		

	/**
	 * Write System out
	 * @param state - message string
	 */
	private void log(String state){
//		System.out.println(getTS()+"  ScreenAliveIndicator: "+name+": "+state);
	}
	
	
	/**
	 * Returns a timestamp to write out for logging purpose
	 * @return string - formated as "kk:mm:ss.SSS"
	 */
	@SuppressWarnings("unused")
	private String getTS(){
		return FormatterService.getDateHHMMSSS(new Date());
	}

}
