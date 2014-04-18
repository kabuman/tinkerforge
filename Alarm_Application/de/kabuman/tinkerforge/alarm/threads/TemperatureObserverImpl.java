package de.kabuman.tinkerforge.alarm.threads;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;



public class TemperatureObserverImpl extends Thread{

	// Alarm Signal Object
	List<Unit> unitList = new ArrayList<Unit>();
	
	// distance between two alive checks
	long sequence;

	boolean isAlive = false;
	
	boolean active = true;

	private final String sep = ";";
	/**
	 * Constructor
	 */
	public TemperatureObserverImpl(List<Unit> unitList, long sequence) {
		this.unitList = unitList;
		this.sequence = sequence;

		LogControllerImpl.getInstance().createTechnicalLogMessage("TemperatureObserver", "Start", "sequence="+sequence);

		start();  // calls the run() method
	}


	public void deactivate(){
		active = false;
		this.interrupt();
	}
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Sleeps and then switch OFF the Alarm Signal
		try {
			boolean doLoop = true;
			while (doLoop) {
				Thread.sleep(sequence);
				if (active){
					StringBuffer temperatureLogData = new StringBuffer();
					StringBuffer humidityLogData = new StringBuffer();
					
					for (int i = 0; i < unitList.size(); i++) {
						Unit unit = unitList.get(i);
						
						if (unit.getTemperatureSensorItem() != null){
							LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "TemperatureObserver: TemperaturSensor", "Reported value = " + unit.getTemperatureSensorItem().getCurrentValue());
							temperatureLogData.append(sep + Double.toString(unit.getTemperatureSensorItem().getCurrentValue()).replace('.', ','));
						}
						
						if (unit.getHumiditySensorItem() != null){
							LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "TemperatureObserver: Humidity Sensor", "Reported value = " +  + unit.getHumiditySensorItem().getCurrentValue());
							humidityLogData.append(sep + Double.toString(unit.getHumiditySensorItem().getCurrentValue()).replace('.', ','));
						}
					}
					LogControllerImpl.getInstance().createTemperatureLogMessage(temperatureLogData.toString());
					LogControllerImpl.getInstance().createHumidityLogMessage(humidityLogData.toString());
				}
			}
		} catch (InterruptedException e) {
		} 
	}
	
}
