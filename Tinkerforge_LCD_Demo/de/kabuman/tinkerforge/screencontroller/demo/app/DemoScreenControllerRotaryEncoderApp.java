package de.kabuman.tinkerforge.screencontroller.demo.app;
import de.kabuman.tinkerforge.alarm.units.AbstractUnit;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackRotaryImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderCountSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderOldCountSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedDurationSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedTimerReachedSource;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * Local Weather StationDiplays
 */
public class DemoScreenControllerRotaryEncoderApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "localhost";
    private static final int port = 4223;
    private DemoTfStackRotaryImpl rotaryTfStack = null;
    
    ScreenController sc;
    
    /**
     * Constructor for Rotary Encoder Demo  <br>
     *  <br>
     * Diplays  <br>
     * - current count  <br>
     * on a LCD.  <br>
     *  <br>
     * The values will be currently updated every 500 milliseconds.  <br>
     *  <br>
     *  See class DemoTfStackRotaryImpl.java for required Tinferforge Hardware
     */
	public DemoScreenControllerRotaryEncoderApp()  {
		
	    rotaryTfStack = new DemoTfStackRotaryImpl(this);
		new TfConnectService(host, port, null, rotaryTfStack);

		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+rotaryTfStack.isConnected()+") ***");
		
			defineScreenController();

	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+rotaryTfStack.isConnected()+") ***");
		if (sc != null){
			sc.deactivateAll();
		}
	}

	private void defineScreenController(){
		if (sc != null){
			sc.deactivateAll();
		}
	
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position

		//---------------------
		// Screen Controller 
		//---------------------
		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(rotaryTfStack.getTfLcd());
		
		//---------------------
		// Source Definitions 
		//---------------------

		ItemSourceToPush rotaryCountSource = new RotaryEncoderCountSource(rotaryTfStack.getTfRotaryEncoder());
		ItemSourceToPush rotaryOldCountSource = new RotaryEncoderOldCountSource(rotaryTfStack.getTfRotaryEncoder());
		ItemSourceToPush rotaryPressedSource = new RotaryEncoderPressedSource(rotaryTfStack.getTfRotaryEncoder());
		ItemSourceToPush rotaryPressedDurationSource = new RotaryEncoderPressedDurationSource(rotaryTfStack.getTfRotaryEncoder());
		RotaryEncoderPressedTimerReachedSource rotaryPressedTimeReachedSource = new RotaryEncoderPressedTimerReachedSource(rotaryTfStack.getTfRotaryEncoder());
		rotaryPressedTimeReachedSource.setPressedTimeReached(2000);
		

		// Push Rotary Encoder Count
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  RotaryEncoder(PUSH)");
		sc.addMaskLine(s, 1, "Count:+999  old:+999");
		// The counter source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem item5 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, rotaryCountSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, 6
				, "%+4d");			// position
		sc.addItem(item5);
		ScreenItem oldCountItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, rotaryOldCountSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, l			// line
				, 16
				,"%+4d");			// position
		sc.addItem(oldCountItem);
		ScreenItem item6 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, rotaryPressedSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p
				,"ein"
				,"aus");			// position
		sc.addItem(item6);
		ScreenItem pressedDurationItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, rotaryPressedDurationSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, l			// line
				, p+5);
		sc.addItem(pressedDurationItem);
		ScreenItem pressedTimeReachedItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, rotaryPressedTimeReachedSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, l			// line
				, p+15);
		sc.addItem(pressedTimeReachedItem);

		
		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
