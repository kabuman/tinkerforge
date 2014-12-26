package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;

import de.kabuman.tinkerforge.alarm.config.CfgAlertUnitRemote;
import de.kabuman.tinkerforge.services.connect.TfAbstractStack;
import de.kabuman.tinkerforge.services.connect.TfDeviceInfo;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * ProtectUnit specific Tf Stack Implementation
 */
public class AlertUnitRemoteTfStackImpl extends TfAbstractStack implements TfStack{
	
	// Tinkerforge devices
	private BrickMaster alarmMaster = null;
	private BrickletIO16 alarmSignal = null;
	private BrickletLCD20x4 alarmDisplay;

	
	// Parameter
	private CfgAlertUnitRemote cfgAlertUnitRemote;

	
	/**
	 * Constructor for a ProtectUnit specific TfStack Implementation<br>
	 *  <br>
	 * TfStack Implementation
	 * @param tfCallback - via this interface the application will be called back for ReConnect/Disconnect  <br>
	 * @param cfgAlertUnit - ProtectUnit specific configuration
	 */
	public AlertUnitRemoteTfStackImpl(TfStackCallbackApp tfCallback, CfgAlertUnitRemote cfgAlertUnitRemote) {
		super(tfCallback);
		this.cfgAlertUnitRemote = cfgAlertUnitRemote;
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

	public BrickletIO16 getAlarmSignal() {
		return alarmSignal;
	}


	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
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

		case BrickletIO16.DEVICE_IDENTIFIER:
			alarmSignal = (cfgAlertUnitRemote.getIo() > 0)? new BrickletIO16(tfDeviceInfo.getUid(), tfDeviceInfo.getIpcon()) : null;
			break;

		default:
			break;
		}
		
//		tfDeviceInfo.println();

	}


}
