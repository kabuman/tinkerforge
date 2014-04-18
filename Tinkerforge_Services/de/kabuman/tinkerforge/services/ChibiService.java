package de.kabuman.tinkerforge.services;


public interface ChibiService {
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getSignalStrength()
	 */
	public String getChibiSignalStrength();

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiAddress()
	 */
	public String getChibiAddress();

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiMasterAddress()
	 */
	public String getChibiMasterAddress();

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiErrorLog()
	 */
	public String getChibiErrorLog();
	
	public Integer getChibiErrorNoAck();

}
