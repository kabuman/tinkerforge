package de.kabuman.tinkerforge.screencontroller.demo.app;
import de.kabuman.tinkerforge.alarm.units.AbstractUnit;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.screencontroller.source.multitouch.MultiTouchStateBinSource;
import de.kabuman.tinkerforge.screencontroller.source.multitouch.MultiTouchStateElectrodeXSource;
import de.kabuman.tinkerforge.screencontroller.source.multitouch.MultiTouchStateSource;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * Local Weather StationDiplays
 */
public class DemoScreenControllerMultiTouchApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "localhost";
    private static final int port = 4223;
    private DemoTfStackImpl demoTfStack = null;
    
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
	public DemoScreenControllerMultiTouchApp()  {
		
	    demoTfStack = new DemoTfStackImpl(this);
		new TfConnectService(host, port, null, demoTfStack);

		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+demoTfStack.isConnected()+") ***");
		
			defineScreenController();

	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+demoTfStack.isConnected()+") ***");
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
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		//---------------------
		// Source Definitions 
		//---------------------

		// Push Multi Touch State
		ItemSourceToPush multiTouchStateSource = new MultiTouchStateSource(demoTfStack.getTfMultiTouch());
		ItemSourceToPush multiTouchStateBinSource = new MultiTouchStateBinSource(demoTfStack.getTfMultiTouch());
		ItemSourceToPush multiTouchStateElectrodeXSource = new MultiTouchStateElectrodeXSource(demoTfStack.getTfMultiTouch(), 1);

		++s; l=0; p=7;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  MultiTouch(PUSH)");
		sc.addMaskLine(s, 1, "state: ");
		// The source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem multiTouchItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, multiTouchStateSource	// object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p
				, "%4d");			// position
		sc.addItem(multiTouchItem);

		ScreenItem multiTouchElectrodeXItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, multiTouchStateElectrodeXSource	// object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, l			// line
				, p+6);			// position
		sc.addItem(multiTouchElectrodeXItem);
		
		// The source is to put into a screen item container
		// For Push or Pull Items the same container (ScreenItemImpl) is used
		ScreenItem multiTouchBinItem = new ScreenItemImpl(
				null			// null: the screen controller writes to LCD. Not the item directly
				, multiTouchStateBinSource	// object (has to implement specific methods)
				, 0				// pull refresh period: 0: no pull, the item pushes by itself  
				, s				// screen id
				, ++l			// line
				, p);			// position
		sc.addItem(multiTouchBinItem);


		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, 0, "  MultiTouch(PUSH)");
		sc.addMaskLine(s, 1, "state: ");
		sc.addMaskLine(s, 2, "Bit  : ");
		sc.addMaskLine(s, 3, "BitNr: 210987654321");

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
