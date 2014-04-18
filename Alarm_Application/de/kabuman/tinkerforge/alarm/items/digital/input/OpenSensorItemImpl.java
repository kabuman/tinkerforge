package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.controller.AlertController;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.devicehelper.IO4device;
import de.kabuman.tinkerforge.alarm.units.ProtectUnit;
import de.kabuman.tinkerforge.services.IOService;

public class OpenSensorItemImpl extends IO4device implements OpenSensorItem,
		InterruptConsumerSwitch {

	// Parameter Values
	private ProtectUnit protectUnit;
	private BrickletIO4 openSensor = null;
	private long debouncePeriod;
	private short interrupt;

	// state
	boolean active = false;

	// Interrupt Listener with test
	IO4InterruptListenerImpl interruptListener = null;

	/**
	 * Installs the Open Sensor and keep it deactivated
	 * 
	 * @param protectUnit
	 * @param openSensor
	 * @param debouncePeriod
	 * @param interrupt
	 */
	public OpenSensorItemImpl(
			ProtectUnit protectUnit,
			BrickletIO4 openSensor,
			long debouncePeriod,
			short interrupt) {
		super(openSensor);

		this.protectUnit = protectUnit;
		this.openSensor = openSensor;
		this.debouncePeriod = debouncePeriod;
		this.interrupt = interrupt;

		installOpenSensor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#
	 * deactivateOpenSensor()
	 */
	public void deactivateOpenSensor() {
		if (!active) {
			return;
		}

		active = false;
		setInterrupt(getSubtractedInterrupt(interrupt), 500l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#isActive
	 * ()
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Installs the Open Sensor and keep it deactivated
	 */
	private void installOpenSensor() {
		active = false;

		try {
			openSensor.setConfiguration(IOService.setBitON(interrupt), 'i',
					true);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}

		interruptListener = new IO4InterruptListenerImpl(this, interrupt);

		openSensor.addInterruptListener(interruptListener);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#removeListener()
	 */
	public void removeListener(){
		openSensor.removeInterruptListener(interruptListener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#
	 * activateOpenSensor()
	 */
	public void activateOpenSensor() {
		
		checkSensorOpened();
		
		active = true;
		setInterrupt(getAddedInterrupt(interrupt), debouncePeriod);
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Kontaktsensor", "Sensor aktiviert. Interrupt="+interrupt);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#checkSensorOpened()
	 */
	public boolean checkSensorOpened(){
		// Checks if the sensor is closed (no alert) 
		// before activating, reset or restart it
		
		try {
			if (IOService.isInterruptedBy(interrupt, openSensor.getValue())){
				// sensor is opened!  
				switchedOFF();	// Set ALERT!
				return true;
			} else {
				// Sensor is fine. Is closed.
				return false;
			}
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		} catch (NotConnectedException e1) {
			e1.printStackTrace();
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.kabuman.tinkerforge.alarm.items.digital.input.OpenSensorItem#test()
	 */
	public void test() {
		interruptListener.test();
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Kontaktsensor", "Test ausgelöst");
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InterruptConsumerSwitch#switchedOFF()
	 */
	public void switchedOFF() {
		protectUnit.activateAlert(LogController.SENSOR_OPEN, LogController.MSG_OPEN, AlertController.ALERT_TYPE_INTRUSION);
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Kontaktsensor", "Opened. Alert triggered.");
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InterruptConsumerSwitch#switchedON()
	 */
	public void switchedON() {
		LogControllerImpl.getInstance().createTechnicalLogMessage(protectUnit.getUnitName(), "Kontaktsensor", "closed. Ready to observe.");
	}
	


}
