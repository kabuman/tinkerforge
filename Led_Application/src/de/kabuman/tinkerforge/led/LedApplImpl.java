package de.kabuman.tinkerforge.led;

import java.io.IOException;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.DeviceIdentifier;
import de.kabuman.tinkerforge.services.StopWatchApplService;

public class LedApplImpl implements LedAppl{

	// Devices
//	private BrickMaster alarmMaster;
	private BrickletIO16 ledProcessor;
	
	IPConnection ipcon;

	/**
	 * Launcher
	 * 
	 * - Instantiates and configures the needed services
	 * - Adds the Listener of events 
	 * 
	 * @return boolean - true: yes exception detected; false if not
	 */
	public Exception launcher(boolean restart){
		
		if (!StopWatchApplService.getInstance().isActive()){
			StopWatchApplService.getInstance().start();
		}
		
		if (restart){
		}
	
		try {
			ConnectServiceImpl.getNewInstance();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ipcon = ConnectServiceImpl.getInstance().createConnect("localhost", 4223);

		try {
			ledProcessor = (BrickletIO16) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.IO162, "Led Processor");
		} catch (Exception e) {
			return e;
		}

		
		LedLabor ledLabor = new LedLabor(ledProcessor);
		
		try {
			writeStartMsgToConsole();
		} catch (TimeoutException e) {
			System.out.println("launcher:: writeStartMsgToConsole(): TimeoutException");
			return e;
		}
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Application Start Message
	 * @throws TimeoutException 
	 */
	private void writeStartMsgToConsole() throws TimeoutException{
		ConnectServiceImpl.getInstance().report();
	}

	

}
