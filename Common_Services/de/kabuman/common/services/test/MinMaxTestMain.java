package de.kabuman.common.services.test;

import de.kabuman.common.services.MinMaxService;
import de.kabuman.common.services.MinMaxServiceImpl;
import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.common.services.StringService;

public class MinMaxTestMain {

	private static MinMaxService minMaxService;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		minMaxService = new MinMaxServiceImpl();
		minMaxService.regardValue(1001.3999);
		minMaxService.regardValue(1001.3999);
//		minMaxService.regardValue(1020);
//		resultsToConsole();
//		
////		minMaxService = new MinMaxServiceImpl();
//		for (int i = 0; i < 100; i++) {
////			minMaxService.regardValue(i);
//			minMaxService.regardValue(100);
//		}
		resultsToConsole();

	}
	
	private static void resultsToConsole(){
		System.out.println("count="+minMaxService.getCount());
		System.out.println("min="+minMaxService.getMin());
		System.out.println("max="+minMaxService.getMax());
		System.out.println("average="+minMaxService.getAverage());
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
