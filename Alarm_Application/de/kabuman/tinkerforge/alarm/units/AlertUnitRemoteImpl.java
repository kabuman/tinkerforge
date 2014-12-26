package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnitRemote;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcOnOffSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcQuietSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.RcRestartSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO16ItemImpl;
import de.kabuman.tinkerforge.alarm.threads.LedObserver;
import de.kabuman.tinkerforge.alarm.threads.LedObserverImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;


/**
 * Implements the Alert Unit Remote
 */
public class AlertUnitRemoteImpl implements AlertUnitRemote, TfStackCallbackApp {

	// Tinkerforge Stack & Connection
    private AlertUnitRemoteTfStackImpl tfStack = null;
    private TfConnectService tfConnectService = null;

	// Devices
	private BrickMaster alarmMaster;
	private BrickletIO16 alarmSignal;
	private BrickletLCD20x4 alarmDisplay;

	// alarmSignal dependent objects
	private OutputIO16ItemImpl ledItem;
	private OutputIO16ItemImpl beeperItem;

	// Extras for Alert Unit Remote 
	private RcOnOffSwitchItemImpl rcOnOffSwitchItemImpl;
	private RcQuietSwitchItemImpl rcQuietSwitchItemImpl;
	
	// Parameter: Configuration Data
	private CfgAlertUnitRemote cfgAlertUnitRemote;

	// Process vars
	private boolean firstConnect = true;
	private boolean unitActivated = false;
	private boolean connected = false;


	/**
	 * Constructor
	 * @param cfgAlertUnitRemote - the configuration data for this Alert Unit Remote
	 */
	public AlertUnitRemoteImpl(CfgAlertUnitRemote cfgAlertUnitRemote){
		this.cfgAlertUnitRemote = cfgAlertUnitRemote;
		
	    tfStack = ((AlertUnitRemoteTfStackImpl) new AlertUnitRemoteTfStackImpl(this, cfgAlertUnitRemote));
	    
		tfConnectService = new TfConnectService(cfgAlertUnitRemote.getHost(), cfgAlertUnitRemote.getPort(), null, tfStack);
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reset()
	 */
	public void reset() {
		// Stop Alert Signal
		deactivateAlert();

		// Stop Alert Happened Signal
		ledItem.switchOFF();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#activateAlert(de.kabuman.tinkerforge.alarm.units.ProtectUnit, java.lang.String)
	 */
	public void activateAlert(ProtectUnit protectionUnit, String message, boolean isQuiet) {
		//		this.protectionUnit = protectionUnit;

		if (!isQuiet){
			// Start Alarm Signal (Beeper)
			beeperItem.switchON();
		}
		// Start Alert Happened Signal (LED)
		ledItem.switchON();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#activateAlert()
	 */
	public void activateAlert(){
		// Start Alarm Signal (Beeper)
		beeperItem.switchON();

		// Start Alert Happened Signal (LED)
		ledItem.switchON();
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getBrickMaster()
	 */
	public BrickMaster getBrickMaster() {
		return alarmMaster;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getUnitName()
	 */
	public String getUnitName() {
		return cfgAlertUnitRemote.getUnitName();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#deactivateAlert()
	 */
	public void deactivateAlert() {
		// Stop Alarm Signal
		beeperItem.switchOFF();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reconnect()
	 */
	public void reconnect() {
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#isConnected()
	 */
	public boolean isConnected() {
		return connected;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnitRemote#getAlarmDisplay()
	 */
	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getTemperatureSensorItem()
	 */
	public TemperatureSensorItem getTemperatureSensorItem() {
		// Remote Alert has no capacity for a Temperature Sensor
		return null;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getHumiditySensorItem()
	 */
	public HumiditySensorItem getHumiditySensorItem() {
		// Remote Alert has no capacity for a Humidity Sensor
		return null;
	}

	
	@Override
	public void power(boolean switchOn) {
		if (cfgAlertUnitRemote.getCfgRemoteSwitchData().getSwitchType()<=0){
			// no powerSwitch available
			return;
		}
		
		short switchTo = ( switchOn) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF;

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(),"power(switchOn)","switchOn="+switchOn);
		RemoteSwitchControllerImpl.getInstance().switchPowerSecurely(
				cfgAlertUnitRemote.getCfgRemoteSwitchData()
				, switchTo);
	}


	@Override
	public Object getLEDItem() {
		return ledItem;
	}

	
	@Override
	public void activateUnit() {
		unitActivated = true;

		// confirm via LED
		int ledSchema = (AlertControllerImpl.getInstance().isOn()) ? LedObserver.LED_ALARM_ON : LedObserver.LED_ALARM_OFF;
		new LedObserverImpl(ledItem,ledSchema, getUnitName());
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnitRemote#getRcOnOffSwitchItemImpl()
	 */
	public RcOnOffSwitchItemImpl getRcOnOffSwitchItemImpl() {
		return rcOnOffSwitchItemImpl;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnitRemote#getRcQuietSwitchItemImpl()
	 */
	public RcQuietSwitchItemImpl getRcQuietSwitchItemImpl() {
		return rcQuietSwitchItemImpl;
	}

	
	/**
	 * Checks if mandatory BrickLets are available 
	 */
	private void checkRequiredTfStackBrickLets(){
		boolean missing = false;
		
		if (tfStack.getAlarmMaster() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmMaster required");
			missing = true;
		}
		
		if (tfStack.getAlarmSignal() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmSignal required");
			missing = true;
		}

		if (tfStack.getAlarmDisplay() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmDisplay required");
			missing = true;
		}

		if (missing){
			throw new IllegalArgumentException("See technical log for missing BrickLets");
		}
	}
	

	/**
	 * Handles first and reconnect 
	 */
	private void handleConnect(){
		checkRequiredTfStackBrickLets();
		
		alarmMaster = tfStack.getAlarmMaster();
		alarmSignal = tfStack.getAlarmSignal();
		alarmDisplay = tfStack.getAlarmDisplay();

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "handleConnect", "Try to connect to host="+cfgAlertUnitRemote.getHost()+" port="+cfgAlertUnitRemote.getPort());

		rcOnOffSwitchItemImpl = new RcOnOffSwitchItemImpl(alarmSignal, 500l, 'a', (short)0, true);
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "handleConnect", "alarm switchedOn="+rcOnOffSwitchItemImpl.isSwitchedON());
		
		rcQuietSwitchItemImpl = new RcQuietSwitchItemImpl(alarmSignal, 500l, 'a', (short)1, true);
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "handleConnect", "alert quiet="+rcQuietSwitchItemImpl.isQuiet());
		
		new RcRestartSwitchItemImpl(alarmSignal, 500l, 'a', (short)2, true);
		new RcResetSwitchItemImpl(alarmSignal, 500l, 'a', (short)3, true);

		ledItem = new OutputIO16ItemImpl(alarmSignal,'a',(short)6,null);
		beeperItem = new OutputIO16ItemImpl(alarmSignal,'a',(short)7,null);
		
		reset();
		
		if (unitActivated){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "handleConnect", "'activateUnit() triggered");
			activateUnit();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "handleConnect", "'activateUnit() NOT triggered");
		}

		
	}
	
	
	/**
	 * Logs Wifi parameter 
	 */
	private void logConnect(){
		try {
			if (tfStack.getAlarmMaster().isWifiPresent()){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "AlertUnitMobileImpl", " Wifi Key="+tfStack.getAlarmMaster().getLongWifiKey());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "AlertUnitMobileImpl", " Wifi BufferInfo="+tfStack.getAlarmMaster().getWifiBufferInfo());
			}
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "AlertUnitMobileImpl", "Host="+tfConnectService.getHost()+" Port="+tfConnectService.getPort()+" IP="+tfConnectService.getIP());
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStackCallbackApp#tfStackReConnected()
	 */
	@Override
	public synchronized void tfStackReConnected() {
		if (firstConnect){
			firstConnect = false;
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "tfStackReConnected", "TfStack firstly connected");
			logConnect();
			handleConnect();
//			triggerAutoReconnect();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "tfStackReConnected", "TfStack reconnected");
			handleConnect();
			
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				ledItem.switchON();
			}

			
			if (ScreenControllerImpl.getInstance() != null){
				ScreenControllerImpl.getInstance().setCloneLcdList(alarmDisplay);
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "tfStackReConnected", "Clone alarmDisplay replaced for screen controller ");
			} else {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "tfStackReConnected", "Clone alarmDisplay NOT replaced for screen controller (is null)");
			}

		}
		
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnitRemote.getUnitName(), "tfStackReConnected", "Connection timeout="+tfConnectService.getIpcon().getTimeout());
		connected = true;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStackCallbackApp#tfStackDisconnected()
	 */
	@Override
	public synchronized void tfStackDisconnected() {
		connected = false;
		System.out.println(this.getUnitName()+" disconnected");
	}


	@Override
	public String getTfStackName() {
		return getUnitName();
	}

}
