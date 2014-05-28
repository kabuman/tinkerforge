package de.kabuman.tinkerforge.alarm.threads;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItem;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
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
							temperatureLogData.append(writeTechnicalLogAndCollectForTemperatureLog(unit));
						}
						
						if (unit.getHumiditySensorItem() != null){
							humidityLogData.append(writeTechnicalLogAndCollectForHumidityLog(unit));
						}
						
						if (unit instanceof ProtectUnit){
							ProtectUnit protectUnit = (ProtectUnit) unit;
							if (protectUnit.getWaterSensorItem() != null){
								writeTechnicalLogForWaterSensor(protectUnit);
							}
						}
					}
					LogControllerImpl.getInstance().createTemperatureLogMessage(temperatureLogData.toString());
					LogControllerImpl.getInstance().createHumidityLogMessage(humidityLogData.toString());
				}
			}
		} catch (InterruptedException e) {
		} 
	}

	private String writeTechnicalLogAndCollectForTemperatureLog(Unit unit){
		TemperatureSensorItem si = unit.getTemperatureSensorItem(); 
		LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "TemperatureObserver: TemperaturSensor", concat(si.getCurrentValue(),si.getMinimumValue(),si.getMaximumValue(),si.getAverageValue()));
		return sep + Double.toString(si.getCurrentValue()).replace('.', ',');
	}
	
	private String writeTechnicalLogAndCollectForHumidityLog(Unit unit){
		HumiditySensorItem si = unit.getHumiditySensorItem(); 
		LogControllerImpl.getInstance().createTechnicalLogMessage(unit.getUnitName(), "TemperatureObserver: Humidity Sensor", concat(si.getCurrentValue(),si.getMinimumValue(),si.getMaximumValue(),si.getAverageValue()));
		return sep + Double.toString(si.getCurrentValue()).replace('.', ',');
	}
	
	private void writeTechnicalLogForWaterSensor(ProtectUnit protectUnit){
		WaterSensorItem si = protectUnit.getWaterSensorItem(); 
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "TemperatureObserver: Water Sensor", concat(si.getCurrentValue(),si.getMinimumValue(),si.getMaximumValue(),si.getAverageValue()));
	}
	
	private String concat(double... values){
		StringBuffer sb = new StringBuffer();
		for (double value : values) {
			String s = (sb.length()==0)? "Reported values (current/Min/Max/Avg) = " : " / ";
			sb.append(s);
			sb.append(value);
		}
		return sb.toString();
	}
}
