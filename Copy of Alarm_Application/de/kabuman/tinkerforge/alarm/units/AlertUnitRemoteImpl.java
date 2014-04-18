package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnitRemote;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcOnOffSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcQuietSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcRestartSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO16ItemImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;

/**
 * Implements the Alert Unit Remote
 */
public class AlertUnitRemoteImpl implements AlertUnitRemote {

	IPConnection ipcon;

	// Devices
	private BrickMaster alarmMaster;
	private BrickletIO16 alarmSignal;
	private BrickletLCD20x4 alarmDisplay;

	
	RcOnOffSwitchItemImpl rcOnOffSwitchItemImpl;
	RcQuietSwitchItemImpl rcQuietSwitchItemImpl;
	RcResetSwitchItemImpl rcResetSwitchItemImpl;
	RcRestartSwitchItemImpl rcRestartSwitchItemImpl;
	OutputIO16ItemImpl led;
	OutputIO16ItemImpl beeper;

	private InetService inetService = new InetService();
	
	// Parameter: Configuration Data
	private CfgAlertUnitRemote cfgAlertUnitRemote;

	/**
	 * Constructor
	 * @param cfgAlertUnitRemote - the configuration data for this Alert Unit Remote
	 */
	public AlertUnitRemoteImpl(CfgAlertUnitRemote cfgAlertUnitRemote){
		this.cfgAlertUnitRemote = cfgAlertUnitRemote;
		connect();
	}

	private boolean connectBrickLets(){
		try {
			ipcon = ConnectServiceImpl.getInstance().createConnectE(inetService.resolveURL(cfgAlertUnitRemote.getHost()), cfgAlertUnitRemote.getPort());
		} catch (UnknownHostException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","host="+cfgAlertUnitRemote.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
			return false;
		} catch (AlreadyConnectedException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
		} catch (IOException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			return false;
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			return false;
		}

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","Connected to host="+cfgAlertUnitRemote.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
		
		try {
			alarmMaster = (BrickMaster) ConnectServiceImpl.getInstance()
					.createAndConnect(ipcon, cfgAlertUnitRemote.getMb(), cfgAlertUnitRemote.getUnitName() + ": " + cfgAlertUnitRemote.getMbUsedFor());
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","BrickMaster: " +e.toString());
			return false;
		}

		try {
			alarmSignal = (BrickletIO16) ConnectServiceImpl.getInstance()
					.createAndConnect(ipcon, cfgAlertUnitRemote.getIo(), cfgAlertUnitRemote.getUnitName() + ": " + cfgAlertUnitRemote.getIoUsedFor());
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","BrickletIO16: " +e.toString());
			return false;
		}
		
		try {
			alarmDisplay = (BrickletLCD20x4) ConnectServiceImpl.getInstance()
					.createAndConnect(ipcon, cfgAlertUnitRemote.getLcd(), cfgAlertUnitRemote.getUnitName() + ": " + cfgAlertUnitRemote.getLcdUsedFor());
			AlertControllerImpl.getInstance().setAlarmDisplay(alarmDisplay);

		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"connectBrickLets","BrickletLCD20x4: " +e.toString());
			return false;
		}
		
		return true;
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

	private void connectInput(){
		rcOnOffSwitchItemImpl = new RcOnOffSwitchItemImpl(alarmSignal, 500l, 'a', (short)0, true);
		rcQuietSwitchItemImpl = new RcQuietSwitchItemImpl(alarmSignal, 500l, 'a', (short)1, true);
		rcRestartSwitchItemImpl = new RcRestartSwitchItemImpl(alarmSignal, 500l, 'a', (short)2, true);
		rcResetSwitchItemImpl = new RcResetSwitchItemImpl(alarmSignal, 500l, 'a', (short)3, true);
	}
	
	private void connectOutput(){
		led = new OutputIO16ItemImpl(alarmSignal,'a',(short)6,null);
		beeper = new OutputIO16ItemImpl(alarmSignal,'a',(short)7,null);
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reset()
	 */
	public void reset() {
		// Stop Alert Signal
		deactivateAlert();

		// Stop Alert Happened Signal
		led.switchOFF();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#activateAlert(de.kabuman.tinkerforge.alarm.units.ProtectUnit, java.lang.String)
	 */
	public void activateAlert(ProtectUnit protectionUnit, String message, boolean isQuiet) {
		//		this.protectionUnit = protectionUnit;

		if (!isQuiet){
			// Start Alarm Signal (Beeper)
			beeper.switchON();
		}
		// Start Alert Happened Signal (LED)
		led.switchON();
	}

	public void activateAlert(){
//		if (!isQuiet){
			// Start Alarm Signal (Beeper)
			beeper.switchON();
//		}
		// Start Alert Happened Signal (LED)
		led.switchON();
	}
	
	public BrickMaster getBrickMaster() {
		return alarmMaster;
	}

	public String getUnitName() {
		return cfgAlertUnitRemote.getUnitName();
	}

	public void deactivateAlert() {
		// Stop Alarm Signal
		beeper.switchOFF();
	}

	public void reconnect() {
		try {
			rcOnOffSwitchItemImpl.removeListener();
		} catch (Exception e) {
		}

		try {
			rcQuietSwitchItemImpl.removeListener();
		} catch (Exception e) {
		}
		
		try {
			rcRestartSwitchItemImpl.removeListener();
		} catch (Exception e) {
		}
		
		try {
			rcResetSwitchItemImpl.removeListener();
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
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"reconnect","BrickMaster: " +e.toString());
			}
		}
		
		if (connectBrickLets()){
			connectInput();
			connectOutput();
			deactivateAlert();
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				led.switchON();
			}
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnitRemote.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECTED);
		} else {
			alarmMaster = null;
			LogControllerImpl.getInstance().createUserLogMessage(cfgAlertUnitRemote.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECT_FAILED);
		}
	}

	public boolean isConnected() {
		if (alarmMaster == null){
			return false;
		}
		
		try {
			alarmMaster.getWifiStatus();
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"isConnected()","alarmMaster.getWifiStatus(): " +e.toString());
			return false;
		}

		try {
			if (alarmSignal.getPortInterrupt('a') == 0){
				return false;
			}
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"isConnected()","alarmSignal.getPortInterrupt('a'): " +e.toString());
		}

		return true;
	}

	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
	}
}
