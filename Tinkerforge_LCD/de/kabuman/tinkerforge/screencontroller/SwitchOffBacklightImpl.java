package de.kabuman.tinkerforge.screencontroller;



/**
 * Displays a clock at the given line and position in the specified format
 * on a 20x4 LCD Bricklet
 */
public class SwitchOffBacklightImpl extends Thread implements SwitchOffBacklight{

	// Calculated sleep time in milliseconds
	private long switchOffAfter;

	// activates/deactivates the display of the clock
	private boolean active = true;

	
		/**
		 * Constructor and Starter
		 * 
	 * @param switchOffAfter - switch off after "switchOffAfter"- milliseconds
	 */
	public SwitchOffBacklightImpl(long switchOffAfter) {
		this.switchOffAfter = switchOffAfter;
		
		start();  // calls the run() method
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.threads.DisplayClock#terminate()
	 */
	public void terminate(){
		active = false;
		this.interrupt();
	}
	
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(switchOffAfter);
			if (active){
				ScreenControllerImpl.getInstance().backlightOff();
			}
		} catch (InterruptedException e) {
		}
		terminate();
	}


	public boolean isActive() {
		return active;
	}


	@Override
	public void startBacklight() {
		// TODO Auto-generated method stub
		
	}
	
}
