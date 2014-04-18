package de.kabuman.tinkerforge.trafocontroller.services;

import com.tinkerforge.*;

import de.kabuman.common.services.InetService;
import de.kabuman.tinkerforge.services.*;

import java.io.IOException;

// Referenced classes of package de.kabuman.tinkerforge.trafocontroller.services:
//            TrafoControllerService, TrafoServiceImpl, TrafoService

/**
 * Creates@author Karsten Buchmann
 *
 */
public class TrafoControllerServiceImpl implements TrafoControllerService {
	
	IPConnection ipcon;
	
	// Devices
	private BrickMaster brickMaster;
	private BrickletRotaryPoti lok1PowerControl;
	private BrickDC lok1PowerSource;
	private BrickletRotaryPoti lok2PowerControl;
	private BrickDC lok2PowerSource;
	private BrickletIO4 modeSwitch;

	
	private VehicleService vehicleService;
	private StackService stackService;
	private TrafoService trafoService;
	private InetService inetService = new InetService();

	public TrafoControllerServiceImpl()
			throws IOException, AlreadyConnectedException, TimeoutException {
		
		ipcon = ConnectServiceImpl.getNewInstance().createConnectE(inetService.resolveURL("Tf-Fleischmann"), 4223);
		ipcon.setTimeout(20000);
		ipcon.setAutoReconnect(true);

		brickMaster = (BrickMaster) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.MB6, "TrafoController Master"); 						
		lok1PowerControl = (BrickletRotaryPoti) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.RP4, "TrafoController RotaryPoti Lok1 Power"); 	
		lok1PowerSource = (BrickDC) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.DC1, "TrafoController DC Lok1 Control"); 				
		lok2PowerControl = (BrickletRotaryPoti) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.RP7, "TrafoController RotaryPoti Lok2 Power");
		lok2PowerSource = (BrickDC) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.DC4, "TrafoController DC Lok2 Control");					
		modeSwitch = (BrickletIO4) ConnectServiceImpl.getInstance().createAndConnect(ipcon, DeviceIdentifier.IO41, "TrafoController IO modeSwitch");					
		
//		vehicleService = new VehicleServiceImpl();
//		stackService = new StackServiceImpl(brickMaster);
		
		trafoService = new TrafoServiceImpl(
				lok1PowerControl, 
				lok1PowerSource,
				lok2PowerControl, 
				lok2PowerSource, 
				modeSwitch);
	}

	public TrafoService getTrafoService() {
		return trafoService;
	}

	public StackService getStackService() {
		return stackService;
	}

	public VehicleService getVehicleService() {
		return vehicleService;
	}

	public BrickDC getLok1PowerSource() {
		return lok1PowerSource;
	}

	public BrickDC getLok2PowerSource() {
		return lok2PowerSource;
	}

	public BrickletRotaryPoti getLok1PowerControl() {
		return lok1PowerControl;
	}

	public BrickletRotaryPoti getLok2PowerControl() {
		return lok2PowerControl;
	}

}
