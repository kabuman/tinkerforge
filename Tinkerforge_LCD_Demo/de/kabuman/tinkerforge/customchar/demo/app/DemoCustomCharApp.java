package de.kabuman.tinkerforge.customchar.demo.app;

import java.util.Date;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.customchar.CustomCharDriverLargeNumbers;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.demo.helper.ScreenDemoHelper;
import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

public class DemoCustomCharApp  implements TfStackCallbackApp{

	DemoTfStackImpl demoTfStack = null; 
	
	ScreenController sc = null; 
			
	boolean firstConnect = true;

	String[] args;
	
	/**
	 * Demonstrates the settings and usage of  <br>
	 * - Push Items which pushes their values by its own  <br>
	 */
	public DemoCustomCharApp(String[] args) {
		// transfer args to start the selected demo 
		// via listener callback method "tfStackReConnected()" 
		// => see end of this class
		this.args = args;
		

		// Define Tinkerforge Stack (Listener)
		demoTfStack = new DemoTfStackImpl(this);

		
		//Create connection and add listener
		new TfConnectService("localhost",4223, null, demoTfStack);
	}

	
	private void demoNumbers(){
		CustomCharDriverLargeNumbers lcc = new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());
		
		clearAndSwitchON(true);
		lcc.writeNumber(1);
		ScreenDemoHelper.sleep(2000);
		lcc.writeNumber(11);
		ScreenDemoHelper.sleep(2000);
		lcc.writeNumber(111);
		ScreenDemoHelper.sleep(2000);
		lcc.writeNumber(1111);
		ScreenDemoHelper.sleep(2000);
		lcc.writeNumber(11111);
		ScreenDemoHelper.sleep(4000);
		clearAndSwitchON(false);
	}
	
	
	private void demoDate(){
		CustomCharDriverLargeNumbers lcc = new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());

		clearAndSwitchON(true);
		lcc.writeDate(new Date());
		ScreenDemoHelper.sleep(10000);
		clearAndSwitchON(false);
	}
	
	
	private void demoWeekDays(){
		CustomCharDriverLargeNumbers lcc = new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());

		clearAndSwitchON(true);
		lcc.writeWeekDay("Mo");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("Di");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("Mi");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("Do");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("Fr");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("Sa");
		ScreenDemoHelper.sleep(2000);
		lcc.writeWeekDay("So");
		ScreenDemoHelper.sleep(10000);

		clearAndSwitchON(false);
	}
	
	
	private void demoWeekDayAndMonthDay(){
		CustomCharDriverLargeNumbers lcc = new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());

		clearAndSwitchON(true);
		lcc.writeWeekMonthDay(new Date());
		ScreenDemoHelper.sleep(10000);

		clearAndSwitchON(false);
	}
	

	private void demoAllNumbers(){
		CustomCharDriverLargeNumbers lcc = new CustomCharDriverLargeNumbers(demoTfStack.getTfLcd());

		clearAndSwitchON(true);
		short pos = 0;
		short count = 0;
		
		while (count++ < 4) {
			
			// Show the number: 01234
			pos = 0;
			for (int i = 0; i <= 4; i++) {
				lcc.write((short)(pos++*4), i);
			}
			ScreenDemoHelper.sleep(3000);
			
			// Show the number 56789
			pos = 0;
			for (int i = 5; i <= 9; i++) {
				lcc.write((short)(pos++*4), i);
			}
			ScreenDemoHelper.sleep(3000);
		}
		
		clearAndSwitchON(false);
	}
	
	
	/**
	 * Clear Display and switch ON or OFF depending on the parameter
	 * @param on - true: switch backlight on;  false: switch off
	 */
	private void clearAndSwitchON(boolean on){
		try {
			if (on){
				demoTfStack.getTfLcd().clearDisplay();
				demoTfStack.getTfLcd().backlightOn();
			} else {
				demoTfStack.getTfLcd().clearDisplay();
				demoTfStack.getTfLcd().backlightOff();
			}
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			ScreenDemoHelper.writeMsgFirstlyConnected();
			int demoId = Integer.valueOf(args[0]);
			
			switch (demoId) {
			case 1:
				System.out.println("\nDemo Numbers(1) started");
				System.out.println("Demonstrates the output of a 5 digit large number");
				System.out.println("Shows all sizes of this number");
				System.out.println("Each size will be displayed 2 seconds");
				demoNumbers();
				break;

			case 2:
				System.out.println("\nDemo Date(2) started");
				System.out.println("Demonstrates the output of a date hh:mm");
				System.out.println("The date will be displayed 10 seconds");
				demoDate();
				break;

			case 3:
				System.out.println("\nDemo WeekDays(3) started");
				System.out.println("Demonstrates the output of a week day in 2 letters");
				System.out.println("Each weekday will be displayed 2 seconds");
				demoWeekDays();
				break;

			case 4:
				System.out.println("\nDemo WeekDay and MonthDay(4) started");
				System.out.println("Demonstrates the output of a week day + month day in 4 digits: dd.99.");
				System.out.println("Will be displayed 10 seconds");
				demoWeekDayAndMonthDay();
				break;

			case 5:
				System.out.println("\nDemo AllNumbers(5) started");
				System.out.println("Demonstrates the appearance of each number");
				System.out.println("The Numbers will be displayd in 2 groups: 01234 and 56789");
				System.out.println("Each group will be displayed 3 seconds");
				System.out.println("Each group will be displayed 4 times");
				demoAllNumbers();
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
		return "DemoCustomCharApp";
	}

}
