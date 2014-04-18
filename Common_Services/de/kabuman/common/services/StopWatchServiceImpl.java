package de.kabuman.common.services;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Stop Watch Implementation
 * 
 * @author Karsten Buchmann
 */
public class StopWatchServiceImpl implements StopWatchService{
	
	Date startDate;		
	Date stopOverDate;
	
	boolean active;
	
	long sumMSec;
	 
	StringBuffer report;
	
	SimpleDateFormat formSimpleDate = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
	
	List<StopWatchEvent> stopWatchEventList;
	
	class StopWatchEvent{
		Date startDate;
		Date stopOverDate;
		long duration;
		String comment;
		StopWatchEvent(Date startDate, Date stopOverDate, long duration, String comment){
			this.startDate = startDate;
			this.stopOverDate = stopOverDate;
			this.duration = duration;
			this.comment = comment;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getStopOverDate() {
			return stopOverDate;
		}
		public void setStopOverDate(Date stopOverDate) {
			this.stopOverDate = stopOverDate;
		}
	}
	
	/**
	 * Initialize 
	 */
	private void init(){
		startDate = null;
		stopOverDate = null;
		sumMSec = 0;
		report = new StringBuffer();
		active = false;
		stopWatchEventList = new ArrayList<StopWatchEvent>();
	}
	
	/**
	 * Constructor
	 * @param sumMSec - elapsed time (duration) in milli seconds
	 */
	public StopWatchServiceImpl(long sumMSec){
		init();
		this.sumMSec = sumMSec;
	}
	
	/**
	 * Constructor
	 * Use it to start the measure of duration with the given start date
	 * @param startDate - start date 
	 */
	public StopWatchServiceImpl(Date startDate){
		init();
		this.startDate = startDate;
		active = true;
	}
	
	/**
	 * Constructor
	 * Use it to start the measure of duration with the given start date
	 * and the given default duration in milli seconds
	 * @param startDate - start date
	 * @param sumMSec - elapsed time (duration) in milli seconds
	 */
	public StopWatchServiceImpl(Date startDate, long sumMSec){
		init();
		this.startDate = startDate;
		this.sumMSec = sumMSec;
		active = true;
	}
	
	/**
	 * Constructor
	 */
	public StopWatchServiceImpl(){
		init();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#start()
	 */
	public void start(){
		startDate = new Date(Calendar.getInstance().getTimeInMillis());
		stopOverDate = null;
		active = true;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#stopover()
	 */
	public void stopOver(String comment){
		if (!active){
//			System.out.println("StoppUhr kann nicht gestoppt werden, da sie nicht aktiv ist.");
			return;
		}
		stopOverDate = new Date(Calendar.getInstance().getTimeInMillis());
		sumMSec = sumMSec + stopOverDate.getTime() - startDate.getTime();
		
		// Report Buffer String
		long duration = stopOverDate.getTime() - startDate.getTime();
		report.append("\n" + formSimpleDate.format(startDate) + " - " + formSimpleDate.format(stopOverDate) + " = " + getFormStopWatch(duration) + "  " + comment);
		
		// Report raw data
		StopWatchEvent stopWatchEvent = new StopWatchEvent(startDate, stopOverDate, duration, comment);
		stopWatchEventList.add(stopWatchEvent);
		
		// set inactive
		active = false;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#stopover()
	 */
	public void stopOver(){
		stopOver("");
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getSumMSec()
	 */
	public long getSumMSec(){
		return sumMSec;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getSumString()
	 */
	public String getSumString(){
		return getFormStopWatch(sumMSec);
	}
	

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getCurrentString()
	 */
	public String getCurrentString(){
		return getFormStopWatch(getCurrent());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getCurrent()
	 */
	public long getCurrent(){
		if (active){
			return Calendar.getInstance().getTimeInMillis() - startDate.getTime() + sumMSec;
		} else {
			return getSumMSec();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getStartDate()
	 */
	public Date getStartDate(){
		return startDate;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getStopOverDate()
	 */
	public Date getStopOverDate(){
		return stopOverDate;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getFormStopWatch(long)
	 */
	public String getFormStopWatch(long millis){
		DecimalFormat format = new DecimalFormat("00");
		DecimalFormat format3= new DecimalFormat("000");

//		 Tage,Stunden und Minuten berechnen
//		long day = millis/86400000;					// 24Std. x 60Min. x 60Sek. x 1000 (als MilliSek.)
		long hour = (millis % 86400000)/3600000;
		long min = (millis % 3600000)/60000;
		long sec = (millis - (hour * 3600000) - (min * 60000)) / 1000;
		long mil = millis - (hour * 3600000) - (min * 60000) - (sec * 1000);
		

		return format.format(hour)+":"+format.format(min)+":"+format.format(sec)+"."+format3.format(mil);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getLCDFormStopWatch(long)
	 */
	public String getLCDFormStopWatch(long millis){
		// hh:mm:ss:mmmm => h:mm
		return getFormStopWatch(millis).substring(1,5);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#restart()
	 */
	public void restart(){
		init();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#isActive()
	 */
	public boolean isActive(){
		return active;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#getReport()
	 */
	public String getReport(String title){
		return "\n" + title + report.toString() + "\n" + title + " gesamt: " + getFormStopWatch(sumMSec);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.StopWatchService#writeStatusToConsole(java.lang.String)
	 */
	public void writeStatusToConsole(String title){
		System.out.println(getReport(title));
	}
	
}
