package de.kabuman.tinkerforge.strobecontroller;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;


public class StrobeControllerMain {
	
	final static int TRY_COUNTER = 5;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		StrobeControllerAppl strobeController;
		Exception e;
		
		do {
			strobeController = new StrobeControllerAppl();
			e = strobeController.strobeControllerLauncher(false);
			if (e == null){
				break;
			}
			System.out.println("StrobeControllerMain:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}
	}
}
