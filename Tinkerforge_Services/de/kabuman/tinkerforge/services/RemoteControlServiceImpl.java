package de.kabuman.tinkerforge.services;

import java.io.IOException;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.PercentageService;
import de.kabuman.common.services.StringService;


/**
 * Service for Universal Remote Control
 * - Instantiates the objects for the devices
 * - Makes the mapping between object and used device (identified by UID)
 * - Creates and connects each needed device
 * - Provides the object devices by getter
 * - Provides getter for different measures (voltage, current)
 * - Provides getter for network parameter
 */
public class RemoteControlServiceImpl implements RemoteControlService{
	
	IPConnection ipcon;

	// Devices Remote Control
	private BrickMaster brickMaster;
	private BrickletJoystick joystick;
	private BrickletIO16 io16;
	private BrickletLCD20x4 lcd;
	private BrickletRotaryPoti rotaryPoti;

	// Services
	private StackService stackService;

	// Further Helper
	private DisplayObserver displayObserver;
	private final long BACKLIGHT_ON_DURATION = 5000;
	private RefreshObserver refreshObserver;
	private final long REFRESH_FREQUENCY = 5000;

	private String line0old = "";
	private String line1old = "";
	private String line2old = "";
	private String line3old = "";
	
	/**
	 * Constructor
	 * 
	 * Instantiates and maps the devices
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public RemoteControlServiceImpl() throws TimeoutException, IOException{
		try {
			ipcon = ConnectServiceImpl.getInstance().createConnect(ConnectService.WE1_HOST, ConnectService.PORT);

			brickMaster =  (BrickMaster) ConnectServiceImpl.getInstance().createAndConnect(ipcon, ConnectService.MB3, "RC Master", 6.7);
//			joystick = (BrickletJoystick) ConnectServiceImpl.getInstance().createAndConnect(ipcon, ConnectService.JS1, "RC Joystick Nav Control");
			io16 = (BrickletIO16) ConnectServiceImpl.getInstance().createAndConnect(ipcon, ConnectService.IO161, "RC IO16 Switches/Taster");
			lcd =  (BrickletLCD20x4) ConnectServiceImpl.getInstance().createAndConnect(ipcon, ConnectService.LCD201, "RC LCD Display");
			rotaryPoti = (BrickletRotaryPoti) ConnectServiceImpl.getInstance().createAndConnect(ipcon, ConnectService.RP1, "RC RotaryPoti Max. Velocity Limiter");
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Devices

		// Services
		stackService = new StackServiceImpl(brickMaster);

		configLcd();
		
		configIo16();
	}
	
	private void configIo16(){
		short pinMask = PINMASK_ALL;

		try {
			// switch/taster configuration of 2 Swtiches + 4 Taster: 0,..,5: they are to configure as INPUT and PULLUP
			io16.setPortConfiguration('a', pinMask, 'i', true);
			io16.setPortConfiguration('a', pinMask, 'i', true);
			io16.setPortConfiguration('a', pinMask, 'i', true);
			io16.setPortConfiguration('a', pinMask, 'i', true);
			io16.setPortConfiguration('a', pinMask, 'i', true);

			// Enable interrupt on all pins
			io16.setPortInterrupt('a', pinMask);
			io16.setPortInterrupt('a', pinMask);
			io16.setPortInterrupt('a', pinMask);
			io16.setPortInterrupt('a', pinMask);
			io16.setPortInterrupt('a', pinMask);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Configuration of the LCD Display 
	 */
	private void configLcd(){
		try {
			getLcd().clearDisplay();
			getLcd().setConfig(false, false);
			getLcd().backlightOff();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String prepareLine(String line){
		String lcdLine = StringService.create(20);
		return StringService.overwrite(lcdLine, 0, line);
	}
	
	public void refreshObserverStart(
			HostService hostService,
			VehicleService clientVehicleService,
			StackService clientStackService) {
		
		refreshObserver = new RefreshObserverImpl(lcd, REFRESH_FREQUENCY, hostService, clientVehicleService, clientStackService, this);
	}
	
	public void refreshObserverStop(){
		if (refreshObserver != null){
			refreshObserver.deactivate();
		}
	}

	private void writeTextToLCD(BrickletLCD20x4 lcd, int lineNo, int pos, String text){
		try {
			lcd.writeLine((short)lineNo,(short)pos,text);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void refreshAllLcd(
			HostService hostService,
			VehicleService clientVehicleService,
			StackService clientStackService) throws TimeoutException{

		// Line 1
		String rcShortFormVoltage = stackService.getShortFormVoltage();
		String rcAlertIndicatorMinVoltage = String.valueOf(stackService.getAlertIndicatorMinVoltage()); 
		String rcShortFormCurrent = stackService.getShortFormCurrent();
		String rcSignalStrength = stackService.getChibiService().getChibiSignalStrength();
		String line1 = "RC  "+rcShortFormVoltage + rcAlertIndicatorMinVoltage +" "+rcShortFormCurrent+" "+rcSignalStrength;
		if (!line1.equals(line1old)){
			writeTextToLCD(getLcd(), 1, 0, prepareLine(line1));
			line1old = line1;
		}

		// Line 0
		String hostSignalStrength = hostService.getStackService().getChibiService().getChibiSignalStrength();
		String noAck = hostService.getStackService().getChibiService().getChibiErrorNoAck().toString();
		String line0 = "HOST"+" "+hostSignalStrength+" "+noAck;
		if (!line0.equals(line0old)){
			writeTextToLCD(getLcd(), 0, 0, prepareLine(line0));
			line0old = line0;
		}
		

		// Line 3
		String operatingTimeAppl = StopWatchApplService.getInstance().getLCDFormStopWatch(StopWatchApplService.getInstance().getCurrent());
		String operatingTimeMotor = StopWatchMotorService.getInstance().getLCDFormStopWatch(StopWatchMotorService.getInstance().getCurrent());
		String clientMaxVelocity = PercentageService.getShortFormPercentage(clientVehicleService.getMaxVelocity(), 0, 32767);
		String line3 = clientMaxVelocity+"/"+operatingTimeAppl+"/"+operatingTimeMotor;
		if (!line3.equals(line3old)){
			String lcdLine3 = StringService.create(14);
			StringService.overwrite(lcdLine3, 0, line3);
			writeTextToLCD(getLcd(), 3, 0, prepareLine(line3));
			line3old = line3;
		}
		
		// Line 2
		String clientShortFormVoltage = clientStackService.getShortFormVoltage();
		String clientAlertIndicatorMinVoltage = String.valueOf(clientStackService.getAlertIndicatorMinVoltage()); 
		String clientShortFormCurrent = clientStackService.getShortFormCurrent();
		String clientSignalStrength = clientStackService.getChibiService().getChibiSignalStrength();
		String line2 = "Cli "+clientShortFormVoltage+clientAlertIndicatorMinVoltage+" "+clientShortFormCurrent+" "+clientSignalStrength;
		if (!line2.equals(line2old)){
			writeTextToLCD(getLcd(), 2, 0, prepareLine(line2));
			line2old = line2;
		}

			// Refresh the LCD Display
		
		// Create and run thread to observe the LCD backlight. Will be automatically switched OFF after a few seconds
		checkAndStartDisplayObserver();
	}

	public void refreshMaxVelocity(VehicleService clientVehicleService) {
	
//		String clientMaxVelocity = clientVehicleService.getFormMaxVelocity();
		
		String clientMaxVelocity = PercentageService.getShortFormPercentage(clientVehicleService.getMaxVelocity(), 0, 32767);

			// Refresh the LCD Display
		writeTextToLCD(getLcd(), 3, 0, clientMaxVelocity);

		// Create and run thread to observe the LCD backlight. Will be automatically switched OFF after a few seconds
		checkAndStartDisplayObserver();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#switchLCDBackLight()
	 */
	public void switchLCDBackLight(){
		// most left button on lcd display
		try {
			if (lcd.isBacklightOn()){
				lcd.backlightOff();
				checkAndStopDisplayObserver();
			} else {
				// Create and run thread to observe the LCD backlight. Will be automatically switched OFF after a few seconds
				checkAndStartDisplayObserver();
			}
		} catch (TimeoutException e) {
			System.out.println("switchLCDBackLight:: TimeoutException BrickletLCD20x4 backlightOff/backlightOn");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#refreshMsgLcd(java.lang.String)
	 */
	public void refreshMsgLcd(String msg){
		// Prepare message in a string with length = 6
		String lcdMsg = StringService.create(6);
		lcdMsg = StringService.overwrite(lcdMsg, 0, msg);
		
		// Refresh the LCD Display
		writeTextToLCD(getLcd(), 3, 14, lcdMsg);

		
		// Create and run thread to observe the LCD backlight. Will be automatically switched OFF after a few seconds
		checkAndStartDisplayObserver();
	}
	
	public void reportIo16(){
		try {
//			System.out.println("RemoteControlServiceImpl:: reportIo16: Port 'a'="+rcService.getIO16().getPort('a'));
			System.out.println("RemoteControlServiceImpl:: reportIo16: PortConfiguration 'a'="+io16.getPortConfiguration('a'));
			System.out.println("RemoteControlServiceImpl:: reportIo16: PortInterrupt'a'="+io16.getPortInterrupt('a'));
			System.out.println("RemoteControlServiceImpl:: reportIo16: DebouncePeriod="+io16.getDebouncePeriod());
		} catch (TimeoutException e) {
			System.out.println("RemoteControlServiceImpl:: reportControlSwitch: TimeoutException occurred");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#checkAndStartDisplayObserver()
	 */
	public void checkAndStartDisplayObserver(){
		if (displayObserver != null){
			checkAndStopDisplayObserver();
		}
//		System.out.println("before new DisplayObserverThreadImpl");
		displayObserver = new DisplayObserverThreadImpl(getLcd(), BACKLIGHT_ON_DURATION);
//		System.out.println("after new DisplayObserverThreadImpl");
		
	}

	public void checkAndStopDisplayObserver(){
		if (displayObserver != null){
			// DisplayObser is alread alive. Stop it.
			displayObserver.deactivate();
		} 
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getIO4()
	 */
	public BrickletIO16 getIO16() {
		return io16;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getJoystick()
	 */
	public BrickletJoystick getJoystick() {
		return joystick;
	}

	public BrickletLCD20x4 getLcd() {
		return lcd;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getRotaryPoti()
	 */
	public BrickletRotaryPoti getRotaryPoti() {
		return rotaryPoti;
	}
	
	public boolean isSignalToWeak(short thresholdSignalStrength) {
		try {
			if (brickMaster.getChibiSignalStrength() <= thresholdSignalStrength){
				return true;
			} else {
				return false;
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public StackService getStackService() {
		return stackService;
	}

}
