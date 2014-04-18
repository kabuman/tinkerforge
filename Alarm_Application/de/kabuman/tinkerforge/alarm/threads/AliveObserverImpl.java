package de.kabuman.tinkerforge.alarm.threads;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;



public class AliveObserverImpl extends Thread{

	// Alarm Signal Object
	List<Unit> unitList = new ArrayList<Unit>();
	
	// distance between two alive checks
	long sequence;

	boolean isAlive = false;
	
	boolean active = true;

	/**
	 * Constructor
	 */
	public AliveObserverImpl(List<Unit> unitList, long sequence) {
		this.unitList = unitList;
		this.sequence = sequence;

		LogControllerImpl.getInstance().createTechnicalLogMessage("AliveObserver", "Start", "sequence="+sequence);

		start();  // calls the run() method
	}


	public void deactivate(){
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
		// Sleeps and then switch OFF the Alarm Signal
		try {
			boolean doLoop = true;
			while (doLoop) {
				Thread.sleep(sequence);
				if (active){
					for (int i = 0; i < unitList.size(); i++) {
						if (!active){
							break;
						}
						if (!unitList.get(i).isConnected()){
							LogControllerImpl.getInstance().createUserLogMessage(unitList.get(i).getUnitName(),"Alive Observer",LogController.MSG_UNIT_LOST);
							if (!active){
								break;
							}
							unitList.get(i).reconnect();
						}
					}
				}
			}
		} catch (InterruptedException e) {
		} 
	}
	
}
