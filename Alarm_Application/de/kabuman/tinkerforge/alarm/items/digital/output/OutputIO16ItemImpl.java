package de.kabuman.tinkerforge.alarm.items.digital.output;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.services.IOService;

public class OutputIO16ItemImpl implements OutputItem{
	
	// Parameter Values
	private BrickletIO16 io16 = null;
	private char port = 'a';
	private short interrupt;
	private Long durationMonoflop = null;

	public OutputIO16ItemImpl(
			BrickletIO16 io16,
			char port,
			short interrupt,
			Long durationMonoflop){
		
		this.io16 = io16;
		this.port = port;
		this.interrupt = interrupt;
		this.durationMonoflop = durationMonoflop;
		
		switchOFF();
	}

	public OutputIO16ItemImpl(
			BrickletIO16 io16,
			char port,
			short interrupt){
		
		this.io16 = io16;
		this.port = port;
		this.interrupt = interrupt;
		
		switchOFF();
	}

	public void switchON(){
		switcher('o');
	}
	
	public void switchON(long durationMonoflop){
		this.durationMonoflop = durationMonoflop;
		switchON();
	}
	
	public void switchOFF(){
		switcher('i');
	}
	
	private void switcher(char direction){
		try {
			io16.setPortConfiguration(port, IOService.setBitON(interrupt), direction, true);
			if (durationMonoflop != null){
				io16.setPortMonoflop(port, IOService.setBitON(interrupt),IOService.setBitON(interrupt),durationMonoflop);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
}
