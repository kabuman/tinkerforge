package de.kabuman.common.services;

/**
 * Interface for TableFormatterService
 * 
 * Provides methods to build a simple table model for console output

 * Create a new row via constructor 
 * Fill the row with addValue(pos, value) with different values on different positions
 * Print row via getRow() 
 * Create a new row via newRow()
 * ...
 */
public interface TableFormatterService {
	
	/**
	 * Returns a new row (String) filled with blanks in a fixed length (set by Constructor)
	 * 
	 * @return String - the new row
	 */
	public String newRow();
	
	/**
	 * Returns a new row (String filled with blanks in a fixed length) (set by Constructor)<br>
	 * The given value will replace blanks at the given position
	 * 
	 * @param pos - the position for the value to put in (pos >= 0)
	 * @param value - the value which is to put in (not null)
	 * @return String - the new row
	 */
	public String newRow(int pos, String value);
	
	/**
	 * Adds the given value at the given position within the current row<br>
	 * Throws IllegalArgumentException if parameter conditions violated
	 * 
	 * @param pos - the position for the value to put in (pos >= 0; pos < rowLength (set by Constructor)
	 * @param value - the value which is to put in (not null)
	 * @return String - the current row
	 */
	public String addValue(int pos, String value);
	
	/**
	 * Returns the current row
	 * 
	 * @return String - the current row
	 */
	public String getRow();

}
