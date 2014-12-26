package de.kabuman.tinkerforge.services.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.InetService;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.services.ConnectServiceImpl;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitch;
import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;

/**
 * Remote Switch Controller to switch on/off Intertech- or Elro- devices
 */
/**
 * @author Karsten Buchmann
 *
 */
public class RemoteSwitchControllerImpl implements RemoteSwitchController {

	IPConnection ipcon;

	private static RemoteSwitchControllerImpl instance = null;

	private CfgRemoteSwitch cfgRemoteSwitch;

	private boolean active = false;

	private boolean connected = false;

	BrickletRemoteSwitch remoteSwitch;

	// Services
	private InetService inetService = new InetService();


	/**
	 * Constructor (private) to instantiate the class
	 * @param cfgRemoteSwitch
	 */
	private RemoteSwitchControllerImpl(CfgRemoteSwitch cfgRemoteSwitch) {
		if (cfgRemoteSwitch != null) {
			this.cfgRemoteSwitch = cfgRemoteSwitch;
			connected = connectBrickLets();
			active = connected;
		}
	}


	/**
	 * Instantiates and returns a new instance of this class 
	 * @param cfgRemoteSwitch
	 * @return instance of RemoteSwitchControllerImpl
	 */
	public static RemoteSwitchControllerImpl getNewInstance(CfgRemoteSwitch cfgRemoteSwitch) {
		instance = new RemoteSwitchControllerImpl(cfgRemoteSwitch);
		return instance;
	}


	/**
	 * Returns the already created instance of this class
	 * @return instance of RemoteSwitchControllerImpl
	 */
	public static RemoteSwitchControllerImpl getInstance() {
		return instance;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController#switchPower(de.kabuman.tinkerforge.alarm.config.
	 * CfgRemoteSwitchData, short)
	 */
	public void switchPowerSecurely(CfgRemoteSwitchData cfgRemoteSwitchData, short switchTo) {
		if (cfgRemoteSwitchData.getSwitchType() <= 0 || !isActive()) {
			return;
		}

		// Firstly: switch off
		switchPower(cfgRemoteSwitchData, BrickletRemoteSwitch.SWITCH_TO_OFF);
		sleep(cfgRemoteSwitch.getSleep());

		// Secondly: switch on if required
		if (switchTo == BrickletRemoteSwitch.SWITCH_TO_ON) {
			switchPower(cfgRemoteSwitchData, BrickletRemoteSwitch.SWITCH_TO_ON);
			sleep(cfgRemoteSwitch.getSleep());
		}

	}


//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController#switchPower(short, long, short, short)
//	 */
//	private void switchPower(short switchType, long systemCode, short deviceCode, short switchTo, long sleep) {
//		if (switchType <= 0 || !isActive()) {
//			return;
//		}
//
//		// Firstly: switch off
//		switcher(switchType, systemCode, deviceCode, BrickletRemoteSwitch.SWITCH_TO_OFF);
//		sleep(sleep);
//
//		// Secondly: switch on if required
//		if (switchTo == BrickletRemoteSwitch.SWITCH_TO_ON) {
//			switcher(switchType, systemCode, deviceCode, BrickletRemoteSwitch.SWITCH_TO_ON);
//			sleep(sleep);
//		}
//	}
//

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController#sleep(long)
	 */
	public void sleep(long msec) {
		if (!isActive()) {
			return;
		}

		try {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"thread will sleep",
					"msec=" + msec);
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Switches according the given switchType
	 * 
	 * @param cfgRemoteSwitchData
	 * @param switchTo
	 */
	public void switchPower(CfgRemoteSwitchData cfgRemoteSwitchData, short switchTo) {

		switch (cfgRemoteSwitchData.getSwitchType()) {
		case SWITCH_TYPE_A:
			switchSocketA(cfgRemoteSwitchData.getSystemCode(), cfgRemoteSwitchData.getDeviceCode(), switchTo);
			break;
		case SWITCH_TYPE_B:
			switchSocketB(cfgRemoteSwitchData.getSystemCode(), cfgRemoteSwitchData.getDeviceCode(), switchTo);
			break;
		case SWITCH_TYPE_C:
			switchSocketC(cfgRemoteSwitchData.getSystemCode(), cfgRemoteSwitchData.getDeviceCode(), switchTo);
			break;
		default:
			System.out.println(new IllegalArgumentException("switcher:: unknown switchType=" + cfgRemoteSwitchData.getSwitchType()));
		}
	}


	/**
	 * Switch for Elro devices
	 * 
	 * @param systemCode - used as houseCode
	 * @param deviceCode - used as receiverCode
	 * @param switchTo
	 */
	private void switchSocketA(long systemCode, short deviceCode, short switchTo) {
		try {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"switchSocketA",
					"houseCode=" + systemCode + " receiverCode=" + deviceCode + " switchTo=" + switchTo);
			remoteSwitch.switchSocketA((short) systemCode, deviceCode, switchTo);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Switch for ... devices
	 * 
	 * @param systemCode
	 * @param deviceCode
	 * @param switchTo
	 */
	private void switchSocketB(long systemCode, short deviceCode, short switchTo) {
		LogControllerImpl.getInstance().createTechnicalLogMessage(
				cfgRemoteSwitch.getUnitName(),
				"switchSocketB",
				"address=" + systemCode + " unit=" + deviceCode + " switchTo=" + switchTo);
		switchSocketC(systemCode, deviceCode, switchTo);
	}


	/**
	 * Switch for Intertech devices
	 * 
	 * @param systemCode
	 * @param deviceCode
	 * @param switchTo
	 */
	private void switchSocketC(long systemCode, short deviceCode, short switchTo) {
		try {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"switchSocketC",
					"systemCode=" + (char) (int) systemCode + " deviceCode=" + deviceCode + " switchTo=" + switchTo);
			char systemCodeChar = (char) (int) systemCode;
			remoteSwitch.switchSocketC(systemCodeChar, deviceCode, switchTo);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Creates Connection and connects Bricks and Bricklets to it
	 * @return boolean - true: if successfully connected / false: if not
	 */
	private boolean connectBrickLets() {
		if (cfgRemoteSwitch.getMb() <= 0) {
			return false;
		}

		LogControllerImpl.getInstance().createTechnicalLogMessage(
				cfgRemoteSwitch.getUnitName(),
				"connectBrickLets",
				"Try to connect to host=" + cfgRemoteSwitch.getHost() + " port=" + cfgRemoteSwitch.getPort());

		// Connection
		try {
			ipcon = ConnectServiceImpl.getInstance().createConnectE(
					inetService.resolveURL(cfgRemoteSwitch.getHost()),
					cfgRemoteSwitch.getPort());
			ipcon.setTimeout(20000);
			ipcon.setAutoReconnect(true);
		} catch (UnknownHostException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"createConnectE: " + e.toString());
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"host=" + cfgRemoteSwitch.getHost() + " inetService: URL=" + inetService.getURL() + " IP="
							+ inetService.getIP());
			return false;
		} catch (AlreadyConnectedException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"createConnectE: " + e.toString());
			return false;
		} catch (IOException e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"createConnectE: " + e.toString());
			return false;
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"createConnectE: " + e.toString());
			return false;
		}
		LogControllerImpl.getInstance().createTechnicalLogMessage(
				cfgRemoteSwitch.getUnitName(),
				"connectBrickLets",
				"Connected to host=" + cfgRemoteSwitch.getHost() + " inetService: URL=" + inetService.getURL() + " IP="
						+ inetService.getIP());

		// Master Brick
		try {
			ConnectServiceImpl.getInstance().createAndConnect(
					ipcon,
					cfgRemoteSwitch.getMb(),
					cfgRemoteSwitch.getUnitName() + ": " + "Master",
					6.5);
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"BrickMaster: " + e.toString());
			return false;
		}

		// Remote Switch Bricklet
		try {
			remoteSwitch = (BrickletRemoteSwitch) ConnectServiceImpl.getInstance().createAndConnect(
					ipcon,
					cfgRemoteSwitch.getRs(),
					cfgRemoteSwitch.getUnitName() + ": RemoteSwitch");
			remoteSwitch.setRepeats(cfgRemoteSwitch.getRepeat());
		} catch (Exception e) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"connectBrickLets",
					"Remote Switch Bricklet failed. Exception=" + e.toString());
			return false;
		}
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kabuman.tinkerforge.alarm.controller.RemoteSwitchController#isActive()
	 */
	public boolean isActive() {
		return active;
	}


	public boolean isConnected() {
		return connected;
	}


	public void setActive(boolean active) {
		if (connected && active) {
			LogControllerImpl.getInstance().createTechnicalLogMessage(
					cfgRemoteSwitch.getUnitName(),
					"setActive(..)",
					"RemoteSwitchController is not connected and therefore can not be set to active.");
			return;
		}

		LogControllerImpl.getInstance().createTechnicalLogMessage(
				cfgRemoteSwitch.getUnitName(),
				"setActive(..)",
				"RemoteSwitchController is set to active="+active);

		this.active = active;
	}

}
