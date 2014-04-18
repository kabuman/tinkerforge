package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.ResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO4ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;

/**
 * Implementation of "Alert Unit Mobile"
 * - BrickMaster
 * - Wifi Brick Extension
 * - Bricklet IO4 (1:Beeper, 2:LED, 3:Reset Switch)
 */
public class AlertUnitMobileImpl implements AlertUnit {

	IPConnection ipcon;
	
	// Devices
	private BrickMaster alarmMaster;
	private BrickletIO4 alarmSignal;
	
	OutputItem led = null;
	OutputItem beeper = null;

	ResetSwitchItemImpl resetSwitchItemImpl = null;
	InetService inetService = new InetService();
	
	private CfgAlertUnit cfgAlertUnit;

	public AlertUnitMobileImpl(CfgAlertUnit cfgAlertUnit){
		this.cfgAlertUnit = cfgAlertUnit;
		connect();
	}

	public void reset(){
		deactivateAlert();
		led.switchOFF();
	}

	public void activateAlert(ProtectUnit protectionUnit, String message, boolean isQuiet) {
		led.switchON();
		if (!isQuiet){
			beeper.switchON();
		}
	}
	
	public void activateAlert() {
		led.switchON();
		beeper.switchON();
	}
	
	public void deactivateAlert(){
		beeper.switchOFF();
	}

	public BrickMaster getBrickMaster() {
		return alarmMaster;
	}

	public String getUnitName() {
		return cfgAlertUnit.getUnitName();
	}
	
	private boolean connectBrickLets(){
			try {
				ipcon = ConnectServiceImpl.getInstance().createConnectE(inetService.resolveURL(cfgAlertUnit.getHost()), cfgAlertUnit.getPort());
			} catch (UnknownHostException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","host="+cfgAlertUnit.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
				return false;
			} catch (AlreadyConnectedException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			} catch (IOException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
				return false;
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
				return false;
			}
			
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","Connected to host="+cfgAlertUnit.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
			
			try {
				alarmMaster = (BrickMaster) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgAlertUnit.getMb(), cfgAlertUnit.getUnitName()+": "+cfgAlertUnit.getMbUsedFor());
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","BrickMaster: " +e.toString());
				return false;
			}

			
			try {
				alarmSignal = (BrickletIO4) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgAlertUnit.getIo(), cfgAlertUnit.getUnitName()+": "+cfgAlertUnit.getIoUsedFor());
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"connectBrickLets","BrickletIO16: " +e.toString());
				return false;
			}

			return true;
	}

	private void connectInput(){
		// Install Reset Taster
		resetSwitchItemImpl = new ResetSwitchItemImpl(cfgAlertUnit.getUnitName(), alarmSignal, 500l, (short)3, true);
	}
	
	private void connectOutput(){
		// Install LED
		led = new OutputIO4ItemImpl(
				alarmSignal,
				(short)2,
				null);
		
		// Install Beeper
		beeper = new OutputIO4ItemImpl(
				alarmSignal,
				(short)1,
				null);
	}
	
	private synchronized boolean connect(){
		if (connectBrickLets()){
			connectInput();
			connectOutput();
			reset();
			return true;
		} else {
			return false;
		}
	}


	public void reconnect(){
		try {
			resetSwitchItemImpl.removeListener();
		} catch (Exception e) {
		}
		
		try {
			ipcon.disconnect();
		} catch (Exception e1) {
		}
		
		if (getBrickMaster() != null){
			try {
				getBrickMaster().reset();
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"reconnect","BrickMaster: " +e.toString());
			}
		}
		
		if (connectBrickLets()){
			connectInput();
			connectOutput();
			deactivateAlert();
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				led.switchON();
			}
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECTED);
		} else {
			alarmMaster = null;
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECT_FAILED);

		}
	}

	public boolean isConnected() {
		if (alarmMaster == null){
			return false;
		}
		
		try {
			alarmMaster.getWifiStatus();
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"isConnected()","alarmMaster.getWifiStatus(): " +e.toString());
			return false;
		}

		try {
			if (alarmSignal.getInterrupt() == 0){
				return false;
			}
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"isConnected()","alarmSignal.getPortInterrupt('a'): " +e.toString());
		}

		return true;
	}
}
