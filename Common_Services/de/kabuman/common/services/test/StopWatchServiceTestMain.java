package de.kabuman.common.services.test;

import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;

public class StopWatchServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StopWatchService stopWatchService = new StopWatchServiceImpl();
		System.out.println("Instance-Time = "+ stopWatchService.getCurrentString());
		stopWatchService.start();
		System.out.println("Instance-Time = "+ stopWatchService.getCurrentString());
		for (int i = 0; i < 100; i++) {
			System.out.println("Current = "+ stopWatchService.getCurrentString());
			for (int j = 0; j < 100000000; j++) {
			}
		}
		System.out.println("isActive = "+ stopWatchService.isActive());
		stopWatchService.stopOver();
		System.out.println("isActive = "+ stopWatchService.isActive());
		System.out.println("StopOver = "+ stopWatchService.getSumString());
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100000000; j++) {
			}
		}
		stopWatchService.start();
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100000000; j++) {
			}
		}
		stopWatchService.stopOver();
		System.out.println("Gesamt = "+ stopWatchService.getSumString());
		System.out.println("Report = "+ stopWatchService.getReport("Testreport"));
	}

}
