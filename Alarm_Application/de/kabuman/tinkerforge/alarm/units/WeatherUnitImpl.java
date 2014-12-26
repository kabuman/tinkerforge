package de.kabuman.tinkerforge.alarm.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserverImpl;
import de.kabuman.common.services.InetService;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.config.CfgWeatherUnit;
import de.kabuman.tinkerforge.alarm.items.digital.input.BarometerSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.BarometerSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;

/**
 * Implementation of "Weather Unit"
 * - BrickMaster
 * - Wifi Brick Extension
 */
public class WeatherUnitImpl extends AbstractUnit 
	implements WeatherUnit, TfStackCallbackApp, CommonCallback {

	IPConnection ipcon;
	
	
	// Devices
	private BrickMaster alarmMaster;

	// Temperature Sensor
	private BrickletTemperature temperatureSensor = null;
	private TemperatureSensorItem temperatureSensorItem = null;
	private long temperatureSensorCallback = 1000;
	
	// Humidity Sensor
	private BrickletHumidity humiditySensor = null;
	private HumiditySensorItem humiditySensorItem = null;
	private long humiditySensorCallback = 1000;

	// Humidity Sensor
	private BrickletBarometer barometerSensor = null;
	private BarometerSensorItem barometerSensorItem = null;
	private long barometerSensorCallback = 1000;

	// Services
	InetService inetService = new InetService();

	// Parameter
	private CfgWeatherUnit cfgWeatherUnit;

    private WeatherUnitTfStackImpl tfStack = null;
    private TfConnectService tfConnectService = null;

	private boolean firstConnect = true;
	private boolean unitActivated = false;
	
	private boolean connected = false;

    
	/**
	 * Constructor
	 * @param cfgWeatherUnit
	 */
	public WeatherUnitImpl(CfgWeatherUnit cfgWeatherUnit){
		this.cfgWeatherUnit = cfgWeatherUnit;
		
	    tfStack = ((WeatherUnitTfStackImpl) new WeatherUnitTfStackImpl(this));
	    
		tfConnectService = new TfConnectService(cfgWeatherUnit.getHost(), cfgWeatherUnit.getPort(), null, tfStack);

	}

	public void reset(){
		deactivateAlert();
	}

	
	
	public BrickMaster getBrickMaster() {
		return alarmMaster;
	}

	public String getUnitName() {
		return cfgWeatherUnit.getUnitName();
	}
	
	public void reconnect(){
	}

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

	@Override
	public void power(boolean switchOn) {
		if (cfgWeatherUnit.getCfgRemoteSwitchData().getSwitchType()<=0){
			// no powerSwitch available
			return;
		}
		
		short switchTo = ( switchOn) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF;

		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(),"power(switchOn)","switchOn="+switchOn);
		RemoteSwitchControllerImpl.getInstance().switchPowerSecurely(
				cfgWeatherUnit.getCfgRemoteSwitchData()
				, switchTo);
	}

	@Override
	public void activateUnit() {
		unitActivated = true;

		if (temperatureSensor != null){
			temperatureSensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "activateUnit", "temperatureSensorItem activated");

		}

		if (humiditySensor != null){
			humiditySensorItem.activateSensor();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "activateUnit", "humiditySensorItem activated");
		}
		
	}

	private synchronized void handleConnect(){
		alarmMaster = tfStack.getAlarmMaster();
		temperatureSensor = tfStack.getTemperatureSensor();
		humiditySensor = tfStack.getHumiditySensor();
		barometerSensor = tfStack.getBarometerSensor();

		// tp: Temperature Sensor
		if (temperatureSensor != null){
			storePreviousInstance(temperatureSensorItem);
			temperatureSensorItem = new TemperatureSensorItemImpl(this, temperatureSensor, temperatureSensorCallback);
			temperatureSensorItem.activateSensor();
			if (replacePreviousInstance(temperatureSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "Temperature Sensor replaced and activated.");
			}
		}
		
		// hm: Humidity Sensor
		if (humiditySensor != null){
			storePreviousInstance(humiditySensorItem);
			humiditySensorItem = new HumiditySensorItemImpl(this, humiditySensor, humiditySensorCallback);
			humiditySensorItem.activateSensor();
			if (replacePreviousInstance(humiditySensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "Humidity Sensor replaced and activated.");
			}
		}
		
		// Barometer Sensor
		if (barometerSensor != null){
			storePreviousInstance(barometerSensorItem);
			barometerSensorItem = new BarometerSensorItemImpl(this, barometerSensor, barometerSensorCallback);
			barometerSensorItem.activateSensor();
			if (replacePreviousInstance(barometerSensorItem)){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "Humidity Sensor replaced and activated.");
			}
		}
		
		
		reset();
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "Reset of alert beeper and alert LED");
		
		if (unitActivated){
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "'activateUnit() triggered");
			activateUnit();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "handleConnect", "'activateUnit() NOT triggered");
		}
	}
	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			firstConnect = false;
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "tfStackReConnected", "TfStack firstly connected");
			handleConnect();
//			triggerAutoReconnect();
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "tfStackReConnected", "TfStack reconnected");
			handleConnect();
			
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "tfStackReConnected", "Connection timeout="+tfConnectService.getIpcon().getTimeout());
		connected = true;
	}

	@Override
	public void tfStackDisconnected() {
		connected = false;
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "tfStackReConnected", "TfStack disconnected");
	}
	

	@SuppressWarnings("unused")
	private void triggerAutoReconnect(){
		new CommonObserverImpl(this, 1, 90000,"AutoReconnect").startObservation();
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "commonObserverTriggeredMethod", "===> ATTENTION: Test Mode: AutoReconnect triggered by CommonObserver after 90 seconds <===");
	}
	
	@Override
	public void commonObserverTriggeredMethod(Integer functionCode) {
		switch (functionCode) {
		case 1:
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgWeatherUnit.getUnitName(), "commonObserverTriggeredMethod", "===> ATTENTION: Test Mode: AutoReconnect will be executed now <===");
			tfStack.connected(IPConnection.CONNECT_REASON_AUTO_RECONNECT);
			break;

		default:
			break;
		}
	}

	@Override
	public void deactivateAlert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateAlert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getLEDItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public BarometerSensorItem getBarometerSensorItem() {
		return barometerSensorItem;
	}

	@Override
	public String getTfStackName() {
		return getUnitName();
	}

}
