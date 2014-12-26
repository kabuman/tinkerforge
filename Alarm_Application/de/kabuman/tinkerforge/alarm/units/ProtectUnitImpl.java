package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.util.Date;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.ResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionDetectionItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionDetectionItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO4ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.SmokeCurrentSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.SmokeSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.SmokeVoltageSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItemImpl;
import de.kabuman.tinkerforge.alarm.threads.LedObserver;
import de.kabuman.tinkerforge.alarm.threads.LedObserverImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;

public class ProtectUnitImpl extends AbstractUnit implements ProtectUnit, TfStackCallbackApp{
	
	// Tinkerforge Stack & Connection
    private ProtectUnitTfStackImpl tfStack = null;
    private TfConnectService tfConnectService = null;

	// Devices
	private BrickMaster alarmMaster;
	private BrickletIO4 openSensor;
	private BrickletDistanceIR motionSensor = null;
	private BrickletMotionDetector motionDetection = null;
	private BrickletAnalogIn waterSensor = null;
	private BrickletTemperature temperatureSensor = null;
	private BrickletHumidity humiditySensor = null;
	private BrickletVoltageCurrent smokeSensor = null;
	
	// Items
	private MotionSensorItem motionSensorItem = null;
	private MotionDetectionItem motionDetectionItem = null;
	private OpenSensorItem openSensorItem = null; 
	private WaterSensorItem waterSensorItem = null;
	private SmokeSensorItem smokeVoltageSensorItem = null;
	private SmokeSensorItem smokeCurrentSensorItem = null;
	private OutputItem ledItem = null;
	private OutputItem beeperItem = null;

	// Open Sensor, LED, Beeper
	private final long openSensorDebounce = 500l;   // 100 l  (for Long)
	private final short openSensorInterrupt = 0;

	// Motion Sensor
	private final long motionSensorDebounce = MotionSensorItem.DEBOUNCE_PERIOD_STANDARD;		// msec. z.B.: 100l  (100 long)
	
	// Water Sensor
	private final long waterSensorDebounce = 100l;   // 100 l  (for Long)
	
	// Smoke Sensor
	private final long smokeSensorDebounce = SmokeSensorItem.DEBOUNCE_PERIOD_STANDARD;
	
	// Temperature Sensor
	private TemperatureSensorItem temperatureSensorItem = null;
	private long temeperatureSensorCallback = 1000;
	
	// Humidity Sensor
	private HumiditySensorItem humiditySensorItem = null;
	private long humiditySensorCallback = 1000;
	
	// Beeper & LED
	private final short beeperInterrupt = 1;
	private final short ledInterrupt = 2;

	// Reset Switch
	private final short resetSwitchInterrupt = 3;
	private final long resetSwitchDebounce = 100l;   // 100 l  (for Long)
	
	// Parameter
	private CfgProtectUnit cfgProtectUnit;

    // Process vars
	private boolean firstConnect = true;
	private boolean unitActivated = false;
	private boolean connected = false;
	
	
	/**
	 * Instantiates Protect Unit with
	 * - brick master
	 * - wifi brick extension
	 * - bricklet IO4 (Open Sensor, LED, Beeper, Reset Switch)
	 * - bricklet IR (Motion Sensor)
	 * 
	 * @param cfgProtectUnit - configuration data for Protect Unit
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public ProtectUnitImpl(CfgProtectUnit cfgProtectUnit){
		this.cfgProtectUnit = cfgProtectUnit;
		
	    tfStack = ((ProtectUnitTfStackImpl)new ProtectUnitTfStackImpl(this, cfgProtectUnit));
	    
		tfConnectService = new TfConnectService(cfgProtectUnit.getHost(), cfgProtectUnit.getPort(), null, tfStack);
		
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#activate()
	 */
	public void activateUnit(){
		unitActivated = true;
		
		if (motionSensor != null){
			motionSensorItem.activateMotionSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "motionSensorItem activated");
		}
		
		if (motionDetection != null){
			motionDetectionItem.activateMotionDetection();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "motionDetectionItem activated");
		}
		
		if (openSensor != null){
			openSensorItem.activateOpenSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "openSensorItem activated");
		}

		if (temperatureSensor != null){
			temperatureSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "temperatureSensorItem activated");
		}

		if (humiditySensor != null){
			humiditySensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "humiditySensorItem activated");
		}
		
		if (waterSensor != null){
			waterSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "waterSensorItem activated");
		}
		
		if (smokeSensor != null){
			smokeVoltageSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "smokeVoltageSensorItem activated");

			smokeCurrentSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "activateUnit", "smokeCurrentSensorItem activated");
		}
		
		
		// confirm via LED
		int ledSchema = (AlertControllerImpl.getInstance().isOn()) ? LedObserver.LED_ALARM_ON : LedObserver.LED_ALARM_OFF;
		new LedObserverImpl(ledItem,ledSchema, getUnitName());
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#activateAlert(java.lang.String)
	 */
	public void activateAlert(String sensorName, int msgId, short alertType){
		// Trigger ALERT to Controller
		AlertControllerImpl.getInstance().activateAlert(this, sensorName, msgId, alertType);
		
		// Handle local connected alert elements
		if (AlertControllerImpl.getInstance().isOn()){
			// Alarm Application is active (switched ON)
			if (!AlertControllerImpl.getInstance().isQuiet()){
				// Not switched to Quiet
				beeperItem.switchON();
			}
			ledItem.switchON();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#activateAlert()
	 */
	public void activateAlert(){
		beeperItem.switchON();
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#deactivateAlert()
	 */
	public void deactivateAlert(){
		beeperItem.switchOFF();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reset()
	 */
	public void reset(){
		ledItem.switchOFF();
		beeperItem.switchOFF();
		openSensorItem.checkSensorOpened();
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
		return cfgProtectUnit.getUnitName();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#getOpenSensorItem()
	 */
	public OpenSensorItem getOpenSensorItem() {
		return openSensorItem;
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
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getTemperatureSensorItem()
	 */
	public TemperatureSensorItem getTemperatureSensorItem() {
		return temperatureSensorItem;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#getHumiditySensorItem()
	 */
	public HumiditySensorItem getHumiditySensorItem() {
		// Remote Alert has no capacity for a Humidity Sensor
		return humiditySensorItem;
	}

	
	@Override
	public void power(boolean switchOn) {
		if (cfgProtectUnit.getCfgRemoteSwitchData().getSwitchType()<=0){
			// no powerSwitch available
			return;
		}
		
		short switchTo = ( switchOn) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF;

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"power(switchOn)","switchOn="+switchOn);
		RemoteSwitchControllerImpl.getInstance().switchPowerSecurely(
				cfgProtectUnit.getCfgRemoteSwitchData()
				, switchTo);
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#getAlertLEDItem()
	 */
	public OutputItem getLEDItem() {
		return ledItem;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#getWaterSensorItem()
	 */
	public WaterSensorItem getWaterSensorItem() {
		return waterSensorItem;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#getCfgProtectUnit()
	 */
	public CfgProtectUnit getCfgProtectUnit() {
		return cfgProtectUnit;
	}

	
	/**
	 * Checks if all mandary BrickLets are available 
	 */
	private void checkRequiredTfStackBrickLets(){
		boolean missing = false;
		
		if (tfStack.getAlarmMaster() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getAlarmMaster required");
			missing = true;
		}
		
		if (tfStack.getOpenSensor() == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "checkRequiredTfStackBrickLets", "tfStack.getOpenSensor required");
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
		motionDetection = tfStack.getMotionDetection();
		waterSensor = tfStack.getWaterSensor();
		smokeSensor = tfStack.getSmokeSensor();
		motionSensor = tfStack.getMotionSensor();
		temperatureSensor = tfStack.getTemperatureSensor();
		humiditySensor = tfStack.getHumiditySensor();
		openSensor = tfStack.getOpenSensor();

		// OpenSensor and dependants: OpenSensorItem, ResetSwitch, LedItem, BeeperItem
		openSensorItem = new OpenSensorItemImpl(
				this,
				openSensor,
				openSensorDebounce,					// Debounce Period
				openSensorInterrupt);

		new ResetSwitchItemImpl(
				cfgProtectUnit.getUnitName(),
				openSensor,
				resetSwitchDebounce,					// Debounce Period
				resetSwitchInterrupt,
				true);

		ledItem = new OutputIO4ItemImpl(
				openSensor,
				ledInterrupt,
				null);

		beeperItem = new OutputIO4ItemImpl(
				openSensor,
				beeperInterrupt,
				null);

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), 
				"handleConnect", "openSensorItem created (incl. dependant ResetSwitchItemImpl, ledItem, beeperItem)");
		
		
		if (motionSensor != null){
			motionSensorItem = new MotionSensorItemImpl(
					this,
					motionSensor,
					motionSensorDebounce,
					MotionSensorItem.OPTION_SMALLER,
					cfgProtectUnit.getDistance());
			motionSensorItem.activateMotionSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "motionSensorItem created");
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "motionSensorItem NOT created");
		}

		
		// IR Bewegungsmelder
		if (motionDetection != null){
			motionDetectionItem = new MotionDetectionItemImpl(
					this,
					motionDetection);
			motionDetectionItem.activateMotionDetection();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "motionDetectionItem created");
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "motionDetectionItem NOT created");
		}

		// ai: Water Sensor
		if (waterSensor != null){
			storePreviousInstance(waterSensorItem);
			waterSensorItem = new WaterSensorItemImpl(
					this,
					waterSensor,
					waterSensorDebounce,
					WaterSensorItem.OPTION_GREATER,
					cfgProtectUnit.getAiVoltageThreshold(),
					false);
			waterSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "waterSensorItem created");
			if (replacePreviousInstance(waterSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "waterSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "waterSensorItem NOT created");
		}
		
		// vc: Smoke Voltage Sensor (Betriebsspannung vorhanden?)
		if (smokeSensor != null){
			storePreviousInstance(smokeVoltageSensorItem);
			smokeVoltageSensorItem = new SmokeVoltageSensorItemImpl(
					this,
					smokeSensor,
					smokeSensorDebounce,
					SmokeSensorItem.OPTION_SMALLER,
					cfgProtectUnit.getVcVoltageThreshold(),
					false);
			smokeVoltageSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeVoltageSensorItem created");
			if (replacePreviousInstance(waterSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeVoltageSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeVoltageSensorItem NOT created");
		}
		
		// vc: Smoke Current Sensor (Rauchmelder ausgelöst?)
		if (smokeSensor != null){
			storePreviousInstance(smokeCurrentSensorItem);
			smokeCurrentSensorItem = new SmokeCurrentSensorItemImpl(
					this,
					smokeSensor,
					smokeSensorDebounce,
					SmokeSensorItem.OPTION_GREATER,
					cfgProtectUnit.getVcCurrentThresholdAlert(),
					false);
			smokeCurrentSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeCurrentSensorItem created");
			if (replacePreviousInstance(waterSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeCurrentSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "smokeCurrentSensorItem NOT created");
		}
		
		// tp: Temperature Sensor
		if (temperatureSensor != null){
			storePreviousInstance(temperatureSensorItem);
			temperatureSensorItem = new TemperatureSensorItemImpl(this, temperatureSensor, temeperatureSensorCallback);
			temperatureSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "temperatureSensorItem created");
			if (replacePreviousInstance(temperatureSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "temperatureSensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "temperatureSensorItem NOT created");
		}
		
		// hm: Humidity Sensor
		if (humiditySensor != null){
			storePreviousInstance(humiditySensorItem);
			humiditySensorItem = new HumiditySensorItemImpl(this, humiditySensor, humiditySensorCallback);
			humiditySensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "humiditySensorItem created");
			if (replacePreviousInstance(humiditySensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "humiditySensorItem activated and replaced (ScreenController)");
			}
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "humiditySensorItem NOT created");
		}
		

		reset();
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "Reset of alert beeper and alert LED");
		
		if (unitActivated){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "'activateUnit() triggered");
			activateUnit();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "handleConnect", "'activateUnit() NOT triggered");
		}
	}
	
	
	/**
	 * Logs Wifi parameter
	 */
	private void logConnect(){
		try {
			if (tfStack.getAlarmMaster().isWifiPresent()){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "AlertUnitMobileImpl", " Wifi Key="+tfStack.getAlarmMaster().getLongWifiKey());
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "AlertUnitMobileImpl", " Wifi BufferInfo="+tfStack.getAlarmMaster().getWifiBufferInfo());
			}
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "AlertUnitMobileImpl", "Host="+tfConnectService.getHost()+" Port="+tfConnectService.getPort()+" IP="+tfConnectService.getIP());
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.connect.TfStackCallbackApp#tfStackReConnected()
	 */
	@Override
	public synchronized void tfStackReConnected() {
		System.out.println(  FormatterService.getDate(new Date())+":  "+  this.getUnitName()+" connection timeout="+tfConnectService.getIpcon().getTimeout());
		if (firstConnect){
			firstConnect = false;
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "tfStackReConnected", "TfStack firstly connected");
			logConnect();
			handleConnect();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "tfStackReConnected", "TfStack reconnected");
			handleConnect();
			
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				ledItem.switchON();
			}
			
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "tfStackReConnected", "Connection timeout="+tfConnectService.getIpcon().getTimeout());
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
