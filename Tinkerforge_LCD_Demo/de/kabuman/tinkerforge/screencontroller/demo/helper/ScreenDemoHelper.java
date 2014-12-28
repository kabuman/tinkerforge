package de.kabuman.tinkerforge.screencontroller.demo.helper;

import java.io.IOException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConnectServiceImpl;

public class ScreenDemoHelper {
	
	public static BrickletLCD20x4 createAndConnectLCD2(String host, int port){
		IPConnection ipcon;

		// Devices
		BrickletLCD20x4 lcd;
		

		// Connection & Bricklet initialization
		try {
			ipcon = ConnectServiceImpl.getNewInstance().createConnectE(host,port);
			ipcon.setTimeout(20000);
			ipcon.setAutoReconnect(true);
			lcd = new BrickletLCD20x4("o6e",ipcon);
			lcd.clearDisplay();
			lcd.backlightOn();
		} catch (AlreadyConnectedException | IOException | TimeoutException | NotConnectedException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		return lcd;
		
	}
	
	public static void sleep(int sleepTime){
		try {
			Thread.sleep((long)sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeMsgFirstlyConnected(){
		System.out.println("=> Stack firstly connected <=");
	}

	public static void writeMsgReconnected(){
		System.out.println("=> Stack reconnected <=");
	}

	public static void writeMsgDisconnected(){
		System.out.println("=> Stack disconnected <=");
	}


	
	public static void writeMsgCloneFirstlyConnected(int cloneId){
		System.out.println("=> Clone Stack firstly connected <=  id="+cloneId);
	}

	public static void writeMsgCloneReconnected(int cloneId){
		System.out.println("=> Clone Stack reconnected <=  id="+cloneId);
	}

	public static void writeMsgCloneDisconnected(int cloneId){
		System.out.println("=> Clone Stack disconnected <=  id="+cloneId);
	}


}
