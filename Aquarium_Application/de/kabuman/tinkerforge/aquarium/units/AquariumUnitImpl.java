package de.kabuman.tinkerforge.aquarium.units;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.InetService;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.aquarium.config.CfgUnit;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.threads.AliveObservable;

/**
 * Implements the Alert Unit Remote
 */
public class AquariumUnitImpl implements AquariumUnit, AliveObservable{

	IPConnection ipcon;

	// Devices
	private BrickMaster master;
	private BrickServo servo;

	
	private InetService inetService = new InetService();
	
	// Parameter: Configuration Data
	private CfgUnit cfgUnit;
	
	boolean reconnectRunning = false;

	/**
	 * Constructor
	 * @param cfgUnit - the configuration data for this Alert Unit Remote
	 */
	public AquariumUnitImpl(CfgUnit cfgUnit){
		this.cfgUnit = cfgUnit;
		connect();
	}

	private boolean connectBrickLets(){
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(), "connectBrickLets", "Try to connect to host="+cfgUnit.getHost()+" port="+cfgUnit.getPort());

		try {
			ipcon = ConnectServiceImpl.getInstance().createConnectE(inetService.resolveURL(cfgUnit.getHost()), cfgUnit.getPort());
			ipcon.setTimeout(5000);
			ipcon.setAutoReconnect(true);
			servo = (BrickServo) ConnectServiceImpl.getInstance().createAndConnect(ipcon, cfgUnit.getSv(), cfgUnit.getUnitName());
			servo.getChipTemperature();
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"connectBrickLets","successful: host="+cfgUnit.getHost()+" inetService: URL=" + inetService.getURL()+" IP="+inetService.getIP());
			return true;
		} catch (Exception  e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"connectBrickLets","not successful: " +e.toString());
			return false;
		}
	}
	
	private synchronized boolean connect(){
		if (connectBrickLets()){
			return true;
		} else {
			return false;
		}
	}

	public BrickMaster getBrickMaster() {
		return master;
	}

	public BrickServo getBrickServo() {
		return servo;
	}

	public String getUnitName() {
		return cfgUnit.getUnitName();
	}

	public void reconnect() {
		LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"reconnect","requested"	);
		reconnectRunning = true;

		try {
			ipcon.disconnect();
		} catch (Exception e1) {
		}
		
		connectBrickLets();
	}

	public boolean isConnected() {

		try {
			servo.getChipTemperature();
			if (reconnectRunning){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"reconnect","succesful finished");
				reconnectRunning = false;
			}
			return true;
		} catch (Exception e) {
			if (reconnectRunning){
				LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"reconnect","failed");
				reconnectRunning = false;
			}
			LogControllerImpl.getInstance().createTechnicalLogMessage(cfgUnit.getUnitName(),"isConnected()","exeption occurred: " +e.toString());
			return false;
		}
	}

}
