package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Find out the best matching velocity (-1,...,-32767) for a given maximum voltage
 * 
 * Be sure:
 * - to connect the Bricklet.Voltage in a correct manner
 * - to connect the LED Device in a correct manner
 */
public class VelocityFindingLimitedByVoltage {
	
	// Constructor Parameter
	BrickDC brickDC;
	BrickletVoltage brickletVoltage;
	
	//  properties of the finding method
	int limitMilliVoltage = 0;
	short velocity = 0;
	
	boolean continueFinding = true;
	int tryMilliVoltage = 0;
	
	
	@SuppressWarnings("unused")
	private VelocityFindingLimitedByVoltage(){
	}
	
	/**
	 * Constructor
	 * 
	 * @param brickDC - for the LED
	 * @param brickletVoltage - for the voltage measuring
	 */
	public VelocityFindingLimitedByVoltage (BrickDC brickDC, BrickletVoltage brickletVoltage){
		this.brickDC = brickDC;
		this.brickletVoltage = brickletVoltage;
	}

	/**
	 * Retrieve the current voltage for the given velocity
	 * Will send a lot of requests against Bricklet.Voltage, but will take over the last value only
	 * 
	 * @param tryVelocity - the given velocity
	 * @return int - the last retrieved voltage
	 */
	private int retrieveCurrentMilliVolt(short tryVelocity){
		// set and test (via installed voltage change listener)
		try {
			brickDC.setVelocity(tryVelocity);

			// wait after velocity setting
			pause(1000000);

			int currentMilliVolt = 0;
			// retrieve the measured voltage values
			for (int i = 0; i < 30; i++) {
				try {
					currentMilliVolt = brickletVoltage.getVoltage();
//					System.out.println("retrieveCurrentMilliVolt:: "+i+".MilliVolt="+currentMilliVolt);
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}

			// return the last retrieved value
			return currentMilliVolt;
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		// return 0 in the case of an throwed exception
		return 0;
	}

	/**
	 * Loops for the given counter
	 * 
	 * @param duration - number of loops
	 */
	private void pause(int duration){
		for (int i = 0; i < duration; i++) {
		}
	}
	
	/**
	 * Find the velocity arround the given maximum voltage
	 *  
	 * @param limitMilliVoltage - the maximum allowed voltage (in milli volt)
	 * @param findingVelocitySteps - the steps in which the velocity will be increased
	 * @return short - the best matching velocity arround the given maximum voltage
	 */
	public short findVelocity(int limitMilliVoltage, short findingVelocitySteps){
		this.limitMilliVoltage = limitMilliVoltage;
		
		int accelerationBefore = 0;

		// stop motor / LED, what ever
		try {
			brickDC.disable();

			// get current acceleration and keep it
			accelerationBefore = brickDC.getAcceleration();
			
			// set acceleration OFF due to needed immediately measuring...
			brickDC.setAcceleration(0);

			// start motor / LED, what ever
			brickDC.enable();
			// find a rough velocity first 
			short start = 0;
			short steps = findingVelocitySteps;
			short roughVelocity = findVelocityBySteps(start, steps);
			
			// find the exact velocity: go back a few steps and continue with the smallest step (-1)
			start = (short) (roughVelocity - (3 * findingVelocitySteps));
			steps = -1;
			velocity = findVelocityBySteps(start, steps);

			// stop motor / LED, what ever
			brickDC.disable();

			// restore old acceleration value
			brickDC.setAcceleration(accelerationBefore);

		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return velocity;
	}
	
	/**
	 * Increases (tries out) the velocity by the given step until the given maximum voltage is exceeded
	 *  
	 * @param steps - the steps in which the velocity will be increased
	 * @return short - the best matching velocity arround the given maximum voltage
	 */
	private short findVelocityBySteps(short start, short steps){
		
		// Loop to find the matching velocity
		short tryVelocity = start;
		
		int beforeStartMilliVoltage = retrieveCurrentMilliVolt(tryVelocity);
		int afterStartMilliVoltage = 0;
		boolean checkVoltageMeasuring = true;
		
		continueFinding = true;
		int exceedCounter = 0;
		short exceedVelocity = 0;
		short tryVelocityOld = 0;
		
		while (continueFinding) {
			
			// calculate next velocity to try out...
			tryVelocityOld = tryVelocity;
			tryVelocity = (short) (tryVelocity + steps);
			
			// retrieve current voltage
			tryMilliVoltage = retrieveCurrentMilliVolt(tryVelocity);

//			System.out.println("VelocityFindingLimitedByVoltage.findVelocity:: velocity/voltage"+tryVelocity+"/"+tryMilliVoltage);

			if (checkVoltageMeasuring && tryVelocity >= 500) {
            	checkVoltageMeasuring = false;
        		afterStartMilliVoltage = retrieveCurrentMilliVolt(tryVelocity);
        		int diff = afterStartMilliVoltage - beforeStartMilliVoltage;
        		if (diff <= 0.2){
        			throw new IllegalArgumentException("No MilliVoltage detected. Please insure, that Voltage Measuring is connected alright.");
        		}
            	
            }
			
			// Check if target reached
			if (tryMilliVoltage > limitMilliVoltage){
				exceedCounter++;
				if (exceedCounter ==1){
					exceedVelocity = tryVelocityOld;
				}
            } else {
            	exceedCounter = 0;
            }

			// set End-Of-Loop if more than 2 times in sequence exceeds the requested voltage
			if (exceedCounter > 2){
				continueFinding = false;
				tryVelocity = exceedVelocity;
				tryMilliVoltage = retrieveCurrentMilliVolt(tryVelocity);
			}
		}
		
		return tryVelocity;
	}
	
	public short getVelocity(){
		return velocity;
	}
	
	public int getVoltage(){
		return tryMilliVoltage;
	}
}
