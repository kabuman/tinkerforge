package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * ProtectUnit specific Tf Stack Implementation
 */
public class AlertUnitMobileTfStackImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge devices
	private BrickMaster alarmMaster = null;
	private BrickletIO4 alarmSignal = null;
	private BrickletLCD20x4 alarmDisplay;
	private BrickletTemperature temperatureSensor = null;
	private BrickletHumidity humiditySensor = null;
	private BrickletAmbientLight ambientLightSensor = null;
	private BrickletRotaryEncoder rotaryEncoderSensor = null;
	private BrickletMultiTouch multiTouchSensor = null;

	
	// Parameter
	private CfgAlertUnit cfgAlertUnit;

	
	/**
	 * Constructor for a ProtectUnit specific TfStack Implementation<br>
	 *  <br>
	 * TfStack Implementation
	 * @param tfCallback - via this interface the application will be called back for ReConnect/Disconnect  <br>
	 * @param cfgAlertUnit - ProtectUnit specific configuration
	 */
	public AlertUnitMobileTfStackImpl(TfStackCallbackApp tfCallback, CfgAlertUnit cfgAlertUnit) {
		super(tfCallback);
		this.cfgAlertUnit = cfgAlertUnit;
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
		System.out.println("tfDeviceDisConnected: uid="+uid);
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


	public BrickletIO4 getAlarmSignal() {
		return alarmSignal;
	}


	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
	}


	public BrickletAmbientLight getAmbientLightSensor() {
		return ambientLightSensor;
	}


	public BrickletMultiTouch getMultiTouchSensor() {
		return multiTouchSensor;
	}


	public BrickletRotaryEncoder getRotaryEncoderSensor() {
		return rotaryEncoderSensor;
	}

	
	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		switch (tfDeviceInfo.getDeviceIdentifier()) {
		case BrickMaster.DEVICE_IDENTIFIER:
			alarmMaster = new BrickMaster(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletLCD20x4.DEVICE_IDENTIFIER:
			alarmDisplay = new BrickletLCD20x4(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletIO4.DEVICE_IDENTIFIER:
			alarmSignal = (cfgAlertUnit.getIo() > 0)? new BrickletIO4(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletTemperature.DEVICE_IDENTIFIER:
			temperatureSensor = (cfgAlertUnit.getTp() > 0)? new BrickletTemperature(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletHumidity.DEVICE_IDENTIFIER:
			humiditySensor = (cfgAlertUnit.getHm() > 0)? new BrickletHumidity(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletAmbientLight.DEVICE_IDENTIFIER:
			ambientLightSensor = (cfgAlertUnit.getAl() > 0)? new BrickletAmbientLight(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletRotaryEncoder.DEVICE_IDENTIFIER:
			rotaryEncoderSensor = (cfgAlertUnit.getAl() > 0)? new BrickletRotaryEncoder(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletMultiTouch.DEVICE_IDENTIFIER:
			multiTouchSensor = (cfgAlertUnit.getAl() > 0)? new BrickletMultiTouch(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();

	}


}
