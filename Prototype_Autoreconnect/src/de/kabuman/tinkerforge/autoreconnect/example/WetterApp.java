package de.kabuman.tinkerforge.autoreconnect.example;
import de.kabuman.tinkerforge.alarm.items.digital.input.BarometerSensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.HumiditySensorItemImpl;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItem;
import de.kabuman.tinkerforge.alarm.items.digital.input.TemperatureSensorItemImpl;
import de.kabuman.tinkerforge.alarm.units.AbstractUnit;
import de.kabuman.tinkerforge.screencontroller.ScreenController;
import de.kabuman.tinkerforge.screencontroller.ScreenControllerImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClock;
import de.kabuman.tinkerforge.screencontroller.items.ScreenClockImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItemImpl;
import de.kabuman.tinkerforge.services.connect.TfConnectService;
import de.kabuman.tinkerforge.services.connect.TfStackCallbackApp;


/**
 * Local Weather StationDiplays
 */
public class WetterApp extends AbstractUnit implements TfStackCallbackApp {
    private static final String host = "localhost";
    private static final int port = 4223;
    private WetterTfStackImpl wetterTfStack = null;
    
    ScreenController sc;
    
    
    TemperatureSensorItem temperatureSensorItem;
    HumiditySensorItemImpl humiditySensorItem;
    BarometerSensorItemImpl barometerSensorItem;

	long callbackPeriod = 2000;

	boolean firstConnect = true;
    
    
    /**
     * Constructor for Weather Station  <br>
     *  <br>
     * Diplays  <br>
     * - current temperature  <br>
     * - current humidity  <br>
     * - current air pressure  <br>
     * on a LCD.  <br>
     *  <br>
     * The values will be currently updated every 2 seconds.  <br>
     *  <br>
     *  See class WetterTfStackImpl.java for required Tinferforge Hardware
     */
	public WetterApp()  {
		
	    wetterTfStack = new WetterTfStackImpl(this);
		new TfConnectService(host, port, null, wetterTfStack);

		
		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	
	@Override
	public void tfStackReConnected() {
		if (firstConnect){
			System.out.println("*** Stack firstly connected ***");
			firstConnect = false;
			defineScreenController();
		} else {
			System.out.println("*** Stack reconnected ***");

			// ItemSources: temperature, humidity, barometer are to replace
			createAndReplaceItemSources();
			
			// LCD is to replace
			sc.replaceLcd(wetterTfStack.getTfLcd());

		}

	}

	
	private void createAndReplaceItemSources(){
		storePreviousInstance(temperatureSensorItem);
		temperatureSensorItem = new TemperatureSensorItemImpl(null, wetterTfStack.getTfTemperature(), callbackPeriod);

		if (replacePreviousInstance(temperatureSensorItem)){
			temperatureSensorItem.activateSensor();
		} 

		storePreviousInstance(humiditySensorItem);
		humiditySensorItem = new HumiditySensorItemImpl(null, wetterTfStack.getTfHumidity(), callbackPeriod);
		if (replacePreviousInstance(humiditySensorItem)){
			humiditySensorItem.activateSensor();
		}

		storePreviousInstance(barometerSensorItem);
		barometerSensorItem = new BarometerSensorItemImpl(null, wetterTfStack.getTfBarometer(), 50); // need a shorter callback period to initialize the first value
		if (replacePreviousInstance(barometerSensorItem)){
			barometerSensorItem.activateSensor();
		}
		
	}
	
	
	@Override
	public void tfStackDisconnected() {
		System.out.println("*** Stack disconnected ***");
		if (sc != null){
			sc.deactivateAll();
		}
	}

	
	private void defineScreenController(){
		if (sc != null){
			sc.deactivateAll();
		}
	
		sc = ScreenControllerImpl.getNewInstance(wetterTfStack.getTfLcd());
		sc.clearDisplay();
		
	

		temperatureSensorItem = new TemperatureSensorItemImpl(null, wetterTfStack.getTfTemperature(), callbackPeriod);
		humiditySensorItem = new HumiditySensorItemImpl(null, wetterTfStack.getTfHumidity(), callbackPeriod);
		
		// Wait 2 callback periods of the barometer to stabilize the value 
		try {
			Thread.sleep(110);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		int screenId = 0;
		sc.addItem(new ScreenClockImpl(wetterTfStack.getTfLcd(), ScreenClock.FORMAT_WEEK_DAY_S, screenId, 0, 0));
		sc.addItem(new ScreenClockImpl(wetterTfStack.getTfLcd(), ScreenClock.FORMAT_DATE_TIME_M, screenId, 0, 3));
		sc.addItem(new ScreenItemImpl(null, temperatureSensorItem, callbackPeriod, screenId, 1, 2));
		sc.addItem(new ScreenItemImpl(null, humiditySensorItem, callbackPeriod, screenId, 2, 2));

		if (wetterTfStack.getTfBarometer() != null){
			barometerSensorItem = new BarometerSensorItemImpl(null, wetterTfStack.getTfBarometer(), 50); // need a shorter callback period to initialize the first value
			sc.addItem(new ScreenItemImpl(null, barometerSensorItem, callbackPeriod, screenId, 3, 0,"%6.1f"));
			sc.addMaskLine(screenId, 3, "       mBar");
		}
		
		sc.addMaskLine(screenId, 0, "  .");
		sc.addMaskLine(screenId, 1, "       C");
		sc.addMaskLine(screenId, 2, "       %");
		
		sc.assignLcdButton3ToScreenId(screenId);
		sc.addNewScreenToSequence(screenId);
		sc.setUseAliveIndicator(false);
		sc.activateScreen(screenId);
	}


	@Override
	public String getTfStackName() {
		return this.getClass().getName();
	}

}
