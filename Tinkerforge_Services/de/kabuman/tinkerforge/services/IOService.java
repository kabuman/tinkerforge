package de.kabuman.tinkerforge.services;

public class IOService {
	/**
	 * Returns
	 * true, if the given switch is the trigger of these interrupt
	 * false if no.
	 * 
	 * So this method makes a bit check.
	 * 
	 * @param switchPos - the position of the given switch which is to test
	 * @param interruptMask - the given interrupMask given by IO4-Device
	 * @return boolean - true: yes the given switchPos is the reason for the interrupt
	 */
	public static synchronized boolean isInterruptedBy( int switchPos, int interruptMask )
	{
		// Create a mask (based on the given switchPos) for comparison with the interrupt mask
	  int mask = 1 << switchPos;
	
	  // return true if interrupt mask and the created mask (based on the given switchPos) is equal
	  return (interruptMask & mask) == mask; 
	}

	/**
	 * Returns
	 * true, if the given switch is switched on
	 * false if not.
	 * 
	 * So this method makes a bit check.
	 * 
	 * @param switchPos - the position of the given switch which is to test
	 * @param valueMask - the given valueMask given by IO4-Device (contains the states of each switch)
	 * @return boolean - true: yes the given switchPos is switched on
	 */
	public static synchronized boolean isSwitchedON(int switchPos, int valueMask )
	{
		// Create a mask (based on the given switchPos) for comparison with the interrupt mask
	  int mask = 1 << switchPos;
	
	  // return true if value mask and the created mask (based on the given switchPos) is equal
	  return (valueMask & mask) == mask;
	}
	
	/**
	 * @param bitNo - the bit to set (0,..15)
	 * @return short - the result
	 */
	public static synchronized short setBitON(int bitNo) {
		return (short)(1 << bitNo);
	}
	
	public static synchronized short setBitOFF(int bitNo) {
		return (short)(0 << bitNo);
	}
	
	public static synchronized short setBitON(short target, int bitNo){
		return (short) (setBitON(bitNo) | target);
	}
}
