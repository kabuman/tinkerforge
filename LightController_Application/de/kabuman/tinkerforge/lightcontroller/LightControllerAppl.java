package de.kabuman.tinkerforge.lightcontroller;

import java.io.IOException;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.tinkerforge.lightcontroller.services.LightControllerService;
import de.kabuman.tinkerforge.lightcontroller.services.LightControllerServiceImpl;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.EnsureMotorStop;
import de.kabuman.tinkerforge.services.HostService;
import de.kabuman.tinkerforge.services.HostServiceImpl;
import de.kabuman.tinkerforge.services.MotorAbstractService;
import de.kabuman.tinkerforge.services.RemoteControlService;
import de.kabuman.tinkerforge.services.RemoteControlServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.services.StopWatchMotorService;
import de.kabuman.tinkerforge.services.TransformerService;

/**
 * LightController Application
 * 
 * Contains the complete logic to operate the LightController
 */
public class LightControllerAppl {
	
	// Common Services
	RemoteControlService rcService;
	HostService hostService;
	StopWatchService stopWatchOFFduration = new StopWatchServiceImpl();
	StopWatchService stopWatchONduration = new StopWatchServiceImpl();

	// Application Specific Service
	LightControllerService lightControllerService;
	
	// Constants: Helper
	final short SHORT_ZERO = 0;

	// Constants: Switch assignment (position) on IO-Pin
	final short SWITCH1_REFRESH_PERIODICALLY = 0;
	final short SWITCH2_STROPOSKOP = 1;
	final short TASTER1_RESTART = 2;
	final short TASTER2_RESET = 3;
	final short TASTER3_KEEP = 4;
	final short TASTER4_TEST = 5;
	
	// Callback Periods
	final long CALLBACK_PERIOD_JOYSTICK = 100;
	final long CALLBACK_PERIOD_MAXVELOCITY = 100;
	final long CALLBACK_PERIOD_OFF = 0;

	// Rotary Poti: Fibrillation detecter
	short oldPos1 = 0;
	short oldPos2 = 0;
	short oldPos3 = 0;
	
	/**
	 * Launcher
	 * 
	 * - Instantiates and configures the needed services
	 * - Adds the Listener of events 
	 * 
	 * @return boolean - true: yes exception detected; false if not
	 */
	public Exception lightControllerLauncher(boolean restart){
		
		if (!StopWatchApplService.getInstance().isActive()){
			StopWatchApplService.getInstance().start();
		}
		
		if (restart){
            rcService.checkAndStopDisplayObserver();
		}
	
		try {
			configServices();
		} catch (IOException e1) {
			System.out.println("lightControllerLauncher:: IOException");
			return e1;
		} catch (TimeoutException e) {
			System.out.println("lightControllerLauncher:: TimeoutException");
			return e;
		}
		
		System.out.println("lightControllerLauncher:: Start");
		
		// Add Listener for the needed events
		controlMotorAndRudder();
		controlDisplay();
		controlSwitches();
		controlMaxVelocityLimiter();
		
		try {
			writeStartMsgToConsole();
			refreshDisplay();
			if (restart){
	            rcService.refreshMsgLcd("Rstart");
			}
		} catch (TimeoutException e) {
			System.out.println("lightControllerLauncher:: writeStartMsgToConsole(), refreshDisplay(): TimeoutException");
			return e;
		}
		
		// Install Listener to ensure motor stop if joystick pos is nearly zero
		new EnsureMotorStop((MotorAbstractService) lightControllerService.getMotorAndRudderService(), rcService.getJoystick());
		
		// Waiting for threads (the created listener) to finish
//		ConfigServiceImpl.getInstance().getConnect().joinThread();

		// destroy connect
		try {
			ConfigServiceImpl.getInstance().getConnect().disconnect();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Application Start Message
	 * @throws TimeoutException 
	 */
	private void writeStartMsgToConsole() throws TimeoutException{
		System.out.println("Anwendung \"LightController\" erfolgreich gestartet\n");
		ConfigServiceImpl.getInstance().report();
		writeStatusToConsole();
		writeRCFunctionsToConsole();
	}
	
	/**
	 * Writes the switch- and taster-functions upon console
	 */
	private void writeRCFunctionsToConsole(){
		System.out.println("\nSchalter1: Anzeige-Refresh EIN/AUS");
		System.out.println("Taster1: Restart");
		System.out.println("Taster2: Reset");
		System.out.println("Taster3: Joystick(Motor & Ruder)-Einstellung und Limit-Einstellung feststellen (programmieren)");
		System.out.println("Taster4: Testprogramm für Motor & Ruder");
	}
	
	/**
	 * Writes the status to console
	 * @throws TimeoutException 
	 */
	private void writeStatusToConsole() throws TimeoutException{
		// Deactivate the most powerful listener temporarily
//		deactivateAllCallbackListener();
		
		hostService.getStackService().writeStatusToConsole();
		rcService.getStackService().writeStatusToConsole();
		lightControllerService.getStackService().writeStatusToConsole();
		
		System.out.println("\nLaufzeiten (bisher)");
		System.out.println("Anwendung: "+StopWatchApplService.getInstance().getCurrentString());
		System.out.println("Motor:     "+StopWatchMotorService.getInstance().getCurrentString());
		
		// Activate the listener again
//		activateAllCallbackListener();
	}
		
	/**
	 * Configuration of services
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void configServices() throws IOException, TimeoutException {
		// Instantiate the Singleton "ConfigServiceImpl"
		ConfigServiceImpl.getNewInstance();
		
		// Services
		rcService = new RemoteControlServiceImpl();
		hostService = new HostServiceImpl();
		
		// Application Specific Services
		lightControllerService = new LightControllerServiceImpl();
	}

	/**
	 * Switch Control (2 switches + 2 taster)
	 */
	private void controlSwitches(){
//		System.out.println("controlSwitches");
		
		rcService.getIO16().addInterruptListener(new BrickletIO16.InterruptListener() {
			public void interrupt(char port, short interruptMask, short valueMask) {
                System.out.println("Interrupt on port: " + port);
                System.out.println("Interrupt by: " + Integer.toBinaryString(interruptMask));
                System.out.println("Value: " + Integer.toBinaryString(valueMask));

	            if (isInterruptedBy(SWITCH1_REFRESH_PERIODICALLY,interruptMask )){
	            	if (isSwitchedON(SWITCH1_REFRESH_PERIODICALLY,valueMask )){
	                    System.out.println("\n=> Schalter1: Anzeige-Refresh AUS");
						rcService.refreshObserverStop();
	            	} else {
	                    System.out.println("\n=> Schalter1: Anzeige-Refresh EIN");
	                    rcService.refreshObserverStart(
	                    		hostService, 
	                    		lightControllerService.getVehicleService(),
	                    		lightControllerService.getStackService());
	            	}
	            }
	            if (isInterruptedBy(SWITCH2_STROPOSKOP,interruptMask )){
	            	if (isSwitchedON(SWITCH2_STROPOSKOP,valueMask )){
	                    System.out.println("\n=> Schalter2: Stroposkop-Licht AUS");
	            	} else {
	                    System.out.println("\n=> Schalter2: Stroposkop-Licht EIN");
	                    stroposkop();
	            	}
	            }
	            if (isInterruptedBy(TASTER1_RESTART,interruptMask )){
	            	if (!isSwitchedON(TASTER1_RESTART,valueMask )){
	                    System.out.println("\n=> Taster1: Anwendung neu gestartet");
	                    lightControllerLauncher(true);
	            	}
	            }
	            if (isInterruptedBy(TASTER2_RESET,interruptMask )){
	            	if (!isSwitchedON(TASTER2_RESET,valueMask )){
	                    System.out.println("\n=> Taster2: Motor gestoppt und Joystick kalibriert");
	                    rcService.refreshMsgLcd("Reset");
	                    lightControllerService.getMotorAndRudderService().calculateAndSet(SHORT_ZERO,SHORT_ZERO);
	                    StopWatchMotorService.getInstance().stopOver();
//						rcService.getJoystick().calibrate();
						activateAllCallbackListener();
	            	}
	            }
	            if (isInterruptedBy(TASTER3_KEEP,interruptMask )){
	            	if (!isSwitchedON(TASTER3_KEEP,valueMask )){
	                    System.out.println("\n=> Taster3: Joystick- und Limit-Poti-Position werden gehalten. Zurücksetzen mit Taster2.");
	                    rcService.refreshMsgLcd("Keep");
	                    deactivateAllCallbackListener();
	            	}
	            }
	            if (isInterruptedBy(TASTER4_TEST,interruptMask )){
	            	if (!isSwitchedON(TASTER4_TEST,valueMask )){
	                    System.out.println("\n=> Taster4: Testprogramm gestartet...");
	                    rcService.refreshMsgLcd("Test");
	                    motorAndRudderTest();
	                    System.out.println("\n=> Taster4: Testprogramm beendet.");
	            	}
	            }

			}
		});
	}

	/**
	 *  Motor & Rudder Control
	 *  
	 *  Motor and Rudder will be controlled in tenner oriented steps.
	 *  Due to -100,0,+100 possible joystick position their will be 10 steps per direction available
	 *  This will be reduce the data transfer within the network significantly.
	 */
	private void controlMotorAndRudder(){
		try {
			rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_JOYSTICK);
			rcService.getJoystick().calibrate();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		rcService.getJoystick().addPositionListener(new BrickletJoystick.PositionListener() {
			public void position(short x, short y) {
//				System.out.println("x/y="+x+"/"+y);
				
				// round down to the next tenner value
				short xTenClustered = getTenClusteredPosition(x);
				short yTenClustered = getTenClusteredPosition(y);
				
				// Maintain the stop watch for the motor
				if (yTenClustered == 0){
					// velocity is zero
					if (StopWatchMotorService.getInstance().isActive()){
						StopWatchMotorService.getInstance().stopOver();
					}
				} else {
					// velocity is NOT zero
					if (StopWatchMotorService.getInstance().isActive() == false){
						StopWatchMotorService.getInstance().start();
					}
				}
				
				// Set the values for Motor and Rudder
				lightControllerService.getMotorAndRudderService().calculateAndSet(xTenClustered,yTenClustered);
			}
		});
	}
	
	/**
	 * LCD Display Control 
	 */
	private void controlDisplay(){
		// Add and implement listener for pressed and released events
		rcService.getLcd().addButtonPressedListener(new BrickletLCD20x4.ButtonPressedListener() {
			public void buttonPressed(short button) {
				switch (button) {
				case 0:
					rcService.switchLCDBackLight();
					break;
				case 1:
					// middle button on lcd display
					try {
							refreshDisplay();
						} catch (TimeoutException e) {
							System.out.println("controlDisplay:: refreshDisplay(): TimeoutException");
							lightControllerLauncher(true);
						}
					break;
					
				case 2:
					// most right button on lcd display
					try {
							writeStatusToConsole();
						} catch (TimeoutException e) {
							System.out.println("controlDisplay:: writeStatusToConsole(): TimeoutException");
							lightControllerLauncher(true);
						}
					break;
					
				default:
					break;
				}
			}
		});
	}

	/**
	 * Maximum Velocity Limiter Control
	 * Limits the max velocity choosable by joystick
	 * The limit is set via rotary potentiometer
	 */
	private void controlMaxVelocityLimiter(){
		
		// Set max velocity according the current position of the rotary potentiometer
		lightControllerService.getVehicleService().setMaxVelocity(getMaxVelocityFromRotaryPoti());
		lightControllerService.getMotorAndRudderService().setVelocityMax(lightControllerService.getVehicleService().getMaxVelocity());
	
		try {
			rcService.getRotaryPoti().setPositionCallbackPeriod(CALLBACK_PERIOD_MAXVELOCITY);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Add and implement position listener (called if position changes)
		rcService.getRotaryPoti().addPositionListener(new BrickletRotaryPoti.PositionListener() {
			public void position(short position) {
				if (oldPos1 == oldPos3 && oldPos2 == position){
					// fibrillation detected:
//					System.out.println("=> Erkannt: Limit-Regler flimmert. Unterdrückt.");
					
				} else {
					
					// do the job
					lightControllerService.getVehicleService().setMaxVelocity(TransformerService.rotaryDegreeToVelocity(position));
					lightControllerService.getMotorAndRudderService().setVelocityMax(lightControllerService.getVehicleService().getMaxVelocity());
						rcService.refreshMaxVelocity(lightControllerService.getVehicleService());
				}
				
				// Maintain Fillibration Detecter
				oldPos1 = oldPos2;
				oldPos2 = oldPos3;
				oldPos3 = position;
				
			}
		});
	}
	
	/**
	 * Determines new values and refreshes the LCD Display content 
	 * @throws TimeoutException 
	 */
	private void refreshDisplay() throws TimeoutException{
		// Deactivate the most powerful listener temporarily
//		deactivateAllCallbackListener();
		
		    // refresh lcd content
			rcService.refreshAllLcd(
					hostService, 
					lightControllerService.getVehicleService(),
					lightControllerService.getStackService());
	
		// Activate the listener again
//		activateAllCallbackListener();
	}

	/**
	 * Executes a test for the given parameter and the given time
	 * 
	 * @param position - position of the rudder servo
	 * @param velocity - the velocity of the motor
	 * @param duration - the duration of the test
	 * @param stopAfterTest - true: stop motor after test / false: no motor stop after test
	 */
	private void executeTest(int position, int velocity, long duration, boolean stopAfterTest){
		StopWatchApplService.getInstance().start();
		lightControllerService.getMotorAndRudderService().calculateAndSet(position,velocity);
		while (StopWatchApplService.getInstance().getCurrent()<duration) {
		}
		if (stopAfterTest){
			lightControllerService.getMotorAndRudderService().calculateAndSet(SHORT_ZERO,SHORT_ZERO);
		}
	}

	/**
	 * Controls a test for Motor and Rudder
	 */
	private void motorAndRudderTest(){
		deactivateAllCallbackListener();
		
		// Set Max Velocity to half
		lightControllerService.getVehicleService().setMaxVelocity(TransformerService.rotaryDegreeToVelocity((short)0));
		lightControllerService.getMotorAndRudderService().setVelocityMax(lightControllerService.getVehicleService().getMaxVelocity());

		for (int i = -100; i < 110; i=i+10) {
			executeTest(i,i,1000,false);
		}
		lightControllerService.getMotorAndRudderService().calculateAndSet(SHORT_ZERO,SHORT_ZERO);

		
		// Set Max Velocity according Rotary Poti
		lightControllerService.getVehicleService().setMaxVelocity(getMaxVelocityFromRotaryPoti());
		lightControllerService.getMotorAndRudderService().setVelocityMax(lightControllerService.getVehicleService().getMaxVelocity());
		
		activateAllCallbackListener();
	}
	
	/**
	 * Controls a test for Motor and Rudder
	 */
	private void stroposkop(){
		deactivateAllCallbackListener();
		
		short maxVelocity = getMaxVelocityFromRotaryPoti();
		System.out.println("stroposkop:: maxVelocity="+maxVelocity);
		
		for (int i = 0; i < 500; i++) {
			System.out.println("stroposkop:: loop="+i);
			executeTest(0,maxVelocity,10,true);

			stopWatchOFFduration.restart();
			stopWatchOFFduration.start();
			while (stopWatchOFFduration.getCurrent()< 50) {
			}
		}
		
		activateAllCallbackListener();
	}
	
	/**
	 * Activates all call back listener
	 */
	private void activateAllCallbackListener(){
		try {
			rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_JOYSTICK);
			rcService.getRotaryPoti().setPositionCallbackPeriod(CALLBACK_PERIOD_MAXVELOCITY);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Deactivates all call back listener
	 */
	private void deactivateAllCallbackListener(){
		try {
			rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_OFF);
			rcService.getRotaryPoti().setPositionCallbackPeriod(CALLBACK_PERIOD_OFF);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns
	 * true, if the given switch is the trigger of these interrupt
	 * false if no.
	 * 
	 * So this method makes a bit check.
	 * 
	 * @param switchPos - the position of the given switch which is to test
	 * @param interruptMask - the given interrupMask given by IO4-Device
	 * @return boolean - true: yes the given switchPos is the reason for the interrupt
	 */
	boolean isInterruptedBy( int switchPos, int interruptMask )
	{
		// Create a mask (based on the given switchPos) for comparison with the interrupt mask
	  int mask = 1 << switchPos;
	
	  // return true if interrupt mask and the created mask (based on the given switchPos) is equal
	  return (interruptMask & mask) == mask; 
	}

	/**
	 * Returns
	 * true, if the given switch is switched on
	 * false if not.
	 * 
	 * So this method makes a bit check.
	 * 
	 * @param switchPos - the position of the given switch which is to test
	 * @param valueMask - the given valueMask given by IO4-Device (contains the states of each switch)
	 * @return boolean - true: yes the given switchPos is switched on
	 */
	boolean isSwitchedON( int switchPos, int valueMask )
	{
		// Create a mask (based on the given switchPos) for comparison with the interrupt mask
	  int mask = 1 << switchPos;
	
	  // return true if value mask and the created mask (based on the given switchPos) is equal
	  return (valueMask & mask) == mask;
	}

	/**
	 * Returns the max possible velocity currently set by Rotary Potentiometer
	 *
	 * Read out the position of the Rotary Potentiometer
	 * Transforms it to maxVelocity
	 * Store it into lightControllerService
	 * returns it
	 * 
	 * @return short - max. velocity
	 */
	private short getMaxVelocityFromRotaryPoti(){
		short maxVelocity = 0;
		try {
			maxVelocity = TransformerService.rotaryDegreeToVelocity(rcService.getRotaryPoti().getPosition());
			lightControllerService.getVehicleService().setMaxVelocity(maxVelocity);
	
		} catch (TimeoutException e) {
	        System.out.println("getMaxVelocity:: TimeoutException rcService.getRotaryPoti().getPosition()");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxVelocity;
	}

	/**
	 * round down to the next tenner value
	 * Examples:
	 * 9->0
	 * 10->10
	 * 11->10
	 * 19->10
	 * 20->20
	 * 
	 * @param pos - the value which is to round down
	 * @return short - rounded down to the next tenner value
	 */
	private short getTenClusteredPosition(short pos){
		short result =  (short) ((pos/ 10) * 10);
		return result;
	}
	
}
