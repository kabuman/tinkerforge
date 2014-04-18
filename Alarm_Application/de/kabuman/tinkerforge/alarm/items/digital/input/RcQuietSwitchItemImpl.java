package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;

public class RcQuietSwitchItemImpl extends InputIO16ItemImpl {

	public RcQuietSwitchItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable) {
		super(io16, debouncePeriod, port, interrupt, enable);
	}

	@Override
	public void switchedOFF() {
//		System.out.println("RcQuiet SwitchedOFF: quiet=true");
        AlertControllerImpl.getInstance().setQuiet(true);
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.SWITCH_QUIET,LogController.MSG_QUIET_ON);

	}

	@Override
	public void switchedON() {
//		System.out.println("RcQuiet SwitchedON : quiet=false");
        AlertControllerImpl.getInstance().setQuiet(false);
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.SWITCH_QUIET,LogController.MSG_QUIET_OFF);
	}

}
