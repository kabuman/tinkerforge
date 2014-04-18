package de.kabuman.tinkerforge.services.test;

import java.io.IOException;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.DisplayObserver;
import de.kabuman.tinkerforge.services.DisplayObserverThreadImpl;

public class DisplayObserverServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigService configService = null;
		BrickletLCD20x4 lcd;

		// ConfigService 
		try {
			configService = ConfigServiceImpl.getNewInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// LCD create & connect
		try {
			lcd = (BrickletLCD20x4) configService.createAndConnect(ConfigService.LCD201);
			try {
				lcd.writeLine((short)3,(short)0,"Hello World");
				lcd.backlightOn();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			@SuppressWarnings("unused")
			DisplayObserver displayObserver = new DisplayObserverThreadImpl(lcd, 5000);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
