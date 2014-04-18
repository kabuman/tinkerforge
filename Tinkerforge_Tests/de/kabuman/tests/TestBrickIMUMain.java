package de.kabuman.tests;

import java.io.IOException;

import com.tinkerforge.BrickIMU;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;

public class TestBrickIMUMain {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	public static void main(String[] args) throws IOException {
		ConfigService configService = ConfigServiceImpl.getNewInstance();
		BrickIMU imu = null;
		try {
			imu = (BrickIMU) configService.createAndConnect(ConfigService.IMU1, "IMU");
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Set period for quaternion callback to 1s
        try {
			imu.setQuaternionPeriod(1000);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Add and implement quaternion listener 
        imu.addQuaternionListener(new BrickIMU.QuaternionListener() {
            public void quaternion(float x, float y, float z, float w) {
                System.out.println("x: " + x + "\ny: " + y + "\nz: " + z + "\nw: " + w + "\n");
            }
        });

	}

}
