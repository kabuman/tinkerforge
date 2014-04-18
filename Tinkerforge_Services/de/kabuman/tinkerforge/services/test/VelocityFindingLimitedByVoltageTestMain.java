package de.kabuman.tinkerforge.services.test;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.VelocityFindingLimitedByVoltage;

public class VelocityFindingLimitedByVoltageTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigService configService = null;
		VelocityFindingLimitedByVoltage velocityFindingLimitedByVoltage;
		@SuppressWarnings("unused") BrickMaster brickMaster;
		BrickDC brickDC;
		BrickletVoltage brickletVoltage;

		// ConfigService 
		try {
			configService = ConfigServiceImpl.getNewInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// LCD create & connect
		try {
			brickMaster = (BrickMaster) configService.createAndConnect(ConfigService.MB4);
			brickDC = (BrickDC) configService.createAndConnect(ConfigService.DC1);
			brickletVoltage = (BrickletVoltage) configService.createAndConnect(ConfigService.VO1);
			velocityFindingLimitedByVoltage = new VelocityFindingLimitedByVoltage(brickDC, brickletVoltage);

			brickDC.disable();
			brickDC.setDriveMode((short) 1);	// 1=drive/coast
			brickDC.setVelocity((short) 0);
			brickDC.setAcceleration(0);		// no acceleration
			brickDC.setPWMFrequency(15000);	// lighter than 15khz
			brickDC.setCurrentVelocityPeriod(0); // no listener to trigger
			
			int targetVoltage = 4500;
			short findingSteps = -10;

			brickDC.setDriveMode((short) 1);
			short velocity = velocityFindingLimitedByVoltage.findVelocity(targetVoltage,findingSteps);

			System.out.println("VelocityFindingLimitedByVoltageTestMain:: velocity="+velocity);
			System.out.println("VelocityFindingLimitedByVoltageTestMain:: voltage="+velocityFindingLimitedByVoltage.getVoltage());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			

	}

}
