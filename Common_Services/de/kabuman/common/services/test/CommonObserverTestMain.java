package de.kabuman.common.services.test;

import java.util.Date;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserver;
import de.kabuman.common.services.CommonObserverImpl;
import de.kabuman.common.services.FormatterService;

public class CommonObserverTestMain {

	
	/**
	 * @param args
	 */
	public static void main(String[] args ) {
		CommonObservationDemo cod = new CommonObserverTestMain().new CommonObservationDemo();
		cod.start();

	}

	public class CommonObservationDemo implements CommonCallback{
		public void start(){
			CommonObserver co1 = new CommonObserverImpl(this, 1, 2000,"Backlight");
			CommonObserver co2 = new CommonObserverImpl(this, 2, 9000,"DefaultScreen");
			
			co1.startObservation();
			co2.startObservation();
			TestHelper.sleep(1900);
			co1.startObservation(5000);
			TestHelper.sleep(5100);
			co1.startObservation();
			
		}

		public synchronized void commonObserverTriggeredMethod(Integer functionCode) {
			switch (functionCode) {
			case 1:
				
				break;

			default:
				break;
			}
			System.out.println(FormatterService.getDateHHMMSSS(new Date()) + "  callback triggert with function="+functionCode);
		}
		
	}
	
}
