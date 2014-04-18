package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.controller.LogController;
import de.kabuman.tinkerforge.alarm.controller.LogControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;

public class RcRestartSwitchItemImpl extends InputIO16ItemImpl {

	public RcRestartSwitchItemImpl(
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
		ResetControllerImpl.getInstance().restart();
		LogControllerImpl.getInstance().createUserLogMessage(LogController.UNIT_ALERT_RC,LogController.TASTER_RESTART,LogController.MSG_RESTART);

	}

}
