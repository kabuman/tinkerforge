package de.kabuman.tinkerforge.screencontroller.demo.app;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenAliveIndicator;
import de.kabuman.tinkerforge.screencontroller.items.ScreenAliveIndicatorImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallDateImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

/**
 * Demo Application for:  <br> 
 * - Standalone Hall Clock  <br>
 * - Standalone Hall Date  <br>
 * - Standalone Alive Indicator  <br>
 * 
 * Tinkerforge:  <br>
 * - Master  <br>
 * - Lcd  <br>
 *
 */
public class DemoStandAloneHallClockDateAliveApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Push Items which pushes their values by its own  <br>
	 */
	public DemoStandAloneHallClockDateAliveApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
	}

	
	/**
	 * 
	 */
	private void demoAlive(){
		try {
			demoTfStack.getTfLcd().clearDisplay();
			demoTfStack.getTfLcd().backlightOn();
			
		} catch (TimeoutException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct and start Alive Indicator immediately
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(), null, "Alive Indicator Demo", 0, 0).setActive(true);
		
		// Construct and start Alive Indicator immediately
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(), null, "Colon:", 1, 2).setActive(true);
		
		// Load the customized characters
		new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());
				
		// Construct and start Hall Colon immediately
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(), null, "Dot:", 3, 3).setActive(true);
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(),null,ScreenAliveIndicator.TYPE_HALLCLOCK_COLON).setActive(true);

		// Construct and start Hall Date Dots immediately
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(), null, "Dot:", 3, 14).setActive(true);
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(),null,ScreenAliveIndicator.TYPE_HALLDATE_DOTS).setActive(true);
	}
	
	private void demoHallDate(){
		try {
			demoTfStack.getTfLcd().clearDisplay();
			demoTfStack.getTfLcd().backlightOn();
			
		} catch (TimeoutException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct and start Hall Date + Alive Indicator
		new ScreenHallDateImpl(demoTfStack.getTfLcd(), null);
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(),null,ScreenAliveIndicator.TYPE_HALLDATE_DOTS).setActive(true);

	}
	
	private void demoHallClock(){
		try {
			demoTfStack.getTfLcd().clearDisplay();
			demoTfStack.getTfLcd().backlightOn();
			
		} catch (TimeoutException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct and start Hall Clock + Alive Indicator
		new ScreenHallClockImpl(demoTfStack.getTfLcd(), null);
		new ScreenAliveIndicatorImpl(demoTfStack.getTfLcd(),null,ScreenAliveIndicator.TYPE_HALLCLOCK_COLON).setActive(true);
	}
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo Hall Clock + Alive Indicator (1) started");
				demoHallClock();
				break;

			case 2:
				System.out.println("\nDemo Hall Date + Alive Indicator (2) started");
				demoHallDate();
				break;

			case 3:
				System.out.println("\nDemo Alive Indicator (3) started");
				demoAlive();
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
