package de.kabuman.tinkerforge.screencontroller.demo.app;

import java.util.ArrayList;
import java.util.Arrays;

import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClock;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClockImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class DemoScreenControllerDefaultApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - assigning a screen id to button 4 of the lcd  <br>
	 * - automatically switch back to this screen  <br>
	 */
	public DemoScreenControllerDefaultApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
	}

	
	private void demoSimple(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Main");
		// Define and assign clock item to screen, line and position
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_DATE_TIME_M, s, ++l, p));

		
		++s; l=0;
		sc.addMaskLine(s, l, "  Second");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_S, s, ++l, p));
		
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Third");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_M, s, ++l, p));


		// Screen sequence to switch through via button
		sc.setScreenSequence(new ArrayList<Integer>(Arrays.asList(1,3,2)));

		// Preset button4 with screen 2
		sc.assignLcdButton3ToScreenId(2);
		
		// Define switch back time 4 seconds 
		sc.setAutoSwitchBackToDefaultScreenTime(4000l);
		
		// Start auto switch back
		sc.activateAutoSwitchBackToDefaultScreen(true);

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);

	}
	
	
	private void demoAssignedOnly(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Main");
		// Define and assign clock item to screen, line and position
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_DATE_TIME_M, s, ++l, p));

		
		++s; l=0;
		sc.addMaskLine(s, l, "  Second");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_S, s, ++l, p));
		
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Third");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_M, s, ++l, p));


		// Screen sequence to switch through via button
		sc.setScreenSequence(new ArrayList<Integer>(Arrays.asList(1,2,3)));

		// Preset button4 with screen 2
		sc.assignLcdButton3ToScreenId(1);
		
		// Start auto switch back
		sc.activateAutoSwitchBackToDefaultScreen(false);

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(3);

	}
	
	
	private void demoAutoNotAssigned(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Main");
		// Define and assign clock item to screen, line and position
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_DATE_TIME_M, s, ++l, p));

		
		++s; l=0;
		sc.addMaskLine(s, l, "  Second");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_S, s, ++l, p));
		
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Third");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_M, s, ++l, p));


		// Screen sequence to switch through via button
		sc.setScreenSequence(new ArrayList<Integer>(Arrays.asList(1,2,3)));

		// No Preset to button4 
//		sc.setDefaultScreenId(1);
		
		// Define switch back time 4 seconds 
		sc.setAutoSwitchBackToDefaultScreenTime(4000l);
		
		// Start auto switch back
		sc.activateAutoSwitchBackToDefaultScreen(true);

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(3);
	}
	
	private void demoRotaryEncoderMultitouch(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 0;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(
				demoTfStack.getTfLcd()
				, demoTfStack.getTfRotaryEncoder()
				, demoTfStack.getTfMultiTouch());
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Main");
		// Define and assign clock item to screen, line and position
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_DATE_TIME_M, s, ++l, p));

		
		++s; l=0;
		sc.addMaskLine(s, l, "  Second");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_S, s, ++l, p));
		
		
		++s; l=0;
		sc.addMaskLine(s, l, "  Third");
		sc.addItem(new ScreenClockImpl(null, ScreenClock.FORMAT_TIME_M, s, ++l, p));


		// Screen sequence to switch through via button
		sc.setScreenSequence(new ArrayList<Integer>(Arrays.asList(1,2,3)));

		// Preset to button4 and Default Screen for AutoSwitchBack 
		sc.assignLcdButton3ToScreenId(1);
		
		// Define switch back time 4 seconds and enables it 
//		sc.setAutoSwitchBackToDefaultScreenTime(3000l);
		sc.activateAutoSwitchBackToDefaultScreen(true);
		

		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);

		sc.assignMultiTouchElectrodeToScreenId(0, ScreenController.BACKLIGHT_ON);
		sc.assignMultiTouchElectrodeToScreenId(1, ScreenController.NEXT_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(2, ScreenController.PREV_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(3, ScreenController.DEFAULT_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(4, ScreenController.BACKLIGHT_OFF);
		sc.assignMultiTouchElectrodeToScreenId(5, ScreenController.BACKLIGHT_ON_TEMP);
		
		sc.report();
	}
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo SIMPLE(1) started");
				System.out.println("3 defined screens");
				System.out.println("Screen sequence: 1-3-2");
				System.out.println("Default screen: 2");
				System.out.println("switch back time: 4000 milliseconds");
				System.out.println("starts with screen 1\n");
				demoSimple();
				break;

			case 2:
				System.out.println("\nDemo Assigned Only(2) started");
				System.out.println("3 defined screens");
				System.out.println("Screen sequence: 1-2-3");
				System.out.println("Default screen: 1");
				System.out.println("No automatically switch back to default screen");
				System.out.println("starts with screen 3\n");
				demoAssignedOnly();
				break;

			case 3:
				System.out.println("\nDemo Auto Not Assigned(3) started");
				System.out.println("3 defined screens");
				System.out.println("Screen sequence: 1-2-3");
				System.out.println("No Default screen defined (will be 1)");
				System.out.println("switch back time: 4000 milliseconds");
				System.out.println("starts with screen 3\n");
				demoAutoNotAssigned();
				break;

			case 4:
				System.out.println("\nDemo RotaryEncoder + Multitouch,  started");
				System.out.println("3 defined screens");
				System.out.println("Screen sequence: 1-2-3");
				System.out.println("No Default screen defined (will be 1)");
				System.out.println("switch back time: 4000 milliseconds");
				System.out.println("starts with screen 3\n");
				System.out.println("Rotary Encoder assignments:");
				System.out.println("Right: NEXT_SCREEN");
				System.out.println("Left : PREV_SCREEN");
				System.out.println("Click <  2 sec. : BACKLIGHT_ON_TEMP");
				System.out.println("Click >= 2 sec. : BACKLIGHT_ON\n");
				System.out.println("Multitouch assignments:");
				System.out.println("Electrode 0: BACKLIGHT_ON");
				System.out.println("Electrode 1: NEXT_SCREEN");
				System.out.println("Electrode 2: PREV_SCREEN");
				System.out.println("Electrode 3: DEFAULT_SCREEN");
				System.out.println("Electrode 4: BACKLIGHT_OFF");
				System.out.println("Electrode 5: BACKLIGHT_ON_TEMP\n");
				demoRotaryEncoderMultitouch();
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
		return "DemoScreenControllerDefaultApp";
	}

}
