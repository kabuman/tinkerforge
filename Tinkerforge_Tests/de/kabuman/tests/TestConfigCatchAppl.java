package de.kabuman.tests;

import java.io.IOException;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;

public class TestConfigCatchAppl {
	ConfigService configService;

	IPConnection ipCon;

	BrickMaster bm1;

	BrickletLCD20x4 lcd;

	private void config() {
		System.out.println("config:: starting");
		
		System.out.println("config:: ConfigServiceImpl.getNewInstance()");
		try {
			configService = ConfigServiceImpl.getNewInstance();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		System.out.println("config:: configService="+configService);
		
		System.out.println("config:: configService.getConnect()");
		ipCon = configService.getConnect();
		System.out.println("config:: ipCon="+ipCon);

		System.out.println("config:: configService.createAndConnect(..)");
		try {
			bm1 = (BrickMaster) configService.createAndConnect(ConfigService.MB1);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		System.out.println("config:: bm1="+bm1);

		System.out.println("config:: Brick Name + Hardware-Version="+ConfigServiceImpl.getName(bm1));
		System.out.println("config:: bindingVersion="+ConfigServiceImpl.getBindingVersion(bm1));
		System.out.println("config:: FirmwareVersion="+ConfigServiceImpl.getFirmwareVersion(bm1));
//		System.out.println("config:: bm1.getVersion().toString()="+bm1.getVersion().bindingVersion[0]+"."+bm1.getVersion().bindingVersion[1]+"."+bm1.getVersion().bindingVersion[2]);
		
		try {
			System.out.println("config:: versuche: bm1.getStackCurrent()");
			System.out.println("config:: bm1.getStackCurrent()="+bm1.getStackCurrent());
			System.out.println("config:: versuche: bm1.getChibiSignalStrength()");
			System.out.println("config:: bm1.getChibiSignalStrength()="+bm1.getChibiSignalStrength());
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
			return;
		}

		try {
			lcd = (BrickletLCD20x4) configService.createAndConnect(ConfigService.LCD201);
		} catch (TimeoutException e) {
			e.printStackTrace();
			return;
		}
		try {
			lcd.backlightOff();
		} catch (TimeoutException e) {
			e.printStackTrace();
			return;
		} catch (NotConnectedException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("config:: leaving");
	}

	public void start() {
		System.out.println("start:: starting");

		// Konfiguration und Start der Anlage
		config();

		System.out.println("start:: leaving");

	}
}
