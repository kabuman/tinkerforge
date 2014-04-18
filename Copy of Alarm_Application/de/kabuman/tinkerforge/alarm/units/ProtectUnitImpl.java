package de.kabuman.tinkerforge.alarm.units;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.ResetSwitchItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.MotionSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO4ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.output.WaterSensorItemImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;

public class ProtectUnitImpl implements ProtectUnit{
	IPConnection ipcon;
	
	// Unit
	private BrickMaster alarmMaster;
	
	// Items
	private MotionSensorItem motionSensorItem = null; 
	private OpenSensorItem openSensorItem = null; 
	private OutputItem alertLEDItem = null;
	private OutputItem alertBeeperItem = null;

	// Open Sensor, LED, Beeper
	private BrickletIO4 openSensor;
	private final long openSensorDebounce = 500l;   // 100 l  (for Long)
	private final short openSensorInterrupt = 0;
	
	// Motion Sensor
	private BrickletDistanceIR motionSensor; 
	private final long motionSensorDebounce = MotionSensorItem.DEBOUNCE_PERIOD_STANDARD;		// msec. z.B.: 100l  (100 long)
	
	// Water Sensor
	private BrickletAnalogIn waterSensor;
	private final long waterSensorDebounce = 100l;   // 100 l  (for Long)
	private final short waterSensorThreshold = 100;   // milli voltage
	
	// Beeper & LED
	private final short beeperInterrupt = 1;
	private final short ledInterrupt = 2;

	// Reset Switch
	private final short resetSwitchInterrupt = 3;
	private final long resetSwitchDebounce = 100l;   // 100 l  (for Long)
	
	// Services
	private InetService inetService = new InetService();
	
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
		if (cfgProtectUnit.getIr() != null && cfgProtectUnit.getIr() != 0){
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
		if (cfgProtectUnit.getIo() != 0){
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

				alertLEDItem = new OutputIO4ItemImpl(
						openSensor,
						ledInterrupt,
						null);

				alertBeeperItem = new OutputIO4ItemImpl(
						openSensor,
						beeperInterrupt,
						null);
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Open Sensor failed. Timeout");
			}
		}
		
		// ai: Water Sensor
		if (cfgProtectUnit.getAi() != 0){
			try {
				waterSensor = (BrickletAnalogIn) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgProtectUnit.getAi(), cfgProtectUnit.getUnitName()+": Wassersensor");
			
			new WaterSensorItemImpl(
					this,
					waterSensor,
					waterSensorDebounce,
					WaterSensorItem.OPTION_GREATER,
					waterSensorThreshold,
					true);
			} catch (TimeoutException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(), "Connect", "Water Sensor failed. Timeout");
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
	public synchronized void activate(){
		if (motionSensor != null){
			motionSensorItem.activateMotionSensor();
		}
		
		if (openSensor != null){
			openSensorItem.activateOpenSensor();
		}
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
				alertBeeperItem.switchON();
			}
			alertLEDItem.switchON();
		}
	}
	
	public void activateAlert(){
		alertBeeperItem.switchON();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#deactivateAlert()
	 */
	public void deactivateAlert(){
		alertBeeperItem.switchOFF();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.units.Unit#reset()
	 */
	public void reset(){
		alertLEDItem.switchOFF();
		alertBeeperItem.switchOFF();
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
			connect();
			activate();
			if (AlertControllerImpl.getInstance().isAlertOccurred()){
				alertLEDItem.switchON();
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
				return false;
			}
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgProtectUnit.getUnitName(),"isConnected()","alarmSignal.getPortInterrupt('a'): " +e.toString());
		}

		return true;
	}
}
