package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class RefreshObserverImpl extends Thread implements RefreshObserver {

	// duration in milli-sec before switch-off
	long duration;
	
	boolean active = true;

	BrickletLCD20x4 lcd = null;
	
	VehicleService clientVehicleService = null;
	
	HostService hostService = null;
	
	StackService clientStackService = null;
	
	RemoteControlService rcService = null;

	/**
	 * Constructor
	 * 
	 * @param lcd -
	 *            the instantiated lcd device object (used to switch off the
	 *            backlight)
	 */
	public RefreshObserverImpl(BrickletLCD20x4 lcd, long duration, HostService hostService,
			VehicleService clientVehicleService,
			StackService clientStackService, RemoteControlService rcService) {
		this.lcd = lcd;
		this.duration = duration;
		this.clientVehicleService = clientVehicleService;
		this.hostService = hostService;
		this.clientStackService = clientStackService;
		this.rcService = rcService;

		start();
	}

	/**
	 * Stops the thread and set alive flag to false
	 */
	public void deactivate() {
		try {
			lcd.backlightOff();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		active = false;
		this.interrupt();
	}

	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		do {
			// Sleep a few seconds and then refresh messages
			try {
				if (active){
					rcService.refreshAllLcd(hostService, clientVehicleService, clientStackService);
				} 
				Thread.sleep(duration);
			} catch (InterruptedException e) {
//				System.out.println("RefreshObserver:: run(): InterruptedException thrown = "+e);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (active);
	}
}
