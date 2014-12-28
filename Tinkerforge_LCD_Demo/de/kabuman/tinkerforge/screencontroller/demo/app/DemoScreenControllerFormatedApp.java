package de.kabuman.tinkerforge.screencontroller.demo.app;

import java.util.Date;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class DemoScreenControllerFormatedApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - formated output of item values
	 */
	public DemoScreenControllerFormatedApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
	}

	
	private void demo(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position

		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		ScreenController sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());

		// String
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  String Examples");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new String("String"), 0, s, ++l, p, "This is a: %s"));

		// Double
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Double Examples");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(1.15), 0, s, ++l, p, "%08.3f"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(1.15), 0, s, ++l, p, "%8.3f"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(1.15), 0, s, ++l, p, "%8.3f V"));
		l=0; p=10;
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(-1.15), 0, s, ++l, p, "%08.3f"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(-1.15), 0, s, ++l, p, "%8.3f"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Double(-1.15), 0, s, ++l, p, "%8.3f V"));

		// Integer
		++s; l=0; p=0;
		sc.addMaskLine(s, l, "  Integer Examples");
		sc.addNewScreenToSequence(s);
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(4711), 0, s, ++l, p, "%06d"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(4711), 0, s, ++l, p, "%6d"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(4711), 0, s, ++l, p, "%06d V"));
		l=0; p=10;
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(-4711), 0, s, ++l, p, "%06d"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(-4711), 0, s, ++l, p, "%6d"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Integer(-4711), 0, s, ++l, p, "%06d V"));

		// Week Day Name
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Week day");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tA (%%tA)"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%ta       (%%ta)"));
		
		// Month Name
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Month");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tB (%%tB)"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tb    (%%tb)"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tm     (%%tm)"));
		
		// Time
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Time");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tR    (%%tR)"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%tT (%%tT)"));
		
		// Date String Formated: DD.MM.YY-HH:MM
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Date String Format");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Date(), 0, s, ++l, p,"%1$td.%1$tm.%1$ty-%1$tH:%1$tM"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), "%1$td.%1$tm.%1$ty", 0, s, ++l, p+2));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), "-%1$tH:%1$tM", 0, s, ++l, p+2));
		
		// Date String Formated: DD.MM.YY-HH:MM
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Date/Time formated");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), DateTimeService.DF_DATE_TIME_S.format(new Date()), 0, s, ++l, p));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), "DateTimeService", 0, s, ++l, p+2));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), ".DF_DATE_TIME_S", 0, s, ++l, p+2));
		
		// Boolean
		++s; l=0; p=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Boolean formated");
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(true), 0, s, ++l, p, "Ein", "Aus"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(false), 0, s, l, p+8, "Ein", "Aus"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(true), 0, s, ++l, p, "On", "Off"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(false), 0, s, l, p+8, "On", "Off"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(true), 0, s, ++l, p, "True", "False"));
		sc.addItem(new ScreenItemImpl(demoTfStack.getTfLcd(), new Boolean(false), 0, s, l, p+8, "True", "False"));

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo(1) started");
				System.out.println("Demonstrates several formated screen items");
				System.out.println("9 defined screens:");
				System.out.println("Screen 1: String");
				System.out.println("Screen 2: Double");
				System.out.println("Screen 3: Integer");
				System.out.println("Screen 4: Week day");
				System.out.println("Screen 5: Month");
				System.out.println("Screen 6: Time");
				System.out.println("Screen 7: Date 1");
				System.out.println("Screen 8: Date 2");
				System.out.println("Screen 9: Boolean");
				System.out.println("starts with screen 1\n");
				demo();
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
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
