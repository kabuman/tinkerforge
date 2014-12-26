package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * ProtectUnit specific Tf Stack Implementation
 */
public class ProtectUnitTfStackImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge devices
	private BrickMaster alarmMaster = null;
	private BrickletIO4 openSensor;
	private BrickletDistanceIR motionSensor = null;
	private BrickletMotionDetector motionDetection = null;
	private BrickletAnalogIn waterSensor = null;
	private BrickletTemperature temperatureSensor = null;
	private BrickletHumidity humiditySensor = null;
	private BrickletVoltageCurrent smokeSensor = null;
	
	// Parameter
	private CfgProtectUnit cfgProtectUnit;

	
	/**
	 * Constructor for a ProtectUnit specific TfStack Implementation<br>
	 *  <br>
	 * TfStack Implementation
	 * @param tfCallback - via this interface the application will be called back for ReConnect/Disconnect  <br>
	 * @param cfgProtectUnit - ProtectUnit specific configuration
	 */
	public ProtectUnitTfStackImpl(TfStackCallbackApp tfCallback, CfgProtectUnit cfgProtectUnit) {
		super(tfCallback);
		this.cfgProtectUnit = cfgProtectUnit;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfAbstractStack#setIPConnection(com.tinkerforge.IPConnection)
	 */
	public void setIPConnection(IPConnection ipcon){
		super.setIPConnection(ipcon);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfAbstractStack#tfDeviceReConnected(de.kabuman.tinkerforge.services.connect.TfDeviceInfo)
	 */
	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		
		
		switch (tfDeviceInfo.getDeviceIdentifier()) {
		case BrickMaster.DEVICE_IDENTIFIER:
			alarmMaster = new BrickMaster(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletIO4.DEVICE_IDENTIFIER:
			openSensor = (cfgProtectUnit.getIo() > 0)? new BrickletIO4(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletDistanceIR.DEVICE_IDENTIFIER:
			motionSensor = (cfgProtectUnit.getIr() > 0)? new BrickletDistanceIR(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletMotionDetector.DEVICE_IDENTIFIER:
			motionDetection = (cfgProtectUnit.getMd() > 0)? new BrickletMotionDetector(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletAnalogIn.DEVICE_IDENTIFIER:
			waterSensor = (cfgProtectUnit.getAi() > 0)? new BrickletAnalogIn(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletTemperature.DEVICE_IDENTIFIER:
			temperatureSensor = (cfgProtectUnit.getTp() > 0)? new BrickletTemperature(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletHumidity.DEVICE_IDENTIFIER:
			humiditySensor = (cfgProtectUnit.getHm() > 0)? new BrickletHumidity(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		case BrickletVoltageCurrent.DEVICE_IDENTIFIER:
			smokeSensor = (cfgProtectUnit.getVc() > 0)? new BrickletVoltageCurrent(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();
		
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

	public BrickletIO4 getOpenSensor() {
		return openSensor;
	}

	public BrickletDistanceIR getMotionSensor() {
		return motionSensor;
	}

	public BrickletMotionDetector getMotionDetection() {
		return motionDetection;
	}

	public BrickletAnalogIn getWaterSensor() {
		return waterSensor;
	}

	public BrickletTemperature getTemperatureSensor() {
		return temperatureSensor;
	}

	public BrickletHumidity getHumiditySensor() {
		return humiditySensor;
	}

	public BrickletVoltageCurrent getSmokeSensor() {
		return smokeSensor;
	}

}
