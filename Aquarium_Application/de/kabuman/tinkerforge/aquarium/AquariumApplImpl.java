package de.kabuman.tinkerforge.aquarium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.aquarium.config.CfgServo;
import de.kabuman.tinkerforge.aquarium.config.CfgTimer;
import de.kabuman.tinkerforge.aquarium.config.CreateCfgFromInputFile;
import de.kabuman.tinkerforge.aquarium.threads.TimerData;
import de.kabuman.tinkerforge.aquarium.threads.TimerImpl;
import de.kabuman.tinkerforge.aquarium.threads.TimerObserverImpl;
import de.kabuman.tinkerforge.aquarium.units.AquariumUnit;
import de.kabuman.tinkerforge.aquarium.units.AquariumUnitImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.services.config.CfgEmail;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitch;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchController;
import de.kabuman.tinkerforge.services.controller.RemoteSwitchControllerImpl;
import de.kabuman.tinkerforge.services.threads.AliveObserverImpl;

public class AquariumApplImpl implements AquariumAppl{
	
	// Parameter Content
	private String power = null;
	private String path = null;
	private String configFileName = null;
	private String userLogFileName = null;
	private String technicalLogFileName = null;
	
	// Configuration File Content as objects
	CreateCfgFromInputFile cfg;
	
	// Services
	private CfgEmail cfgEmail;
	private CfgRemoteSwitch cfgRemoteSwitch;
	private AliveObserverImpl aliveObserverImpl = null;

	private List<TimerImpl> timerList = new ArrayList<TimerImpl>();


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
	public AquariumApplImpl(String[] args) {
		int argLength = 5;
		if (args.length != argLength){
			throw new IllegalArgumentException("Es werden "+argLength+" Aufrufparameter erwartet. Definiert wurden aber "+args.length);
		}
		
		short argId = 0;
		power = args[argId];
		path = args[++argId];
		configFileName = args[++argId];
		userLogFileName = args[++argId];
		technicalLogFileName = args[++argId];
		
		System.err.println(path + ","+configFileName+","+userLogFileName+","+technicalLogFileName);
		
		cfg = new CreateCfgFromInputFile(path + configFileName);
		
	}


	@Override
	public Exception launcher(boolean restart) {
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
			System.out.println("launcher:: IOException");
			return e1;
		} catch (TimeoutException e) {
			System.out.println("launcher:: TimeoutException");
			return e;
		}
		
		
//		waschkueche.getOpenSensorItem().test();
		
		try {
			writeStartMsgToConsole(restart);
		} catch (TimeoutException e) {
			System.out.println("launcher:: writeStartMsgToConsole(), refreshDisplay(): TimeoutException");
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

		LogControllerImpl.getNewInstance(path, userLogFileName, technicalLogFileName, null, null);
		
		RemoteSwitchController remoteSwitchControllerImpl = RemoteSwitchControllerImpl.getNewInstance(cfgRemoteSwitch);
		
		// 1. Appl Parameter: 1=PowerControlled  0=WithoutPowerControl
		if (power.equals("1") && remoteSwitchControllerImpl.isActive()){
			// Power OFF + ON requested by Parameter
			remoteSwitchControllerImpl.switchPowerSecurely(cfg.getCfgRemoteSwitchData(), BrickletRemoteSwitch.SWITCH_TO_ON);
			remoteSwitchControllerImpl.sleep(30000);
			
		} else {
			// power.equals("0")
//			remoteSwitchControllerImpl.setActive(false);
		}

		
		
		AquariumUnit aquariumUnit = new AquariumUnitImpl(cfg.getCfgUnit());

		// Configure each defined servo: set parameter on BrickServo
		for (CfgServo cfgServo : cfg.getCfgServoList()) {
			BrickServo servo = aquariumUnit.getBrickServo();
			short id = cfgServo.getServoId();
			try {
				servo.setAcceleration(id, cfgServo.getAcceleration());
				servo.setVelocity(id, cfgServo.getVelocity());
				servo.setDegree(id, cfgServo.getDegreeMin(),cfgServo.getDegreeMax());
				servo.setPeriod(id, cfgServo.getPeriod());
				
				// Start Position
				servo.setPosition(id, cfg.findCfgPosition(id, 0));
				LogControllerImpl.getInstance().createTechnicalLogMessage("Servo "+id, "start-pos executed", "Servo-Position="+cfg.findCfgPosition(id, 0));

			} catch (NotConnectedException e) {
				LogControllerImpl.getInstance().createTechnicalLogMessage("Aquarium", "powerUp", "exeception to configure BrickServo="+e.toString());
			}
		}

		short threadId = 0;
		// Create Timer Threads
		for (CfgTimer cfgTimer : cfg.getCfgTimerList()) {
			
			// cfgTimer: record with up to 3 timer entries
			for (short i = 0; i < cfgTimer.getTimerList().size(); i++) {
				
				if (cfgTimer.getTimerList().get(i) != null){
					
					// Create data object for timer
					TimerData timerData = new TimerData(
							threadId++
							, i
							, aquariumUnit.getBrickServo()
							, cfgTimer.getServoId()
							,cfg.findCfgPosition(cfgTimer.getServoId(), i+1)
							,cfgTimer.getTimerList().get(i)
							,cfg.getCfgEmail());
					
					// Create Timer thread
//					TimerImpl timer = new TimerImpl(timerData);
					timerList.add(new TimerImpl(timerData));
				}
				
			}
		}
		
		// Alive Timer Observer: observes the finish of all timers: writes message to log, set position to each servo
		new TimerObserverImpl(timerList, 1000, aquariumUnit.getBrickServo(), cfg.getCfgPositionList());
		
		// Alive BrickServo Observer
		aliveObserverImpl = new AliveObserverImpl(aquariumUnit, cfg.getCfgUnit().getAliveSequence());
		
		
		LogControllerImpl.getInstance().createUserLogMessage("Aquarium","",LogController.MSG_STARTED);
		LogControllerImpl.getInstance().createUserLogMessage(LogController.MSG_INFO,"Programm","Start-Parameter",path + ","+configFileName+","+userLogFileName+","+technicalLogFileName);
	}

	
	/**
	 * Application Start Message
	 * @throws TimeoutException 
	 */
	private void writeStartMsgToConsole(boolean restart) throws TimeoutException{
//		if (cfgAlertUnitRemote!=null && !InetService.isIP(cfgAlertUnitRemote.getHost())){
//			LogControllerImpl.getInstance().createUserLogMessage("Alarmanlage","Verbindung",LogController.MSG_REMOTE);
//		}
		
		
		if (cfg.getCfgEmail().isEmailRequested()){
			LogControllerImpl.getInstance().createUserLogMessage("Aquarium","Email",LogController.MSG_EMAIL_REQUESTED);
		} else {
			LogControllerImpl.getInstance().createUserLogMessage("Aquarium","Email",LogController.MSG_EMAIL_NOT_REQUESTED);
		}

		if (restart){
			LogControllerImpl.getInstance().createUserLogMessage("AU Remote","Restart",LogController.MSG_RESTARTED);
		}
		
		ConnectServiceImpl.getInstance().report();
	}
	


}
