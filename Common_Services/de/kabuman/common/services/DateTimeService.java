package de.kabuman.common.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Provides Date/Time Arithmetic and pre-defined simple date formats
 *
 */
public class DateTimeService {
	public static DateFormat DF_DATE_TIME_XL = new SimpleDateFormat("dd.MM.yyyy-kk:mm:ss.SSS");
	public static DateFormat DF_DATE_TIME_L = new SimpleDateFormat("dd.MM.yyyy-kk:mm:ss");
	public static DateFormat DF_DATE_TIME_M = new SimpleDateFormat("dd.MM.yy-kk:mm:ss");
	public static DateFormat DF_DATE_TIME_S = new SimpleDateFormat("dd.MM.yy-kk:mm");
	public static DateFormat DF_DATE_TIME_XS = new SimpleDateFormat("dd.MM-kk:mm");
	
	public static DateFormat DF_TIME_L = new SimpleDateFormat("kk:mm:ss.SSS");
	public static DateFormat DF_TIME_M = new SimpleDateFormat("kk:mm:ss");
	public static DateFormat DF_TIME_S = new SimpleDateFormat("kk:mm");
	
	public static DateFormat DF_DATE_L = new SimpleDateFormat("dd.MM.yyyy");
	public static DateFormat DF_DATE_M = new SimpleDateFormat("dd.MM.yy");
	public static DateFormat DF_DATE_S = new SimpleDateFormat("dd.MM");

	
	/**
	 * A day in milliseconds 
	 */
	public static final long DAYS = 86400000;
	
	/**
	 * A hour in milliseconds
	 */
	public static final long HOURS = 3600000;
	
	/**
	 * A minute in milliseconds
	 */
	public static final long MINUTES = 60000;
	
	
	/**
	 * A second in milliseconds 
	 */
	public static final long SECONDS = 1000;

	
	/**
	 * Adds or subtracts the given number of specified measure to the given date
	 * 
	 * @param date - the date
	 * @param measure - the meanining of the given number: DAYS, HOURS, MINUTES, SECONDS
	 * @param number - the number to add to the date; must be negative to subtract
	 * @return Date - the calculated new date
	 */
	public static synchronized Date add(Date date, long measure, int number){
		return new Date (date.getTime() + number * measure);
	}
	
	/**
	 * Replace the time within the given date
	 * 
	 * @param date - the date
	 * @param hours - the hours to set (0-23)
	 * @param minutes - the minutes to set (0-59)
	 * @param seconds - the seconds to set (0-59)
	 * @param milli seconds - the milli seconds to set (0-999)
	 * @return date - the date object with the old date and the new time
	 */
	public static synchronized Date replaceTime(Date date, int hours, int minutes, int seconds, int milliSeconds){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		// take over the time
		cal.set(cal.get(Calendar.YEAR)
				, cal.get(Calendar.MONTH)
				, cal.get(Calendar.DATE)
				, hours
				, minutes
				, seconds);
		cal.set(Calendar.MILLISECOND, milliSeconds);
		
		return cal.getTime();
	}

	/**
	 * Takes over the date from the first date object and the time from the second date object
	 * 
	 * @param date - the date to take over
	 * @param date - the time to take over
	 * @return date - the new created date object
	 */
	@SuppressWarnings("deprecation")
	public static synchronized Date replaceTime(Date date, Date time){
		return replaceTime(date, time.getHours(), time.getMinutes(), time.getSeconds(), 0);
	}

}
