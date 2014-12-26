package de.kabuman.tinkerforge.alarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.DateTimeService;
import de.kabuman.common.services.InetService;
import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.common.services.StringService;
import de.kabuman.tinkerforge.alarm.config.CfgAlertSignal;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnitRemote;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.alarm.config.CfgWeatherUnit;
import de.kabuman.tinkerforge.alarm.config.CreateCfgFromInputFile;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;
import de.kabuman.tinkerforge.alarm.threads.TemperatureObserverImpl;
import de.kabuman.tinkerforge.alarm.units.AlertUnit;
import de.kabuman.tinkerforge.alarm.units.AlertUnitMobileImpl;
import de.kabuman.tinkerforge.alarm.units.AlertUnitRemote;
import de.kabuman.tinkerforge.alarm.units.AlertUnitRemoteImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
import de.kabuman.tinkerforge.alarm.units.ProtectUnitImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;
import de.kabuman.tinkerforge.alarm.units.WeatherUnit;
import de.kabuman.tinkerforge.alarm.units.WeatherUnitImpl;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClock;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenHallClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.services.config.CfgEmail;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitch;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchController;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;

/**
 * Implementation of Alarm Application
 * 
 * Protects by Protect Units (Motion Sensor, Open Sensor, Water Detector)
 * Alerts by Alert Units (Beeper, Led, Display, Email)
 * Controlled by Alert Controller 
 */
public class AlarmApplImpl implements AlarmAppl{
	
	// Parameter Content
	private String power = null;
	private String path = null;
	private String configFileName = null;
	private String userLogFileName = null;
	private String technicalLogFileName = null;
	private String temperatureLogFileName = null;
	private String humidityLogFileName = null;
	
	// Configuration File Content as objects
	private CfgAlertSignal cfgAlertSignal;
	private CfgAlertUnitRemote cfgAlertUnitRemote;
	private List<CfgAlertUnit> cfgAlertUnitList = new ArrayList<CfgAlertUnit>();
	private List<CfgProtectUnit> cfgProtectUnitList = new ArrayList<CfgProtectUnit>();
	private List<CfgWeatherUnit> cfgWeatherUnitList = new ArrayList<CfgWeatherUnit>();
	private List<CfgRemoteSwitchData> cfgRemoteSwitchDataList = new ArrayList<CfgRemoteSwitchData>();
	private CfgEmail cfgEmail;
	private CfgRemoteSwitch cfgRemoteSwitch;
	
	private final int SC_TITLE_POS = 3;

	// Services
//	private AliveObserverImpl aliveObserverImpl = null;
	@SuppressWarnings("unused")
	private TemperatureObserverImpl temperatureObserverImpl = null;
	
	/**
	 * Constructor <br>
	 * Expected Parameter:  <br>
	 * - path <br>
	 * - configFileName  <br>
	 * - userLogFileName  <br>
	 * - technicalLogFileName  <br>
	 * 
	 * @param args - main class arguments
	 */
	public AlarmApplImpl(String[] args) {
		if (args.length !=7){
			throw new IllegalArgumentException("Es werden 7 Aufrufparameter erwartet. Definiert wurden aber "+args.length);
		}
		
		power = args[0];
		path = args[1];
		configFileName = args[2];
		userLogFileName = args[3];
		technicalLogFileName = args[4];
		temperatureLogFileName = args[5];
		humidityLogFileName = args[6];
		
		
		System.err.println(path + ","+configFileName+","+userLogFileName+","+technicalLogFileName+","+temperatureLogFileName+","+humidityLogFileName);
		
		CreateCfgFromInputFile createCfgFromInputFile = new CreateCfgFromInputFile(path + configFileName);
		cfgAlertUnitList = createCfgFromInputFile.getCfgAlertUnitList();
		cfgAlertUnitRemote = createCfgFromInputFile.getCfgAlertUnitRemote();
		cfgProtectUnitList = createCfgFromInputFile.getCfgProtectUnitList();
		cfgWeatherUnitList = createCfgFromInputFile.getCfgWeatherUnitList();
		cfgEmail = createCfgFromInputFile.getCfgEmail();
		cfgAlertSignal = createCfgFromInputFile.getCfgAlertSignal(); 
		cfgRemoteSwitch = createCfgFromInputFile.getCfgRemoteSwitch();
		cfgRemoteSwitchDataList = createCfgFromInputFile.getCfgRemoteSwitchDataList();
		
	}

	/**
	 * Launcher  <br>
	 *  <br>
	 * - Instantiates and configures the needed services <br>
	 * - Adds the Listener of events  <br>
	 *  <br>
	 * @return boolean - true: yes exception detected; false if not
	 */
	public Exception launcher(boolean restart){
		
		if (!StopWatchApplService.getInstance().isActive()){
			StopWatchApplService.getInstance().start();
		}
		
//		if (restart){
//			aliveObserverImpl.deactivate();
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	
		// Power up the alert application
		try {
			powerUp(restart);
		} catch (IOException e1) {
			System.out.println("alarmLauncher:: IOException");
			return e1;
		} catch (TimeoutException e) {
			System.out.println("alarmLauncher:: TimeoutException");
			return e;
		}
		
		
//		waschkueche.getOpenSensorItem().test();
		
		try {
			writeStartMsgToConsole(restart);
		} catch (TimeoutException e) {
			System.out.println("alarmLauncher:: writeStartMsgToConsole(), refreshDisplay(): TimeoutException");
			return e;
		}
		
		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// return no exception
		return null;
	}
	
	/**
	 * Creates the units <br>
	 *  <br>
	 * 1) Initializes in this order:  <br>
	 * - Log Controller  <br>
	 * - Alert Controller <br>
	 * - Alert Unit Remote <br>
	 * - Alert Units <br>
	 * - Protect Units <br>
	 * 2) set up the Unit List for the Alert Controller <br>
	 * 3) Activates the alert for all Protect Units <br>
	 * 4) Start of Alive Observer <br>
	 * 5) Start of Restart & Reset Controller <br>
	 *  <br>
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void powerUp(boolean restart) throws IOException, TimeoutException {
		ConnectServiceImpl.getNewInstance();
		
		if (restart){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		LogControllerImpl.getNewInstance(path, userLogFileName, technicalLogFileName, temperatureLogFileName, humidityLogFileName);
 
		RemoteSwitchController remoteSwitchControllerImpl = RemoteSwitchControllerImpl.getNewInstance(cfgRemoteSwitch);
		
		// 1. Appl Parameter: 1=PowerControlled  0=WithoutPowerControl
		if (power.equals("1") && remoteSwitchControllerImpl.isActive()){
			// Power OFF + ON requested by Parameter
			for (CfgRemoteSwitchData cfgRemoteSwitchData : cfgRemoteSwitchDataList) {
				remoteSwitchControllerImpl.switchPowerSecurely(cfgRemoteSwitchData, BrickletRemoteSwitch.SWITCH_TO_ON);
			}
			remoteSwitchControllerImpl.sleep(30000);
			
		} 
		if (power.equals(0)) {
			remoteSwitchControllerImpl.setActive(false);
		}

		AlertControllerImpl.getNewInstance(cfgAlertSignal, cfgEmail);

		
		// List of all units
		List<Unit> unitList = new ArrayList<Unit>();

		// Alert Unit "Remote"
		BrickletLCD20x4 lcdRemote = null;
		if (cfgAlertUnitRemote!=null){
			AlertUnitRemote alertUnitRemote = new AlertUnitRemoteImpl(cfgAlertUnitRemote);
			while (!alertUnitRemote.isConnected()) {
			}
			unitList.add(alertUnitRemote);
			lcdRemote = alertUnitRemote.getAlarmDisplay();
		}

		// Alert Units
		BrickletLCD20x4 lcdMobile = null;
		BrickletRotaryEncoder rotaryEncoderMobile = null;
		BrickletMultiTouch multiTouchMobile = null;
		for (CfgAlertUnit cfgAlertUnit : cfgAlertUnitList) {
			AlertUnit alertUnitMobile = new AlertUnitMobileImpl(cfgAlertUnit, true);
			while (!alertUnitMobile.isConnected()) {
			}
			unitList.add(alertUnitMobile);
			lcdMobile = alertUnitMobile.getAlarmDisplay();
			rotaryEncoderMobile = ((AlertUnitMobileImpl) alertUnitMobile).getRotaryEncoderSensor();
			multiTouchMobile = ((AlertUnitMobileImpl) alertUnitMobile).getMultiTouchSensor();
		}
		
		// Protect Units
		for (CfgProtectUnit cfgProtectUnit : cfgProtectUnitList) {
			ProtectUnit protectUnit = new ProtectUnitImpl(cfgProtectUnit);
			while (!protectUnit.isConnected()) {
			}
			unitList.add(protectUnit);
		}
		
		if (lcdMobile == null){
			throw new IllegalArgumentException("lcdMobile is null");
		}
		if (lcdRemote == null){
			throw new IllegalArgumentException("lcdRemote is null");
		}
			
			
		// Weather Units
		for (CfgWeatherUnit cfgWeatherUnit : cfgWeatherUnitList) {
			WeatherUnit weatherUnit = new WeatherUnitImpl(cfgWeatherUnit);
			while (!weatherUnit.isConnected()) {
			}
			unitList.add(weatherUnit);
		}
		
		// Alert Controller
		AlertControllerImpl.getInstance().setAlertUnitList(unitList);
		
		
		// activate Units
		for (Unit unit : unitList) {
			unit.activateUnit();
		}

//		// Alive Observer
//		aliveObserverImpl = new AliveObserverImpl(unitList, cfgAlertSignal.getAliveSequence());
		
		// Temperature & Humidity Observer
		temperatureObserverImpl = new TemperatureObserverImpl(unitList, 60000);
		
		// Restart & Reset Controller
		ResetControllerImpl.getNewInstance(this, unitList);
		
		// ScreenController
		setUpScreenController(lcdMobile, lcdRemote, rotaryEncoderMobile, multiTouchMobile, unitList);
		
		LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","",LogController.MSG_STARTED);
		LogControllerImpl.getInstance().createUserLogMessage(LogController.MSG_INFO,"Programm","Start-Parameter",path + ","+configFileName+","+userLogFileName+","+technicalLogFileName);
	}

	
	private void setUpScreenController(
			BrickletLCD20x4 lcdMobile
			, BrickletLCD20x4 lcdRemote
			, BrickletRotaryEncoder rotaryEncoderMobile
			, BrickletMultiTouch multiTouchMobile
			, List<Unit> unitList) {
		
		ScreenController sc = ScreenControllerImpl.getNewInstance(
				lcdMobile
				, rotaryEncoderMobile
				, multiTouchMobile);
		
		sc.setCloneLcdList(lcdRemote);

		
		int screenId = -1;
		List<Integer> screenSequence = new ArrayList<Integer>();
		
		// HALL CLOCK
		sc.addItem(new ScreenHallClockImpl(null, ++screenId));
		screenSequence.add(screenId);
		int hallClockScreenId = screenId;

		
		// OVERVIEW Temperatures
		sc = createScreenForOverviewTemperature(unitList, sc, ++screenId, 1);
		screenSequence.add(screenId);
		
		// OVERVIEW Humidities
		sc = createScreenForOverviewTemperature(unitList, sc, ++screenId, 2);
		screenSequence.add(screenId);
		
		
		// STATUS
		sc = createScreenForStatus(sc, null, ++screenId);
		screenSequence.add(screenId);
		int statusScreenId = screenId;
		
		// WEATHER
		for (Unit unit : unitList) {
			if (unit instanceof WeatherUnitImpl){
				sc = createScreenForWeatherUnit(sc, null, unit, ++screenId);
				screenSequence.add(screenId);
			}
		}


		// PROTECT
		for (Unit unit : unitList) {
			if (unit instanceof ProtectUnitImpl){
				sc = createScreenForProtectUnit(sc, null, unit, ++screenId);
				screenSequence.add(screenId);
			}
		}


		// ALERT
		for (Unit unit : unitList) {
			if (unit instanceof AlertUnitRemote){
				sc = createScreenForAlertUnitRemote(sc, null, unit, ++screenId, statusScreenId);
				screenSequence.add(screenId);
			}
			if (unit instanceof AlertUnitMobileImpl){
				sc = createScreenForAlertUnitMobile(sc, null, unit, ++screenId);
				screenSequence.add(screenId);
			}
		}
 
		sc.setScreenSequence(screenSequence);
		sc.activateScreen(hallClockScreenId);

		sc.assignMultiTouchElectrodeToScreenId(0, ScreenController.BACKLIGHT_ON);
		sc.assignMultiTouchElectrodeToScreenId(1, ScreenController.NEXT_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(2, ScreenController.PREV_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(3, ScreenController.DEFAULT_SCREEN);
		sc.assignMultiTouchElectrodeToScreenId(4, ScreenController.BACKLIGHT_OFF);
		sc.assignMultiTouchElectrodeToScreenId(5, ScreenController.BACKLIGHT_ON_TEMP);

//		sc.setDefaultScreenId(0);
//		sc.setAutoSwitchBackToDefaultScreenTime(10000);
		sc.activateAutoSwitchBackToDefaultScreen(true);
		
	}
	
	
	private ScreenController createScreenForStatus(ScreenController sc, BrickletLCD20x4 lcd, int screenId){
		//                          "01234567890123456789"
		sc.addMaskLine(screenId, 0, StringService.create(SC_TITLE_POS," ")+"Status           ");
		sc.addMaskLine(screenId, 1, "Alarm:              ");
		sc.addMaskLine(screenId, 2, "Leise:              ");
		sc.addMaskLine(screenId, 3, "Start:              ");
		
		
		sc.addItem(new ScreenClockImpl(lcd, ScreenClock.FORMAT_TIME_S, screenId, 0, 15));

		return sc;
	}

	private ScreenController createScreenForWeatherUnit(ScreenController sc, BrickletLCD20x4 lcd, Unit unit, int screenId){
		sc.addItem(new ScreenItemImpl(lcd, unit.getUnitName(), 2000, screenId, 0, SC_TITLE_POS));
		sc.addItem(new ScreenClockImpl(lcd, ScreenClock.FORMAT_TIME_S, screenId, 0, 15));
//		sc.addItem(new ScreenItemImpl(lcd, unit.getTemperatureSensorItem(), 2000, screenId, 1, 0, "%5.2f"));
//		sc.addItem(new ScreenItemImpl(lcd, unit.getHumiditySensorItem(), 2000, screenId, 2, 0, "%5.2f"));
		sc.addItem(new ScreenItemImpl(lcd, unit.getTemperatureSensorItem(), 2000, screenId, 1, 0));
		sc.addItem(new ScreenItemImpl(lcd, unit.getHumiditySensorItem(), 2000, screenId, 2, 0));
		WeatherUnitImpl pu = (WeatherUnitImpl)unit;
		if (pu.getBarometerSensorItem() != null){
			sc.addItem(new ScreenItemImpl(lcd, pu.getBarometerSensorItem(), 2000, screenId, 3, 0));
			sc.addMaskLine(screenId, 3, "     mBar");
		}
		
		sc.addMaskLine(screenId, 1, "     C");
		sc.addMaskLine(screenId, 2, "     %");


		return sc;
	}

	private ScreenController createScreenForAlertUnitRemote(ScreenController sc, BrickletLCD20x4 lcd, Unit unit, int screenId, int statusScreenId){
		sc.addItem(new ScreenItemImpl(lcd, unit.getUnitName(), 2000, screenId, 0, SC_TITLE_POS));
		sc.addItem(new ScreenClockImpl(lcd, ScreenClock.FORMAT_TIME_S, screenId, 0, 15));

		
		// Status Screen Items
		ScreenItem onItem = new ScreenItemImpl(lcd,((AlertUnitRemote)unit).getRcOnOffSwitchItemImpl(), 0, statusScreenId, 1, 7, "Ein", "Aus");
		sc.addItem(onItem);

		ScreenItem quietItem = new ScreenItemImpl(lcd,((AlertUnitRemote)unit).getRcQuietSwitchItemImpl(), 0, statusScreenId, 2, 7, "Ja","Nein");
		sc.addItem(quietItem);
		
		ScreenItem startDate = new ScreenItemImpl(lcd,StopWatchApplService.getInstance().getStartDate(), 0, statusScreenId, 3, 7, DateTimeService.DF_DATE_TIME_XS);
		sc.addItem(startDate);

		return sc;
	}

	private ScreenController createScreenForProtectUnit(ScreenController sc, BrickletLCD20x4 lcd, Unit unit, int screenId){
		sc.addItem(new ScreenItemImpl(lcd, unit.getUnitName(), 2000, screenId, 0, SC_TITLE_POS));
		sc.addItem(new ScreenClockImpl(lcd, ScreenClock.FORMAT_TIME_S, screenId, 0, 15));
		
//		sc.addItem(new ScreenItemImpl(lcd, unit.getTemperatureSensorItem(), 2000, screenId, 1, 0, "%5.2f"));
//		sc.addItem(new ScreenItemImpl(lcd, unit.getHumiditySensorItem(), 2000, screenId, 2, 0, "%5.2f"));

		int lineId = 0;
		if (unit.getTemperatureSensorItem() != null){
			++lineId;
			sc.addItem(new ScreenItemImpl(lcd, unit.getTemperatureSensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     C");
		}
		
		if (unit.getHumiditySensorItem() != null){
			++lineId;
			sc.addItem(new ScreenItemImpl(lcd, unit.getHumiditySensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     %");
		}

		ProtectUnit pu = (ProtectUnitImpl)unit;
		if (pu.getWaterSensorItem() != null){
			++lineId;
//			sc.addItem(new ScreenItemImpl(lcd, pu.getWaterSensorItem(), 2000, screenId, lineId, 0, "%5.3f"));
			sc.addItem(new ScreenItemImpl(lcd, pu.getWaterSensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     mV");
		}

		return sc;
	}

	/**
	 * Application Start Message
	 * @throws TimeoutException 
	 */
	private void writeStartMsgToConsole(boolean restart) throws TimeoutException{
		if (cfgAlertUnitRemote!=null && !InetService.isIP(cfgAlertUnitRemote.getHost())){
			LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","Verbindung",LogController.MSG_REMOTE);
		}
		
		
		if (cfgEmail.isEmailRequested()){
			LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","Email",LogController.MSG_EMAIL_REQUESTED);
		} else {
			LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","Email",LogController.MSG_EMAIL_NOT_REQUESTED);
		}

		if (restart){
			LogControllerImpl.getInstance().createUserLogMessage("AU Remote","Restart",LogController.MSG_RESTARTED);
		}
		
		ConnectServiceImpl.getInstance().report();
	}

	private ScreenController createScreenForAlertUnitMobile(ScreenController sc, BrickletLCD20x4 lcd, Unit unit, int screenId){
		sc.addItem(new ScreenItemImpl(lcd, unit.getUnitName(), 2000, screenId, 0, SC_TITLE_POS));
		sc.addItem(new ScreenClockImpl(lcd, ScreenClock.FORMAT_TIME_S, screenId, 0, 15));

		int lineId = 0;
		
		if (unit.getTemperatureSensorItem() != null){
			++lineId;
			sc.addItem(new ScreenItemImpl(lcd, unit.getTemperatureSensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     C");
		}

		if (unit.getHumiditySensorItem() != null){
			++lineId;
			sc.addItem(new ScreenItemImpl(lcd, unit.getHumiditySensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     %");
		}
		
		if (((AlertUnitMobileImpl)unit).getAmbientLightSensorItem() != null){
			++lineId;
			sc.addItem(new ScreenItemImpl(lcd, ((AlertUnitMobileImpl)unit).getAmbientLightSensorItem(), 2000, screenId, lineId, 0));
			sc.addMaskLine(screenId, lineId, "     Lux");
		}
		
		

		return sc;
	}
	
	
	/**
	 * Set up additional screen for temperature overview of all units which implements TemperatureSensorItem
	 * 
	 * @param unitList
	 * @param sc
	 * @param lcd
	 * @param screenId
	 * @return
	 */
	private ScreenController createScreenForOverviewTemperature(List<Unit> unitList, ScreenController sc, int screenId, int source){
		final int sourceTemperature = 1;
		final int sourceHumidity = 2; 
		
		int pNL = 1; 	// position left column: Name
		int pVL = 4; 	// position left column: Value
		
		int pNR = 10; 	// position right column: Name
		int pVR = 13; 	// position right column: Value
		
		int[] lineId = 		{ 1,  1,   1,   1,   2,   2,   2,   2,   3,   3,   3,   3 };
		int[] position = 	{pNL, pVL, pNR, pVR, pNL, pVL, pNR, pVR, pNL, pVL, pNR, pVR};
		
		int numOfValues = -1;
		
		if (lineId.length != position.length){
			throw new IllegalArgumentException("AlarmApplImpl::createScreenForOverviewTemperature: error in position definition");
		}
		
		
		for (Unit unit : unitList) {
			if ((source == sourceTemperature && unit.getTemperatureSensorItem() != null)
					|| (source == sourceHumidity && unit.getHumiditySensorItem() != null)){
				if (numOfValues < lineId.length){  // considering starting array with position 0: {0,..,length-1}
					
					// column name: the last 2 letter from unit name only
					int unp = unit.getUnitName().length() - 2;
					String un = unit.getUnitName().substring(unp);

					// column name
					++numOfValues;
					sc.addItem(new ScreenItemImpl(null, un, 0, screenId, lineId[numOfValues], position[numOfValues]));

					// column value
					++numOfValues;
					if (source == sourceTemperature){
						sc.addItem(new ScreenItemImpl(null, unit.getTemperatureSensorItem(), 2000, screenId, lineId[numOfValues], position[numOfValues]));
					}
					if (source == sourceHumidity){
						sc.addItem(new ScreenItemImpl(null, unit.getHumiditySensorItem(), 2000, screenId, lineId[numOfValues], position[numOfValues]));
					}
				} else {
					throw new IllegalArgumentException("AlarmApplImpl::createScreenForOverviewTemperature: more temperatureSensorItems found than displayable");
				}
			}
		}

		if (source == sourceTemperature){
			sc.addMaskLine(screenId, 0, StringService.create(SC_TITLE_POS," ")+"Temperaturen");
		}
		if (source == sourceHumidity){
			sc.addMaskLine(screenId, 0, StringService.create(SC_TITLE_POS," ")+"Luftfeuchtigkeit");
		}

		return sc;
	}
	
}

