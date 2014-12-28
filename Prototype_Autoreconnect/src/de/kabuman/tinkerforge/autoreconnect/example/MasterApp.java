package de.kabuman.tinkerforge.autoreconnect.example;
import java.util.Date;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


public class MasterApp implements TfStackCallbackApp{
    private static final String host = "localhost";
    private static final int port = 4223;
    private MasterTfStackImpl masterTfStack = null;
    
    
	/**
	 * @param args
	 */
	public MasterApp()  {
		
		masterTfStack = new MasterTfStackImpl(this);
		new TfConnectService(host, port, null, masterTfStack);
		
		while (true) {
			try {
				Thread.sleep(2000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (masterTfStack.isConnected()){
				try {
					System.out.println(FormatterService.getDateHHMMSSS(new Date())+"  Temperatur = "+masterTfStack.getTfMaster().getChipTemperature());
				} catch (TimeoutException | NotConnectedException e) {
				}
			}
			
		}

//		// Keep the listener alive 
//		try {
//			Thread.currentThread().join();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
	}


	@Override
	public void tfStackReConnected() {
		System.out.println("*** Stack connected ("+masterTfStack.isConnected()+") ***");

		try {
			System.out.println("Temperatur = "+ masterTfStack.getTfMaster().getChipTemperature());
		} catch (TimeoutException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ("+masterTfStack.isConnected()+") ***");
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
