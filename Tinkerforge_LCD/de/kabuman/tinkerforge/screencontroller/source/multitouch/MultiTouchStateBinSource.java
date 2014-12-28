package de.kabuman.tinkerforge.screencontroller.source.multitouch;

import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickletMultiTouch;

import de.kabuman.common.services.StringService;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer;
import de.kabuman.tinkerforge.services.multitouch.MultiTouchSupplierImpl;


/**
 * Demonstrates the PUSH of a change in item content  <br>
 * Can push it to more than one screen item  <br>
 *  <br>
 * This thread triggers a change in the Rotary Encoder Counter  <br>
 * up to the screen manager.  <br>
 *  <br>
 * So it must implement the Interface ItemsourceToPush
 */
public class MultiTouchStateBinSource extends Thread  implements ItemSourceToPush, MultiTouchConsumer{

	// the screen item value to push
	int state;
	
	// The usage of a list provices the possibility to use more than one screen items to push
	private List<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
	
	BrickletMultiTouch tfMultiTouch;
	
	MultiTouchSupplierImpl multiTouchSupplier;

	/**
	 * Constructor and Starter
	 */
	public MultiTouchStateBinSource(BrickletMultiTouch tfMultiTouch) {
		this.tfMultiTouch = tfMultiTouch;
		multiTouchSupplier = new MultiTouchSupplierImpl(this, tfMultiTouch, MultiTouchSupplierImpl.ALL_ELECTRODES_WITHOUT_13, null);
		multiTouchSupplier.activate();
		
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
		return StringService.fillLeft(Integer.toBinaryString(state), 12, "0");
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
	 * @see de.kabuman.tinkerforge.services.multitouch.MultiTouchConsumer#multiTouchStateValueChanged(int, int)
	 */
	@Override
	public void multiTouchStateValueChanged(int newValue, int oldValue) {
		// Transfer value
		state = newValue;
		
		for (ScreenItem screenItem : screenItemList) {
			screenItem.refreshValue();
		}

	}


	@Override
	public void multiTouchStateValueTimerReached(int state) {
		// TODO Auto-generated method stub
		
	}
}
