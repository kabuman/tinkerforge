package de.kabuman.tinkerforge.screencontroller.demo.app;

import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.helper.SourceCounter;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClock;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.connect.TfCloneCallbackApp;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

/**
 * Demo Application  <br> 
 * ScreenController: Display same content on Clone Lcds  <br>
 * 
 * Tinkerforge Stack (the Master LCD):  <br>
 * - Master  <br>
 * - Lcd  <br>
 *  <br>
 * Tinkerforge Clone Stack:  <br>
 * - Lcd  <br>
 */
public class DemoScreenControllerMasterApp  implements TfStackCallbackApp, TfCloneCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	boolean cloneFirstConnect = true;

	String[] args;

	// Clone LCD
	int cloneId = 1;
	String cloneHost;
	int clonePort;
	DemoScreenControllerClientApp clone;

	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Push Items which pushes their values by its own  <br>
	 */
	public DemoScreenControllerMasterApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
		
		// Define Clone LCD Connection
		// Connect will be processed after this stack is connected
		cloneHost = "Tf-AU-EG-WZ";
		clonePort = 4223;
	}

	
	/**
	 * This is the same example as it was used in "DemoScreenControllerPUSHApp - 1 Simple"
	 */
	private void demoSimple(){
		
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position

		//---------------------
		// Screen Controller 
		//---------------------
		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		//---------------------
		// Source Definitions 
		//---------------------

		// This counter counts up (one per second) and push its value by itself
		// See SourceCounter for more details how to implement a push item
		ItemSourceToPush counterSource = new SourceCounter();

		// The lateron used Screen Clock needs not source definition
		// It can be directly created as a screen item
		
		
		//---------------------
		// Screens
		//---------------------
		
		// Push Demo Clock
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(1, 0, "  Push Demo-Clock");
		// A screen clock is a push item, depending on the selected format it pushes the new value in different time periods
		ScreenClock item1 = new ScreenClockImpl(
				null			// null: the screen controller writes to LCD. Not the item directly 
				, ScreenClock.FORMAT_DATE_TIME_M
				, s				// screen id
				, ++l				// row
				, p);			// position
		sc.addItem(item1);
		
		
		// Push Demo Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Push Demo-Counter");
		// The counter source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem item2 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, counterSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p);			// position
		sc.addItem(item2);

		
		// Push Demo Clock + Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Clock + Counter");
		// A screen clock is a push item, depending on the selected format it pushes the new value in different time periods
		ScreenClock item3 = new ScreenClockImpl(
				null			// null: the screen controller writes to LCD. Not the item directly 
				, ScreenClock.FORMAT_DATE_TIME_M
				, s				// screen id
				, ++l			// line
				, p);			// position
		sc.addItem(item3);
		// The same counter source is used twice
		ScreenItem item4 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, counterSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p);			// position
		sc.addItem(item4);


		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);

		// Create and connect the Clone LCD
		clone = new DemoScreenControllerClientApp(this, cloneId, cloneHost, clonePort);
		
		// See methods "tfCloneReConnect(..)" and "tfCloneDisconnect(..)" in this class
		// for further handling of the Clone LCD

	}
	
	
	private void demoReplace(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position

		//---------------------
		// Screen Controller 
		//---------------------
		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		//---------------------
		// Source Definitions 
		//---------------------

		// This counter counts up (one per second) and push its value by itself
		// See SourceCounter for more details how to implement a push item
		ItemSourceToPush counterSource = new SourceCounter();

		// The lateron used Screen Clock needs not source definition
		// It can be directly created as a screen item
		
		
		//---------------------
		// Screens
		//---------------------
		
		// Push Demo Counter
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l,   "  Push Demo-Counter");
		sc.addMaskLine(s, l+1, "Value:");
		// The counter source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem item1 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, counterSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p+7);			// position
		sc.addItem(item1);
		
		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);

		// Create and connect the Clone LCD
		clone = new DemoScreenControllerClientApp(this, cloneId, cloneHost, clonePort);
		// See methods "tfCloneReConnect(..)" and "tfCloneDisconnect(..)" in this class
		// for further handling of the Clone LCD


		ScreenDemoHelper.sleep(4000);
		
		// Replace old SourceCounter by another one 
		// This simulates the lost of a connection or something like that
		sc.replaceItemSource(counterSource, new SourceCounter());

		// Add a remark after counter value
		ScreenItem item2 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, "replaced"	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, l				// line
				, p+12);		// position
		sc.addItem(item2);
		
		// Activates the same screen id again (refresh)
		sc.activateScreen(1);
		
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo SIMPLE(1) started");
				System.out.println("Demonstrates Push Items and multi usage");
				System.out.println("3 defined screens");
				System.out.println("Screen 1: Screen Clock");
				System.out.println("Screen 2: Counter");
				System.out.println("Screen 3: Screen Clock + Counter");
				System.out.println("Screen sequence: 1-2-3");
				System.out.println("Default screen: none defined (is 1)");
				System.out.println("switch back time: none defined");
				System.out.println("starts with screen 1\n");
				demoSimple();
				break;

			case 2:
				System.out.println("\nDemo REPLACE(2) started");
				System.out.println("Demonstrates the replacemnt of a Push Item Source");
				System.out.println("A Counter source will be replaced");
				System.out.println("Watch the display. After 4 seconds a new counter source starts again to count");
				demoReplace();
				break;

			default:
				break;
			}
			firstConnect = false;
		} else {
			ScreenDemoHelper.writeMsgReconnected();
			sc.replaceLcd(demoTfStack.getTfLcd());
		}
	}

	
	@Override
	public void tfStackDisconnected() {
		ScreenDemoHelper.writeMsgDisconnected();
	}


	@Override
	public void tfCloneReConnected(int cloneId) {
		if (cloneFirstConnect){
			cloneFirstConnect = false;
			ScreenDemoHelper.writeMsgCloneFirstlyConnected(cloneId);
		} else {
			ScreenDemoHelper.writeMsgCloneReconnected(cloneId);
		}
		sc.setCloneLcdList(clone.getDemoTfStackClone().getTfLcd());
	}


	@Override
	public void tfCloneDisconnected(int cloneId) {
		ScreenDemoHelper.writeMsgCloneDisconnected(cloneId);
		sc.setCloneLcdList(clone.getDemoTfStackClone().getTfLcd());
	}

	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
