package de.kabuman.tinkerforge.services.multitouch;

import com.tinkerforge.BrickletMultiTouch;
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
public class MultiTouchSupplierImpl implements MultiTouchConsumer, CommonCallback{
	
	public final static int ALL_ELECTRODES = 8191;
	public final static int ALL_ELECTRODES_WITHOUT_13 = 4095;
	
	// Parameter Values
	private MultiTouchConsumer consumer;
	private BrickletMultiTouch tfMultiTouch = null;
	private int state;

	// State
	private boolean active = false;

	// Processing
	private Long timerDuration;
	private Boolean skipRelease;
	private boolean skipNextReleaseEvent = false;

	// Services
	private CommonObserver pressedTimer = new CommonObserverImpl(this, 1, 1000l, "MultiTouchSupplier: Pressed Timer");
	private MultiTouchListenerImpl listener;

	
	
	/**
	 * Constructor  <br>
	 * Creates this object but does not activate it. <br>
	 * Use method activate to activate it. <br>
	 *  <br>
	 * @param consumer - the caller of this object
	 * @param tfMultiTouch - the bricklet multi touch
	 * @param state - configures the usable electrodes (Bit Field: Each bit represents one electrode)
	 * @param timerDuration - how long a electrode must be touched before the  <br> 
	 * event "multiTouchStateValueTimerReached(..)" will be triggered  <br>
	 * Null is allowed and means: no time observation
	 */
	public MultiTouchSupplierImpl(
			MultiTouchConsumer consumer,
			BrickletMultiTouch tfMultiTouch,
			int state,
			Long timerDuration) {

		this.consumer = consumer;
		this.tfMultiTouch = tfMultiTouch;
		this.state = state;
		this.timerDuration = timerDuration;

		installListener();
		deactivate();
	}

	
	/**
	 * Set the state of the electrodes (bit field)  <br>
	 * If set to zero all electrodes are deactivated.
	 * 
	 * @param state - Bit Field: Each bit represents one electrode of the multi touch 
	 */
	private void setElectrodeConfig(int state){
		try {
			tfMultiTouch.setElectrodeConfig(state);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	
	// TODO - overwork required
	/**
	 * Set the time in milliseconds before the event "rotaryEncoderPressedTimeReached"  <br>
	 * will be triggered after the rotary encoder is pressed  <br>
	 * 
	 * @param timerDuration - in milliseconds
	 */
	public void setPressedTimer(Long timerDuration){
		this.timerDuration = timerDuration;
	}
	
	
	/**
	 * Set the sensitivity of the electrodes and recalibrates them
	 * 
	 * @param sensitivity - Range: 5-201 (181 is default)
	 */
	public void setElectrodeSensitivity(int sensitivity){
		try {
			tfMultiTouch.setElectrodeSensitivity((short)sensitivity);
			tfMultiTouch.recalibrate();
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Deactivate the bricklet  <br>
	 * After this no event will be triggered anymore. 
	 */
	public void deactivate() {
		active = false;
		setElectrodeConfig(0);
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
		listener = new MultiTouchListenerImpl(this);
		tfMultiTouch.addTouchStateListener(listener);
	}
	

	/**
	 * Activates the supplier 
	 */
	public synchronized void activate() {
		active = true;
		setElectrodeConfig(state);
	}


	/**
	 * Returns the touch state  <br>
	 * 
	 * @return Integer - Bit Field: Each bit represents one electrode
	 */
	public Integer getTouchState(){
		try {
			return tfMultiTouch.getTouchState();
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer#multiTouchStateValueChanged(int, int)
	 */
	public synchronized void multiTouchStateValueChanged(int newValue, int oldValue) {
		// for the first event the values are equal: Do nothing in this case
		if (newValue != oldValue){
			if (pressedTimer.isObservationActive()){
				pressedTimer.stopObservation();
			}
			consumer.multiTouchStateValueChanged(newValue, oldValue);
			
			
			if (newValue > 0 && timerDuration != null){
				// One or more pins are touched
				pressedTimer = new CommonObserverImpl(this, newValue, timerDuration, "MultiTouchSupplier: Pressed Timer");
				pressedTimer.startObservation();
			}
		}
	}


	// TODO - overwork required
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressed()
	 */
	public synchronized void rotaryEncoderPressed() {
		if (active){
//			consumer.rotaryEncoderPressed();
			if (timerDuration != null){
				pressedTimer.startObservation(timerDuration);
			}
		}
	}


	// TODO - overwork required
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
//				consumer.rotaryEncoderReleased(pressedDuration);
			}
		}
	}


	// TODO - overwork required
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CommonCallback#commonObserverTriggeredMethod(java.lang.Integer)
	 */
	public synchronized void commonObserverTriggeredMethod(Integer state) {
		consumer.multiTouchStateValueTimerReached(state);
	}


	// TODO - overwork required
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressedTimeReached()
	 */
	public void rotaryEncoderPressedTimeReached() {
		// this event will be triggered by this object, but not by the super class
		// this is only defined due to usage of the same interface by super class
	}
	
	
	// TODO - overwork required
	/**
	 * Replaces the bricklet multi touch  <br>
	 * - stops a possible pressed timer
	 * - installs the listener again
	 * - keeps the supplier state (active or not)
	 * @param tfMultiTouch - the bricklet multi touch
	 */
	public void replaceMultiTouch(BrickletMultiTouch tfMultiTouch){
		this.tfMultiTouch = tfMultiTouch;
		
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


	@Override
	public void multiTouchStateValueTimerReached(int state) {
		consumer.multiTouchStateValueTimerReached(state);
	}


}
