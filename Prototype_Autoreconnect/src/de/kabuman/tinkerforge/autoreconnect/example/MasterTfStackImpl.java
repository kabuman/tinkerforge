package de.kabuman.tinkerforge.autoreconnect.example;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class MasterTfStackImpl extends TfAbstractStack implements TfStack{
	
	private BrickMaster tfMaster = null;
	
	public MasterTfStackImpl(TfStackCallbackApp tfCallback) {
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

}
