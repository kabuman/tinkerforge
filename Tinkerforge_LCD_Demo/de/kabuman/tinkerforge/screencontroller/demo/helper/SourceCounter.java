package de.kabuman.tinkerforge.screencontroller.demo.helper;

import java.util.ArrayList;
import java.util.List;

import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;


/**
 * Demonstrates the PUSH of a change in item content  <br>
 * Can push it to more than one screen item  <br>
 * 
 * This thread counts seconds from the start  <br>
 * This can be the case for a switch or something like that
 */
public class SourceCounter extends Thread  implements ItemSourceToPush{
	// Push items must implement the interface ItemSourceToPush


	long content = 0;
	
	// The usage of a list provices the possibility to use more than one screen items to push
	private List<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
	
	
	/**
	 * Constructor and Starter
	 */
	public SourceCounter() {
		start();  // calls the run() method
	}

	
	/**
	 * This method will be called by starting the thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			while (true) {
				content++;
				refreshContentOnLCD();
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// this is the end of the the while loop
		} 
	}

	
	/**
	 * This is the PUSH (refresh) of the item content 
	 */
	private void refreshContentOnLCD(){
		for (ScreenItem screenItem : screenItemList) {
			screenItem.refreshValue();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPull#getItemValue()
	 * 
	 * This method is required for the PULL of the value by screen controller
	 */
	@Override
	public Object getItemValue() {
		return content;
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
}
