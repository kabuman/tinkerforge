package de.kabuman.tinkerforge.alarm;

import java.io.FileNotFoundException;
import java.io.FileReader;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;

public class AlarmMain {

	final static int TRY_COUNTER = 5;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		
		AlarmApplImpl alarm;
		Exception e;
		
		do {
			alarm = new AlarmApplImpl(args);
			e = alarm.launcher(false);
			if (e == null){
				break;
			}
			System.out.println("AlarmMain:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}

	}

}
