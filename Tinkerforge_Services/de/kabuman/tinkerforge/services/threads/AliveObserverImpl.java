package de.kabuman.tinkerforge.services.threads;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;


/**
 * Alive Observer: Checks if a unit is alive.
 * 
 * If it is not alive tries to reconnect the unit
 * This Alive Service can be stopped externally 
 * by calling the method deactivate()
 * 
 */
public class AliveObserverImpl extends Thread{

	// Objects to observe
	List<AliveObservable> aliveObservableList = new ArrayList<AliveObservable>();
	
	// Observer sleep in milli seconds between two alive checks
	long observerSequence;

	// Sets the Observer active or inactive
	boolean aliveObserverActive = true;

	
	/**
	 * Constructor and Starter
	 * 
	 * @param object - the list of units to observe
	 * @param sequence - the sequence to observe in milli seconds
	 */
	@SuppressWarnings("unchecked")
	public AliveObserverImpl(Object object, long sequence) {
		// List object
		if (object instanceof List<?>){
			this.aliveObservableList = (List<AliveObservable>)object;
		}
		
		// Single object
		if (object instanceof AliveObservable){
			this.aliveObservableList.add((AliveObservable)object);
		}
		
		this.observerSequence = sequence;

		LogControllerImpl.getInstance().createTechnicalLogMessage("AliveObserver", "Start", "sequence="+sequence);

		start();  // calls the run() method
	}


	/**
	 * Stops this Alive Observer
	 * and destroys the thread 
	 */
	public void deactivate(){
		aliveObserverActive = false;
		this.interrupt();
	}
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Sleeps and then switch OFF the Alarm Signal
		try {
			boolean doLoop = true;
			while (doLoop) {
				Thread.sleep(observerSequence);
				if (aliveObserverActive){
					
					// Alive Check of each unit in the list
					for (int i = 0; i < aliveObservableList.size(); i++) {
						if (!aliveObserverActive){
							break;
						}
						if (!aliveObservableList.get(i).isConnected()){
							// NOT Connected
							LogControllerImpl.getInstance().createUserLogMessage(aliveObservableList.get(i).getUnitName(),"Alive Observer",LogController.MSG_UNIT_LOST);
							if (!aliveObserverActive){
								break;
							}
							aliveObservableList.get(i).reconnect();
						}
					}
				}
			}
		} catch (InterruptedException e) {
		} 
	}
	
}
