package de.kabuman.tinkerforge.screencontroller.source.rotaryencoder;

import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickletRotaryEncoder;

import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer;
import de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderSupplierImpl;


/**
 * Demonstrates the PUSH of a change in item content  <br>
 * Can push it to more than one screen item  <br>
 * 
 * This thread triggers the pressed event <br>
 * up to the screen manager.  <br>
 *  <br>
 * So it must implement the Interface ItemsourceToPush
 */
public class RotaryEncoderPressedSource extends Thread  implements ItemSourceToPush, RotaryEncoderConsumer{

	// the screen item value to push
	private boolean pressed = false;
	
	// The usage of a list provices the possibility to use more than one screen items to push
	private List<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
	
	private RotaryEncoderSupplierImpl re;
	
	/**
	 * Constructor and Starter
	 */
	public RotaryEncoderPressedSource(BrickletRotaryEncoder tfRotaryEncoder) {
		re = new RotaryEncoderSupplierImpl(this, tfRotaryEncoder);
		re.activate();
		
		start();  // calls the run() method
	}

	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPull#getItemValue()
	 *  <br>
	 * This method is required for the PULL of the value by screen controller.  <br>
	 * And the PULL is triggered by this Thread (PUSH). 
	 */
	@Override
	public Object getItemValue() {
		return pressed;
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush#setItem(de.kabuman.tinkerforge.screencontroller.items.ScreenItem)
	 * 
	 * This method is required to receive the screen item
	 */
	@Override
	public void addTtem(ScreenItem screenItem){
		this.screenItemList.add(screenItem);
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderCounterValueChanged(int, int)
	 */
	@Override
	public void rotaryEncoderCounterValueChanged(int newValue, int oldValue) {
		// will not evaluated
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressed()
	 */
	@Override
	public void rotaryEncoderPressed() {
		pressed = true;
		
		for (ScreenItem screenItem : screenItemList) {
			screenItem.refreshValue();
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderReleased(long)
	 */
	@Override
	public void rotaryEncoderReleased(long pressedDuration) {
		pressed = false;

		for (ScreenItem screenItem : screenItemList) {
			screenItem.refreshValue();
		}
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.rotaryencoder.RotaryEncoderConsumer#rotaryEncoderPressedTimeReached()
	 */
	@Override
	public void rotaryEncoderPressedTimeReached() {
		// will not evaluated
	}
}
