package de.kabuman.tinkerforge.services.multitouch;

import java.util.Date;

import com.tinkerforge.BrickletMultiTouch;

import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.LogControllerImpl;


/**
 * Implements the Interface of 3 Listener for the Bricklet Rotary Encoder  <br>
 */
public class MultiTouchListenerImpl implements BrickletMultiTouch.TouchStateListener {

	// Parameter
	private MultiTouchConsumer consumer;
	
	private int oldState = 0;
//	private Date pressedStarted = null;
	
	
	/**
	 * Constructor  <br>
	 * 
	 * @param consumer
	 */
	public MultiTouchListenerImpl(
			MultiTouchConsumer consumer){
		this.consumer = consumer;
	}


//	/* (non-Javadoc)
//	 * @see com.tinkerforge.BrickletRotaryEncoder.CountListener#count(int)
//	 */
//	@Override
//	public synchronized void count(int count) {
//		consumer.rotaryEncoderCounterValueChanged(count, oldCount);
//		oldCount = count;
//	}
//
//
//	/* (non-Javadoc)
//	 * @see com.tinkerforge.BrickletRotaryEncoder.PressedListener#pressed()
//	 */
//	@Override
//	public void pressed() {
//		pressedStarted = new Date();
//		consumer.rotaryEncoderPressed();
//	}
//
//	/* (non-Javadoc)
//	 * @see com.tinkerforge.BrickletRotaryEncoder.ReleasedListener#released()
//	 */
//	@Override
//	public void released() {
//		if (pressedStarted != null){
//			consumer.rotaryEncoderReleased(new Date().getTime() - pressedStarted.getTime());
//		} else {
//			// Released without a catched Press event: return zero as a time period since pressed event
//			consumer.rotaryEncoderReleased(0l);
//		}
//	}
//
//
	/* (non-Javadoc)
	 * @see com.tinkerforge.BrickletMultiTouch.TouchStateListener#touchState(int)
	 * 
	 * Will be called if the listener detects a state change
	 */
	@Override
	public void touchState(int state) {
        // Get current touchState
//        int touchState = mt.getTouchState(); // Can throw com.tinkerforge.TimeoutException

        String msg = "";

        if((state & (1 << 12)) == (1 << 12)) {
            msg += "In proximity, ";
        }

        if((state & 0xfff) == 0) {
            msg += "No electrodes touched";
        } else {
            msg += "Electrodes ";
            for(int i = 0; i < 12; i++) {
                if((state & (1 << i)) == (1 << i)) {
                    msg += i + " ";
                }
            }
            msg += "touched";
        }

        log(msg);

		consumer.multiTouchStateValueChanged(state, oldState);
		oldState = state;
		
	}

	/**
	 * Prepares and writes log msg
	 * @param msg - the state of CommonObserver
	 */
	private void log(String msg){
		
		if (LogControllerImpl.getInstance() == null){
			System.out.println(FormatterService.getDateHHMMSSS(new Date())+ "  MultitouchListener: CommonObserver: "+msg);
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage("Multitouch Listener", "CommonObserver",  msg);
		}
		

	}


}
