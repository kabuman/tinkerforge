package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController;
import de.kabuman.tinkerforge.alarm.controller.RemoteSwitchControllerImpl;
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
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItemImpl;
import de.kabuman.tinkerforge.alarm.threads.LedObserver;
import de.kabuman.tinkerforge.alarm.threads.LedObserverImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;

public class ProtectUnitImpl implements ProtectUnit{
	IPConnection ipcon;
	
	// Unit
	private BrickMaster alarmMaster;
	
	// Items
	private MotionSensorItem motionSensorItem = null;
	private MotionDetectionItem motionDetectionItem = null;
	private OpenSensorItem openSensorItem = null; 
	private WaterSensorItem waterSensorItem = null;
	private OutputItem ledItem = null;
	private OutputItem beeperItem = null;

	// Open Sensor, LED, Beeper
	private BrickletIO4 openSensor;
	private final long openSensorDebounce = 500l;   // 100 l  (for Long)
	private final short openSensorInterrupt = 0;

	// Motion Sensor
	private BrickletDistanceIR motionSensor = null;
	private final long motionSensorDebounce = MotionSensorItem.DEBOUNCE_PERIOD_STANDARD;		// msec. z.B.: 100l  (100 long)
	
	// IR Bewegungsmelder
	// Motion Sensor
	private BrickletMotionDetector motionDetection = null;
	
	// Water Sensor
	private BrickletAnalogIn waterSensor = null;
	private final long waterSensorDebounce = 100l;   // 100 l  (for Long)
	
	// Temperature Sensor
	private BrickletTemperature temperatureSensor = null;
	private TemperatureSensorItem temperatureSensorItem = null;
	private long temeperatureSensorCallback = 1000;
	
	// Humidity Sensor
	private BrickletHumidity humiditySensor = null;
	private HumiditySensorItem humiditySensorItem = null;
	private long humiditySensorCallback = 1000;
	
	// Beeper & LED
	private final short beeperInterrupt = 1;
	private final short ledInterrupt = 2;

	// Reset Switch
	private final short resetSwitchInterrupt = 3;
	private final long resetSwitchDebounce = 100l;   // 100 l  (for Long)
	
	// Services
	private InetService inetService = new InetService();
	private RemoteSwitchController remoteSwitchController = RemoteSwitchControllerImpl.getInstance();
	
	// Parameter
	private CfgProtectUnit cfgProtectUnit;
	
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
		 
		connect();
	}

	/**
	 * Creates Connection and connects Bricks and Bricklets to it
	 * 
	 * @return boolean - true: if successfully connected / false: if not
	 */
	private boolean connectBrickLets(){
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Try to connect to host="+cfgProtectUnit.getHost()+" port="+cfgProtectUnit.getPort());

		// Connection
		try {
			ipcon = ConnectServiceImpl.getInstance().createConnectE(inetService.resolveURL(cfgProtectUnit.getHost()), cfgProtectUnit.getPort());
			ipcon.setTimeout(20000);
			ipcon.setAutoReconnect(true);
		} catch (UnknownHostException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","host="+cfgProtectUnit.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
			return false;
		} catch (AlreadyConnectedException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
		} catch (IOException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			return false;
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","createConnectE: " +e.toString());
			return false;
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","Connected to host="+cfgProtectUnit.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
		
		
		// Master Brick
		try {
			alarmMaster = (BrickMaster) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getMb(), cfgProtectUnit.getUnitName()+": "+"Master", 6.5);
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"connectBrickLets","BrickMaster: " +e.toString());
			return false;
		}

		
		// Distance Infrarot: Motion Sensor
		if (cfgProtectUnit.getIr() != null && cfgProtectUnit.getIr() > 0){
			try {
				motionSensor = (BrickletDistanceIR) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getIr(), cfgProtectUnit.getUnitName()+": Bewegungssensor");
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Motion Sensor failed. Exception="+e.toString());
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * Installs  <br>
	 * - the motion sensor  (if BrickletDistanceIR exists) <br>
	 * - the open sensor  	(if BrickletIO4 exists)<br>
	 * - the water sensor  	(if BrickletAnalogIn exits)<br>
	 * - the LED  			(if BrickletIO4 exits)  <br>
	 * - the Reset Switch 	(if BrickletIO4 exits)  <br>
	 * - the Alert Beeper 	(if BrickletIO4 exits)  <br>
	 * based on the already connected  Bricklets 
	 */
	private void connectSensors(){
		if (motionSensor != null){
			motionSensorItem = new MotionSensorItemImpl(
					this,
					motionSensor,
					motionSensorDebounce,
					MotionSensorItem.OPTION_SMALLER,
					cfgProtectUnit.getDistance());
		}
		
		// IO4: Open Sensor, LED, Beeper, Reset Switch
		if (cfgProtectUnit.getIo() > 0){
			try {
				openSensor = (BrickletIO4) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getIo(), cfgProtectUnit.getUnitName()+": Kontaktsensor");

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
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "connectBrickLets", "Open Sensor failed. Timeout");
			}
		}
		
		// IR Bewegungsmelder
		if (cfgProtectUnit.getMd() > 0){
			try {
				motionDetection = (BrickletMotionDetector) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getMd(), cfgProtectUnit.getUnitName()+": IR Bewegungsmelder");
				motionDetectionItem = new MotionDetectionItemImpl(
						this,
						motionDetection);
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Water Sensor failed. Timeout");
			}
		}

		// ai: Water Sensor
		if (cfgProtectUnit.getAi() > 0){
			try {
				waterSensor = (BrickletAnalogIn) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getAi(), cfgProtectUnit.getUnitName()+": Wassersensor");

				waterSensorItem = new WaterSensorItemImpl(
						this,
						waterSensor,
						waterSensorDebounce,
						WaterSensorItem.OPTION_GREATER,
						cfgProtectUnit.getAiVoltageThreshold(),
						true);
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Water Sensor failed. Timeout");
			}
		}
		
		// tp: Temperature Sensor
		if (cfgProtectUnit.getTp() > 0){
			try {
				temperatureSensor = (BrickletTemperature) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getTp(),cfgProtectUnit.getUnitName());
				
				temperatureSensorItem = new TemperatureSensorItemImpl(this, temperatureSensor, temeperatureSensorCallback);
				
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Temperature Sensor failed. Timeout");
			}
		}
		
		// hm: Humidity Sensor
		if (cfgProtectUnit.getHm() > 0){
			try {
				humiditySensor = (BrickletHumidity) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getHm(),cfgProtectUnit.getUnitName());
				
				humiditySensorItem = new HumiditySensorItemImpl(this, humiditySensor, humiditySensorCallback);
				
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Humidity Sensor failed. Timeout");
			}
		}
		
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "successful");
	}
	
	/**
	 * Connects the whole Protect Unit
	 * 
	 * @return boolean - true: if successfully connected / false: if not
	 */
	private synchronized boolean  connect(){
		if (connectBrickLets()){
			connectSensors();
			reset();
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#activate()
	 */
	public synchronized void activateUnit(){
		if (motionSensor != null){
			motionSensorItem.activateMotionSensor();
		}
		
		if (motionDetection != null){
			motionDetectionItem.activateMotionDetection();
		}
		
		if (openSensor != null){
			openSensorItem.activateOpenSensor();
		}

		if (temperatureSensor != null){
			temperatureSensorItem.activateSensor();
		}

		if (humiditySensor != null){
			humiditySensorItem.activateSensor();
		}
		
		if (AlertControllerImpl.getInstance().isOn()){
			
		} else {
			
		}
		
		// confirm via LED
		int ledSchema = (AlertControllerImpl.getInstance().isOn()) ? LedObserver.LED_ALARM_ON : LedObserver.LED_ALARM_OFF;
		new LedObserverImpl(ledItem,ledSchema, getUnitName());
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#activateAlert(java.lang.String)
	 */
	public synchronized void activateAlert(String sensorName, int msgId, short alertType){
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
		power(true);
		remoteSwitchController.sleep(30000);
		
		try {
			openSensorItem.removeListener();
		} catch (Exception e) {
		}
		
		// TODO - Implement removeListener
//		motionSensorItem.removeListener();
		
		try {
			ipcon.disconnect();
		} catch (Exception e1) {
		}
		
		if (getBrickMaster() != null){
			try {
				getBrickMaster().reset();
			} catch (Exception e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"reconnect","BrickMaster: " +e.toString());
			}
		}
		
		if (connectBrickLets()){
			connectSensors();
			activateUnit();
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				ledItem.switchON();
			}
			LogControllerImpl.getInstance().createUserLogMessage(cfgProtectUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECTED);
		} else {
			alarmMaster = null;
			LogControllerImpl.getInstance().createUserLogMessage(cfgProtectUnit.getUnitName(),"Reconnect",LogController.MSG_UNIT_RECONNECT_FAILED);
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#isConnected()
	 */
	public boolean isConnected() {
		if (alarmMaster == null){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"isConnected()","alarmMaster = null detected. alarmMaster="+alarmMaster);
			return false;
		}
		
		try {
			alarmMaster.getWifiStatus();
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"isConnected()","alarmMaster.getWifiStatus(): " +e.toString());
			return false;
		}

		try {
			if (openSensor.getInterrupt() == 0){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"isConnected()","openSensor.getInterrupt() = 0 detected");
				return false;
			}
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"isConnected()","alarmSignal.getPortInterrupt('a'): " +e.toString());
		}

		return true;
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
		remoteSwitchController.switchPowerSecurely(
				cfgProtectUnit.getCfgRemoteSwitchData()
				, switchTo);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.ProtectUnit#getAlertLEDItem()
	 */
	public OutputItem getLEDItem() {
		return ledItem;
	}

	public WaterSensorItem getWaterSensorItem() {
		return waterSensorItem;
	}


}
