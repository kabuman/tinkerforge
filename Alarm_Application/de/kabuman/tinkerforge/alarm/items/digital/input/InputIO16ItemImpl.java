package de.kabuman.tinkerforge.alarm.items.digital.input;

import com.tinkerforge.BrickletIO16;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.tinkerforge.alarm.devicehelper.IO16device;
import de.kabuman.tinkerforge.services.IOService;

public abstract class InputIO16ItemImpl extends IO16device implements InterruptConsumerSwitch{

	BrickletIO16 io16 = null;
	long debouncePeriod = 0;
	char port = ' ';
	Short interrupt = null;
	
	boolean enable = false;

	// Interrupt Listener with test
	IO16InterruptListenerImpl interruptListener = null;

	/**
	 * Helper for IO16 Bricklet.
	 * 
	 * @param io16 - the BrickletIO16 Object (must be connected)
	 * @param debouncePeriod - the debounce period (long). default is 500 msec (500l)
	 * @param port - the port: 'a' or 'b'
	 * @param interrupt - the interrupt bit which is to use 
	 * @param enable - true, if the input channel interrupt is to set immddiately)
	 */
	public InputIO16ItemImpl(
			BrickletIO16 io16,
			long debouncePeriod,
			char port,
			short interrupt,
			boolean enable){
		super(io16);
		this.io16 = io16;
		this.debouncePeriod = debouncePeriod;
		this.port = port;
		this.interrupt = interrupt;
		
		install();
		
		if (enable){
			enable();
			setStartingPostion();
		} else {
			disable();
		}
	}

	private void install(){
		enable = false;
		
		try {
			io16.setPortConfiguration(port, IOService.setBitON(interrupt), 'i', true);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		
		interruptListener = new IO16InterruptListenerImpl(this, port, interrupt);

		io16.addInterruptListener(interruptListener);

//		io16.addInterruptListener(new BrickletIO16.InterruptListener() {
//			public synchronized void interrupt(char port, short interruptMask, short valueMask) {
//				
//	            if (IOService.isInterruptedBy(interrupt,interruptMask )){
//	            	if (IOService.isSwitchedON(interrupt,valueMask )){
//	            		switchedOFF();
//	            	} else {
//	            		switchedON();
//	            	}
//	            }
//			}
//		});
	}
	
	public void removeListener(){
		io16.removeInterruptListener(interruptListener);
	}
	
	private void setStartingPostion(){
		try {
			if (IOService.isSwitchedON(interrupt, io16.getPort(port))){
                switchedOFFbeforeStart();

			} else {
                switchedONbeforeStart();
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	public void enable(){
		enable = true;
		setInterrupt(port, getAddedInterrupt(port, interrupt), debouncePeriod);
	}

	/**
	 * Disable the input channel. Interrupt Bit will be removed.
	 */
	public void disable(){
		enable = false;
		setInterrupt(port, getSubtractedInterrupt(port, interrupt), debouncePeriod);
	}
	
	/**
	 * Returns true if input channel is active (Interrupt Bit is set)
	 * @return
	 */
	public boolean isEnabled(){
		return enable;
	}
	
	/**
	 * Must be implemented by Class which extends this.
	 */
	public abstract void switchedOFF();
	
	/**
	 * Must be implemented by Class which extends this.
	 */
	public abstract void switchedON();
	
	/**
	 * Defines the action if the input channel is Off (open) after start 
	 * Can be overridden by class which extends
	 */
	public void switchedOFFbeforeStart(){
		switchedOFF();
	}
	
	/**
	 * Defines the action if the input channel is ON (closed) after start 
	 * Can be overridden by class which extends
	 */
	public void switchedONbeforeStart(){
		switchedON();
	}
	

}
