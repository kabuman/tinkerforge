package de.kabuman.common.services;

/**
 * Provides a little bit of statistic for double values
 * - Counter
 * - Minimum
 * - Maximum
 * - Average of the last 100 values (or lower)
 */
public interface MinMaxService {
	
	/**
	 * Set a single value for statictic purpose
	 */
	public void regardValue(double value);
	
	/**
	 * @return Double - mimimun value
	 */
	public Double getMin();
	
	/**
	 * @return Double - maximum value
	 */
	public Double getMax();
	
	/**
	 * @return Double - calculated average about the last 100 values (or lower)
	 */
	public Double getAverage();
	
	/**
	 * @return int - counted values (the number of calls of "regardValue(..)")
	 */
	public int getCount();

}
