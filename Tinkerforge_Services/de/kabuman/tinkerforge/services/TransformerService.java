package de.kabuman.tinkerforge.services;

public class TransformerService {
	
	
	/**
	 * Transforms the rotary potentiometer position to the velocity of a motor (BrickDC)
	 * 
	 * @param position - the rotary potentiometer position (-150,..,0,...,+150)
	 * @return short - the calculated maximum velocity adjusted by the degree setting of the rotary potentiometer
	 */
	public static synchronized short rotaryDegreeToVelocity(short position){
		// Transform to range of 0,...,300 degree
	    double degree = position + 150.00; 	
	    
	    // Calculate Max Velocity. (Minimum is 10987)
	    double velocityMaxDouble = (double)(degree * 72.6 + 10987);
	    
	    // return as short
	    return (short)velocityMaxDouble;
	}

//	/**
//	 * Transforms the rotary potentiometer position to the velocity of a motor (BrickDC)
//	 * 
//	 * @param position - the rotary potentiometer position (-150,..,0,...,+150)
//	 * @return short - the calculated maximum velocity adjusted by the degree setting of the rotary potentiometer
//	 */
//	public static synchronized short rotaryDegreeToVelocity(short position){
//		// Limit the minimum of the maximum velocity
//		if (position < -50){
//			position = -50;
//		}
//		
//		// Map the poti degree to the maximum velocity
//	    double degree = 150.00; 	// its just to enforce the calculation below as data type "double"
//	    
//	    // position + degree:	converts the rotary poti position (-150,..,0,..+150) to the more likely range of (0,1,2,..,300)
//	    // divide by 300:		calculates it as percentage (result = 1: means full power/velocity
//	    // multiply by 32767:	converts the percentage to the required velocity
//	    double velocityMaxDouble = (double)(((position + degree) / 300) * 32767);
//	    
//	    // return as short
//	    return (short)velocityMaxDouble;
//	}

}
