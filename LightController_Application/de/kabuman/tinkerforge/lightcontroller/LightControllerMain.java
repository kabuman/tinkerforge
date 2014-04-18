package de.kabuman.tinkerforge.lightcontroller;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;


public class LightControllerMain {
	
	final static int TRY_COUNTER = 5;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		LightControllerAppl lightController;
		Exception e;
		
		do {
			lightController = new LightControllerAppl();
			e = lightController.lightControllerLauncher(false);
			if (e == null){
				break;
			}
			System.out.println("LightControllerMain:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}
	}
}
