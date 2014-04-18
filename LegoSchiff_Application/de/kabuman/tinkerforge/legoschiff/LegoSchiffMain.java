package de.kabuman.tinkerforge.legoschiff;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;


public class LegoSchiffMain {
	
	final static int TRY_COUNTER = 5;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		LegoSchiffAppl legoSchiff;
		Exception e;
		
		do {
			legoSchiff = new LegoSchiffAppl();
			e = legoSchiff.legoSchiffLauncher(false);
			if (e == null){
				break;
			}
			System.out.println("LegoSchiffMain:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}
	}
}
