package de.kabuman.tinkerforge.services;

import java.io.IOException;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.StopWatchService;

public class HostServiceImpl implements HostService {

	// Devices
	BrickMaster brickMaster;

	// Services
	private StackService stackService;

	// Further Helper
	StopWatchService stopWatchAppl;

	/**
	 * Constructor
	 * 
	 * Instantiates and maps the devices
	 * @param configService - the config Service
	 * @param ipCon - the connection
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public HostServiceImpl() throws TimeoutException, IOException{
		// Devices
		brickMaster =  (BrickMaster)  ConfigServiceImpl.getInstance().createAndConnect(ConfigService.MB1, "Host Master", 0);

		// Services
		stackService = new StackServiceImpl(brickMaster);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.HostService#getStopWatchAppl()
	 */
	public StopWatchService getStopWatchAppl() {
		return stopWatchAppl;
	}

	public StackService getStackService() {
		return stackService;
	}

}
