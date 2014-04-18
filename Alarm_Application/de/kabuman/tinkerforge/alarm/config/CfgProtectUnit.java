package de.kabuman.tinkerforge.alarm.config;


public class CfgProtectUnit extends CfgUnit{

	private Integer ir;
	private Integer ai;
	private Integer tp;
	private Integer hm;
	private Integer md;
	private Short distance;
	
	/**
	 * Config Object for Protect Unit
	 * 
	 * @param host - ip address
	 * @param port - port, typically 4232
	 * @param unitName - the name of this unit
	 * @param mb - UID_xxx of the brick master (see Interface DeviceIdentifier)
	 * @param ir - UID_xxx of the bricklet ir: distance infrarot
	 * @param io4 - UID_xxx of the bricklet io4: input/output 4 channel 
	 * @param tp - UID_xxx of the bricklet temperature 
	 * @param hm - UID_xxx of the bricklet humidity 
	 * @param durationLocalBeeper - the amount of time for an alert duration in msec
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
			Integer tp,
			Integer hm,
			Integer md){
		super(cfgRemoteSwitchData, host,port,unitName,mb,mbUsedFor,io,ioUsedFor);

		this.ir = ir;
		this.ai = ai;
		this.tp = tp;
		this.hm = hm;
		this.md = md;
		this.distance = distance;
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
}
