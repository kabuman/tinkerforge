package de.kabuman.tinkerforge.alarm.items.digital.output;

public interface MotionSensorItem {

	final static char OPTION_SMALLER = '<';
	final static char OPTION_GREATER = '>';
	final static long DEBOUNCE_PERIOD_STANDARD = 500l;  // 500 l (for long)
	
	public void activateMotionSensor();
	
	public void deactivateMotionSensor();
	
	public boolean isActive();
}
