package de.kabuman.tinkerforge.aquarium.threads;

import java.util.Date;

import com.tinkerforge.BrickServo;

import de.kabuman.tinkerforge.aquarium.config.CfgServo;
import de.kabuman.tinkerforge.aquarium.config.CfgUnit;
import de.kabuman.tinkerforge.services.config.CfgEmail;

public class TimerData {
	
	private short threadId;
	private short timerId;
	private BrickServo brickServo;
	private short servoId;
	private Short position;
	private Date dateTime;
	private CfgEmail cfgEmail;
	
	public TimerData(
			short threadId
			, short timerId
			, BrickServo brickServo
			, short servoId
			, Short position
			, Date dateTime
			, CfgEmail cfgEmail){
		this.threadId = threadId;
		this.timerId = timerId;
		this.brickServo = brickServo;
		this.servoId = servoId; 
		this.position = position;
		this.dateTime = dateTime;
		this.cfgEmail = cfgEmail;
	}

	public BrickServo getBrickServo() {
		return brickServo;
	}

	public void setBrickServo(BrickServo brickServo) {
		this.brickServo = brickServo;
	}

	public Short getPosition() {
		return position;
	}

	public void setPosition(Short position) {
		this.position = position;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public CfgEmail getCfgEmail() {
		return cfgEmail;
	}

	public void setCfgEmail(CfgEmail cfgEmail) {
		this.cfgEmail = cfgEmail;
	}

	public short getServoId() {
		return servoId;
	}

	public void setServoId(short servoId) {
		this.servoId = servoId;
	}

	public short getTimerId() {
		return timerId;
	}

	public void setTimerId(short timerId) {
		this.timerId = timerId;
	}

	public short getThreadId() {
		return threadId;
	}

	public void setThreadId(short threadId) {
		this.threadId = threadId;
	}

}
