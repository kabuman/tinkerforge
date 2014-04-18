package de.kabuman.tests;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;

public class TestAppl {

	ConfigService configService;

	IPConnection ipCon;

	BrickDC dc1;

	BrickletDistanceIR ir1a;

	BrickletDistanceIR ir1b;

	private void config() throws IOException, TimeoutException {
		configService = ConfigServiceImpl.getNewInstance();
		ipCon = configService.getConnect();

		ir1a = (BrickletDistanceIR) configService.createAndConnect(ConfigService.IR1);
		ir1b = (BrickletDistanceIR) configService.createAndConnect(ConfigService.IR1);
	}

	public void start() {

		// Konfiguration und Start der Anlage
		try {
			config();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// LISTENER-DEFINITIONEN
		//

		ir1a.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				System.out.println("distance1a:: Distance=" + distance / 10.0 + " cm");
			}
		});
		try {
			ir1a.setDistanceCallbackPeriod(500);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Sensor Berg(#2): Configuration and Listener Definition
		ir1b.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				System.out.println("distance1b:: Distance=" + distance / 10.0 + " cm");
			}
		});
		try {
			ir1b.setDistanceCallbackPeriod(1000);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
