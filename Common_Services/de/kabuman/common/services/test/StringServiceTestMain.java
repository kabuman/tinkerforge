package de.kabuman.common.services.test;

import de.kabuman.common.services.StringService;

public class StringServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("StringService.create(..) - Tests");
		createStrg(3);
		createStrg(100);
		createStrg(0);
		createStrg(-1);
		
		System.out.println("\nStringService.create(..) - Tests (with default value)");
		createWithValueStrg(10, "1234567890");
		createWithValueStrg(10, "----+----!");
		
		System.out.println("\nStringService.overwrite(..) - Tests");
		overwriteStrg("Karsten Buchmann", -1, "e");
		overwriteStrg("K", 0, "A");
		overwriteStrg("K", 1, "A");
		overwriteStrg("Karss", 4, "t");
		overwriteStrg("Karss", 4, "te");
		overwriteStrg("Kars", 4, "ten");
		overwriteStrg("Kars", 5, "ten");
		System.out.println(StringService.create(100));
		System.out.println("\nStringService.insert(..) - Tests");
		insertStrg("Kar Buchmann", 3, "sten");
		insertStrg("Kars", 4, "ten");
		insertStrg("Kars", 5, "ten");
	}
	
	public static void overwriteStrg(String targetStrg, int pos, String overwriteStrg){
		try {
			System.out.println(targetStrg+"/"+pos+"/"+overwriteStrg+" = "+StringService.overwrite(targetStrg, pos, overwriteStrg));
		} catch (Exception e) {
			System.out.println(targetStrg+"/"+pos+"/"+overwriteStrg+" IllegalArgumentException detected: "+e.getMessage());
		}
	}

	public static void insertStrg(String targetStrg, int pos, String insertStrg){
		try {
			System.out.println(targetStrg+"/"+pos+"/"+insertStrg+" = "+StringService.insert(targetStrg, pos, insertStrg));
		} catch (Exception e) {
			System.out.println(targetStrg+"/"+pos+"/"+insertStrg+" IllegalArgumentException detected: "+e.getMessage());
		}
	}

	public static void createStrg(int length){
		try {
			System.out.println(length+" = '"+StringService.create(length)+"'");
		} catch (Exception e) {
			System.out.println(length+" IllegalArgumentException detected: "+e.getMessage());
		}
	}

	public static void createWithValueStrg(int length, String defaultValue){
		try {
			System.out.println(length+" = '"+StringService.create(length, defaultValue)+"'");
		} catch (Exception e) {
			System.out.println(length+" IllegalArgumentException detected: "+e.getMessage());
		}
	}

}
