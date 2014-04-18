package de.kabuman.tinkerforge.alarm.devicehelper;

import de.kabuman.tinkerforge.services.IOService;

public abstract class InterruptBits {
	
	public short getAddedInterrupt(short interrupt){
		short addedInterrupt = (short) (getCurrentInterrupts() | IOService.setBitON(interrupt));

		System.out.println("IO4device:getAddedInterrupt:: getCurrentInterrupts=" + Integer.toBinaryString(getCurrentInterrupts()));
		System.out.println("IO4device:getAddedInterrupt:: interrupt=" + Integer.toBinaryString(IOService.setBitON(interrupt)));
		System.out.println("IO4device:getAddedInterrupt:: addedInterrupt=" + Integer.toBinaryString(addedInterrupt));

//		return (short) (getCurrentInterrupts() | IOService.setBitON(interrupt));
		return addedInterrupt;
	}

	public short getSubtractedInterrupt(short interrupt){
		System.out.println("IO4device:getSubtractedInterrupt:: addedInterrupt=" + Integer.toBinaryString(getCurrentInterrupts()));
		short subtractedInterrupt = (short) (getCurrentInterrupts() ^ IOService.setBitON(interrupt));  // XOR: exclusive or
		System.out.println("IO4device:getSubtractedInterrupt:: interrupt=" + Integer.toBinaryString(IOService.setBitON(interrupt)));
		System.out.println("IO4device:getSubtractedInterrupt:: subtractedInterrupt=" + Integer.toBinaryString(subtractedInterrupt));

//		return (short) (getCurrentInterrupts() ^ interrupt);  // XOR: exclusive or
		return subtractedInterrupt;
	}

//	@Override
	abstract short getCurrentInterrupts();
	

}
