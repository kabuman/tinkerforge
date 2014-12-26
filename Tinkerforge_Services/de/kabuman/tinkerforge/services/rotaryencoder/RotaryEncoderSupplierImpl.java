package de.kabuman.tinkerforge.services.rotaryencoder;

import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserver;
import de.kabuman.common.services.CommonObserverImpl;

/**
 * Provides Bricklet Rotary Encoder  <br>
 * for Applications.  <br>
 *  <br>
 * The Application must implement the Interface "RotaryEncoderConsumer".
 */
public class RotaryEncoderSupplierImpl implements RotaryEncoderConsumer, CommonCallback{
	
	// Parameter Values
	private RotaryEncoderConsumer consumer;
	private BrickletRotaryEncoder tfRotaryEncoder = null;

	// State
	private boolean active = false;

	// Processing
	private Long timerDuration = null;
	private Boolean skipRelease;
	private boolean skipNextReleaseEvent = false;

	// Services
	private CommonObserver pressedTimer = new CommonObserverImpl(this, 1, 1000l,"RotaryEncoder: Pressed Timer");
	private RotaryEncoderListenerImpl listener;

	
	/**
	 * Constructor  <br>
	 * Creates this object but does not activate it. <br>
	 * Use method activateRotaryEncoder to activate it. <br>
	 *  <br>
	 * @param consumer - the caller of this object
	 * @param tfRotaryEncoder - the bricklet rotary encoder
	 */
	public RotaryEncoderSupplierImpl(
			RotaryEncoderConsumer consumer,
			BrickletRotaryEncoder tfRotaryEncoder) {

		this.consumer = consumer;
		this.tfRotaryEncoder = tfRotaryEncoder;

		installListener();
		deactivate();
	}

	
	/**
	 * Set the callback period for the count listener  <br>
	 * If set to zero the listener is deactivated.
	 * 
	 * @param value - int: the callback period
	 */
	private void setCountCallbackPeriod(int value){
		try {
			tfRotaryEncoder.setCountCallbackPeriod(value);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Set the time in milliseconds before the event "rotaryEncoderPressedTimeReached"  <br>
	 * will be triggered after the rotary encoder is pressed  <br>
	 * 
	 * @param timerDuration - in milliseconds
	 * @param skipRelease - true: no Release event will follow / false: Release event will be triggered 
	 */
	public void setPressedTimer(Long timerDuration, Boolean skipRelease){
		this.timerDuration = timerDuration;
		this.skipRelease = skipRelease;
	}
	
	
	/**
	 * Deactivate the Rotary Encoder  <br>
	 * After this no event will be triggered anymore. 
	 */
	public void deactivate() {
		active = false;
		setCountCallbackPeriod(0);
	}

	
	/**
	 * Returns the supplier state
	 * 
	 * @return boolean - true/false
	 */
	public boolean isActive() {
		return active;
	}

	
	/**
	 * Installs all required listener
	 */
	private void installListener() {
		listener = new RotaryEncoderListenerImpl(this);
		tfRotaryEncoder.addCountListener(listener);
		tfRotaryEncoder.addPressedListener(listener);
		tfRotaryEncoder.addReleasedListener(listener);
	}
	

	/**
	 * Activates the supplier  <br>
	 * Set the count to zero 
	 */
	public synchronized void activate() {
		active = true;
		setCountCallbackPeriod(50);
		
		// reset the counter
		getCount(true);
	}


	/**
	 * Returns the count  <br>
	 * 
	 * @param reset - true: count will be set to zero / false: no reset
	 * @return Integer - the count
	 */
	public Integer getCount(boolean reset){
		try {
			return tfRotaryEncoder.getCount(reset);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderCounterValueChanged(int, int)
	 */
	public synchronized void rotaryEncoderCounterValueChanged(int newValue, int oldValue) {
		// for the first event the values are equal: Do nothing in this case
		if (newValue != oldValue){
			consumer.rotaryEncoderCounterValueChanged(newValue, oldValue);
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressed()
	 */
	public synchronized void rotaryEncoderPressed() {
		if (active){
			consumer.rotaryEncoderPressed();
			if (timerDuration != null){
				pressedTimer.startObservation(timerDuration);
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderReleased(long)
	 */
	public synchronized void rotaryEncoderReleased(long pressedDuration) {
		if (active){
			if (skipNextReleaseEvent){
				// presssedTimer had triggered the event consumer.rotaryEncoderPressedTimeReached()
				// So this Release Event should have no further impact: means skip the event rotaryEncoderReleased()
				skipNextReleaseEvent = false;
			} else {
				if (pressedTimer.isObservationActive()){
					pressedTimer.stopObservation();
				}
				consumer.rotaryEncoderReleased(pressedDuration);
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CommonCallback#commonObserverTriggeredMethod(java.lang.Integer)
	 */
	public synchronized void commonObserverTriggeredMethod(Integer functionCode) {
		if (skipRelease){
			skipNextReleaseEvent = true;
		}
		consumer.rotaryEncoderPressedTimeReached();
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressedTimeReached()
	 */
	public void rotaryEncoderPressedTimeReached() {
		// this event will be triggered by this object, but not by the super class
		// this is only defined due to usage of the same interface by super class
	}
	
	
	/**
	 * Replaces the bricklet rotary encoder  <br>
	 * - stops a possible pressed timer
	 * - reset the flag for the skip of the event rotaryEncoderReleased()
	 * - installs the listener again
	 * - keeps the supplier state (active or not)
	 * @param tfRotaryEncoder
	 */
	public void replaceRotaryEncoder(BrickletRotaryEncoder tfRotaryEncoder){
		this.tfRotaryEncoder = tfRotaryEncoder;
		
		if (pressedTimer.isObservationActive()){
			pressedTimer.stopObservation();
		}

		skipNextReleaseEvent = false;
		
		installListener();

		if (active){
			activate();
		} else {
			deactivate();
		}
	}
	

}
