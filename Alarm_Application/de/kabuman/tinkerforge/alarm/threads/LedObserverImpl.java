package de.kabuman.tinkerforge.alarm.threads;

import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO16ItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.output.OutputItem;




public class LedObserverImpl extends Thread implements LedObserver{

	Object ledItem;
	
	int ledSchema;
	
	String unitName;
	
	/**
	 * Constructor
	 */
	public LedObserverImpl(Object ledItem, int ledSchema, String unitName) {
		this.ledItem = ledItem;
		this.ledSchema = ledSchema;
		this.unitName = unitName;

		start();  // calls the run() method
	}
	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		LogControllerImpl.getInstance().createTechnicalLogMessage("LedObserver", "Start unit="+unitName, "ledSchema="+ledSchema);

		switch (ledSchema) {
		case LED_ALARM_ON:
			pause(500);
			on(ledItem);
			pause(500);
			off(ledItem);
			pause(500);
			on(ledItem);
			pause(500);
			off(ledItem);

			break;

		case LED_ALARM_OFF:
			on(ledItem);
			pause(1000);
			off(ledItem);
			
			break;

		case LED_RESET_ON:
			pause(500);
			on(ledItem);
			pause(500);
			off(ledItem);
			pause(500);
			on(ledItem);
			pause(500);
			off(ledItem);

			break;

		default:
			break;
		}
		
		LogControllerImpl.getInstance().createTechnicalLogMessage("LedObserver", "End unit="+unitName, "ledSchema="+ledSchema);
	}

	private void on(Object object){
		if (object instanceof OutputItem){
			((OutputItem) object).switchON(); 
		}
		if (object instanceof OutputIO16ItemImpl){
			((OutputIO16ItemImpl) object).switchON(); 
		}
	}
	
	private void off(Object object){
		if (object instanceof OutputItem){
			((OutputItem) object).switchOFF(); 
		}
		if (object instanceof OutputIO16ItemImpl){
			((OutputIO16ItemImpl) object).switchOFF(); 
		}
	}
	
	private void pause(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
