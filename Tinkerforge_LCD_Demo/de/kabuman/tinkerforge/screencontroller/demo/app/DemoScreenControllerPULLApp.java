package de.kabuman.tinkerforge.screencontroller.demo.app;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPull;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class DemoScreenControllerPULLApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
    TemperatureSensorItem temperatureSensorItem;
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Pull Items whose values will be refreshed by screen controller after a given time period
	 */
	public DemoScreenControllerPULLApp(String[] args) {
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
		int p = 2;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		temperatureSensorItem = new TemperatureSensorItemImpl(null, demoTfStack.getTfTemperature(), 2000);

		// Pull Temperature item
		++s; l=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Pull Item Demo");
		sc.addItem(new ScreenItemImpl(null, temperatureSensorItem, 4000, s, ++l, p));
		sc.addItem(new ScreenItemImpl(null, "C", 0, s, l, p+5)); // no refresh period required
		sc.addItem(new ScreenItemImpl(null, temperatureSensorItem, 2000, s, ++l, p));
		sc.addItem(new ScreenItemImpl(null, "C", 0, s, l, p+5)); // no refresh period required
		// The "C" after the values can be defined as a maskLine too
		
		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}
	
	
	private void demoInnerClass(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 2;  // position

		// Defines a inner class temperature item source.  <br>
		class TemperatureSource implements ItemSourceToPull{
			
			BrickletTemperature t;
			
			// Constructor with bricklet var
			TemperatureSource(BrickletTemperature t){
				this.t = t;
			}
			
			// implementing DisplayableContent
			public String getItemValue(){
				try {
					return String.format("%3.1f", t.getTemperature()*.01);
				} catch (TimeoutException | NotConnectedException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		
		//Creates the source
		ItemSourceToPull temperatureSensorItem = new TemperatureSource(demoTfStack.getTfTemperature());

		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		

		// Pull Temperature item
		++s; l=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Pull Inner Class");
		sc.addItem(new ScreenItemImpl(null, temperatureSensorItem, 4000, s, ++l, p));
		sc.addMaskLine(s, l, "       C");
		sc.addItem(new ScreenItemImpl(null, temperatureSensorItem, 2000, s, ++l, p));
		sc.addMaskLine(s, l, "       C");
		
		// Activate the first screen to show (makes the screenController active too)
		sc.activateScreen(1);
	}
	
	
	private void demoReplace(){
		int s = 0;	// screen id
		int l = 0;  // line
		int p = 2;  // position


		//---------------------
		// Screen Controller 
		//---------------------

		// Instantiate ScreenController
		sc = ScreenControllerImpl.getNewInstance(demoTfStack.getTfLcd());
		
		String firstName = "Karsten";
		String lastName = "Buchmann";
		
		// Source: firstName
		++s; l=0;
		sc.addNewScreenToSequence(s);
		sc.addMaskLine(s, l, "  Replace Pull Item");
		sc.addItem(new ScreenItemImpl(null, firstName,0, s, ++l, p+6));
		sc.addItem(new ScreenItemImpl(null, "Name: ", 0, s, l, p)); // no refresh period required
		
		// Activate the screen to show (makes the screenController active too)
		sc.activateScreen(1);
		
		ScreenDemoHelper.sleep(4000);

		// Source "firstName" will be replaced by "lastName" after 4 seconds
		sc.replaceItemSource(firstName, lastName);
		
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo SIMPLE(1) started");
				System.out.println("Demonstrates 2 Pull Items with same source on one screen");
				System.out.println("1. Screen item pulls its value every 4 seconds");
				System.out.println("2. Screen item pulls its value every 2 seconds");
				System.out.println("Both items uses the same temperature bricklet");
				System.out.println("\nPut your finger on the temp. bricklet and watch the display");
				demoSimple();
				break;

			case 2:
				System.out.println("\nDemo INNERCLASS(2) started");
				System.out.println("Demonstrates 2 Pull Items with same source on one screen");
				System.out.println("The source is defined as a inner class");
				System.out.println("1. Screen item pulls its value every 4 seconds");
				System.out.println("2. Screen item pulls its value every 2 seconds");
				System.out.println("\nPut your finger on the temp. bricklet and watch the display");
				demoInnerClass();
				break;

			case 3:
				System.out.println("\nDemo Replace(3) started");
				System.out.println("Demonstrates the replace of a pull item");
				System.out.println("The sources are 2 Strings (firstName, lastName):");
				System.out.println("The firstName will be displayed for 4 seconds");
				System.out.println("The firstName will be replaced then by the lastName");
				System.out.println("The replacement is to watch on the screen directly without defined refreshment (is Zero)");
				System.out.println("\nIf required a mask line can be defined as a String item and can be replaced in the same way.");
				demoReplace();
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
