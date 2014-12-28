package de.kabuman.tinkerforge.screencontroller.items;


/**
 * Defines a clock item to display on a 20x4 LCD Display
 */
public interface ScreenClock extends FieldItem{

	
	/**
	 * Choose this format definition for "DD.MM.YYYY-HH:MM:SS"
	 */
	public final short FORMAT_DATE_TIME_L = 1;		
	
	/**
	 * Choose this format definition for "DD.MM.YY-HH:MM:SS"
	 */
	public final short FORMAT_DATE_TIME_M = 2; 	

	/**
	 * Choose this format definition for "DD.MM.YY-HH:MM"
	 */
	public final short FORMAT_DATE_TIME_S = 3;

	/**
	 * Choose this format definition for "DD.MM-HH:MM"
	 */
	public final short FORMAT_DATE_TIME_XS = 4;

	/**
	 * Choose this format definition for "HH:MM:SS.sss"
	 */
	public final short FORMAT_TIME_L = 5; 

	/**
	 * Choose this format definition for "HH:MM:SS"
	 */
	public final short FORMAT_TIME_M = 6; 

	/**
	 * Choose this format definition for "HH:MM"
	 */
	public final short FORMAT_TIME_S = 7; 

	/**
	 * Choose this format definition for "DD.MM.YYYY"
	 */
	public final short FORMAT_DATE_L = 8; 

	/**
	 * Choose this format definition for "DD.MM.YY"
	 */
	public final short FORMAT_DATE_M = 9; 

	/**
	 * Choose this format definition for "DD.MM"
	 */
	public final short FORMAT_DATE_S = 10;
	
	/**
	 * Choose this format definition for day of the week: "Mo,Di,Mi,Do,Fr,Sa,So"
	 */
	public final short	FORMAT_WEEK_DAY_S = 11;
	
}
