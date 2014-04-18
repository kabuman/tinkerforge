package de.kabuman.tinkerforge.alarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.alarm.config.CfgAlertSignal;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnit;
import de.kabuman.tinkerforge.alarm.config.CfgAlertUnitRemote;
import de.kabuman.tinkerforge.alarm.config.CfgEmail;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.alarm.config.CreateCfgFromInputFile;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;
import de.kabuman.tinkerforge.alarm.threads.AliveObserverImpl;
import de.kabuman.tinkerforge.alarm.units.AlertUnit;
import de.kabuman.tinkerforge.alarm.units.AlertUnitMobileImpl;
import de.kabuman.tinkerforge.alarm.units.AlertUnitRemote;
import de.kabuman.tinkerforge.alarm.units.AlertUnitRemoteImpl;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
import de.kabuman.tinkerforge.alarm.units.ProtectUnitImpl;
import de.kabuman.tinkerforge.alarm.units.Unit;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;

/**
 * Implementation of Alarm Application
 * 
 * Protects by Protect Units (Motion Sensor, Open Sensor, Water Detector)
 * Alerts by Alert Units (Beeper, Led, Display, Email)
 * Controlled by Alert Controller 
 */
public class AlarmApplImpl implements AlarmAppl{
	
	// Parameter Content
	private String path = null;
	private String configFileName = null;
	private String userLogFileName = null;
	private String technicalLogFileName = null;
	
	// Configuration File Content as objects
	private CfgAlertSignal cfgAlertSignal;
	private CfgAlertUnitRemote cfgAlertUnitRemote;
	private List<CfgAlertUnit> cfgAlertUnitList = new ArrayList<CfgAlertUnit>();
	private List<CfgProtectUnit> cfgProtectUnitList = new ArrayList<CfgProtectUnit>();
	private CfgEmail cfgEmail;
	

	// Services
	private AliveObserverImpl aliveObserverImpl = null;
	
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
		if (args.length !=4){
			throw new IllegalArgumentException("Es werden 4 Aufrufparameter erwartet. Definiert wurden aber "+args.length);
		}
		
		path = args[0];
		configFileName = args[1];
		userLogFileName = args[2];
		technicalLogFileName = args[3];
		
		System.err.println(path + ","+configFileName+","+userLogFileName+","+technicalLogFileName);
		
		CreateCfgFromInputFile createCfgFromInputFile = new CreateCfgFromInputFile(path + configFileName);
		cfgAlertUnitList = createCfgFromInputFile.getCfgAlertUnitList();
		cfgAlertUnitRemote = createCfgFromInputFile.getCfgAlertUnitRemote();
		cfgProtectUnitList = createCfgFromInputFile.getCfgProtectUnitList();
		cfgEmail = createCfgFromInputFile.getCfgEmail();
		cfgAlertSignal = createCfgFromInputFile.getCfgAlertSignal(); 
		
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
		
		if (restart){
			aliveObserverImpl.deactivate();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
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

		
		LogControllerImpl.getNewInstance(path, userLogFileName, technicalLogFileName);
 

		AlertControllerImpl.getNewInstance(cfgAlertSignal.isQuiet(), cfgAlertSignal.getAlertDurationForIntrusion(), cfgAlertSignal.getAlertDurationForWater(), cfgAlertSignal.getAlertDurationForFire(), cfgEmail);

		
		// List of all units
		List<Unit> unitList = new ArrayList<Unit>();

		// Alert Unit "Remote"
		if (cfgAlertUnitRemote!=null){
			AlertUnitRemote alertUnitRemote = new AlertUnitRemoteImpl(cfgAlertUnitRemote);
			unitList.add(alertUnitRemote);
		}

		// Alert Units
		for (CfgAlertUnit cfgAlertUnit : cfgAlertUnitList) {
			AlertUnit alertUnitMobile = new AlertUnitMobileImpl(cfgAlertUnit);
			unitList.add(alertUnitMobile);
		}
		
		// Protect Units
		for (CfgProtectUnit cfgProtectUnit : cfgProtectUnitList) {
			ProtectUnit protectUnit = new ProtectUnitImpl(cfgProtectUnit);
			unitList.add(protectUnit);
		}
		
		// Alert Controller
		AlertControllerImpl.getInstance().setAlertUnitList(unitList);
		
		
		// activate Protect Units
		// Protect Units
		for (Unit unit : unitList) {
			if (unit instanceof ProtectUnit){
				((ProtectUnit) unit).activate();
			}
		}

		// Alive Observer
		aliveObserverImpl = new AliveObserverImpl(unitList, cfgAlertSignal.getAliveSequence());
		
		// Restart & Reset Controller
		ResetControllerImpl.getNewInstance(this, unitList);
		
		LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","",LogController.MSG_STARTED);
		LogControllerImpl.getInstance().createUserLogMessage(LogController.MSG_INFO,"Programm","Start-Parameter",path + ","+configFileName+","+userLogFileName+","+technicalLogFileName);
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
	
}

