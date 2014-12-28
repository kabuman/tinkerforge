package de.kabuman.tinkerforge.screencontroller.items;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;


/**
 * Provides abstract methods for both screen items: ScreenItem and ScreenClock
 *
 */
public abstract class AbstractItem extends Thread {
	
	BrickletLCD20x4 lcd;
	boolean writeDirectToLCD;

	protected AbstractItem(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
		writeDirectToLCD = (lcd == null)? false : true;
	}
	

	
	/**
	 * Checks the range of the given value
	 * 
	 * @param value - the value to check
	 * @param name - its name to identify in the case of an error
	 * @param min - minimum allowed value
	 * @param max - maximum allowed value
	 * @param nullAllowed - true: value may be null; false: value must be not null
	 */
	void check(Integer value, String name, long min, long max, boolean nullAllowed){
		if (nullAllowed && value == null){
			return;
		}
		
		if (value < min || value > max){
			throw new IllegalArgumentException("DisplayImpl:: check(): "+name+" value="+value+"<"+min+" or >"+max);
		}
	}

	
	protected boolean writeLine(short line, short position, String text){
		if (writeDirectToLCD){
			try {
				lcd.writeLine(line, position, text);
				return true;
			} catch (TimeoutException | NotConnectedException e) {
				return false;
			}
		} else {
			if (ScreenControllerImpl.getInstance()!=null){
				ScreenControllerImpl.getInstance().writeLine(line, position, text);
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	protected boolean setCustomCharacter(short index, short[] character){
		if (writeDirectToLCD){
			try {
				lcd.setCustomCharacter(index, character);
				return true;
			} catch (TimeoutException | NotConnectedException e) {
				return false;
			}
		} else {
			if (ScreenControllerImpl.getInstance()!=null){
				ScreenControllerImpl.getInstance().setCustomCharacter(index, character);
				return true;
			} else {
				return false;
			}
		}
		
	}


	public BrickletLCD20x4 getLcd() {
		return lcd;
	}


}
