package de.kabuman.tinkerforge.alarm.units;
import de.kabuman.tinkerforge.alarm.config.CfgProtectUnit;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * Test Application for a ProtectUnit Stack
 */
public class ProtectUnitApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "Tf-PU-OG-HR-WE6";
    private static final int port = 4223;
    private TfStack tfStack;
    private CfgProtectUnit cfgProtectUnit;
    
	/**
	 * @param args
	 */
	public ProtectUnitApp()  {
		
		// Creation of a ProtectUnit specific configuration
		cfgProtectUnit = new CfgProtectUnit(
				null //cfgRemoteSwitchData
				, host
				, port
				, "PU OG-HR" //unitName
				, 1 // mb
				, "Masterbrick" //mbUsedFor
				, 1 // io
				, "Kontaktsensor" //ioUsedFor
				, 0 // ir
				, (short)400 // distance
				, 1 // ai (Wassersensor)
				, (short)600 // aiVoltageThreshold
				, 1 // tp (temperature)
				, 1 // hm (humidity)
				, 0 // md (motion detection)
				, 0 // vc (voltage/current: Rauchmelder)
				, (short)0 // vcVoltageThreshold
				, (short)0 // vcCurrentThresholdOk
				, (short)0 // vcCurrentThresholdAlert
				); 

		// TfStack Implementation
		tfStack = new ProtectUnitTfStackImpl(this, cfgProtectUnit);
	    
		// Connection and Creation of BrickLet Objects
		new TfConnectService(host, port, null, tfStack);
		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+tfStack.isConnected()+") ***");
	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+tfStack.isConnected()+") ***");
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
