package de.kabuman.common.services.test;


public class TestHelper {
	
	public static void sleep(int sleepTime){
		try {
			Thread.sleep((long)sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
