package de.kabuman.tinkerforge.alarm.items.digital.output;

public interface WaterSensorItem {

	final static char OPTION_SMALLER = '<';
	final static char OPTION_GREATER = '>';
	final static long DEBOUNCE_PERIOD_STANDARD = 500l;  // 500 l (for long)
	
	public void activateWaterSensor();
	
	public void deactivateWaterSensor();
	
	public boolean isActive();
}
