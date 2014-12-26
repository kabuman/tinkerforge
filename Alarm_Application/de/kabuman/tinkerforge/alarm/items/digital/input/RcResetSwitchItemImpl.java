package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;

public class RcResetSwitchItemImpl extends InputIO16ItemImpl {

	public RcResetSwitchItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable) {
		super(io16, debouncePeriod, port, interrupt, enable);
	}

	@Override
	public void switchedOFF() {
//		System.out.println("RcReset SwitchedOFF: taster released");
	}

	@Override
	public void switchedON() {
//		System.out.println("RcReset SwitchedON : taster pressed");
		ResetControllerImpl.getInstance().reset();
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.TASTER_RESET,LogController.MSG_RESET);

	}

}
