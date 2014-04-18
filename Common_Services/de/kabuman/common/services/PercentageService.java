package de.kabuman.common.services;


/**
 * Provides static Percentage Calculation Methods
 */
public class PercentageService {
	
	/**
	 * Returns the calculated Percentage<br>
	 * Throws IllegalArgumentExecption if parameter conditions are violated
	 * 
	 * @param currentValue - the current value (lowerBound <= currentValue <= upperBound)
	 * @param lowerBound - the lower bound (lowerBound < upperBound)
	 * @param upperBound - the higher bound (upperBound > lowerBound)
	 * @return Percentage - the calculated Percentage
	 */
	public static synchronized int getPercentage(int currentValue, int lowerBound, int upperBound){
		if (currentValue < lowerBound){
			throw new IllegalArgumentException("PercentageService.getPercentage:: 'lowerBound <= currentValue' violation detected. currentValue="+currentValue+" lowerBound="+lowerBound);
		}
		if (currentValue > upperBound){
			throw new IllegalArgumentException("PercentageService.getPercentage:: 'currentValue <= upperBound' violation detected. currentValue="+currentValue+" upperBound="+upperBound);
		}
		if (lowerBound >= upperBound){
			throw new IllegalArgumentException("PercentageService.getPercentage:: 'lowerBound < upperBound' violation detected. lowerBound="+lowerBound+" upperBound="+upperBound);
		}
		int diff100 = upperBound - lowerBound;	// 30 - 10 = 20
		int diffx = currentValue - lowerBound; 	// 20 - 10 = 10
		int percentage = diffx * 100 / diff100;		// 10 * 100 / 20 = 1000 / 20 = 50
		return percentage;
	}

	/**
	 * Calculates the Percentage and returns it formatted as "##0%"<br>
	 * Throws IllegalArgumentExecption if parameter conditions are violated
	 * 
	 * @param currentValue - the current value (lowerBound <= currentValue <= upperBound)
	 * @param lowerBound - the lower bound (lowerBound < upperBound)
	 * @param upperBound - the higher bound (upperBound > lowerBound)
	 * @return Percentage - the calculated Percentage
	 */
	public static synchronized String getFormPercentage(int currentValue, int lowerBound, int upperBound){
	    return getShortFormPercentage(currentValue, lowerBound, upperBound) + "%";
	}
	
	/**
	 * Calculates the Percentage and returns it formatted as "##0"<br>
	 * Throws IllegalArgumentExecption if parameter conditions are violated
	 * 
	 * @param currentValue - the current value (lowerBound <= currentValue <= upperBound)
	 * @param lowerBound - the lower bound (lowerBound < upperBound)
	 * @param upperBound - the higher bound (upperBound > lowerBound)
	 * @return Percentage - the calculated Percentage
	 */
	public static synchronized String getShortFormPercentage(int currentValue, int lowerBound, int upperBound){
    	String result = StringService.create(3);
	    String form = Integer.toString(getPercentage(currentValue, lowerBound, upperBound));
	    if (form.length() == 2){
	    	result = StringService.overwrite(result, 1, form);
	    } else {
	    	result = StringService.overwrite(result, 0, form);
	    }
	    return result;
	}
	

}
