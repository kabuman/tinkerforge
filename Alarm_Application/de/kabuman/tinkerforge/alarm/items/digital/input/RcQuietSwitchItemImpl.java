package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;

public class RcQuietSwitchItemImpl extends InputIO16ItemImpl implements ItemSourceToPush{

	// ScreenController
	private ScreenItem screenItem = null;
	
	private Boolean quiet = true;
	
	public RcQuietSwitchItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable) {
		super(io16, debouncePeriod, port, interrupt, enable);
		
		// ScreenController
		if (AlertControllerImpl.getInstance().isQuiet()){
			quiet = true;
		} else {
			quiet = false;
		}
	}

	@Override
	public void switchedOFF() {
//		System.out.println("RcQuiet SwitchedOFF: quiet=true");
        AlertControllerImpl.getInstance().setQuiet(true);
//		LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.SWITCH_QUIET,LogController.MSG_QUIET_ON);
		
		// ScreenController
		quiet = true;
		refreshContentOnLCD();
	}

	@Override
	public void switchedON() {
//		System.out.println("RcQuiet SwitchedON : quiet=false");
        AlertControllerImpl.getInstance().setQuiet(false);
//		LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.SWITCH_QUIET,LogController.MSG_QUIET_OFF);
		
		// ScreenController
		quiet = false;
		refreshContentOnLCD();
	}

	/**
	 * ScreenController
	 * This is the PUSH (refresh) of the item content 
	 */
	private void refreshContentOnLCD(){
		if (screenItem != null){
			screenItem.refreshValue();
		}
	}
	
	
	/**
	 * ScreenController
	 * Must be implemented due to ".. implements ItemSourceToPush"
	 * Returns the content of the item 
	 */
	public Object getItemValue() {
		return quiet;
	}

	
	/**
	 * ScreenController
	 * Must be implemented due to ".. implements ItemSourceToPush"
	 * Sets the screenItem after instancing of this class 
	 */
	public void addTtem(ScreenItem screenItem){
		this.screenItem = screenItem;
	}

	public Boolean isQuiet() {
		return quiet;
	}


}
