package de.kabuman.common.services.test;

import de.kabuman.common.services.PercentageService;

public class PercentageServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Tests with IllegalArgumentExceptions");
		calculatePercentage(1, 1, 1);
		calculatePercentage(0, 1, 2);
		calculatePercentage(3, 1, 2);
		
		System.out.println("Tests with pur Percentage Calculation");
		calculatePercentage(10000, 0, 30000);
		calculatePercentage(1, 1, 2);
		calculatePercentage(2, 1, 2);
		calculatePercentage(20000, 10000, 30000);
		calculatePercentage(0,-100, 100);
		
		System.out.println("Tests with formatted Percentage Calculation");
		calculateFormPercentage(10000, 0, 30000);
		calculateFormPercentage(1, 1, 2);
		calculateFormPercentage(2, 1, 2);
		calculateFormPercentage(20000, 10000, 30000);
		
		System.out.println("Tests with short formatted Percentage Calculation");
		calculateShortFormPercentage(10000, 0, 30000);
		calculateShortFormPercentage(1, 1, 2);
		calculateShortFormPercentage(2, 1, 2);
		calculateShortFormPercentage(20000, 10000, 30000);
	}
	
	public static void calculatePercentage (int currentValue, int lowerBound, int upperBound){
		try {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= '"+PercentageService.getPercentage(currentValue, lowerBound, upperBound)+"'");
		} catch (IllegalArgumentException e) {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= "+"IllegalArgumentException was thrown: "+e.getMessage());
		}
	}

	public static void calculateFormPercentage (int currentValue, int lowerBound, int upperBound){
		try {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= '"+PercentageService.getFormPercentage(currentValue, lowerBound, upperBound)+"'");
		} catch (IllegalArgumentException e) {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= "+"IllegalArgumentException was thrown: "+e.getMessage());
		}
	}

	public static void calculateShortFormPercentage (int currentValue, int lowerBound, int upperBound){
		try {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= '"+PercentageService.getShortFormPercentage(currentValue, lowerBound, upperBound)+"'");
		} catch (IllegalArgumentException e) {
			System.out.println(currentValue+"/"+lowerBound+"/"+upperBound+"= "+"IllegalArgumentException was thrown: "+e.getMessage());
		}
	}

}
