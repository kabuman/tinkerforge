package de.kabuman.tinkerforge.hallclock;

import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallClockImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class HallClockDateApp  implements TfStackCallbackApp{

	HallClockTfStackImpl demoTfStack = null; 
    TemperatureSensorItem temperatureSensorItem;
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Pull Items whose values will be refreshed by screen controller after a given time period
	 */
	public HallClockDateApp(String[] args) {

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new HallClockTfStackImpl(this);

		
		//Create connection and add listener
//		new TfConnectService("localhost",4223, null, demoTfStack);
		new TfConnectService("Tf-AU-EG-WZ",4223, null, demoTfStack);
		
	}

	
	private void demoClock(){
		int s = 0;	// screen id


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// Hall Clock
		++s; 
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallClockImpl(null, s));
		
		
		// Activate the first screen (and makes the screenController active too)
		sc.activateScreen(1);
		
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			HallClockHelper.writeMsgFirstlyConnected();
			System.out.println("\nHall Clock(1) started");
			demoClock();
			firstConnect = false;
		} else {
			HallClockHelper.writeMsgReconnected();
			sc.replaceLcd(demoTfStack.getTfLcd());
//			demoClock();
		}
	}

	
	@Override
	public void tfStackDisconnected() {
		HallClockHelper.writeMsgDisconnected();
		if (sc != null){
			sc.deactivateAll();
		}

	}

	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
