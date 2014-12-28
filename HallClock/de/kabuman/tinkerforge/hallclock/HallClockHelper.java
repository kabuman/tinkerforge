package de.kabuman.tinkerforge.hallclock;


public class HallClockHelper {
	
	public static void writeMsgFirstlyConnected(){
		System.out.println("=> Stack firstly connected <=");
	}

	public static void writeMsgReconnected(){
		System.out.println("=> Stack reconnected <=");
	}

	public static void writeMsgDisconnected(){
		System.out.println("=> Stack disconnected <=");
	}


	
	public static void writeMsgCloneFirstlyConnected(int cloneId){
		System.out.println("=> Clone Stack firstly connected <=  id="+cloneId);
	}

	public static void writeMsgCloneReconnected(int cloneId){
		System.out.println("=> Clone Stack reconnected <=  id="+cloneId);
	}

	public static void writeMsgCloneDisconnected(int cloneId){
		System.out.println("=> Clone Stack disconnected <=  id="+cloneId);
	}


}
