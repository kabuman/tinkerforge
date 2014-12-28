package de.kabuman.tinkerforge.screencontroller.source.rotaryencoder;

import java.util.ArrayList;
import java.util.List;

import com.tinkerforge.BrickletVoltageCurrent;

import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;
import de.kabuman.tinkerforge.services.voltagecurrent.VoltageConsumer;
import de.kabuman.tinkerforge.services.voltagecurrent.VoltageCurrentSupplierImpl;


/**
 * Demonstrates the PUSH of a change in item content  <br>
 * Can push it to more than one screen item  <br>
 *  <br>
 * This thread triggers a change in the Rotary Encoder Counter  <br>
 * up to the screen manager.  <br>
 *  <br>
 * So it must implement the Interface ItemsourceToPush
 */
public class CurrentSource extends Thread  implements ItemSourceToPush, VoltageConsumer{

	// the screen item value to push
	int current = 0;
	
	// The usage of a list provices the possibility to use more than one screen items to push
	private List<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
	
	BrickletVoltageCurrent tfVoltageCurrent;
	
	VoltageCurrentSupplierImpl re;
	
	/**
	 * Constructor and Starter
	 */
	public CurrentSource(BrickletVoltageCurrent tfVoltageCurrent) {
		this.tfVoltageCurrent = tfVoltageCurrent;
		re = new VoltageCurrentSupplierImpl(this, tfVoltageCurrent);
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
		return current;
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


	@Override
	public void voltageValueChanged(int newValue, int oldValue) {
	}


	@Override
	public void currentValueChanged(int newValue, int oldValue) {
		// Transfer value
		current = newValue;
		
		for (ScreenItem screenItem : screenItemList) {
			screenItem.refreshValue();
		}
	}
}
