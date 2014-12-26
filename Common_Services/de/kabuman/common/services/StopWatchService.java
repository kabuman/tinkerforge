package de.kabuman.common.services;

import java.util.Date;


public interface StopWatchService {
	/**
	 * Starts or continues the stop watch
	 */
	public void start();
	
	/**
	 * Stops the stop watch temporarily and returns the stop over duration.  <br>
	 * Use methods getSumMSec(), getSumString() to get further returns. <br>
	 * To continue the time measure use the method start() again.
	 * @param comment - a log comment
	 */
	public void stopOver(String comment);
	
	/**
	 * Stops the stop watch temporarily and returns the stop over duration.  <br>
	 * Use methods getSumMSec(), getSumString() to get further returns. <br>
	 * To continue the time measure use the method start() again.
	 */
	public void stopOver();
	
	/**
	 * Returns the duration
	 * Use method stopover() before you use this method otherwise the result will be zero.
	 * @return long - the duration in milli seconds
	 */
	public long getSumMSec();
	
	/**
	 * Returns the formated (HH:MM:SS:nnn) duration
	 * Use method stopover() before you use this method otherwise the result will be zero.
	 * @return String - the formated duration
	 */
	public String getSumString();

	/**
	 * Returns the current duration until now  <br>
	 * If StopWatch is not active, the duration (method "getSumSec()") is return (may be zero). <br>
	 * It is like a stopover, but the StopWatch will not be stopped <br>
	 * @return long - the duration in milliseconds
	 */
	public long getCurrent();
	
	/**
	 * Returns the result from method "getCurrent()" but in a formated way
	 * @return String - the formated duration in "HH:MM:SS:nnn"
	 */
	public String getCurrentString();
	
	/**
	 * Returns a formated string (HH:MM:SS:nnn) representing the given duration
	 * @return String - the formated given duration
	 */
	public String getFormStopWatch(long mSec);
	
	/**
	 * Returns a formated string (H:MM) representing the given duration
	 * @return String - the formated given duration
	 */
	public String getLCDFormStopWatch(long mSec);
	
	/**
	 * Returns the start date
	 * @return Date
	 */
	public Date getStartDate();
	
	/**
	 * Returns the stop over date
	 * @return Date
	 */
	public Date getStopOverDate();

	/**
	 * Returns a report with "startDate - endDate = duration"
	 * @return
	 */
	public String getReport(String title);
	
	/**
	 * Restart. All parameter are set to default.
	 * A refreshed StopWatch is available then.
	 */
	public void restart();
	
	/**
	 * Returns true if StopWatch is running
	 * After using "stopOver()" method it will return "false"
	 * @return boolean
	 */
	public boolean isActive();

	/**
	 * Write report (rows with "startDate - stopOverDate = duration") upon console
	 */
	public void writeStatusToConsole(String title);

}
