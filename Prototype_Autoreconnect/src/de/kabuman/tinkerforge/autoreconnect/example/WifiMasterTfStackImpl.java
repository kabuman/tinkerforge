package de.kabuman.tinkerforge.autoreconnect.example;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class WifiMasterTfStackImpl extends TfAbstractStack implements TfStack{
	
	private BrickMaster tfMaster = null;
	private BrickletDualButton tfDualButton = null;
	
	public WifiMasterTfStackImpl(TfStackCallbackApp tfCallback) {
		super(tfCallback);
	}
	
	public void setIPConnection(IPConnection ipcon){
		super.setIPConnection(ipcon);
	}

	@Override
	public void tfDeviceReConnected(TfDeviceInfo tfDeviceInfo) {
		
		
		switch (tfDeviceInfo.getDeviceIdentifier()) {
		case BrickMaster.DEVICE_IDENTIFIER:
			tfMaster = new BrickMaster(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		case BrickletDualButton.DEVICE_IDENTIFIER:
			tfDualButton = new BrickletDualButton(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon());
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();
		
	}

	@Override
	public void tfDeviceDisConnected(String uid) {
		System.out.println("uid="+uid);
	}

	public BrickMaster getTfMaster() {
		return tfMaster;
	}

	public BrickletDualButton getTfDualButton() {
		return tfDualButton;
	}

}
