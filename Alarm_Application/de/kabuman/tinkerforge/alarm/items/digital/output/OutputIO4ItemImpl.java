package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletIO4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.IOService;

public class OutputIO4ItemImpl implements OutputItem{
	
	// Parameter Values
	private BrickletIO4 io4 = null;
	private short interrupt;
	private Long durationMonoflop = null;

	public OutputIO4ItemImpl(
			BrickletIO4 io4,
			short interrupt,
			Long durationMonoflop){
		
		this.io4 = io4;
		this.interrupt = interrupt;
		this.durationMonoflop = durationMonoflop;
		
		switchOFF();
	}

	public void switchON(){
		switcher('o');
	}
	
	public void switchOFF(){
		switcher('i');
	}
	
	private void switcher(char direction){
		try {
			io4.setConfiguration(IOService.setBitON(interrupt), direction, true);
			if (durationMonoflop != null){
				io4.setMonoflop(IOService.setBitON(interrupt),IOService.setBitON(interrupt),durationMonoflop);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
}
