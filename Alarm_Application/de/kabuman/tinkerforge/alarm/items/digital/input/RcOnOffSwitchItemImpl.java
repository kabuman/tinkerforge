package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;
import de.kabuman.tinkerforge.screencontroller.sources.ItemSourceToPush;

public class RcOnOffSwitchItemImpl extends InputIO16ItemImpl implements ItemSourceToPush{
	
	// ScreenController
	private ScreenItem screenItem = null;
	private Boolean switchedON = true;

	public RcOnOffSwitchItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable) {
		super(io16, debouncePeriod, port, interrupt, enable);
		

		// ScreenController
		if (AlertControllerImpl.getInstance().isOn()){
			switchedON = true;
		} else {
			switchedON = false;
		}
		
	}

	
	@Override
	public void switchedOFF() {
//		System.out.println("RcOnOff SwitchedOFF: OFF");
        AlertControllerImpl.getInstance().switchOff();
//		LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.SWIITCH_ON_OFF,LogController.MSG_OFF);
		
		// ScreenController
		switchedON = false;
		refreshContentOnLCD();
	}

	
	@Override
	public void switchedON() {
//		System.out.println("RcOnOff SwitchedON : ON");
        AlertControllerImpl.getInstance().switchOn();
//		LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.SWIITCH_ON_OFF,LogController.MSG_ON);

		// ScreenController
		switchedON = true;
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
		return switchedON;
	}

	
	/**
	 * ScreenController
	 * Must be implemented due to ".. implements ItemSourceToPush"
	 * Sets the screenItem after instancing of this class 
	 */
	public void addTtem(ScreenItem screenItem){
		this.screenItem = screenItem;
	}


	public Boolean isSwitchedON() {
		return switchedON;
	}

}
