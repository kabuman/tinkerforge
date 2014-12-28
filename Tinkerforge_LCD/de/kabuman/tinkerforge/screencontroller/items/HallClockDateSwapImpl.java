package de.kabuman.tinkerforge.screencontroller.items;

import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;


/**
 * Controller to swap automatically between  Hall Clock and Hall Date
 */
public class HallClockDateSwapImpl extends AbstractItem{

	// Constructor Parameter
	private int screenIdHallClock;
	private int screenIdHallDate;

	// Calculated sleep time in milliseconds
	private long durationHallClock;
	private long durationHallDate;

	// activates/deactivates the display of the clock
	private boolean swapActive = false;


	private Integer requestedScreenId = null;

	ScreenControllerImpl sc;
	
	/**
	 * Constructor  <br>
	 * Creates and start the controller to swap between Hall Clock and Hall Date  <br>
	 * This Thread requires a running ScreenController Instance.  <br>
	 * 
	 * @param screenIdHallClock - item Hall Clock
	 * @param durationHallClock - item Hall Date
	 * @param screenIdHallDate - screen id of Hall Clock
	 * @param durationHallDate - screen id of Hall Date
	 */
	public HallClockDateSwapImpl(
			int screenIdHallClock
			,long durationHallClock
			,int screenIdHallDate
			,long durationHallDate) {
		super(null);
		
		sc = ScreenControllerImpl.getInstance();
		if (sc == null){
			throw new IllegalArgumentException("HallClockDateSwapImpl:: ScreenController Instance required but is Null.");
		} 

		this.screenIdHallClock = screenIdHallClock;
		this.screenIdHallDate = screenIdHallDate;
		this.durationHallClock = durationHallClock;
		this.durationHallDate = durationHallDate;
		
		check(screenIdHallClock,"screenIdHallClock", 0, 999, true);
		check(screenIdHallDate,"screenIdHallDate", 0, 999, true);
		
		start();  // calls the run() method
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.DisplayClock#terminate()
	 */
	public void terminate(){
		swapActive = false;
		this.interrupt();
	}
	
	
	/**
	 * Implements the swap function
	 * Checks 
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		do {
			while (swapActive) {
				goSleep(durationHallDate);
				if (!swapActive) break;
				
				requestedScreenId = screenIdHallClock;
				activateScreen(requestedScreenId);
				
				if (!swapActive) break;
				goSleep(durationHallClock);
				if (!swapActive) break;
				
				requestedScreenId = screenIdHallDate;
				activateScreen(requestedScreenId);
			}

			goSleep(1000);

		} while (true);
	}
	
	
	/**
	 * Actives the given screen id via ScreenController  <br>
	 * 
	 * @param screenId - the screen id to activate
	 */
	private void activateScreen(int screenId){
		sc.activateScreen(requestedScreenId);
	}
	
	
	/**
	 * Set the Thread active or not active
	 * @param swapActive - true: active / false: not active
	 */
	public void setSwapActive(boolean swapActive){
		this.swapActive = swapActive;
	}
	
	
	/**
	 * Returns the state of the thread
	 * @return active - true: active / false: not active
	 */
	public boolean isSwapActive(){
		return swapActive;
	}

	
	/**
	 * Checks if the current screen id is screen id from Hall Date
	 * If yes, the thread will become the active state
	 * @param currentScreenId
	 */
	public void reportScreenChange(int currentScreenId){
		if (requestedScreenId != null && requestedScreenId == currentScreenId){
			requestedScreenId = null;
			return;
		}
		
		if (swapActive){
			swapActive = false;
			return;
		}
		
		if (currentScreenId == screenIdHallDate){
			swapActive = true;
		}
		
	}
	
	
	/**
	 * Let this thread sleep for the given milliseconds
	 * @param sleepTime - milliseconds
	 */
	private void goSleep(long sleepTime){
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
