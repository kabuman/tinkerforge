package de.kabuman.tinkerforge.screencontroller.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.common.services.StringService;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPull;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPullAlarm;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;


/**
 * Displays an item on the 20x4 LCD Display
 * and refreshes it according the given time period (if specified) [PULL]  <br>
 * or the item pushes the value by itself [PUSH]
 */
public class ScreenItemImpl extends AbstractItem implements ScreenItem, ScreenItemReplace{


	// Constructor Parameter
	private Object item;
	private Integer screenId;
	private int lineId;
	private int position;
	
	// activates/deactivates the display of the item
	private Boolean active;
	
	// sleep time in milliseconds
	private long sleepTime;
	
	
	private Integer previousValueLength = null;
	
	private String parmStrg1 = null;
	private String parmStrg2 = null;
	
	private SimpleDateFormat parmSDForm = null;
	
	private boolean isToStringFormat = false;
	private boolean isToStringReplace = false;
	private boolean isToSimpleDateFormat = false;
	
	
	
	/**
	 * Defines a item to display on 20x4 LCD Bricklet  <br>
	 * For PULL and PUSH items. <br>
	 * Constructor and Starter
	 * 
	 * @param lcd - the 20x4 LCD Bricklet
	 * @param Object - with interface DisplayableContent, DisplayableDouble, String, Integer, Long, Short, Byte
	 * @param sleepTime - the refresh period (0=no refresh (only when switching to another menue))
	 * @param screenId - screen id (if Null: Content becomes immediately active state)
	 * @param lineId - line number: 0,..,3
	 * @param position - start position to display: 0,..,19
	 * @param parmArray - optional parameters (0, 1, or 2 parameters) 
	 *  <pre>
	 * for floating point numbers: paramArray[0] specifies String.Format Parameter 
	 * 	for instance: "%08.3f": specifies floating point:
	 * 	-- leading zeroes
	 * 	-- 8 digits overall
	 * 	-- 3 decimal digits (after comma)
	 * for decimal numbers: paramArray[0] specifies String.Format Parameter 
	 * 	for instance: "%08d": specifies decimal integers:
	 * 	-- leading zeroes
	 * 	-- 8 digits overall
	 * 	-- no comma
	 * for strings: parmArray[0] specifies String.Format Parameter
	 *  for instance: ": %s" specifies 2 strings
	 * for Boolean: specifies alternative true/false text
	 * 	parmArray[0]: alternative text for true
	 * 	parmArray[1]: alternative text for false
	 * 	=> Both entries required, if alternative text should be displayed
	 *  </pre>
	 */
	public ScreenItemImpl(
			BrickletLCD20x4 lcd
			, Object item
			, long sleepTime
			, Integer screenId
			, int lineId
			, int position
			, Object...parmArray) {
		super(lcd);
		
		check((int)sleepTime,"sleepTime",0,DateTimeService.DAYS, false);
		check(screenId,"screenId", 0, 999, true);
		check(lineId,"lineId",0,3, false);
		check(position,"postion",0,19, false);
		
		this.item = item;
		this.screenId = screenId;
		this.sleepTime = sleepTime;
		this.lineId = lineId;
		this.position = position;
		
		if (item instanceof ItemSourceToPush){
			((ItemSourceToPush) item).addTtem(this);
		}
		
		if (screenId == null){
			// not operated by ScreenController
			active = true;
		} else {
			// operated by ScreenController
			active = false;
		}

		if (parmArray.length == 1 && parmArray[0] instanceof SimpleDateFormat){
			parmSDForm = (SimpleDateFormat) parmArray[0];
		}
		if (parmArray.length > 0 && parmArray[0] instanceof String){
			parmStrg1 = (String) parmArray[0];
		}
		if (parmArray.length > 1 && parmArray[0] instanceof String){
			parmStrg2 = (String) parmArray[1];
		}

		isToStringFormat =  (parmArray.length == 1 && parmArray[0] instanceof String) ? true : false;
		isToStringReplace = (parmArray.length ==2 && parmArray[0] instanceof String && parmArray[1] instanceof String) ? true : false;
		isToSimpleDateFormat = (parmArray.length ==1 && parmArray[0] instanceof SimpleDateFormat) ? true : false;

		start();  // calls the run() method
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#terminate()
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
	public void run() {
		if (sleepTime == 0){
			if (active){
				display(position, getValue());
			}
		} else {
			try {
				while (true) {
					if (active){
						display(position, getValue());
					}				
					Thread.sleep(sleepTime);
				}
			} catch (InterruptedException e) {
				// this is the end of the the while loop
			} 
		}
	}
	
	
	/**
	 * Reads (PULL) the value from the bricklet and returns the formated value 
	 * 
	 * @return String - the formated value
	 */
	private String getValue(){
		if (item instanceof ItemSourceToPullAlarm){
//			return Double.toString(((ItemSourceToPullAlarm) item).getCurrentValue()).replace('.', ',');
			Double mydouble = ((ItemSourceToPullAlarm) item).getCurrentValue();
			Object object =  ((Double) mydouble);
			return clearPlaceForValue(castItemValueToString(object));
		}
		if (item instanceof ItemSourceToPull){
			Object object =  ((ItemSourceToPull) item).getItemValue();
			return clearPlaceForValue(castItemValueToString(object));
		}
		return (castItemValueToString(item));
	}
	

	/**
	 * Overwrites older content from LCD with spaces.
	 * 
	 * Makes it efficiently and calculates which digits are really to 
	 * overwrite by spaces in order to reduce the transfer rate to LCD device.
	 * 
	 * 																R1 R2 R3 R4 R5  et
	 * previousValueLength = NULL									J  J  N  N  N	2hoch0 = 1
	 * value               = NULL									J  N  N  N  J	2hoch1 = 2
	 * previousValueLength > value.length()							-  -  J  N  -	2hoch2 = 4
	 * ---------------------------------------------------------------------------------------
	 * BlankString length = previousValueLength - value.length()	-  -  X  -  -
	 * position           = position + value.length()				-  -  X  -  -
	 * BlankString length = previousValueLength						-  -  -  -  X
	 * position           = position								-  -  -  -  X
	 * ---------------------------------------------------------------------------------------
	 * 														et = 	3  1  4  0  2
	 * @param value
	 * @return
	 */
	private String clearPlaceForValue(String value){
		int et = 0;
		
		if (previousValueLength == null){
			et = et + 1;
		}
		if (value == null){
			et = et + 2;
		}
		
		if (et == 0 && previousValueLength > value.length()){
			et = et + 4;
		}
		
		switch (et) {
		case 0:
			previousValueLength = value.length();
			break;

		case 1:
			previousValueLength = value.length();
			break;

		case 2:
			display(position, StringService.create(previousValueLength));
			break;

		case 3:
			break;

		case 4:
			display(position + value.length(), StringService.create(previousValueLength - value.length()));
			previousValueLength = value.length();
			break;

		default:
			throw new IllegalArgumentException("clearPlaceForValue:: switch not possible. et="+et);
		}
		
		
		return value;
	}
	
	
	/**
	 * Cast the object from its original type to String
	 * for items which are instance of ItemSourceToPull
	 * 
	 * @return String - the casted object to string
	 */
	private String castItemValueToString(Object object){
		if (object instanceof Double){
			return (isToStringFormat) ? String.format(parmStrg1,(Double) object) : Double.toString((double) object).replace('.', ',');
		}
		if (object instanceof Integer){
			return (isToStringFormat) ? String.format(parmStrg1,(Integer)object) : Integer.toString((int)object);
		}
		if (object instanceof Boolean){
			Boolean b = (boolean)object;
			if (isToStringReplace){
				if (b){
					return parmStrg1;
				} else {
					return parmStrg2;
				}
			} else {
				return Boolean.toString(b);
			}
		}
		if (object instanceof Date){
			Date d = (Date) object;
			if (isToStringFormat){
				return String.format(parmStrg1,d);
			}
			if (isToSimpleDateFormat){
				return parmSDForm.format(d);
			}
			return DateTimeService.DF_DATE_TIME_M.format(d);
		}
		if (object instanceof String){
			return (isToStringFormat) ? String.format(parmStrg1,(String) object) : (String) object;
		}
		if (object instanceof Long){
			return (isToStringFormat) ? String.format(parmStrg1,(Long)object) : Long.toString((long)object);
		}
		if (object instanceof Short){
			return (isToStringFormat) ? String.format(parmStrg1,(Short)object) : Short.toString((short)object);
		}
		if (object instanceof Byte){
			return (isToStringFormat) ? String.format(parmStrg1,(Byte)object) : Byte.toString((byte)object);
		}
		if (object == null){
			throw new IllegalArgumentException("castToString:: object is null (screenId, lineId, position)="+screenId+"/"+lineId+"/"+position);
		}
		throw new IllegalArgumentException("castToString:: object not valid="+object.getClass().getName()+" (screenId, lineId, position)="+screenId+"/"+lineId+"/"+position);

	}
	
	
	/**
	 * Displays the item on LCD
	 * 
	 * @param position - the position within a line
	 * @param value - the string value to display
	 */
	private void display(int position, String value){
			writeLine((short)lineId, (short)position, value);
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
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#isActive()
	 */
	public boolean isActive() {
		return active;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setActive(boolean)
	 */
	public void setActive(boolean active) {
		this.active = active;
		if (active){
			display(position, getValue());
		}
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.ScreenItem#refreshValue()
	 */
	public void refreshValue() {
		if (active){
			display(position, getValue());
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.ScreenItem#replaceItemSource(java.lang.Object)
	 */
	@Override
	public boolean replaceItemSource(Object oldItemSource, Object newItemSource) {
		if (this.item == oldItemSource){
			this.item =  newItemSource;
			return true;
		} else {
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.FieldItem#getLineId()
	 */
	public int getLineId() {
		return lineId;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.FieldItem#getPosition()
	 */
	public int getPosition() {
		return position;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.items.Item#setLcd(com.tinkerforge.BrickletLCD20x4)
	 */
	public void setLcd(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
		refreshValue();
	}

}
