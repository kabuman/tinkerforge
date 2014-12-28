package de.kabuman.tinkerforge.screencontroller.demo.app;

import de.kabuman.tinkerforge.screencontroller.demo.tfstack.DemoTfStackCloneImpl;
import de.kabuman.tinkerforge.services.connect.TfCloneCallbackApp;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;

/**
 *
 */
public class DemoScreenControllerClientApp  implements TfStackCallbackApp{

	private DemoTfStackCloneImpl demoTfStackClone = null; 
	
	private TfCloneCallbackApp master;
	
	private int cloneId;

	/**
	 */
	public DemoScreenControllerClientApp(
			TfCloneCallbackApp master
			, int cloneId
			, String host
			, int port) {
		this.master = master;
		this.cloneId = cloneId;

		// Define Tinkerforge Stack (Listener)
		demoTfStackClone = new DemoTfStackCloneImpl(this);

		
		//Create connection and add listener
		new TfConnectService(host,port, null, demoTfStackClone);
	}

	
	@Override
	public void tfStackReConnected() {
		master.tfCloneReConnected(cloneId);
	}

	
	@Override
	public void tfStackDisconnected() {
		master.tfCloneDisconnected(cloneId);
	}


	public DemoTfStackCloneImpl getDemoTfStackClone() {
		return demoTfStackClone;
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
