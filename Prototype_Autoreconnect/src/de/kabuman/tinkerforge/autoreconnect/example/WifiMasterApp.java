package de.kabuman.tinkerforge.autoreconnect.example;
import de.kabuman.tinkerforge.alarm.units.AbstractUnit;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStack;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class WifiMasterApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "Tf-AU-AG-WE5";
    private static final int port = 4223;
    private WifiMasterTfStackImpl wifiMasterTfStack = null;
    
	/**
	 * @param args
	 */
	public WifiMasterApp()  {
		
		wifiMasterTfStack = new WifiMasterTfStackImpl(this);
		new TfConnectService(host, port, null, wifiMasterTfStack);
		
		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+wifiMasterTfStack.isConnected()+") ***");
	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+wifiMasterTfStack.isConnected()+") ***");
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
