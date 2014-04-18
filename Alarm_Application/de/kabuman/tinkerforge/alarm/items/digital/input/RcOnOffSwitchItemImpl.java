package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;

public class RcOnOffSwitchItemImpl extends InputIO16ItemImpl{
	
	public RcOnOffSwitchItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable) {
		super(io16, debouncePeriod, port, interrupt, enable);
	}

	@Override
	public void switchedOFF() {
//		System.out.println("RcOnOff SwitchedOFF: OFF");
        AlertControllerImpl.getInstance().switchOff();
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.SWIITCH_ON_OFF,LogController.MSG_OFF);

	}

	@Override
	public void switchedON() {
//		System.out.println("RcOnOff SwitchedON : ON");
        AlertControllerImpl.getInstance().switchOn();
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.SWIITCH_ON_OFF,LogController.MSG_ON);
	}

}
