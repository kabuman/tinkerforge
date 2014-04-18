package de.kabuman.common.services.test;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;

public class CountDownServiceTestMain {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CountDownService countDownService = new CountDownServiceImpl(3);
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		countDownService.reset();
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		countDownService.reset(5);
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		countDownService.reset();
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());
		System.out.println(countDownService.down());


	}

}
