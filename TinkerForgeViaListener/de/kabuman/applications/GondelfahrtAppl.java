package de.kabuman.applications;

import java.io.IOException;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.ConfigService;
import de.kabuman.tinkerforge.services.ConfigServiceImpl;

public class GondelfahrtAppl {

	// Geschwindigkeiten
	final short FULL_VELOCITY_TALFAHRT = -30000; 		// min: -32567 (links drehend)
	final short FULL_VELOCITY_BERGFAHRT = 30000; 		// max:  32767 (rechts drehend)
	final short REDUCE_VELOCITY_TALFAHRT = -15000; 		// min: -32567 (links drehend)
	final short REDUCE_VELOCITY_BERGFAHRT = 15000; 		// max:  32767 (rechts drehend)

	// Distanzen
	final int STOPP_DISTANCE_TALFAHRT = 200; 			// mm
	final int STOPP_DISTANCE_BERGFAHRT = 200; 			// mm
	final int REDUCE_DISTANCE_TALFAHRT = 450; 			// mm
	final int REDUCE_DISTANCE_BERGFAHRT = 450; 			// mm

	// Listener
	final long CALLBACK_PERIOD = 100; 					// milli seconds

	// Service & Connection
	ConfigService configService;
	IPConnection ipCon;

	// Geräte
	BrickDC gondelMotor;
	BrickletDistanceIR sensorTal;
	BrickletDistanceIR sensorBerg;
	BrickletJoystick schalter;
	BrickletRotaryPoti regler;

	// Betriebsmodus 
	final int AUS = 0;
	final int AUTO_EIN = 1;
	final int MAN_EIN = 2;
	int zustand = 0;
	
	/**
	 * Initialisierung und Konfiguration
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void anlageKonfigurieren() throws IOException, TimeoutException {
		configService = ConfigServiceImpl.getNewInstance();
		ipCon = configService.getConnect();
	
		// Geräte anmelden und connecten
		gondelMotor = (BrickDC) configService.createAndConnect(ConfigService.DC1);
		sensorTal = (BrickletDistanceIR) configService.createAndConnect(ConfigService.IR1);
		sensorBerg = (BrickletDistanceIR) configService.createAndConnect(ConfigService.IR2);
		schalter = (BrickletJoystick) configService.createAndConnect(ConfigService.JS1);
		regler =  (BrickletRotaryPoti) configService.createAndConnect(ConfigService.RP1);
	
		try {
			gondelMotor.setPWMFrequency(10000); 	// Use PWM frequency of 10khz
			gondelMotor.setDriveMode((short) 0); 	// use 1 = Drive/Coast instead of 0 = Drive/Brake
			gondelMotor.setAcceleration(5000);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} 		// Slow acceleration
	}

	/**
	 * Main-Methode "Gondelstart"
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public void betriebAufnehmen() throws IOException, TimeoutException {

		// Konfiguration und Start der Anlage
		anlageKonfigurieren();

		// Controller
		betriebsartWaehlen();

		System.out.println("gondelStart:: Anlage AUS.");
		System.out.println("gondelStart:: Drücke Joystick für:  -> MANUELL-EIN -> AUTOMATIC-EIN -> ANLAGE-AUS");

		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	
	private void betriebsartWaehlen(){
		// Add and implement listener for pressed and released events
		schalter.addPressedListener(new BrickletJoystick.PressedListener() {
			public void pressed() {
				System.out.println("addListenerSchalter:: Schalter gedrückt");
				switch (zustand) {
				case AUS:
					System.out.println("addListenerSchalter:: MAN_EIN");
					zustand = MAN_EIN;
					manBetriebStoppen();
					autoBetriebStoppen();
					manBetriebStarten();
					break;
				case MAN_EIN:
					System.out.println("addListenerSchalter:: AUTO_EIN");
					zustand = AUTO_EIN;
					manBetriebStoppen();
					autoBetriebStoppen();
					autoBetriebStarten();
					break;
				case AUTO_EIN:
					System.out.println("addListenerSchalter:: AUS");
					zustand = AUS;
					manBetriebStoppen();
					autoBetriebStoppen();
					break;
				default:
					break;
				}
			}
		});
	
	}

	private void manBetriebStarten(){
		System.out.println("gondelManuell:: Regler auf Mittenstellung positionieren, um Motor zu starten");

		try {
			regler.setPositionCallbackThreshold('i', (short)-28, (short)28);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		regler.addPositionReachedListener(new BrickletRotaryPoti.PositionReachedListener() {
			public void positionReached(short position) {
				try {
					regler.setPositionCallbackThreshold('x', (short)0, (short)0);
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	// Listener mit option 'x' ausschalten
				System.out.println("addListenerReglerStart:: Position zum Motorstart erreicht. Position="+position);
				manBetriebSteuern();
			}
		});
	}

	private void manBetriebSteuern(){
	
		try {
			gondelMotor.setAcceleration(0); 		// Fahren ohne Verzögerung
			regler.setPositionCallbackPeriod(50);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		regler.addPositionListener(new BrickletRotaryPoti.PositionListener() {
			public void position(short position) {
				short velocity = (short)(position * 213);
				if ((velocity < 6000 && velocity > 0)			// Bergfahrt, aber zu langsam
						|| (velocity > -6000 && velocity < 0)	// Talfahrt, aber zu langsam
						|| (velocity == 0)){					// Nullposition des Reglers
					System.out.println("addListenerReglerUeberwachung:: Motor gestoppt. Reglerposition="+position);
					try {
						gondelMotor.disable();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					if (velocity < 0){
						System.out.println("addListenerReglerUeberwachung:: Talfahrt mit velocity=" + velocity + ". Reglerposition="+position + " Volt="+getVolt());
					}
					if (velocity > 0){
						System.out.println("addListenerReglerUeberwachung:: Bergfahrt mit velocity=" + velocity + ". Reglerposition="+position + " Volt="+getVolt());
					}
					try {
						gondelMotor.setVelocity(velocity);
						gondelMotor.enable();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	
	}

	private void manBetriebStoppen(){
		try {
			regler.setPositionCallbackPeriod(0);
			gondelMotor.disable();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getVolt(){
		try {
			return gondelMotor.getStackInputVoltage();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	

	/**
	 * Festlegen der Start-Parameter für den Gondelstart
	 */
	private void autoBetriebStarten() {
		try {
			gondelMotor.setAcceleration(5000);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		// Slow

		int distanceToTalStation = getDistanceToTalStation();
		int distanceToBergStation = getDistanceToBergStation();

		if (distanceToTalStation < STOPP_DISTANCE_TALFAHRT
				&& distanceToBergStation < STOPP_DISTANCE_BERGFAHRT) {
			System.out.println("gondelAnfahren:: Tal- und Bergstation stehen zu dicht bei einander.");
			System.out.println("gondelAnfahren:: Gondelstart dadurch nicht möglich, da Motor keine volle Geschwindigkeit erreichen kann und dadurch auch nicht das Flag zurückgesetzt werden kann. Unfallgefahr!");
			throw new IllegalArgumentException();
		}

		if (distanceToTalStation < distanceToBergStation) {
			// Gondel steht also näher bei der Talstation: Bergfahrt setzen
			try {
				gondelMotor.setVelocity(FULL_VELOCITY_BERGFAHRT);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("gondelAnfahren:: Gondel wird mit einer Bergfahrt gestartet");
			autoBetriebBergFahrtSteuern();

		} else {
			// Gondel steht näher bei der Bergstation: Talfahrt setzen
			try {
				gondelMotor.setVelocity(FULL_VELOCITY_TALFAHRT);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("gondelAnfahren:: Gondel wird mit einer Talfahrt gestartet");
			autoBetriebTalFahrtSteuern();
		}

		// Nu gehts los
		try {
			gondelMotor.enable();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Einfahrt Talstation: 
	 * - Stop-Distanz überwachen
	 * Wenn unterschritten:
	 * - Motor stoppen
	 * - Richtung ändern
	 * - Motor einschalten
	 * - Listener Ausfahrt Talstation aktivieren
	 */
	private void autoBetriebTalEinfahrtSteuern() {
		try {
			sensorTal.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sensorTal.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance < STOPP_DISTANCE_TALFAHRT) {
					try {
						gondelMotor.disable();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out
							.println("Tal -> Berg   Geschwindigkeit= Voll   eingeleitet durch SensorTal(Ist/Soll)= "
									+ distance
									/ 10.0
									+ "/"
									+ STOPP_DISTANCE_TALFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(FULL_VELOCITY_BERGFAHRT);
						gondelMotor.enable();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebTalAusfahrtSteuern();
				}
			}
		});
	}

	/**
	 * Überwachung Talstation / Gondel befindet sich zwischen Tal- und Bergstation auf freier Strecke
	 * - Überwachen Reduzier-Distanz
	 * Wenn unterschritten:
	 * - Geschwindigkeit reduzieren
	 * - Listener Einfahrt Talstation aktivieren
	 */
	private void autoBetriebTalFahrtSteuern() {
		try {
			sensorTal.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sensorTal.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance < REDUCE_DISTANCE_TALFAHRT) {
					System.out
							.println("addListenerTalStationUeberwachung:: Talfahrt mit reduzierter Geschwindigkeit fortgesetzt bei SensorTal-Distance(Ist/Soll): "
									+ distance
									/ 10.0
									+ "/"
									+ REDUCE_DISTANCE_TALFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(REDUCE_VELOCITY_TALFAHRT);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebTalEinfahrtSteuern();
				}
			}
		});
	}

	/**
	 * Ausfahrt Talstation / Gondel verläßt die Talstation
	 * - Überwachen Reduzier-Distanz
	 * Wenn überschritten:
	 * - Volle Geschwindigkeit
	 * - Listener Überwachung Bergstation aktivieren
	 */
	private void autoBetriebTalAusfahrtSteuern() {
		try {
			sensorTal.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sensorTal.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance > REDUCE_DISTANCE_TALFAHRT) {
					System.out
							.println("addListenerTalStationAusfahrt:: Bergfahrt mit voller Geschwindigkeit bei SensorTal-Distance(Ist/Soll): "
									+ distance
									/ 10.0
									+ "/"
									+ REDUCE_DISTANCE_TALFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(FULL_VELOCITY_BERGFAHRT);
						sensorTal.setDistanceCallbackPeriod(0);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebBergFahrtSteuern();
				}
			}
		});
	}

	/**
	 * Einfahrt Bergstation: 
	 * - Stop-Distanz überwachen
	 * Wenn unterschritten:
	 * - Motor stoppen
	 * - Richtung ändern
	 * - Motor einschalten
	 * - Listener Ausfahrt Bergstation aktivieren
	 */
	private void autoBetriebBergEinfahrtSteuern() {
		try {
			sensorBerg.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (NotConnectedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sensorBerg.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance < STOPP_DISTANCE_BERGFAHRT) {
					try {
						gondelMotor.disable();
					} catch (TimeoutException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NotConnectedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out
							.println("addListenerBergStationEinfahrt:: Talfahrt eingeleitet bei SensorBerg-Distance(Ist/Soll): "
									+ distance
									/ 10.0
									+ "/"
									+ STOPP_DISTANCE_BERGFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(FULL_VELOCITY_TALFAHRT);
						gondelMotor.enable();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebBergAusfahrtSteuern();
				}
			}
		});

	}

	/**
	 * Überwachung Bergstation / Gondel befindet sich zwischen Tal- und Bergstation auf freier Strecke
	 * - Überwachen Reduzier-Distanz
	 * Wenn unterschritten:
	 * - Geschwindigkeit reduzieren
	 * - Listener Einfahrt Talstation aktivieren
	 */
	private void autoBetriebBergFahrtSteuern() {
		// Sensor Berg(#2): Configuration and Listener Definition
		try {
			sensorBerg.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sensorBerg.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance < REDUCE_DISTANCE_BERGFAHRT) {
					System.out
							.println("addListenerBergStationUeberwachung:: Bergfahrt mit reduzierter Geschwindigkeit fortgesetzt bei SensorBerg-Distance(Ist/Soll): "
									+ distance
									/ 10.0
									+ "/"
									+ REDUCE_DISTANCE_BERGFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(REDUCE_VELOCITY_BERGFAHRT);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebBergEinfahrtSteuern();
				}
			}
		});
	}

	/**
	 * Ausfahrt Bergstation / Gondel verläßt die Bergstatiion
	 * - Überwachen Reduzier-Distanz
	 * Wenn überschritten:
	 * - Volle Geschwindigkeit
	 * - Listener Überwachung Bergstation aktivieren
	 */
	private void autoBetriebBergAusfahrtSteuern() {
		// Sensor Berg(#2): Configuration and Listener Definition
		try {
			sensorBerg.setDistanceCallbackPeriod(CALLBACK_PERIOD);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sensorBerg.addDistanceListener(new BrickletDistanceIR.DistanceListener() {
			public void distance(int distance) {
				if (distance > REDUCE_DISTANCE_BERGFAHRT) {
					System.out
							.println("addListenerBergStationAusfahrt:: Talfahrt mit voller Geschwindigkeit bei SensorBerg-Distance(Ist/Soll): "
									+ distance
									/ 10.0
									+ "/"
									+ REDUCE_DISTANCE_BERGFAHRT / 10.0 + " cm");
					try {
						gondelMotor.setVelocity(FULL_VELOCITY_TALFAHRT);
						sensorBerg.setDistanceCallbackPeriod(0);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					autoBetriebTalFahrtSteuern();
				}
			}
		});
	}

	/**
	 * Liefert Distanz zwischen Talstation und Gondel
	 */
	private Integer getDistanceToTalStation() {
		try {
				return sensorTal.getDistance();
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Liefert Distanz zwischen Bergstation und Gondel
	 */
	private Integer getDistanceToBergStation() {
		try {
			return sensorBerg.getDistance();
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void autoBetriebStoppen(){
		try {
			sensorTal.setDistanceCallbackPeriod(0);
			sensorBerg.setDistanceCallbackPeriod(0);
			gondelMotor.disable();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
