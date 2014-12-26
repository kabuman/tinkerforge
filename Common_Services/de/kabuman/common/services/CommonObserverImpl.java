package de.kabuman.common.services;

import java.util.Date;




/**
 * Common Observer  <br>
 * Observers a given time period and triggers via callback the defined functionCode.
 */
public class CommonObserverImpl extends Thread implements CommonObserver{

	// Constructor parameter
	private CommonCallback callback;
	private Integer functionCode;
	private long observationTime;
	private String observationName;
	
	
	private boolean doObserving = false;
	private Long tempObservationTime = null;
	
	
	/**
	 * Constructor and Starter
	 * 
	 * @param callback - the instance of the caller (must implement CommonCallback)
	 * @param functionCode - a specific function code which allows the caller to operate several observer instances
	 * @param observationTime - the time period in milliseconds to observe 
	 * @param observationNames[] - for logging: max. 1 name allowed. If specified log is enabled
	 */
	public CommonObserverImpl(CommonCallback callback, Integer functionCode, long observationTime, String...observationNames) {
		if (observationNames.length > 1){
			throw new IllegalArgumentException("CommonObserverImpl:: max. one observationName allowed");
		}
		this.callback = callback;
		this.functionCode = functionCode;
		this.observationTime = observationTime;
		
		this.observationName = (observationNames.length == 1)? observationNames[0] : null;

		start();  // calls the run() method
	}


	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				
				// Waiting for something to do
				while (true) {Thread.sleep(DateTimeService.DAYS);}
				
			} catch (InterruptedException e) {
				
				if (!doObserving){
					// Termination without running monitoring
					log("Terminated without running observation");
					continue;
				}
				
				// There is something to observe ...
				do {
					try {
						log("Started");
						Thread.sleep((tempObservationTime == null)? observationTime : tempObservationTime);
						callback.commonObserverTriggeredMethod(functionCode);
						doObserving = false;
						tempObservationTime = null;
						log("Finished");
					} catch (InterruptedException e1) {
						if (!doObserving){
							log("Terminated");
							doObserving = false;
						} else {
							log("Refreshed (new Start initiated)");
						}
					}
					continue;
					
				} while (doObserving);
			}
		}
	}
	
	
	/**
	 * Prepares and writes log msg
	 * @param state - the state of CommonObserver
	 */
	private void log(String state){
		if (observationName == null){
			return;
		}
		
		
		if (LogControllerImpl.getInstance() == null){
			System.out.println(getTS()+"  "+observationName + ": CommonObserver: "+state+" for functionCode="+functionCode);
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(observationName, "CommonObserver",  state+" for functionCode="+functionCode);
		}
		

	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonObserver#startObservation()
	 */
	@Override
	public synchronized void startObservation(){
		doObserving = true;
		this.interrupt();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonObserver#stopObservation()
	 */
	@Override
	public void stopObservation(){
		doObserving = false;
		this.interrupt();
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonObserver#isObservationActive()
	 */
	@Override
	public boolean isObservationActive() {
		return doObserving;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonObserver#startObservation(long)
	 */
	@Override
	public void startObservation(long observationTime) {
		this.tempObservationTime = observationTime;
		doObserving = true;
		this.interrupt();
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.CommonObserver#setObservationTime(long)
	 */
	@Override
	public void setObservationTime(long observationTime) {
		this.observationTime = observationTime;
	}
	
	/**
	 * Returns a timestamp to write out for logging purpose
	 * @return string - formated as "kk:mm:ss.SSS"
	 */
	private String getTS(){
		return FormatterService.getDateHHMMSSS(new Date());
	}
	
	public void setFunctionCode(Integer functionCode){
		this.functionCode = functionCode;
	}
	
}
