package de.kabuman.tinkerforge.rp6;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;


public class RP6Main {
	
	final static int TRY_COUNTER = 5;

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		CountDownService countDownService = new CountDownServiceImpl(TRY_COUNTER);
		
		RP6Appl rp6;
		Exception e;
		
		do {
			rp6 = new RP6Appl();
			e = rp6.rp6Launcher(false);
			if (e == null){
				break;
			}
			System.out.println("RP6Main:: Exception detected: "+ e);
			countDownService.down();
		} while (!countDownService.isDown());
		
		if (e != null){
			e.printStackTrace();
		}
	}
}
