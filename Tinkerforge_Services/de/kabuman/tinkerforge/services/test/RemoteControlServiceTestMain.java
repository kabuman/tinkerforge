package de.kabuman.tinkerforge.services.test;

import java.io.IOException;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConnectDeviceServiceImpl;
import de.kabuman.tinkerforge.services.ConnectService;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.HostServiceImpl;
import de.kabuman.tinkerforge.services.RemoteControlService;
import de.kabuman.tinkerforge.services.RemoteControlServiceImpl;

public class RemoteControlServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConnectServiceImpl.getNewInstance();
			RemoteControlService rcService = new RemoteControlServiceImpl();
			ConnectServiceImpl.getInstance().report();
			
			rcService.refreshMsgLcd("Start");
			try {
				rcService.getLcd().backlightOn();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
		

		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}

}
