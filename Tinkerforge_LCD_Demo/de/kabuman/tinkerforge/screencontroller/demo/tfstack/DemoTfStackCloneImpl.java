package de.kabuman.tinkerforge.screencontroller.demo.tfstack;

import com.tinkerforge.BrickletLCD20x4;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class DemoTfStackCloneImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge Stack Devices
	private BrickletLCD20x4 tfLcd = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param tfCallback - callback interface used by TfStackImpl
	 */
	public DemoTfStackCloneImpl(TfStackCallbackApp tfCallback) {
		super(tfCallback);
	}

	
	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		
		switch (tfDeviceInfo.getDeviceIdentifier()) {

		case BrickletLCD20x4.DEVICE_IDENTIFIER:
			tfLcd = new BrickletLCD20x4(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();
		
	}

	
	@Override
	public void tfDeviceDisConnected(String uid) {
	}


	public BrickletLCD20x4 getTfLcd() {
		return tfLcd;
	}

}
