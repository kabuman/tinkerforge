package de.kabuman.tinkerforge.services.rotaryencoder;

import java.util.Date;

import com.tinkerforge.BrickletRotaryEncoder;


/**
 * Implements the Interface of 3 Listener for the Bricklet Rotary Encoder  <br>
 */
public class RotaryEncoderListenerImpl implements BrickletRotaryEncoder.CountListener, BrickletRotaryEncoder.PressedListener, BrickletRotaryEncoder.ReleasedListener {

	// Parameter
	private RotaryEncoderConsumer consumer;
	
	private int oldCount = 0;
	private Date pressedStarted = null;
	
	
	/**
	 * Constructor  <br>
	 * 
	 * @param consumer
	 */
	public RotaryEncoderListenerImpl(
			RotaryEncoderConsumer consumer){
		this.consumer = consumer;
	}


	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletRotaryEncoder.CountListener#count(int)
	 */
	@Override
	public synchronized void count(int count) {
		consumer.rotaryEncoderCounterValueChanged(count, oldCount);
		oldCount = count;
	}


	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletRotaryEncoder.PressedListener#pressed()
	 */
	@Override
	public void pressed() {
		pressedStarted = new Date();
		consumer.rotaryEncoderPressed();
	}

	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletRotaryEncoder.ReleasedListener#released()
	 */
	@Override
	public void released() {
		if (pressedStarted != null){
			consumer.rotaryEncoderReleased(new Date().getTime() - pressedStarted.getTime());
		} else {
			// Released without a catched Press event: return zero as a time period since pressed event
			consumer.rotaryEncoderReleased(0l);
		}
	}

}
