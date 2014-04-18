package de.kabuman.tinkerforge.alarm.config;


public class CfgProtectUnit extends CfgUnit{

	private Integer ir;
	private Integer ai;
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
	 * @param durationLocalBeeper - the amount of time for an alert duration in msec
	 */
	public CfgProtectUnit (
			String host, 
			int port, 
			String unitName, 
			int mb, 
			String mbUsedFor,
			Integer io,
			String ioUsedFor,
			Integer ir,
			Short distance,
			Integer ai){
		super(host,port,unitName,mb,mbUsedFor,io,ioUsedFor);

		this.ir = ir;
		this.ai = ai;
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
}
