package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController;
import de.kabuman.tinkerforge.alarm.controller.RemoteSwitchControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.ResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO4ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;
import de.kabuman.tinkerforge.alarm.threads.LedObserver;
import de.kabuman.tinkerforge.alarm.threads.LedObserverImpl;
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
	
	OutputItem ledItem = null;
	OutputItem beeperItem = null;

	ResetSwitchItemImpl resetSwitchItemImpl = null;
	InetService inetService = new InetService();
	private RemoteSwitchController remoteSwitchController = RemoteSwitchControllerImpl.getInstance();
	
	private CfgAlertUnit cfgAlertUnit;

	public AlertUnitMobileImpl(CfgAlertUnit cfgAlertUnit){
		this.cfgAlertUnit = cfgAlertUnit;
		connect();
	}

	public void reset(){
		deactivateAlert();
		ledItem.switchOFF();
	}

	public void activateAlert(ProtectUnit protectionUnit, String message, boolean isQuiet) {
		ledItem.switchON();
		if (!isQuiet){
			beeperItem.switchON();
		}
	}
	
	public void activateAlert() {
		ledItem.switchON();
		beeperItem.switchON();
	}
	
	public void deactivateAlert(){
		beeperItem.switchOFF();
	}

	public BrickMaster getBrickMaster() {
		return alarmMaster;
	}

	public String getUnitName() {
		return cfgAlertUnit.getUnitName();
	}
	
	private boolean connectBrickLets(){
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "connectBrickLets", "Try to connect to host="+cfgAlertUnit.getHost()+" port="+cfgAlertUnit.getPort());

			try {
				ipcon = ConnectServiceImpl.getInstance().createConnectE(inetService.resolveURL(cfgAlertUnit.getHost()), cfgAlertUnit.getPort());
				ipcon.setTimeout(20000);
				ipcon.setAutoReconnect(true);
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
		ledItem = new OutputIO4ItemImpl(
				alarmSignal,
				(short)2,
				null);
		
		// Install Beeper
		beeperItem = new OutputIO4ItemImpl(
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
		power(true);
		remoteSwitchController.sleep(30000);
		

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
				ledItem.switchON();
			}
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECTED);
		} else {
			alarmMaster = null;
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECT_FAILED);
		}
	}

	public boolean isConnected() {
		if (alarmMaster == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"isConnected()","alarmMaster = null detected. alarmMaster="+alarmMaster);
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
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"isConnected()","openSensor.getInterrupt() = 0 detected");
				return false;
			}
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"isConnected()","alarmSignal.getPortInterrupt('a'): " +e.toString());
		}

		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getTemperatureSensorItem()
	 */
	public TemperatureSensorItem getTemperatureSensorItem() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getHumiditySensorItem()
	 */
	public HumiditySensorItem getHumiditySensorItem() {
		return null;
	}

	@Override
	public void power(boolean switchOn) {
		if (cfgAlertUnit.getCfgRemoteSwitchData().getSwitchType()<=0){
			// no powerSwitch available
			return;
		}
		
		short switchTo = ( switchOn) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF;

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(),"power(switchOn)","switchOn="+switchOn);
		RemoteSwitchControllerImpl.getInstance().switchPowerSecurely(
				cfgAlertUnit.getCfgRemoteSwitchData()
				, switchTo);
	}

	@Override
	public Object getLEDItem() {
		return ledItem;
	}

	@Override
	public void activateUnit() {
		// confirm via LED
		int ledSchema = (AlertControllerImpl.getInstance().isOn()) ? LedObserver.LED_ALARM_ON : LedObserver.LED_ALARM_OFF;
		new LedObserverImpl(ledItem,ledSchema, getUnitName());
	}

}
