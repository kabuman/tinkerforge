package de.kabuman.tinkerforge.aquarium.config;

public class CfgServo {

	private String name;
	private short servoId;
	private int velocity;
	private int acceleration;
	private short degreeMin;
	private short degreeMax;
	private int period;
	
	CfgServo(String name
			, short servoId
			, int velocity
			, int acceleration
			, short degreeMin
			, short degreeMax
			, int period){
		this.name = name;
		this.servoId = servoId;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.degreeMin = degreeMin;
		this.degreeMax = degreeMax;
		this.period = period;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getServoId() {
		return servoId;
	}

	public void setServoId(short servoId) {
		this.servoId = servoId;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public int getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(int acceleration) {
		this.acceleration = acceleration;
	}

	public short getDegreeMin() {
		return degreeMin;
	}

	public void setDegreeMin(short degreeMin) {
		this.degreeMin = degreeMin;
	}

	public short getDegreeMax() {
		return degreeMax;
	}

	public void setDegreeMax(short degreeMax) {
		this.degreeMax = degreeMax;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

}
