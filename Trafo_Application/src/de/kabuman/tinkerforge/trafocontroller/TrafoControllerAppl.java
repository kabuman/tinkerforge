package de.kabuman.tinkerforge.trafocontroller;

import java.io.IOException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;
import de.kabuman.tinkerforge.services.StopWatchApplService;
import de.kabuman.tinkerforge.services.StopWatchMotorService;
import de.kabuman.tinkerforge.trafocontroller.services.TrafoControllerService;
import de.kabuman.tinkerforge.trafocontroller.services.TrafoControllerServiceImpl;

public class TrafoControllerAppl {

	StopWatchService stopWatchOFFduration;
	StopWatchService stopWatchONduration;

	TrafoControllerService trafoControllerService;

	final short FROM_POS_OFF = -150;
	final short TO_POS_OFF = -144;
	final short FROM_POS_ON = -145;
	final short TO_POS_ON = -116;

	public TrafoControllerAppl() {
		stopWatchOFFduration = new StopWatchServiceImpl();
		stopWatchONduration = new StopWatchServiceImpl();
	}

	public Exception controllerLauncher(boolean restart) {
		if (!StopWatchApplService.getInstance().isActive())
			StopWatchApplService.getInstance().start();
		
		try {
			configServices();
		} catch (AlreadyConnectedException e) {
			return e;
		} catch (TimeoutException e) {
			return e;
		} catch (IOException e) {
			return e;
		}

		System.out.println("controllerLauncher:: IOException");

		System.out.println("controllerLauncher:: Start");
		
		try {
			writeStartMsgToConsole();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (restart){
			System.out.println("controllerLauncher:: Rstart");
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

	private void configServices() throws IOException, AlreadyConnectedException, TimeoutException {
//		ConfigServiceImpl.getNewInstance();
		
		trafoControllerService = new TrafoControllerServiceImpl();
	}

	private void writeStartMsgToConsole()
			throws TimeoutException {
		System.out
				.println("Anwendung \"TrafoController\" erfolgreich gestartet\n");
//		ConfigServiceImpl.getInstance().report();
		writeStatusToConsole();
	}

	private void writeStatusToConsole()
			throws TimeoutException {
		System.out.println("\nLaufzeiten (bisher)");
		System.out.println((new StringBuilder("Anwendung: ")).append(
				StopWatchApplService.getInstance().getCurrentString())
				.toString());
		System.out.println((new StringBuilder("Motor:     ")).append(
				StopWatchMotorService.getInstance().getCurrentString())
				.toString());
	}

}
