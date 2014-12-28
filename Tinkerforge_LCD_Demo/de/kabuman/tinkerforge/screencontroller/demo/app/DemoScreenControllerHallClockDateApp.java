package de.kabuman.tinkerforge.screencontroller.demo.app;

import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.helper.SourceCounter;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallDateImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class DemoScreenControllerHallClockDateApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
    TemperatureSensorItem temperatureSensorItem;
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Pull Items whose values will be refreshed by screen controller after a given time period
	 */
	public DemoScreenControllerHallClockDateApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
		
	}

	
	private void demoClock(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// Hall Clock
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallClockImpl(null, s));
		
		// Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  Counter Source");
		sc.addItem(new ScreenItemImpl(null, new SourceCounter(), 0, s, ++l, p));
		
		// Activate the first screen (and makes the screenController active too)
		sc.activateScreen(1);
		
	}
	
	
	private void demoDate(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// Hall Date
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallDateImpl(null, s));
		
		// Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  Counter Source");
		sc.addItem(new ScreenItemImpl(null, new SourceCounter(), 0, s, ++l, p));
		
		// Activate the first screen (and makes the screenController active too)
		sc.activateScreen(1);
		
	}
	
	
	private void demoClockDate(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// Hall Clock
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallClockImpl(null, s));
		
		// Hall Date
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallDateImpl(null, s));
		
		// Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  Counter Source");
		sc.addItem(new ScreenItemImpl(null, new SourceCounter(), 0, s, ++l, p));
		
		// Activate the first screen (and makes the screenController active too)
		sc.activateScreen(1);
	}
	
	
	private void demoClockDateSwap(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// Hall Clock
		++s;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallClockImpl(null, s));
		
		// Hall Date
		++s;
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenHallDateImpl(null, s));
		
		// Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  Counter Source");
		sc.addItem(new ScreenItemImpl(null, new SourceCounter(), 0, s, ++l, p));
		

		// Activate the first screen (and makes the screenController active too)
		sc.setHallClockDateSwap(true, 10000l, 4000l);
		sc.activateScreen(1);
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo Clock(1) started");
				System.out.println("Demonstrates the Hall Clock + Push Item Counter on a 2. Screen");
				demoClock();
				break;

			case 2:
				System.out.println("\nDemo Date(2) started");
				System.out.println("Demonstrates the Hall Date + Push Item Counter on a 2. Screen");
				demoDate();
				break;

			case 3:
				System.out.println("\nDemo ClockDate(3) started");
				System.out.println("Demonstrates the Hall Clock, Hall Date and Push Item Counter on a 3. Screen");
				demoClockDate();
				break;

			case 4:
				System.out.println("\nDemo ClockDate Swap(4) started");
				System.out.println("Demonstrates the Hall Clock, Hall Date and th swap between them every 4 seconds");
				demoClockDateSwap();
				break;

			default:
				break;
			}
			firstConnect = false;
		} else {
			ScreenDemoHelper.writeMsgReconnected();
			sc.replaceLcd(demoTfStack.getTfLcd());

			TemperatureSensorItem oldItemSource = temperatureSensorItem;
			
			temperatureSensorItem = new TemperatureSensorItemImpl(null, demoTfStack.getTfTemperature(), 2000);
			sc.replaceItemSource(oldItemSource, temperatureSensorItem);
			
			sc.activateScreen(1);

		}
	}

	
	@Override
	public void tfStackDisconnected() {
		ScreenDemoHelper.writeMsgDisconnected();
		if (sc != null){
			sc.deactivateAll();
		}

	}

	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
