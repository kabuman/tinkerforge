package de.kabuman.tinkerforge.alarm.config;

import de.kabuman.tinkerforge.services.config.CfgRemoteSwitchData;


public class CfgProtectUnit extends CfgUnit{

	private Integer ir;
	private Integer ai;
	private Integer tp;
	private Integer hm;
	private Integer md;
	private Integer vc;
	private Short distance;
	private Short aiVoltageThreshold;
	private Short vcVoltageThreshold;
	private Short vcCurrentThresholdOk;
	private Short vcCurrentThresholdAlert;
	
	/**
	 * Config Object for Protect Unit
	 * 
	 * @param durationLocalBeeper - the amount of time for an alert duration in msec
	 */
	
	
	/**
	 * @param cfgRemoteSwitchData - group of config data for the remote switch
	 * @param host - ip address
	 * @param port - port, typically 4232
	 * @param unitName - the name of this unit
	 * @param mb - UID_xxx of the brick master (see Interface DeviceIdentifier)
	 * @param mbUsedFor
	 * @param io4 - UID_xxx of the bricklet io4: input/output 4 channel 
	 * @param ioUsedFor
	 * @param ir - UID_xxx of the bricklet ir: distance infrarot (motion detector)
	 * @param distance - config of bricklet ir
	 * @param ai - UID_xxx of the bricklet analog input (smoke detector)
	 * @param aiVoltageThreshold - config of bricklet ai
	 * @param tp - UID_xxx of the bricklet temperature (air thermometer) 
	 * @param hm - UID_xxx of the bricklet humidity (hygrometer) 
	 * @param md - motion detection bricklet (Bewegungsmelder)
	 * @param vc - voltage/current bricklet (Rauchmelder)
	 * @param vcVoltageThreshold - config of bricklet vc
	 * @param vcCurrentThresholdOk - config of bricklet vc
	 * @param vcCurrentThresholdAlert - config of bricklet vc
	 */
	public CfgProtectUnit (
			CfgRemoteSwitchData cfgRemoteSwitchData,
			String host, 
			int port, 
			String unitName, 
			int mb, 
			String mbUsedFor,
			Integer io,
			String ioUsedFor,
			Integer ir,
			Short distance,
			Integer ai,
			Short aiVoltageThreshold,
			Integer tp,
			Integer hm,
			Integer md,
			Integer vc,
			Short vcVoltageThreshold,
			Short vcCurrentThresholdOk,
			Short vcCurrentThresholdAlert){
		super(cfgRemoteSwitchData, host,port,unitName,mb,mbUsedFor,io,ioUsedFor);

		this.ir = ir;
		this.ai = ai;
		this.tp = tp;
		this.hm = hm;
		this.md = md;
		this.vc = vc;
		this.distance = distance;
		this.aiVoltageThreshold = aiVoltageThreshold;
		this.vcVoltageThreshold = vcVoltageThreshold;
		this.vcCurrentThresholdOk = vcCurrentThresholdOk;
		this.vcCurrentThresholdAlert = vcCurrentThresholdAlert;
	}

	public Integer getIr() {
		return ir;
	}

	public Integer getAi() {
		return ai;
	}

	public Short getDistance() {
		return distance;
	}

	public Integer getTp() {
		return tp;
	}

	public Integer getHm() {
		return hm;
	}

	public Integer getMd() {
		return md;
	}

	public Short getAiVoltageThreshold() {
		return aiVoltageThreshold;
	}

	public Integer getVc() {
		return vc;
	}

	public Short getVcVoltageThreshold() {
		return vcVoltageThreshold;
	}

	public Short getVcCurrentThresholdOk() {
		return vcCurrentThresholdOk;
	}

	public Short getVcCurrentThresholdAlert() {
		return vcCurrentThresholdAlert;
	}
}
