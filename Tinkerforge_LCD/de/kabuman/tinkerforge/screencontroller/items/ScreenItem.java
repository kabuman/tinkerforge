package de.kabuman.tinkerforge.screencontroller.items;



/**
 * Defines a item to display on the 20x4 LCD Display <br>
 * For PULL and PUSH items
 */
public interface ScreenItem extends FieldItem{
	
	/**
	 * Refresh the value on the LCD (PUSH)
	 */
	void refreshValue();
	
}
