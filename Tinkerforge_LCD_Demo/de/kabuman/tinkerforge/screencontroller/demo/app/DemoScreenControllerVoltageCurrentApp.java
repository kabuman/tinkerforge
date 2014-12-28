package de.kabuman.tinkerforge.screencontroller.demo.app;
import de.kabuman.tinkerforge.alarm.units.AbstractUnit;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.CurrentSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderCountSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderOldCountSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedDurationSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.RotaryEncoderPressedTimerReachedSource;
import de.kabuman.tinkerforge.screencontroller.source.rotaryencoder.VoltageSource;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * Local Weather StationDiplays
 */
public class DemoScreenControllerVoltageCurrentApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "localhost";
    private static final int port = 4223;
    private DemoTfStackImpl tfStack = null;
    
    ScreenController sc;
    
    /**
     * Constructor for Voltage Current Demo  <br>
     *  <br>
     * Diplays  <br>
     * - current count  <br>
     * on a LCD.  <br>
     *  <br>
     * The values will be currently updated every 500 milliseconds.  <br>
     *  <br>
     *  See class DemoTfStackRotaryImpl.java for required Tinferforge Hardware
     */
	public DemoScreenControllerVoltageCurrentApp()  {
		
	    tfStack = new DemoTfStackImpl(this);
		new TfConnectService(host, port, null, tfStack);

		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+tfStack.isConnected()+") ***");
		
			defineScreenController();

	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+tfStack.isConnected()+") ***");
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
		sc = ScreenControllerImpl.getNewInstance(tfStack.getTfLcd());
		
		//---------------------
		// Source Definitions 
		//---------------------

		ItemSourceToPush voltageSource = new VoltageSource(tfStack.getTfVoltageCurrent());
		ItemSourceToPush currentSource = new CurrentSource(tfStack.getTfVoltageCurrent());

		// Push Rotary Encoder Count
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  Voltage Current");
		sc.addMaskLine(s, 1, "Voltage:");
		sc.addMaskLine(s, 2, "Current:");
		// The counter source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem item5 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, voltageSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, 8
				, "%7d");			// position
		sc.addItem(item5);
		ScreenItem item6 = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, currentSource	// counter object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, 8
				, "%7d");			// position
		sc.addItem(item6);

		
		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
