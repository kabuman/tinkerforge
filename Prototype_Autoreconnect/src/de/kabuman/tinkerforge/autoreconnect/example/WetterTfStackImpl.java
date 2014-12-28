package de.kabuman.tinkerforge.autoreconnect.example;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletTemperature;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class WetterTfStackImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge Stack Devices
	private BrickMaster tfMaster = null;
	private BrickletLCD20x4 tfLcd = null;
	private BrickletHumidity tfHumidity = null;
	private BrickletTemperature tfTemperature = null;
	private BrickletBarometer tfBarometer = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param tfCallback - callback interface used by TfStackImpl
	 */
	public WetterTfStackImpl(TfStackCallbackApp tfCallback) {
		super(tfCallback);
	}

	
	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		
		switch (tfDeviceInfo.getDeviceIdentifier()) {
		case BrickMaster.DEVICE_IDENTIFIER:
			tfMaster = new BrickMaster(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletLCD20x4.DEVICE_IDENTIFIER:
			tfLcd = new BrickletLCD20x4(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletHumidity.DEVICE_IDENTIFIER:
			tfHumidity = new BrickletHumidity(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletTemperature.DEVICE_IDENTIFIER:
			tfTemperature = new BrickletTemperature(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletBarometer.DEVICE_IDENTIFIER:
			tfBarometer = new BrickletBarometer(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();
		
	}

	
	@Override
	public void tfDeviceDisConnected(String uid) {
	}


	public BrickMaster getTfMaster() {
		return tfMaster;
	}

	public BrickletLCD20x4 getTfLcd() {
		return tfLcd;
	}

	public BrickletHumidity getTfHumidity() {
		return tfHumidity;
	}

	public BrickletTemperature getTfTemperature() {
		return tfTemperature;
	}

	public BrickletBarometer getTfBarometer() {
		return tfBarometer;
	}

}
