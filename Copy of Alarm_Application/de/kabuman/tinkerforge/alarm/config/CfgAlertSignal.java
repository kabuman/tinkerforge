package de.kabuman.tinkerforge.alarm.config;

public class CfgAlertSignal {

	private boolean quiet; 
	private int alertDurationForIntrusion;
	private int alertDurationForWater;
	private int alertDurationForFire;
	private int aliveSequence;
	
	
	public CfgAlertSignal(
			boolean quiet, 
			int alertDurationForIntrusion,
			int alertDurationForWater,
			int alertDurationForFire,
			int aliveSequence) {
		this.quiet = quiet;
		this.alertDurationForIntrusion = alertDurationForIntrusion;
		this.alertDurationForWater = alertDurationForWater;
		this.alertDurationForFire = alertDurationForFire;
		this.aliveSequence = aliveSequence;
	}
	
	public boolean isQuiet() {
		return quiet;
	}
	
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	
	public int getAlertDurationForIntrusion() {
		return alertDurationForIntrusion;
	}
	
	public void setAlertDurationForIntrusion(int alertDurationForIntrusion) {
		this.alertDurationForIntrusion = alertDurationForIntrusion;
	}
	
	public int getAlertDurationForWater() {
		return alertDurationForWater;
	}
	
	public void setAlertDurationForWater(int alertDurationForWater) {
		this.alertDurationForWater = alertDurationForWater;
	}
	
	public int getAlertDurationForFire() {
		return alertDurationForFire;
	}
	
	public void setAlertDurationForFire(int alertDurationForFire) {
		this.alertDurationForFire = alertDurationForFire;
	}
	
	public int getAliveSequence() {
		return aliveSequence;
	}
	
	public void setAliveSequence(int aliveSequence) {
		this.aliveSequence = aliveSequence;
	}

}
