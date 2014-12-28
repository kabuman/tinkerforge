package de.kabuman.tinkerforge.customchar;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;

/**
 * Provides basic functions to write output to LCD  <br>
 * especially for applications which uses customized characters.
 */
public abstract class AbstractLCDoutput {
	
	BrickletLCD20x4 lcd;
	boolean writeDirectToLCD;

	/**
	 * Constructor - called by inherited class 
	 * @param lcd - BrickletLCD20x4: may be null: writes directly to LCD
	 */
	protected AbstractLCDoutput(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
		writeDirectToLCD = (lcd == null)? false : true;
	}
	
	/**
	 * Writes the given text to LCD  <br>
	 *  <br>
	 * @param line - line number
	 * @param position - column number
	 * @param text - the text to write
	 */
	protected void writeLine(short line, short position, String text){
		if (writeDirectToLCD){
			try {
				lcd.writeLine(line, position, text);
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
			}
		} else {
			if (ScreenControllerImpl.getInstance()!=null){
				ScreenControllerImpl.getInstance().writeLine(line, position, text);
			}
		}
	}
	
	
	/**
	 * Defines customized character  <br>
	 *  <br>
	 * @param index - the number of the character (0-7)
	 * @param character - the character defines via short[]
	 */
	protected void setCustomCharacter(short index, short[] character){
		if (writeDirectToLCD){
			try {
				lcd.setCustomCharacter(index, character);
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
			}
		} else {
			if (ScreenControllerImpl.getInstance()!=null){
				ScreenControllerImpl.getInstance().setCustomCharacter(index, character);
			}
		}
		
	}
	
	
	/**
	 * Set the flag "custom character already loaded" in ScreenController Instance
	 */
	protected void setCustomCharAlreadyLoaded() {
		if (ScreenControllerImpl.getInstance() != null){
			ScreenControllerImpl.getInstance().setCustomCharAlreadyLoaded();
		}
		
	}


}
