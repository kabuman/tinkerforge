package de.kabuman.tinkerforge.screencontroller.demo.tfstack;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRotaryEncoder;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class DemoTfStackRotaryImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge Stack Devices
	private BrickMaster tfMaster = null;
	private BrickletLCD20x4 tfLcd = null;
	private BrickletRotaryEncoder tfRotaryEncoder = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param tfCallback - callback interface used by TfStackImpl
	 */
	public DemoTfStackRotaryImpl(TfStackCallbackApp tfCallback) {
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

		case BrickletRotaryEncoder.DEVICE_IDENTIFIER:
			tfRotaryEncoder = new BrickletRotaryEncoder(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
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


	public BrickletRotaryEncoder getTfRotaryEncoder() {
		return tfRotaryEncoder;
	}

}
