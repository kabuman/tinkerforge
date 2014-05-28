package de.kabuman.tinkerforge.alarm.items.digital.input;

public interface Item {
	/**
	 * Returns the average of the last 100 values (or lower)
	 * @return double - the average value
	 */
	Double getAverageValue();
	
	/**
	 * Returns the minimum value since application was started
	 * @return double - the minimum value
	 */
	Double getMinimumValue();
	
	/**
	 * Returns the maximum value since application was started
	 * @return double - the maximum value
	 */
	Double getMaximumValue();
	
	/**
	 * Returns the number of value changes in the temperature since application was started
	 * @return int - the number of value changes
	 */
	int getCounter();

}
