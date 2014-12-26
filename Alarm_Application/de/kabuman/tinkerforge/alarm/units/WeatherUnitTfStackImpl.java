package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * ProtectUnit specific Tf Stack Implementation
 */
public class WeatherUnitTfStackImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge devices
	private BrickMaster alarmMaster = null;
	private BrickletTemperature temperatureSensor = null;
	private BrickletHumidity humiditySensor = null;
	private BrickletBarometer barometerSensor = null;

	
	/**
	 * Constructor for a ProtectUnit specific TfStack Implementation<br>
	 *  <br>
	 * TfStack Implementation
	 * @param tfCallback - via this interface the application will be called back for ReConnect/Disconnect  <br>
	 * @param cfgWeatherUnit - ProtectUnit specific configuration
	 */
	public WeatherUnitTfStackImpl(TfStackCallbackApp tfCallback) {
		super(tfCallback);
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfAbstractStack#setIPConnection(com.tinkerforge.IPConnection)
	 */
	public void setIPConnection(IPConnection ipcon){
		super.setIPConnection(ipcon);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStack#tfDeviceDisConnected(java.lang.String)
	 */
	@Override
	public void tfDeviceDisConnected(String uid) {
		System.out.println("uid="+uid);
	}

	public BrickMaster getAlarmMaster() {
		return alarmMaster;
	}

	public BrickletTemperature getTemperatureSensor() {
		return temperatureSensor;
	}

	public BrickletHumidity getHumiditySensor() {
		return humiditySensor;
	}


	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		switch (tfDeviceInfo.getDeviceIdentifier()) {
		case BrickMaster.DEVICE_IDENTIFIER:
			alarmMaster = new BrickMaster(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletTemperature.DEVICE_IDENTIFIER:
			temperatureSensor = new BrickletTemperature(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletHumidity.DEVICE_IDENTIFIER:
			humiditySensor = new BrickletHumidity(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletBarometer.DEVICE_IDENTIFIER:
			barometerSensor = new BrickletBarometer(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();

	}


	public BrickletBarometer getBarometerSensor() {
		return barometerSensor;
	}


}
