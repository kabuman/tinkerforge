package de.kabuman.tinkerforge.rp6;

import java.io.IOException;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.rp6.services.MotorControlByJoystickService;
import de.kabuman.tinkerforge.rp6.services.MotorControlByJoystickServiceImpl;
import de.kabuman.tinkerforge.rp6.services.RP6Service;
import de.kabuman.tinkerforge.rp6.services.RP6ServiceImpl;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.EnsureMotorStop;
import de.kabuman.tinkerforge.services.HostServiceImpl;
import de.kabuman.tinkerforge.services.MotorAbstractService;
import de.kabuman.tinkerforge.services.RemoteControlServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.services.StopWatchMotorService;
import de.kabuman.tinkerforge.services.TransformerService;

/**
 * RP6 Application
 * 
 * Contains the complete logic to operate the RP6 vehicle
 */
public class RP6Appl {

	// Common Services
	RemoteControlServiceImpl rcService;
	HostServiceImpl hostService;

	// Application Specific Services
	RP6Service rp6Service;
	MotorControlByJoystickService motorControlByJoystickService;
	
	// Operation Mode 
	int mode = 0;
	
	// Constants: Helper
	final short SHORT_ZERO = 0;

	// Constants: Switch assignment (position) on IO-Pin
	final short SWITCH1_REFRESH_PERIODICALLY = 0;
	final short SWITCH_RESERVE = 1;
	final short TASTER1_RESTART = 2;
	final short TASTER2_RESET = 3;
	final short TASTER3_SOUND1 = 4;
	final short TASTER4_SOUND2 = 5;
	
	final long CALLBACK_PERIOD_OFF = 0;
	final long CALLBACK_PERIOD_JOYSTICK = 100;
	final long CALLBACK_PERIOD_MAXVELOCITY = 500;
	
	// Rotary Poti: Fibrillation detector
	short oldPos1 = 0;
	short oldPos2 = 0;
	short oldPos3 = 0;
	
	/**
	 * Launcher
	 * @throws TimeoutException 
	 */
	public Exception rp6Launcher(boolean restart) {
	
		if (!StopWatchApplService.getInstance().isActive()){
			StopWatchApplService.getInstance().start();
		}
		
		if (restart){
            rcService.checkAndStopDisplayObserver();
            rcService.refreshObserverStop();
		}
	

		try {
			configServices();
		} catch (IOException e1) {
			System.out.println("rp6Launcher:: configService: IOException");
			return e1;
		} catch (TimeoutException e) {
			System.out.println("rp6Launcher:: configService: TimeoutException");
			return e;
		}
		
		configRP6();
		System.out.println("RP6Launcher:: Start");

		// Add Listener for the needed events
		controlMode();
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
			System.out.println("rp6Launcher:: writeStartMsgToConsole(), refreshDisplay(): TimeoutException");
			return e;
		}
		
		// Install Listener to ensure motor stop if joystick pos is nearly zero
		new EnsureMotorStop((MotorAbstractService) motorControlByJoystickService, rcService.getJoystick());
		
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
		System.out.println("Anwendung \"Raupi\" erfolgreich gestartet\n");
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
		System.out.println("Taster3: Sound-1");
		System.out.println("Taster4: Sound-2");
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
		rp6Service.getStackService().writeStatusToConsole();
		
		System.out.println("\nLaufzeiten (bisher)");
		System.out.println("Anwendung: "+StopWatchApplService.getInstance().getCurrentString());
		System.out.println("Motor:     "+StopWatchMotorService.getInstance().getCurrentString());
		
		// Activate the listener again
//		activateAllCallbackListener();
	}
		

	/**
	 * Configuration for services and the connection
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
		rp6Service = new RP6ServiceImpl();
		motorControlByJoystickService = new MotorControlByJoystickServiceImpl(rp6Service.getMotorLeft(), rp6Service.getMotorRight());
	}

	/**
	 * Configuration of RP6
	 */
	private void configRP6(){
		// drive mode
		mode = MotorControlByJoystickService.MODE_DRIVE;
		
		// Rotary Poti: Fibrillation detector
		oldPos1 = 0;
		oldPos2 = 0;
		oldPos3 = 0;
	}
	
	private void setSoundTrigger(BrickletDualRelay soundTrigger, boolean b1, boolean b2){
		try {
			soundTrigger.setState(b1, b2);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Switch Control (2 switches + 2 taster)
	 */
	private void controlSwitches(){
//		System.out.println("controlSwitches");

		rcService.getIO16().addInterruptListener(new BrickletIO16.InterruptListener() {
			public void interrupt(char port, short interruptMask, short valueMask) {
//              System.out.println("Interrupt on port: " + port);
//              System.out.println("Interrupt by: " + Integer.toBinaryString(interruptMask));
//              System.out.println("Value: " + Integer.toBinaryString(valueMask));

	            if (isInterruptedBy(SWITCH1_REFRESH_PERIODICALLY,interruptMask )){
	            	if (isSwitchedON(SWITCH1_REFRESH_PERIODICALLY,valueMask )){
	                    System.out.println("\n=> Schalter1: Anzeige-Refresh AUS");
						rcService.refreshObserverStop();
	            	} else {
	                    System.out.println("\n=> Schalter1: Anzeige-Refresh EIN");
	                    rcService.refreshObserverStart(
	                    		hostService, 
	                    		rp6Service.getVehicleService(),
	                    		rp6Service.getStackService());
	            	}
	            }
	            if (isInterruptedBy(TASTER1_RESTART,interruptMask )){
	            	if (!isSwitchedON(TASTER1_RESTART,valueMask )){
	                    System.out.println("\n=> Taster1: Anwendung neu gestartet");
	                    rp6Launcher(true);
	            	}
	            }
	            if (isInterruptedBy(TASTER2_RESET,interruptMask )){
	            	if (!isSwitchedON(TASTER2_RESET,valueMask )){
	                    System.out.println("\n=> Taster2: Motor gestoppt und Joystick kalibriert");
	                    rcService.refreshMsgLcd("Reset");
	                    try {
							rp6Service.getMotorLeft().setVelocity(SHORT_ZERO);
						} catch (TimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotConnectedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    try {
							rp6Service.getMotorRight().setVelocity(SHORT_ZERO);
						} catch (TimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotConnectedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    StopWatchMotorService.getInstance().stopOver();
//						activateAllCallbackListener();
	            	}
	            }
	            if (isInterruptedBy(TASTER3_SOUND1,interruptMask )){
	            	if (isSwitchedON(TASTER3_SOUND1,valueMask )){
	                    System.out.println("Sound1 is switched OFF");
	                    setSoundTrigger(rp6Service.getSoundTrigger(),false,false);
	            	} else {
	                    System.out.println("Sound1 is switched ON");
	                    setSoundTrigger(rp6Service.getSoundTrigger(),true,false);
	            	}
	            }
	            if (isInterruptedBy(TASTER4_SOUND2,interruptMask )){
	            	if (isSwitchedON(TASTER4_SOUND2,valueMask )){
	                    System.out.println("Sound2 is switched OFF");
	                    setSoundTrigger(rp6Service.getSoundTrigger(),false,false);
	            	} else {
	                    System.out.println("Sound2 is switched ON");
	                    setSoundTrigger(rp6Service.getSoundTrigger(),false,true);
	            	}
	            }
	            if (isInterruptedBy(SWITCH_RESERVE,interruptMask )){
	            	boolean stateLight = false;
	            	try {
	            		stateLight = rp6Service.getCommonTrigger().getState().relay1;
					} catch (TimeoutException e) {
			            System.out.println("controlSwitches:: TimeoutException SWITCH_RESERVE backlightOff/backlightOn");
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	if (isSwitchedON(SWITCH_RESERVE,valueMask )){
	                    System.out.println("Reserve is switched OFF");
	                    setCommonTriggerState(rp6Service.getCommonTrigger(),stateLight, false);
	            	} else {
	                    System.out.println("Reserve is switched ON");
	                    setCommonTriggerState(rp6Service.getCommonTrigger(),stateLight, true);
	            	}
	            }
			}
		});
	}

	private void setCommonTriggerState(BrickletDualRelay commonTrigger, boolean stateLight, boolean b1){
		try {
			commonTrigger.setState(stateLight, b1);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *  Mode control
	 */
	private void controlMode(){
		// Activate the normal drive mode as default
		controlDriveMode();
		
		// Add and implement listener for pressed and released events
		rcService.getJoystick().addPressedListener(new BrickletJoystick.PressedListener() {
			public void pressed() {
				System.out.println("Joystick pressed");
				switch (mode) {
				case MotorControlByJoystickService.MODE_DRIVE:
					mode = MotorControlByJoystickService.MODE_ROTATE;
					controlRotateMode();
					break;
				case MotorControlByJoystickService.MODE_ROTATE:
					mode = MotorControlByJoystickService.MODE_DRIVE;
					controlDriveMode();
					break;
				default:
					break;
				}
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
				System.out.println("Pressed: " + button);
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
							rp6Launcher(true);
						}
					break;
					
				case 2:
					// most right button on lcd display
					try {
							writeStatusToConsole();
						} catch (TimeoutException e) {
							System.out.println("controlDisplay:: refreshDisplay(): TimeoutException");
							rp6Launcher(true);
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
		
		// Set max velocity according the position of the rotary potentiometer
		rp6Service.getVehicleService().setMaxVelocity(getMaxVelocity());
		motorControlByJoystickService.setVelocityMax(rp6Service.getVehicleService().getMaxVelocity());
	
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
					rp6Service.getVehicleService().setMaxVelocity(TransformerService.rotaryDegreeToVelocity(position));
					motorControlByJoystickService.setVelocityMax(rp6Service.getVehicleService().getMaxVelocity());
					rcService.refreshMaxVelocity(rp6Service.getVehicleService());
				}
				
				// Maintain Fillibration Detector
				oldPos1 = oldPos2;
				oldPos2 = oldPos3;
				oldPos3 = position;

			}
		});
	
	}

	/**
	 *  Drive Mode Control
	 */
	private void controlDriveMode(){
		System.out.println("Drive mode activated");
		try {
			rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_JOYSTICK);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rcService.getJoystick().addPositionListener(new BrickletJoystick.PositionListener() {
			public void position(short x, short y) {
				
				// Maintain the stop watch for the motor
				if (x == 0){
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

				motorControlByJoystickService.calculateAndSetVelocity(MotorControlByJoystickService.MODE_DRIVE, x,y);
			}
		});
	}

	/**
	 *  Rotate Mode Control
	 */
	private void controlRotateMode(){
		System.out.println("Rotate mode activated");
//		rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_JOYSTICK);
	
		rcService.getJoystick().addPositionListener(new BrickletJoystick.PositionListener() {
			public void position(short x, short y) {
				motorControlByJoystickService.calculateAndSetVelocity(MotorControlByJoystickService.MODE_ROTATE, x,y);
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
					rp6Service.getVehicleService(),
					rp6Service.getStackService());
	
		// Activate the listener again
//		activateAllCallbackListener();
	}
	

//	/**
//	 * Activates all call back listener
//	 */
//	private void activateAllCallbackListener(){
//		rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_JOYSTICK);
//		rcService.getRotaryPoti().setPositionCallbackPeriod(CALLBACK_PERIOD_MAXVELOCITY);
//		rcService.getIO16().setPortInterrupt('a', RemoteControlServiceImpl.PINMASK_ALL);
////		rcService.getIO16().setDebouncePeriod(100);
//
//	}
//
//	/**
//	 * Deactivates all call back listener
//	 */
//	private void deactivateAllCallbackListener(){
//		rcService.getJoystick().setPositionCallbackPeriod(CALLBACK_PERIOD_OFF);
//		rcService.getRotaryPoti().setPositionCallbackPeriod(CALLBACK_PERIOD_OFF);
//		rcService.getIO16().setPortInterrupt('a', RemoteControlServiceImpl.PINMASK_OFF);
////		rcService.getIO16().setDebouncePeriod((long)100000);
//	}
	
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
	 * Store it into legoService
	 * returns it
	 * 
	 * @return short - max. velocity
	 */
	private short getMaxVelocity(){
		short maxVelocity = 0;
		try {
			maxVelocity = TransformerService.rotaryDegreeToVelocity(rcService.getRotaryPoti().getPosition());
			rp6Service.getVehicleService().setMaxVelocity(maxVelocity);
	
		} catch (TimeoutException e) {
	        System.out.println("getMaxVelocity:: TimeoutException rcService.getRotaryPoti().getPosition()");
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxVelocity;
	}


}
