package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserverImpl;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.AmbientLightSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.AmbientLightSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.ResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO4ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;
import de.kabuman.tinkerforge.alarm.threads.LedObserver;
import de.kabuman.tinkerforge.alarm.threads.LedObserverImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;

/**
 * Implementation of "Alert Unit Mobile"
 * - BrickMaster
 * - Wifi Brick Extension
 * - Bricklet IO4 (1:Beeper, 2:LED, 3:Reset Switch)
 */
public class AlertUnitMobileImpl extends AbstractUnit 
	implements AlertUnit, TfStackCallbackApp, CommonCallback {

	// Tinkerforge Stack & Connection
    private AlertUnitMobileTfStackImpl tfStack = null;
    private TfConnectService tfConnectService = null;

	// Devices
	private BrickMaster alarmMaster;
	private BrickletIO4 alarmSignal;
	private BrickletLCD20x4 alarmDisplay;
	
	// alarmSignal dependent objects
	private OutputItem ledItem = null;
	private OutputItem beeperItem = null;

	// Temperature Sensor
	private BrickletTemperature temperatureSensor = null;
	private TemperatureSensorItem temperatureSensorItem = null;
	private long temperatureSensorCallback = 1000;
	
	// Humidity Sensor
	private BrickletHumidity humiditySensor = null;
	private HumiditySensorItem humiditySensorItem = null;
	private long humiditySensorCallback = 1000;

	// AmbientLight Sensor
	private BrickletAmbientLight ambientLightSensor = null;
	private AmbientLightSensorItem ambientLightSensorItem = null;
	private long ambientLightSensorCallback = 1000;
	
	// Rotary Encoder Sensor
	private BrickletRotaryEncoder rotaryEncoderSensor = null;
	
	// Multi Touch Sensor
	private BrickletMultiTouch multiTouchSensor = null;
	
	// Parameter
	private CfgAlertUnit cfgAlertUnit;
	private boolean operateLcdAsMaster;

	// Process vars
	private boolean firstConnect = true;
	private boolean unitActivated = false;
	private boolean connected = false;
    
	
	/**
	 * Constructor
	 * @param cfgAlertUnit
	 * @param operateLcdAsMaster - true: the LCD of this stack will be the master  <br>
	 * - false: will be a clone LCD
	 */
	public AlertUnitMobileImpl(CfgAlertUnit cfgAlertUnit, boolean operateLcdAsMaster){
		this.cfgAlertUnit = cfgAlertUnit;
		this.operateLcdAsMaster = operateLcdAsMaster;
		
	    tfStack = ((AlertUnitMobileTfStackImpl) new AlertUnitMobileTfStackImpl(this, cfgAlertUnit));
	    
		tfConnectService = new TfConnectService(cfgAlertUnit.getHost(), cfgAlertUnit.getPort(), null, tfStack);
		

	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reset()
	 */
	public void reset(){
		// Stop Alert Signal
		deactivateAlert();
		
		// Stop Alert Happened Signal
		ledItem.switchOFF();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#activateAlert(de.kabuman.tinkerforge.alarm.units.ProtectUnit, java.lang.String, boolean)
	 */
	public void activateAlert(ProtectUnit protectionUnit, String message, boolean isQuiet) {
		ledItem.switchON();
		if (!isQuiet){
			beeperItem.switchON();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#activateAlert()
	 */
	public void activateAlert() {
		// Start Alarm Signal (Beeper)
		beeperItem.switchON();
		
		// Start Alert Happened Signal (LED)
		ledItem.switchON();
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#deactivateAlert()
	 */
	public void deactivateAlert(){
		beeperItem.switchOFF();
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
		return cfgAlertUnit.getUnitName();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reconnect()
	 */
	public void reconnect(){
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#isConnected()
	 */
	public boolean isConnected() {
		return connected;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getTemperatureSensorItem()
	 */
	public TemperatureSensorItem getTemperatureSensorItem() {
		return temperatureSensorItem;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getHumiditySensorItem()
	 */
	public HumiditySensorItem getHumiditySensorItem() {
		return humiditySensorItem;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#power(boolean)
	 */
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

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getLEDItem()
	 */
	@Override
	public Object getLEDItem() {
		return ledItem;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#activateUnit()
	 */
	@Override
	public void activateUnit() {
		unitActivated = true;

		// confirm via LED
		int ledSchema = (AlertControllerImpl.getInstance().isOn()) ? LedObserver.LED_ALARM_ON : LedObserver.LED_ALARM_OFF;
		new LedObserverImpl(ledItem,ledSchema, getUnitName());
		
		if (temperatureSensor != null){
			temperatureSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "activateUnit", "temperatureSensorItem activated");

		}

		if (humiditySensor != null){
			humiditySensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "activateUnit", "humiditySensorItem activated");
		}
		
		if (ambientLightSensor != null){
			ambientLightSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "activateUnit", "ambientLightSensorItem activated");
		}
		
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.AlertUnit#getAlarmDisplay()
	 */
	@Override
	public BrickletLCD20x4 getAlarmDisplay() {
		return alarmDisplay;
	}

	
	/**
	 * @return
	 */
	public AmbientLightSensorItem getAmbientLightSensorItem() {
		return ambientLightSensorItem;
	}

	
	/**
	 * Checks if the mandatory BrickLets are available
	 */
	private void checkRequiredTfStackBrickLets(){
		boolean missing = false;
		
		if (tfStack.getAlarmMaster() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmMaster required");
			missing = true;
		}
		
		if (tfStack.getAlarmSignal() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmSignal required");
			missing = true;
		}

		if (tfStack.getAlarmDisplay() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmDisplay required");
			missing = true;
		}

		if (missing){
			throw new IllegalArgumentException("See technical log for missing BrickLets");
		}
	}
	

	/**
	 *  Handles the first or reconnect
	 */
	private void handleConnect(boolean firstConnect){
		checkRequiredTfStackBrickLets();
		
		alarmMaster = tfStack.getAlarmMaster();
		alarmSignal = tfStack.getAlarmSignal();
		temperatureSensor = tfStack.getTemperatureSensor();
		humiditySensor = tfStack.getHumiditySensor();
		ambientLightSensor = tfStack.getAmbientLightSensor();
		rotaryEncoderSensor = tfStack.getRotaryEncoderSensor();
		multiTouchSensor = tfStack.getMultiTouchSensor();
		
		if (firstConnect){
			// First Connect
			alarmDisplay = tfStack.getAlarmDisplay();
		} else {
			// Reconnect: Replace LCD
			if (operateLcdAsMaster){
				// Replace Master LCD
				alarmDisplay = tfStack.getAlarmDisplay();
				ScreenControllerImpl.getInstance().replaceLcd(tfStack.getAlarmDisplay());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "master lcd replaced for screen controller ");
				
			} else {
				// Replace Clone LCD
				ScreenControllerImpl.getInstance().replaceCloneLcd(alarmDisplay, tfStack.getAlarmDisplay());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "clone lcd replaced for screen controller ");
				alarmDisplay = tfStack.getAlarmDisplay();
			}
		}
		
		
		// Install Reset Taster
		new ResetSwitchItemImpl(cfgAlertUnit.getUnitName(), alarmSignal, 500l, (short)3, true);

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

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), 
				"handleConnect", "alarmSignal dependant objects created: ResetSwitchItemImpl, ledItem, beeperItem)");
		

		// tp: Temperature Sensor
		if (temperatureSensor != null){
			storePreviousInstance(temperatureSensorItem);
			temperatureSensorItem = new TemperatureSensorItemImpl(this, temperatureSensor, temperatureSensorCallback);
			temperatureSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "temperatureSensorItem created");
			if (replacePreviousInstance(temperatureSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "temperatureSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "temperatureSensorItem NOT created");
		}
		
		// hm: Humidity Sensor
		if (humiditySensor != null){
			storePreviousInstance(humiditySensorItem);
			humiditySensorItem = new HumiditySensorItemImpl(this, humiditySensor, humiditySensorCallback);
			humiditySensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "humiditySensorItem created");
			if (replacePreviousInstance(humiditySensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "humiditySensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "humiditySensorItem NOT created");
		}
		
		// al: AmbientLight Sensor
		if (ambientLightSensor != null){
			storePreviousInstance(ambientLightSensorItem);
			ambientLightSensorItem = new AmbientLightSensorItemImpl(this, ambientLightSensor,cfgAlertUnit.getAlThreshold(), ambientLightSensorCallback);
			ambientLightSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "ambientLightSensorItem created");
			if (replacePreviousInstance(ambientLightSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "ambientLightSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "ambientLightSensorItem NOT created");
		}

		
		// Rotary Encoder  Sensor / to transfer to Screen Controller only
		if (rotaryEncoderSensor != null){
			if (ScreenControllerImpl.getInstance() != null){
				ScreenControllerImpl.getInstance().replaceRotaryEncoder(rotaryEncoderSensor);
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "rotaryEncoderSensor replaced (ScreenController)");
			} 
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "rotaryEncoderSensor NOT created");
		}

		
		// Rotary Encoder  Sensor / to transfer to Screen Controller only
		if (multiTouchSensor != null){
			if (ScreenControllerImpl.getInstance() != null){
				ScreenControllerImpl.getInstance().replaceMultiTouch(multiTouchSensor);
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "multiTouchSensor replaced (ScreenController)");
			} 
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "multiTouchSensor NOT created");
		}

		
		// Set this alarmDisplay as the ALERT alarmDisplay  
		AlertControllerImpl.getInstance().setAlarmDisplay(alarmDisplay);
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "Set this alarmDisplay as the ALERT alarmDisplay");
		

		reset();
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "Reset of alert beeper and alert LED");
		
		if (unitActivated){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "'activateUnit() triggered");
			activateUnit();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "handleConnect", "'activateUnit() NOT triggered");
		}
		
		
	}
	
	
	/**
	 * Logs Wifi connection parameter 
	 */
	private void logConnect(){
		try {
			if (tfStack.getAlarmMaster().isWifiPresent()){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "AlertUnitMobileImpl", " Wifi Key="+tfStack.getAlarmMaster().getLongWifiKey());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "AlertUnitMobileImpl", " Wifi BufferInfo="+tfStack.getAlarmMaster().getWifiBufferInfo());
			}
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "AlertUnitMobileImpl", "Host="+tfConnectService.getHost()+" Port="+tfConnectService.getPort()+" IP="+tfConnectService.getIP());
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStackCallbackApp#tfStackReConnected()
	 */
	@Override
	public synchronized void tfStackReConnected() {
		if (firstConnect){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "TfStack firstly connected");
			logConnect();
			handleConnect(firstConnect);
//			triggerAutoReconnect();
			firstConnect = false;
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "TfStack reconnected");
			handleConnect(firstConnect);
			
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				ledItem.switchON();
			}

		}

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "Connection timeout="+tfConnectService.getIpcon().getTimeout());
		connected = true;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStackCallbackApp#tfStackDisconnected()
	 */
	@Override
	public synchronized void tfStackDisconnected() {
		connected = false;
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "tfStackReConnected", "TfStack disconnected");
	}
	

	/**
	 * Test: Triggers "Auto reconnect" for a stack
	 */
	@SuppressWarnings("unused")
	private void triggerAutoReconnect(){
		new CommonObserverImpl(this, 1, 90000,"AutoReconnect").startObservation();
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "commonObserverTriggeredMethod", "===> ATTENTION: Test Mode: AutoReconnect triggered by CommonObserver after 90 seconds <===");
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CommonCallback#commonObserverTriggeredMethod(java.lang.Integer)
	 */
	@Override
	public void commonObserverTriggeredMethod(Integer functionCode) {
		switch (functionCode) {
		case 1:
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgAlertUnit.getUnitName(), "commonObserverTriggeredMethod", "===> ATTENTION: Test Mode: AutoReconnect will be executed now <===");
			tfStack.connected(IPConnection.CONNECT_REASON_AUTO_RECONNECT);
			break;

		default:
			break;
		}
	}


	public BrickletRotaryEncoder getRotaryEncoderSensor() {
		return rotaryEncoderSensor;
	}


	public BrickletMultiTouch getMultiTouchSensor() {
		return multiTouchSensor;
	}


	@Override
	public String getTfStackName() {
		return getUnitName();
	}

}
