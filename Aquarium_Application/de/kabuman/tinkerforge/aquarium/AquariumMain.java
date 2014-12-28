package de.kabuman.tinkerforge.aquarium;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;


public class AquariumMain {
	
	final static int TRY_COUNTER = 5;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		
		AquariumApplImpl aquarium;
		Exception e;
		
		do {
			aquarium = new AquariumApplImpl(args);
			e = aquarium.launcher(false);
			if (e == null){
				break;
			}
			System.out.println("AquariumMain:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}


		// TODO Auto-generated method stub

	}

}
