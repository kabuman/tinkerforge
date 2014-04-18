package de.kabuman.tinkerforge.strobecontroller;

import java.io.IOException;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.strobecontroller.services.StrobeControllerService;
import de.kabuman.tinkerforge.strobecontroller.services.StrobeControllerServiceImpl;

/**
 * LightController Application
 * 
 * Contains the complete logic to operate the LightController
 */
public class StrobeControllerAppl {
	
	// Common Services
	StopWatchService stopWatchOFFduration = new StopWatchServiceImpl();
	StopWatchService stopWatchONduration = new StopWatchServiceImpl();

	// Application Specific Service
	StrobeControllerService strobeControllerService;
	
	// Constants: Helper
	final short FROM_POS_OFF = -150;
	final short TO_POS_OFF = -144;
	
	final short FROM_POS_ON = -145;
	final short TO_POS_ON = -116;
	

	/**
	 * Launcher
	 * 
	 * - Instantiates and configures the needed services
	 * - Adds the Listener of events 
	 * 
	 * @return boolean - true: yes exception detected; false if not
	 */
	public Exception strobeControllerLauncher(boolean restart){
		
		if (!StopWatchApplService.getInstance().isActive()){
			StopWatchApplService.getInstance().start();
		}
		
	
		try {
			configServices();
		} catch (IOException e1) {
			System.out.println("strobeControllerLauncher:: IOException");
			return e1;
		} catch (TimeoutException e) {
			System.out.println("strobeControllerLauncher:: TimeoutException");
			return e;
		}
		
		System.out.println("strobeControllerLauncher:: Start");
		
		// Start STROBE Mode
		strobe();
		
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
	 * Configuration of services
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void configServices() throws IOException, TimeoutException {
		// Instantiate the Singleton "ConfigServiceImpl"
		ConfigServiceImpl.getNewInstance();
		
		// Application Specific Services
		strobeControllerService = new StrobeControllerServiceImpl();
	}


	/**
	 * Creates a Strobe sequence (ON time + OFF time)
	 * 
	 * @param velocity - the velocity to set
	 * @param ONduration - the on time duration
	 * @param OFFduration - the off time duration
	 */
	private void strobeSequence(short velocity, long ONduration, long OFFduration){
		// Light ON
		stopWatchONduration.restart();
		stopWatchONduration.start();
		strobeControllerService.getMotorAndRudderService().setVelocity(velocity);
		while (stopWatchONduration.getCurrent()<ONduration) {
		}

		// Light OFF
		strobeControllerService.getMotorAndRudderService().setVelocity((short)0);
		stopWatchOFFduration.restart();
		stopWatchOFFduration.start();
		while (stopWatchOFFduration.getCurrent()< OFFduration) {
		}
	}

	/**
	 * Returns true if rotary poti position is in ON position
	 * 
	 * @param position - the given position of rotary poti
	 * @return boolean - true: ON / false: not in ON position
	 */
	private boolean isPosON(short position){
		if (position < FROM_POS_ON || position > TO_POS_ON){
			return false;
		} else {
			System.out.println("isPosON:: on at position="+position);
			return true;
		}
	}
	
	/**
	 * Returns true if rotary poti position is in OFF position
	 * 
	 * @param position - the given rotary poti
	 * @return boolean - true: OFF /false not in OFF position
	 */
	private boolean isPosOFF(short position){
		if (position < FROM_POS_OFF || position > TO_POS_OFF){
			return false;
		} else {
			System.out.println("isPosFF:: off at position="+position);
			return true;
		}
	}
	
	/**
	 * Strobe Controller
	 */
	private void strobe(){
		// maximum velocity is detected in constructor of MotorAndRudderService
		short maxVelocity = strobeControllerService.getMotorAndRudderService().getLEDVelocity();
		System.out.println("strobe:: maxVelocity="+maxVelocity);
		
		short OFFduration = strobeControllerService.getMotorAndRudderService().getFrequency();
		short ONduration = 1;

		System.out.println("strobe:: ONduration="+ONduration);
		System.out.println("strobe:: OFFduration="+OFFduration);
		
		
		short position;
		
		boolean process = true;
		while (process) {
			// Detect the position of rotary poti
			position = strobeControllerService.getMotorAndRudderService().getPosition();
			
			// Position depending actions:
			if (isPosOFF(position)){
				// off completely
				strobeControllerService.getMotorAndRudderService().setVelocity((short)0);
				while (isPosOFF(strobeControllerService.getMotorAndRudderService().getPosition())) {
				}
			} else if (isPosON(position)) {
				// on completely
				strobeControllerService.getMotorAndRudderService().setVelocity(maxVelocity);
				while (isPosON(strobeControllerService.getMotorAndRudderService().getPosition())) {
				}
				
			} else {
				// Strobe sequence (ON + OFF)
				strobeSequence(maxVelocity,ONduration, OFFduration);
			}
		}
	}
}
