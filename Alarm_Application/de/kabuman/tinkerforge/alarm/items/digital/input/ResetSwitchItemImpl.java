package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO4;

import de.kabuman.common.services.LogController;
import de.kabuman.common.services.LogControllerImpl;
import de.kabuman.common.services.StopWatchService;
import de.kabuman.common.services.StopWatchServiceImpl;
import de.kabuman.tinkerforge.alarm.controller.AlertControllerImpl;
import de.kabuman.tinkerforge.alarm.controller.ResetControllerImpl;

public class ResetSwitchItemImpl extends InputIO4ItemImpl {

	String unitName;
	
	StopWatchService stopWatchService;
	
	/**
	 * @param unitName
	 * @param io4
	 * @param debouncePeriod
	 * @param interrupt
	 * @param enable
	 */
	public ResetSwitchItemImpl(
			String unitName,
			BrickletIO4 io4,
			long debouncePeriod,
			short interrupt,
			boolean enable) {
		super(io4, debouncePeriod, interrupt, enable);
		this.unitName = (unitName == null)? "ResetSwitchItemImpl" : unitName;
		System.out.println("ResetSwitchItemImpl:: unitName="+unitName);
		
		stopWatchService = new StopWatchServiceImpl();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InputIO4ItemImpl#switchedOFF()
	 */
	@Override
	public void switchedOFF() {
		String unitName = (this.unitName == null)? "ResetSwitchItemImpl" : this.unitName;
		
		// Taster is released
		LogControllerImpl.getInstance().createTechnicalLogMessage(unitName, "ResetSwitchItemImpl::switchedOFF()", "triggered by Interrupt Listener");
		if (stopWatchService != null){
			stopWatchService.stopOver();
			
			if (stopWatchService.getCurrent() > 2000){
				
				// Switch ON / OFF
				LogControllerImpl.getInstance().createTechnicalLogMessage(unitName, "ResetSwitchItemImpl::switchedOFF()", "Reset Taster pressed longer than 2 seconds. Value = "+stopWatchService.getCurrent());
				LogControllerImpl.getInstance().createUserLogMessage(unitName,"Reset Taster",LogController.MSG_RESET_PRESSED);
				if (AlertControllerImpl.getInstance().isOn()){
					AlertControllerImpl.getInstance().switchOff();
					LogControllerImpl.getInstance().createUserLogMessage(unitName,"Reset Taster",LogController.MSG_STOPPED);
				} else {
					AlertControllerImpl.getInstance().switchOn();
					LogControllerImpl.getInstance().createUserLogMessage(unitName,"Reset Taster",LogController.MSG_STARTED);
				}
			} else {
				
				// Reset
				LogControllerImpl.getInstance().createTechnicalLogMessage(unitName, "ResetSwitchItemImpl::switchedON()", "triggered by Interrupt Listener");
				ResetControllerImpl.getInstance().reset();
				LogControllerImpl.getInstance().createUserLogMessage(unitName,LogController.TASTER_RESET,LogController.MSG_RESET);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.alarm.items.digital.input.InputIO4ItemImpl#switchedON()
	 */
	@Override
	public void switchedON() {
		// Taster is pressed
		
		// start the stopWatch (to decide lateron if it is a Reset or a Switch On/Off
		stopWatchService.restart();
		stopWatchService.start();
	}

}
