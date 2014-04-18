package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;

import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;

public class ResetSwitchItemImpl extends InputIO4ItemImpl {

	String unitName = null;
	
	public ResetSwitchItemImpl(
			String unitName,
			BrickletIO4 io4,
			long debouncePeriod,
			short interrupt,
			boolean enable) {
		super(io4, debouncePeriod, interrupt, enable);
		this.unitName = unitName;
	}

	@Override
	public void switchedOFF() {
//		System.out.println("Reset SwitchedOFF");
	}

	@Override
	public void switchedON() {
//		System.out.println("Reset SwitchedON");
		ResetControllerImpl.getInstance().reset();
		LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.TASTER_RESET,LogController.MSG_RESET);
	}

}
