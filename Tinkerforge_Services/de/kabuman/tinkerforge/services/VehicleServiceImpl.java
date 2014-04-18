package de.kabuman.tinkerforge.services;

import java.text.DecimalFormat;

public class VehicleServiceImpl implements VehicleService {

	short maxVelocity = 0;
	
	String operatingTimeAppl;
	String operatingTimeMotor;
	
	
	public VehicleServiceImpl() {
	}

	public short getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(short maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public String getFormMaxVelocity() {
		// max velocity
	    DecimalFormat velocityForm = new DecimalFormat( "####0" );
		return velocityForm.format(maxVelocity);
	}

}
